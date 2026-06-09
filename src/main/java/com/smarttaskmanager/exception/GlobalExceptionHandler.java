package com.smarttaskmanager.exception;

import com.smarttaskmanager.util.ErrorResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler
 * Centralized exception handling across all controllers
 * Uses @ControllerAdvice for application-wide exception management
 * Follows REST API best practices for error responses
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException
     * Triggered when a task is not found in database
     * Returns 404 Not Found status
     *
     * @param ex ResourceNotFoundException
     * @param request WebRequest
     * @return Error response with 404 status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        log.warn("Resource not found: {}", ex.getMessage());
        Map<String, Object> errorResponse = ErrorResponseBuilder.build(
                HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle validation errors from @Valid annotations
     * Triggered when request body validation fails
     * Returns 400 Bad Request status with field errors
     *
     * @param ex MethodArgumentNotValidException
     * @param request WebRequest
     * @return Error response with validation details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        log.warn("Validation failed");
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        Map<String, Object> errorResponse = ErrorResponseBuilder.build(
                HttpStatus.BAD_REQUEST, "Validation Failed", errors, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle IllegalArgumentException
     * Triggered when invalid enum values or bad arguments are provided
     * Returns 400 Bad Request status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        log.warn("Invalid argument: {}", ex.getMessage());
        Map<String, Object> errorResponse = ErrorResponseBuilder.build(
                HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle generic exceptions
     * Fallback handler for unexpected errors
     * Returns 500 Internal Server Error status
     *
     * @param ex Exception
     * @param request WebRequest
     * @return Error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        log.error("Unexpected error occurred", ex);
        Map<String, Object> errorResponse = ErrorResponseBuilder.build(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred. Please try again later.", request);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
