package delivery.kafka;


import delivery.service.OrderService;
import kafka.DeliveryAssignedEvent;
import kafka.DeliveryFinishedEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
@AllArgsConstructor
public class DeliveryKafkaConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "${delivery-assigned-topic}",
            containerFactory = "deliveryAssignedEventListenerFactory"
    )
    public void listenAssigned(DeliveryAssignedEvent event) {
        orderService.processDeliveryAssigned(event);
    }

    @KafkaListener(
            topics = "${delivery-finished-topic}",
            containerFactory = "deliveryFinishedEventListenerFactory"
    )
    public void listenFinished(DeliveryFinishedEvent event) {
        orderService.processDeliveryFinished(event);
    }
}
