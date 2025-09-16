package org.ebndrnk.authorizationservice.exception.token;

import org.ebndrnk.common.common.exception.BaseServiceException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseServiceException {
    public TokenExpiredException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
    }
}
