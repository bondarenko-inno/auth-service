package org.ebndrnk.authorizationservice.kafka.dto;

import java.time.LocalDateTime;

public record UserCreatedEvent(
        String email,
        LocalDateTime birthDate,
        String name,
        String surname
){
}
