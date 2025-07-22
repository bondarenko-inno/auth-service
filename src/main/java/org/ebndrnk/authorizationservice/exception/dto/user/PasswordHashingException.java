package org.ebndrnk.authorizationservice.exception.dto.user;

import org.ebndrnk.authorizationservice.exception.dto.AuthServiceException;

public class PasswordHashingException extends AuthServiceException {
    public PasswordHashingException(String message) {
        super(message);
    }
}
