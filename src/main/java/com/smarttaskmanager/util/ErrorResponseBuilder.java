package com.smarttaskmanager.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for building standardized error response maps.
 * Eliminates repeated error HashMap construction in GlobalExceptionHandler.
 */
public final class ErrorResponseBuilder {

    private ErrorResponseBuilder() {}

    public static Map<String, Object> build(HttpStatus status, String error, String message, WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
        return errorResponse;
    }
}
