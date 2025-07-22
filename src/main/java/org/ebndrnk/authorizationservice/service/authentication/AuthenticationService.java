package org.ebndrnk.authorizationservice.service.authentication;

import org.ebndrnk.authorizationservice.model.dto.AuthenticationRequest;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;

/**
 * Service interface for user authentication operations.
 */
public interface AuthenticationService {

    /**
     * Authenticates a user based on provided credentials.
     *
     * @param authenticationRequest the authentication request containing user credentials
     * @return a {@link JwtResponse} containing access and refresh tokens upon successful authentication
     */
    JwtResponse authenticate(AuthenticationRequest authenticationRequest);
}
