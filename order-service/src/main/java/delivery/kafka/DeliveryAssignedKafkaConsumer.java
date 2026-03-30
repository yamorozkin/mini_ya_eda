package delivery.kafka;


import delivery.service.OrderService;
import kafka.DeliveryAssignedEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
@AllArgsConstructor
public class DeliveryAssignedKafkaConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "${delivery-assigned-topic}",
            containerFactory = "deliveryAssignedEventListenerFactory"
    )
    public void listen(DeliveryAssignedEvent event) {
        orderService.processDeliveryAssigned(event);
    }
}
