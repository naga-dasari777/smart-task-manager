package com.smarttaskmanager.exception;

/**
 * Custom exception for resource not found scenarios
 * Thrown when attempting to access a task that doesn't exist
 * Extends RuntimeException for unchecked exception handling
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor with message
     * @param message Exception message describing what was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * @param message Exception message
     * @param cause Throwable cause of this exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
