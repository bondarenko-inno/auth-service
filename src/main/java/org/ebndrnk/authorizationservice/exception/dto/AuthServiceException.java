package org.ebndrnk.authorizationservice.exception.dto;

/**
 * AuthServiceException
 * <p>
 * Base class for all custom exceptions in the Auth Service module.
 * <p>
 * This runtime exception is intended to be extended by specific
 * custom exceptions to represent various error scenarios in the service.
 * It allows you to encapsulate domain-specific error information
 * and propagate meaningful error messages to higher layers
 * or to the API response.
 * <p>
 * Example usage:
 * <pre>
 * throw new InvalidTokenException("Token has expired");
 * </pre>
 */
public class AuthServiceException extends RuntimeException {
    public AuthServiceException(String message) {
        super(message);
    }
}
