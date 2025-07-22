package org.ebndrnk.authorizationservice.exception.dto.token;

import org.ebndrnk.authorizationservice.exception.dto.AuthServiceException;

public class InvalidTokenException extends AuthServiceException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
