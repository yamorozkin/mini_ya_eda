package delivery.service;

import delivery.model.entity.DeliveryEntity;
import delivery.repository.DeliveryEntityRepository;
import kafka.DeliveryAssignedEvent;
import kafka.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryEntityRepository repository;
    private final KafkaTemplate<Long, DeliveryAssignedEvent> kafkaTemplate;

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
        List<DeliveryEntity> deliveries = repository.findFreeCouriers();
        DeliveryEntity closestCourier =  deliveries.stream()
                .min(Comparator.comparingLong(courier ->
                        Math.abs(courier.getHouseNumber() - houseNumber)))
                .orElse(null);

        if (closestCourier == null) {
            throw new RuntimeException("No available couriers found near house " + houseNumber);
        }

        Long distance = Math.abs(houseNumber - closestCourier.getHouseNumber());
        Integer etaMinutes = Math.toIntExact((distance != null) ? distance * 2 : 1);
        closestCourier.setEtaMinutes(etaMinutes);
        closestCourier.setOrderId(orderId);

        return repository.save(closestCourier);
    }
}
