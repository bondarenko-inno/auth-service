package org.ebndrnk.authorizationservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing an authentication request containing
 * user credentials for login.
 */
@Schema(description = "Request object containing user's login credentials.")
public record AuthenticationRequest(

        @NotNull
        @Email
        @Schema(
                description = "User's email address.",
                example = "user@example.com"
        )
        String email,

        @NotNull
        @Schema(
                description = "User's password in plain text.",
                example = "strongPassword123"
        )
        String password

) {}
