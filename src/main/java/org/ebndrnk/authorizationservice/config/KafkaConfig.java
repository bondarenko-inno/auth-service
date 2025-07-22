package org.ebndrnk.authorizationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.ebndrnk.authorizationservice.kafka.dto.UserCreatedEvent;
import org.ebndrnk.authorizationservice.kafka.dto.UserProfileCreationFailedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration class for setting up producer, consumer, topics, and listener factories
 * for handling user creation and failure events.
 */
@Configuration
public class KafkaConfig {

    @Value("${environment.kafka.address}")
    private String bootstrapServers;

    /**
     * Defines a Kafka topic named "user.created" with 3 partitions and 1 replica.
     *
     * @return the configured Kafka topic
     */
    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name("user.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Configures the Kafka producer factory for {@link UserCreatedEvent}.
     *
     * @return the producer factory
     */
    @Bean
    public ProducerFactory<String, UserCreatedEvent> userCreatedEventProducerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.LINGER_MS_CONFIG, 20);
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd");
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * Provides a KafkaTemplate for publishing {@link UserCreatedEvent} messages.
     *
     * @return the KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, UserCreatedEvent> userCreatedEventKafkaTemplate() {
        KafkaTemplate<String, UserCreatedEvent> template = new KafkaTemplate<>(userCreatedEventProducerFactory());
        template.setObservationEnabled(true);
        return template;
    }

    /**
     * Configures the Kafka consumer factory for {@link UserProfileCreationFailedEvent}.
     *
     * @return the consumer factory
     */
    @Bean
    public ConsumerFactory<String, UserProfileCreationFailedEvent> userProfileFailedConsumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "auth-service-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);

        JsonDeserializer<UserProfileCreationFailedEvent> deserializer =
                new JsonDeserializer<>(UserProfileCreationFailedEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    /**
     * Provides a common error handler with fixed backoff policy.
     *
     * @return the error handler
     */
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        return new DefaultErrorHandler(
                new FixedBackOff(1000L, 3L)
        );
    }

    /**
     * Configures a Kafka listener container factory for {@link UserProfileCreationFailedEvent}.
     *
     * @return the listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserProfileCreationFailedEvent>
    userProfileFailedListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserProfileCreationFailedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userProfileFailedConsumerFactory());
        factory.setCommonErrorHandler(kafkaErrorHandler());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}
