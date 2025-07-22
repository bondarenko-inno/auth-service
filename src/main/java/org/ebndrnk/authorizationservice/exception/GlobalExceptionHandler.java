package org.ebndrnk.authorizationservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.authorizationservice.exception.dto.token.InvalidTokenException;
import org.ebndrnk.authorizationservice.exception.dto.token.TokenExpiredException;
import org.ebndrnk.authorizationservice.exception.dto.token.TokenGenerationException;
import org.ebndrnk.authorizationservice.exception.dto.token.TokenParsingException;
import org.ebndrnk.authorizationservice.exception.dto.token.TokenSignatureException;
import org.ebndrnk.authorizationservice.exception.dto.user.DuplicateEmailException;
import org.ebndrnk.authorizationservice.exception.dto.user.InvalidCredentialsException;
import org.ebndrnk.authorizationservice.exception.dto.user.PasswordHashingException;
import org.ebndrnk.authorizationservice.exception.dto.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;



/**
 * Global exception handler for REST controllers.
 * <p>
 * Intercepts exceptions thrown by the application and
 * returns consistent error responses with proper HTTP status codes.
 * Also logs all handled exceptions for debugging purposes.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        log.error("UserNotFoundException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorInfo> handleDuplicateEmailException(
            DuplicateEmailException ex, HttpServletRequest request) {
        log.error("DuplicateEmailException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.CONFLICT, ex, request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorInfo> handleInvalidCredentialsException(
            InvalidCredentialsException ex, HttpServletRequest request) {
        log.error("InvalidCredentialsException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(PasswordHashingException.class)
    public ResponseEntity<ErrorInfo> handlePasswordHashingException(
            PasswordHashingException ex, HttpServletRequest request) {
        log.error("PasswordHashingException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ErrorInfo> handleTokenGenerationException(
            TokenGenerationException ex, HttpServletRequest request) {
        log.error("TokenGenerationException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorInfo> handleInvalidTokenException(
            InvalidTokenException ex, HttpServletRequest request) {
        log.error("InvalidTokenException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorInfo> handleTokenExpiredException(
            TokenExpiredException ex, HttpServletRequest request) {
        log.error("TokenExpiredException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(TokenParsingException.class)
    public ResponseEntity<ErrorInfo> handleTokenParsingException(
            TokenParsingException ex, HttpServletRequest request) {
        log.error("TokenParsingException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(TokenSignatureException.class)
    public ResponseEntity<ErrorInfo> handleTokenSignatureException(
            TokenSignatureException ex, HttpServletRequest request) {
        log.error("TokenSignatureException: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleAllOtherExceptions(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request, "Internal server error");
    }

    private ResponseEntity<ErrorInfo> buildErrorResponse(
            HttpStatus status, Exception ex, HttpServletRequest request) {
        return buildErrorResponse(status, ex, request, status.getReasonPhrase());
    }

    private ResponseEntity<ErrorInfo> buildErrorResponse(
            HttpStatus status, Exception ex, HttpServletRequest request, String error) {
        ErrorInfo errorInfo = new ErrorInfo(
                LocalDateTime.now(),
                status.value(),
                error,
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorInfo, status);
    }
}
