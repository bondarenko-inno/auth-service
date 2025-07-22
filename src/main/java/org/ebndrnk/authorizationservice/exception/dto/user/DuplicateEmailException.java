package org.ebndrnk.authorizationservice.exception.dto.user;

import org.ebndrnk.authorizationservice.exception.dto.AuthServiceException;

public class DuplicateEmailException extends AuthServiceException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}

