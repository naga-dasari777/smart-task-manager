package com.smarttaskmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security configuration for the application.
 * Configures CORS to allow frontend requests to backend API endpoints.
 * Default allowed origin is localhost:8080 (can be overridden via ALLOWED_ORIGINS env var)
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Value("${ALLOWED_ORIGINS:http://localhost:8080}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configure CORS for the entire /api/** path
        // This includes all API endpoints: /api/tasks, /api/tasks/**, etc.
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
