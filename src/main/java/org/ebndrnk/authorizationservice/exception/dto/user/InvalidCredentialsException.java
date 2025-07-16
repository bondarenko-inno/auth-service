package org.ebndrnk.authorizationservice.exception.dto.user;

import org.ebndrnk.authorizationservice.exception.dto.AuthServiceException;

public class InvalidCredentialsException extends AuthServiceException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
