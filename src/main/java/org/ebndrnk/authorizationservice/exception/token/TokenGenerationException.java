package org.ebndrnk.authorizationservice.exception.token;


import org.ebndrnk.common.common.exception.BaseServiceException;
import org.springframework.http.HttpStatus;

public class TokenGenerationException extends BaseServiceException {
    public TokenGenerationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_GENERATION_ERROR");
    }
}