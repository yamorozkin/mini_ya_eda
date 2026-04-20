package delivery.kafka;

import kafka.DeliveryAssignedEvent;
import kafka.DeliveryFinishedEvent;
import kafka.OrderPaidEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfiguration {

    //Конфиги для жесткой типизации, явное указание всех типов.

    @Bean
    DefaultKafkaProducerFactory<Long, DeliveryAssignedEvent> deliveryAssignedEventProducerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    KafkaTemplate<Long, DeliveryAssignedEvent> deliveryAssignedEventKafkaTemplate(
            DefaultKafkaProducerFactory<Long, DeliveryAssignedEvent> deliveryAssignedEventProducerFactory
    ) {
        return new KafkaTemplate<>(deliveryAssignedEventProducerFactory);
    }

    @Bean
    public ConsumerFactory<Long, OrderPaidEvent> orderPaidEventConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "kafka");
        // Явно указываем тип сообщения для десериализации
        JacksonJsonDeserializer<OrderPaidEvent> valueDeserializer =
                new JacksonJsonDeserializer<>(OrderPaidEvent.class);
        return new DefaultKafkaConsumerFactory<>(props, new LongDeserializer(), valueDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<?> orderPaidEventListenerFactory(ConsumerFactory<Long, OrderPaidEvent> orderPaidEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Long, OrderPaidEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderPaidEventConsumerFactory);
        factory.setBatchListener(false);
        return factory;
    }

    //продюсер для отправки сообщения что заказ доставлен
    @Bean
    DefaultKafkaProducerFactory<Long, DeliveryFinishedEvent> deliveryFinishedEventProducerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    KafkaTemplate<Long, DeliveryFinishedEvent> deliveryFinishedEventKafkaTemplate(
            DefaultKafkaProducerFactory<Long, DeliveryFinishedEvent> deliveryFinishedEventProducerFactory
    ) {
        return new KafkaTemplate<>(deliveryFinishedEventProducerFactory);
    }
}