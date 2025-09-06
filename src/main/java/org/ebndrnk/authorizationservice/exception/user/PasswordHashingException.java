package org.ebndrnk.authorizationservice.exception.user;

import org.ebndrnk.common.common.exception.BaseServiceException;
import org.springframework.http.HttpStatus;

public class PasswordHashingException extends BaseServiceException {
    public PasswordHashingException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "PASSWORD_HASHING_ERROR");
    }
}
