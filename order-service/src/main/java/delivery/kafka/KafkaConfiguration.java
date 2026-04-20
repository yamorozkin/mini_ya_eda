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
    DefaultKafkaProducerFactory<Long, OrderPaidEvent> orderPaidEventProducerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    KafkaTemplate<Long, OrderPaidEvent> orderPaidEventKafkaTemplate(DefaultKafkaProducerFactory<Long, OrderPaidEvent> orderPaidEventProducerFactory) {
        return new KafkaTemplate<>(orderPaidEventProducerFactory);
    }

    @Bean
    public ConsumerFactory<Long, DeliveryAssignedEvent> deliveryAssignedEventConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "kafka");
        JacksonJsonDeserializer<DeliveryAssignedEvent> valueDeserializer =
                new JacksonJsonDeserializer<>(DeliveryAssignedEvent.class);
        return new DefaultKafkaConsumerFactory<>(props, new LongDeserializer(), valueDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<?> deliveryAssignedEventListenerFactory(ConsumerFactory<Long, DeliveryAssignedEvent> deliveryAssignedEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Long, DeliveryAssignedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deliveryAssignedEventConsumerFactory);
        factory.setBatchListener(false);
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, DeliveryFinishedEvent> deliveryFinishedEventConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "kafka");

        // Указываем класс DeliveryFinishedEvent для десериализации
        JacksonJsonDeserializer<DeliveryFinishedEvent> valueDeserializer =
                new JacksonJsonDeserializer<>(DeliveryFinishedEvent.class);
        return new DefaultKafkaConsumerFactory<>(props, new LongDeserializer(), valueDeserializer);
    }


    @Bean
    public KafkaListenerContainerFactory<?> deliveryFinishedEventListenerFactory(
            ConsumerFactory<Long, DeliveryFinishedEvent> deliveryFinishedEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Long, DeliveryFinishedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deliveryFinishedEventConsumerFactory);
        factory.setBatchListener(false);
        return factory;
    }
}