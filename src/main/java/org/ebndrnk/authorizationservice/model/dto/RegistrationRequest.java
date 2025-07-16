package org.ebndrnk.authorizationservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
        String password

) {}
