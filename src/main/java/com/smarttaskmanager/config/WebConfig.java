package com.smarttaskmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration for serving static resources and configuring MVC settings
 * Ensures static resources (HTML, CSS, JS) are properly served from the classpath
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static resources from classpath:/static/
        // This ensures index.html, CSS, JavaScript files are accessible at root path
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600); // Cache for 1 hour

        // Serve favicon and other common static files
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/");
    }
}
