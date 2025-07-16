package org.ebndrnk.authorizationservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representing a response containing
 * access and refresh JWT tokens.
 */
@Schema(description = "Response object containing new access and refresh tokens.")
public record JwtResponse(

        @Schema(
                description = "Access token issued for the user.",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String accessToken,

        @Schema(
                description = "Refresh token used to obtain new access tokens.",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String refreshToken

) {}
