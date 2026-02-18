package delivery.kafka;


import delivery.domain.DeliveryEntity;
import delivery.domain.DeliveryEntityRepository;
import kafka.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.ThreadLocalRandom;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class OrderPaidKafkaConsumer  {

    private final DeliveryEntityRepository deliveryEntityRepository;

    @KafkaListener
    public void listen(OrderPaidEvent orderPaidEvent) {
        var orderId = orderPaidEvent.orderId();
        var found = deliveryEntityRepository.findByOrderId(orderId);
        if(found.isPresent()) {
            return;
        }
        assignDelivery(orderId);
    }

    private void assignDelivery(Long orderId) {
        var entity = new DeliveryEntity();
        entity.setOrderId(orderId);
        entity.setCourierName("abob-"+ThreadLocalRandom.current().nextInt(1,100));
        entity.setEtaMinutes(ThreadLocalRandom.current().nextInt(10, 60));
        deliveryEntityRepository.save(entity);
    }
}
