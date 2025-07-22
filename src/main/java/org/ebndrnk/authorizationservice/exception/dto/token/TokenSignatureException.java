package org.ebndrnk.authorizationservice.exception.dto.token;

import org.ebndrnk.authorizationservice.exception.dto.AuthServiceException;

public class TokenSignatureException extends AuthServiceException {
    public TokenSignatureException(String message) {
        super(message);
    }
}
