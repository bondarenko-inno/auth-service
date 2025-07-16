package org.ebndrnk.authorizationservice.exception.dto.token;

import org.ebndrnk.authorizationservice.exception.dto.AuthServiceException;

public class TokenGenerationException extends AuthServiceException {
    public TokenGenerationException(String message) {
        super(message);
    }
}
