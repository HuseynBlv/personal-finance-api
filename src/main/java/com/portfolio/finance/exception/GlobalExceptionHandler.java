package com.portfolio.finance.exception;

import jakarta.validation.ValidationException;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource not found", exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException exception) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed", exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", message);
    }

    @ResponseBody
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException exception) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Resource already exists", exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({BadRequestException.class, InsufficientBalanceException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(ApiException exception) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad request", exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception exception) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(error)
                .message(message)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
