package delivery.service;

import delivery.model.entity.DeliveryEntity;
import delivery.repository.DeliveryEntityRepository;
import kafka.DeliveryAssignedEvent;
import kafka.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DeliveryService {

        private final DeliveryEntityRepository repository;
        // В контексте есть единственный KafkaTemplate с типом <Long, DeliveryAssignedEvent>,
        // поэтому отдельный @Qualifier не нужен и только мешает.
        private final KafkaTemplate<Long, DeliveryAssignedEvent> kafkaTemplate;

    @Value("${delivery-assigned-topic}")
    private String deliveryAssignedTopic;

    public void processOrderPaid(OrderPaidEvent orderPaidEvent) {
        var orderId = orderPaidEvent.orderId();
        var found = repository.findByOrderId(orderId);
        if(found.isPresent()) {
            return;
        }
        var assignedDelivery = assignDelivery(orderId);
        sendDeliveryAssignedEvent(assignedDelivery);
    }

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

    private DeliveryEntity assignDelivery(Long orderId) {
        var entity = new DeliveryEntity();
        entity.setOrderId(orderId);
        entity.setCourierName("abob-"+ ThreadLocalRandom.current().nextInt(1,100));
        entity.setEtaMinutes(ThreadLocalRandom.current().nextInt(10, 60));
        return repository.save(entity);
    }
}
