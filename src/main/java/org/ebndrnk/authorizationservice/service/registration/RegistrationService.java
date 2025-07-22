package org.ebndrnk.authorizationservice.service.registration;

import org.ebndrnk.authorizationservice.model.dto.RegistrationRequest;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;

/**
 * Service interface for user registration operations.
 */
public interface RegistrationService {

    /**
     * Registers a new user account.
     *
     * @param registrationRequest the registration request containing user details (email, password, etc.)
     * @return a {@link JwtResponse} containing access and refresh tokens after successful registration
     */
    JwtResponse register(RegistrationRequest registrationRequest);
}
