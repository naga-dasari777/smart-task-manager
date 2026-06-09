package com.smarttaskmanager.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for building standardized API response maps.
 * Eliminates repeated HashMap construction across controller methods.
 */
public final class ApiResponse {

    private ApiResponse() {}

    public static Map<String, Object> success(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        return response;
    }

    public static Map<String, Object> success(Object data, String message) {
        Map<String, Object> response = success(data);
        response.put("message", message);
        return response;
    }

    public static Map<String, Object> successList(List<?> data) {
        Map<String, Object> response = success(data);
        response.put("total", data.size());
        return response;
    }

    public static Map<String, Object> successMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }
}
