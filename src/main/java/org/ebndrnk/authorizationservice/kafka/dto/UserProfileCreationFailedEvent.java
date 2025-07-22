package org.ebndrnk.authorizationservice.kafka.dto;

public record UserProfileCreationFailedEvent(
         String email,
         String reason
) {}
