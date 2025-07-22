package org.ebndrnk.authorizationservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a request containing a JWT token
 * for validation or refresh operations.
 */
@Schema(description = "Request object containing a JWT token for validation or refresh.")
public record JwtRequest(

        @NotNull
        @Schema(
                description = "JWT token string.",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String token

) {}
