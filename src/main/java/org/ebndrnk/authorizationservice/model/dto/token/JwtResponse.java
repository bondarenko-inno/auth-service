package org.ebndrnk.authorizationservice.model.dto.token;

public record JwtResponse(
    String accessToken,
    String refreshToken
) {}