package org.ebndrnk.authorizationservice.exception.token;

import org.ebndrnk.common.common.exception.BaseServiceException;
import org.springframework.http.HttpStatus;

public class TokenParsingException extends BaseServiceException {
    public TokenParsingException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "TOKEN_PARSING_ERROR");
    }
}
