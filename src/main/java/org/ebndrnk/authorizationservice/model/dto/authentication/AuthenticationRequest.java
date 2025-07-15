package org.ebndrnk.authorizationservice.model.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
    @NotNull @Email String email,
    @NotNull String password
) {}