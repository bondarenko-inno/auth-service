package org.ebndrnk.authorizationservice.model.dto.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotNull @Email String email,
        @NotNull @Size(min = 6) String password
) {}