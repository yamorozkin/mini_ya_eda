package delivery.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.kafka.core.KafkaTemplate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DeliveryServiceIntegrationTest {

    @Autowired
    private DeliveryService deliveryService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean(name = "deliveryAssignedEventKafkaTemplate")
    private KafkaTemplate deliveryAssignedEventKafkaTemplate;

    @org.springframework.test.context.bean.override.mockito.MockitoBean(name = "deliveryFinishedEventKafkaTemplate")
    private KafkaTemplate deliveryFinishedEventKafkaTemplate;

    @Test
    void contextLoads() {
        assertThat(deliveryService).isNotNull();
    }
}
