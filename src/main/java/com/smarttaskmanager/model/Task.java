package com.smarttaskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Task Entity - Represents a task in the database
 * Uses Lombok annotations to reduce boilerplate code
 * Maps to 'tasks' table in MySQL database
 */
@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_priority", columnList = "priority"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the task
     * Auto-generated primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Task title - Required field
     * Validation: Must not be blank
     */
    @NotBlank(message = "Task title cannot be blank")
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Task description - Optional field
     * Can contain long text content
     */
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    /**
     * Task priority level
     * Enum values: HIGH, MEDIUM, LOW
     * Default: MEDIUM
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority = Priority.MEDIUM;

    /**
     * Task due date
     * Optional field for deadline tracking
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * Task status - Current state of the task
     * Enum values: PENDING, COMPLETED
     * Default: PENDING
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.PENDING;

    /**
     * Timestamp when task was created
     * Automatically set by Hibernate
     * Not updatable after creation
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when task was last updated
     * Automatically updated by Hibernate
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum for Task Priority Levels
     */
    public enum Priority {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High");

        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enum for Task Status
     */
    public enum TaskStatus {
        PENDING("Pending"),
        COMPLETED("Completed");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
