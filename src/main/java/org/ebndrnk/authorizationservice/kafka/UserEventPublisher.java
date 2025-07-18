package org.ebndrnk.authorizationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.authorizationservice.kafka.dto.UserCreatedEvent;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for publishing user-related events to Kafka.
 * This class handles the publishing of user creation events to a designated Kafka topic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    /**
     * The Kafka topic to which user creation events will be published.
     */
    private static final String TOPIC = "user.created";

    /**
     * Publishes a user creation event to the Kafka topic.
     *
     * @param event The UserCreatedEvent containing details about the created user
     * @throws KafkaException if there is an error while sending the event to Kafka
     */
    public void publishUserCreated(UserCreatedEvent event) {
        log.info("Publishing UserCreatedEvent to topic {}: {}", TOPIC, event);
        try {
            kafkaTemplate.send(TOPIC, event.email(), event);
        } catch (Exception e) {
            log.error("Failed to send event for email: {}", event.email());
            throw new KafkaException("Failed to send event for email: " + event.email(), e);
        }
    }
}