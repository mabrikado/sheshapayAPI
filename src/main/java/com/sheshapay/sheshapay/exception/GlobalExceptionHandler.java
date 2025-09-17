package com.sheshapay.sheshapay.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the SheshaPay application.
 *
 * This class centralizes exception handling across all controllers by using
 * RestControllerAdvice. It ensures that API responses are consistent
 * and meaningful, instead of exposing raw stack traces or framework messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles malformed JSON input or unreadable HTTP requests.
     *
     * Example: Sending an invalid JSON body to a controller endpoint will
     * trigger this handler.
     *
     * @param ex the exception thrown when the request body cannot be read or parsed
     * @return a 400 Bad Request response with error details
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Bad request");
        error.put("message", "Invalid or malformed JSON in request body");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles validation errors when request payloads fail validation rules.
     *
     * Example: When using @Valid on a DTO and a field does not
     * satisfy the validation constraint, this handler is invoked.
     *
     * @param ex the exception containing validation error details
     * @return a 400 Bad Request response with validation error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Validation failed");
        error.put("message", ex.getBindingResult().getFieldError().getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles any unexpected exceptions that are not explicitly mapped.
     *
     * Example: NullPointerException, IllegalArgumentException,
     * or other runtime exceptions.
     *
     * @param ex the exception thrown at runtime
     * @return a 500 Internal Server Error response with the exception message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
