package org.ebndrnk.authorizationservice.exception.dto.token;

import org.ebndrnk.authorizationservice.exception.dto.AuthServiceException;

public class TokenExpiredException extends AuthServiceException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
