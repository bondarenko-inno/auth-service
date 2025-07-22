package org.ebndrnk.authorizationservice.exception.dto.user;


import org.ebndrnk.authorizationservice.exception.dto.AuthServiceException;

public class UserNotFoundException extends AuthServiceException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
