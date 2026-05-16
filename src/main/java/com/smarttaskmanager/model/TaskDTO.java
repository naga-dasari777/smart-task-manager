package com.smarttaskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
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
    private String title;

    /**
     * Task description - Optional
     */
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
