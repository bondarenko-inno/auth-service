package org.ebndrnk.authorizationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.authorizationservice.exception.user.UserNotFoundException;
import org.ebndrnk.authorizationservice.kafka.dto.UserProfileCreationFailedEvent;
import org.ebndrnk.authorizationservice.service.UserCredentialService;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Kafka listener service for handling user profile creation failure events.
 * This service listens to failure events and performs rollback operations
 * by deleting the corresponding user credentials when profile creation fails.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileCreationFailedListener {

    private final UserCredentialService userCredentialService;

    /**
     * Handles user profile creation failure events by performing rollback operations.
     * <p>
     * This method listens to the "user.profile.creation.failed" topic and:
     * 1. Checks if the user exists in the credential database
     * 2. If exists, deletes the user credentials to rollback the creation
     * 3. Acknowledges the message processing
     * </p>
     *
     * @param event The failure event containing email and failure reason
     * @param acknowledgment Kafka acknowledgment mechanism for manual offset commits
     * @throws UserNotFoundException if the user specified in the event doesn't exist
     * @throws KafkaException if there's an error while processing the Kafka message
     * @throws RuntimeException for any other unexpected errors during processing
     */
    @KafkaListener(
            topics = "user.profile.creation.failed",
            groupId = "auth-service",
            containerFactory = "userProfileFailedListenerContainerFactory")
    public void handleProfileCreationFailed(UserProfileCreationFailedEvent event, Acknowledgment acknowledgment) {
        log.warn("Received UserProfileCreationFailedEvent for email {}. Reason: {}",
                event.email(), event.reason());

        try {
            if (userCredentialService.isExistsByEmail(event.email())) {
                userCredentialService.deleteByEmail(event.email());
                log.info("Rolled back user creation for email {}", event.email());
            } else {
                log.warn("User not found for rollback, email: {}", event.email());
            }
            acknowledgment.acknowledge();
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("User not found for email " + event.email());
        } catch (KafkaException e) {
            throw new KafkaException("Failed to handle event for email: " + event.email(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to handle event for email: " + event.email(), e);
        }
    }
}