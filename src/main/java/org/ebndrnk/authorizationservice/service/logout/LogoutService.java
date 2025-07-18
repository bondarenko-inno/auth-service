package org.ebndrnk.authorizationservice.service.logout;

import org.ebndrnk.authorizationservice.model.dto.JwtRequest;

/**
 * Service interface for user logout operations.
 */
public interface LogoutService {

    /**
     * Logs out the user by invalidating their tokens based on the HTTP request.
     *
     * @param request the HTTP servlet request containing user authentication details (e.g., tokens)
     */
    void logout(JwtRequest request);
}
