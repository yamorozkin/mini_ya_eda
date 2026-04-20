package delivery.service;

import delivery.model.entity.DeliveryEntity;
import delivery.repository.DeliveryEntityRepository;
import kafka.DeliveryAssignedEvent;
import kafka.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryEntityRepository repository;
    private final KafkaTemplate<Long, DeliveryAssignedEvent> kafkaTemplate;
    private final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    @Value("${delivery-assigned-topic}")
    private String deliveryAssignedTopic;

    //Основной метод-обработчик.

    public void processOrderPaid(OrderPaidEvent orderPaidEvent) {
        var orderId = orderPaidEvent.orderId();
//        var found = repository.findByOrderId(orderId);
//        //если заказ с таким номером доставки уже существует значит он уже доставляется
//        if(found.isPresent()) {
//            return;
//        }
        var assignedDelivery = assignDelivery(orderId, orderPaidEvent.street(), orderPaidEvent.houseNumber());
        sendDeliveryAssignedEvent(assignedDelivery);
    }

    //Отправка события о назначении доставки в кафку.

    private void  sendDeliveryAssignedEvent(DeliveryEntity assignedDelivery) {
        kafkaTemplate.send(
                deliveryAssignedTopic,
                assignedDelivery.getOrderId(),
                DeliveryAssignedEvent.builder()
                        .courierName(assignedDelivery.getCourierName())
                        .orderId(assignedDelivery.getOrderId())
                        .etaMinutes(assignedDelivery.getEtaMinutes())
                        .build()
        );
    }

    //Назначение курьера и сохранение его в бд.

    private DeliveryEntity assignDelivery(Long orderId,  String street, Long houseNumber) {
        List<DeliveryEntity> deliveries = repository.findFreeCouriers(street);
        DeliveryEntity closestCourier =  deliveries.stream()
                .min(Comparator.comparingLong(courier ->
                        Math.abs(courier.getHouseNumber() - houseNumber)))
                .orElse(null);

        if (closestCourier == null) {
            throw new RuntimeException("No available couriers found near house " + houseNumber);
        }

        long dist = Math.abs(houseNumber - closestCourier.getHouseNumber());
        int etaMinutes = (int) (dist == 0 ? 2 : dist * 2);
        closestCourier.setEtaMinutes(etaMinutes);
        closestCourier.setOrderId(orderId);
        closestCourier.setDestinationStreet(street);
        closestCourier.setDestinationHouseNumber(houseNumber);

        return repository.save(closestCourier);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateDeliveryEta(){
        List<DeliveryEntity> deliveries = repository.findAllByOrderIdNot(0L);
        for (DeliveryEntity courier : deliveries) {
            Integer currentEta =  courier.getEtaMinutes();
            if(currentEta <= 0){
                continue;
            }
            if(currentEta == 1){
                courier.setEtaMinutes(0);
                sendDeliveryAssignedEvent(courier);
                log.info("Order number ={} has been delivered",  courier.getOrderId());
                courier.setOrderId(0L);
                courier.setHouseNumber(courier.getDestinationHouseNumber());
                courier.setStreet(courier.getDestinationStreet());
            }
            else{
                courier.setEtaMinutes(currentEta-1);
                log.info("Eta for order number ={} has been reduced by 1 minute",  courier.getOrderId());
                sendDeliveryAssignedEvent(courier);
            }
        }
        repository.saveAll(deliveries);
    }
}
