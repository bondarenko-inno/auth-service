package org.ebndrnk.authorizationservice.exception.token;

import org.ebndrnk.common.common.exception.BaseServiceException;
import org.springframework.http.HttpStatus;

public class TokenSignatureException extends BaseServiceException {
    public TokenSignatureException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "TOKEN_SIGNATURE_ERROR");
    }
}