package com.smarttaskmanager.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void constructorWithMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Task not found");

        assertEquals("Task not found", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void constructorWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("DB error");
        ResourceNotFoundException ex = new ResourceNotFoundException("Task not found", cause);

        assertEquals("Task not found", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void isRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("test");

        assertInstanceOf(RuntimeException.class, ex);
    }
}
