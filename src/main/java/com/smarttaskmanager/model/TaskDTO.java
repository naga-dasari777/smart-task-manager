package com.smarttaskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Task Data Transfer Object (DTO)
 * Used for API request/response payloads
 * Separates database entity from API contract
 * This approach allows flexible API design independent of database structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {

    /**
     * Task identifier - returned in responses only
     */
    private Long id;

    /**
     * Task title - Required for creation/update
     */
    @NotBlank(message = "Task title cannot be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    /**
     * Task description - Optional
     */
    @Size(max = 10000, message = "Description must not exceed 10000 characters")
    private String description;

    /**
     * Priority level: HIGH, MEDIUM, LOW
     */
    private String priority;

    /**
     * Due date for the task
     */
    private LocalDateTime dueDate;

    /**
     * Current status: PENDING or COMPLETED
     */
    private String status;

    /**
     * Task creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Task last update timestamp
     */
    private LocalDateTime updatedAt;
}
