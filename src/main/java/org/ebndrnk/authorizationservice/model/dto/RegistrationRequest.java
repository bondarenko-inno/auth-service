package org.ebndrnk.authorizationservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO representing a registration request
 * containing user's credentials for creating a new account.
 */
@Schema(description = "Request object containing user's registration credentials.")
public record RegistrationRequest(

        @NotNull
        @Email
        @Schema(
                description = "Email address to register.",
                example = "newuser@example.com"
        )
        String email,

        @NotNull
        @Size(min = 6)
        @Schema(
                description = "Password for the new user account (minimum 6 characters).",
                example = "newUserPass123"
        )
        String password,

        @NotNull
        @Schema(description = "User's date of birth. \n Format ISO 8601: YYYY-MM-DD hh:mm:ss.000000", example = "1990-05-15T00:00:00")
        LocalDateTime birthDate,

        @NotNull
        @Size(min = 2, max = 50)
        @Schema(description = "User's first name.", example = "John")
        String name,

        @NotNull
        @Size(min = 2, max = 50)
        @Schema(description = "User's surname.", example = "Doe")
        String surname

) {}
