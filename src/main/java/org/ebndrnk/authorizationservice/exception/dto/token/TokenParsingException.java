package org.ebndrnk.authorizationservice.exception.dto.token;

import org.ebndrnk.authorizationservice.exception.dto.AuthServiceException;

public class TokenParsingException extends AuthServiceException {
    public TokenParsingException(String message) {
        super(message);
    }
}
