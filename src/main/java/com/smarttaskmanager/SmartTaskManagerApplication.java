package com.smarttaskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application Entry Point
 * Starts the entire application
 *
 * @SpringBootApplication combines:
 * - @Configuration: Indicates this is a configuration class
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
 * - @ComponentScan: Scans for Spring components in current package and sub-packages
 */
@SpringBootApplication
public class SmartTaskManagerApplication {

    /**
     * Main method - Application starts here
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SmartTaskManagerApplication.class, args);
    }
}
