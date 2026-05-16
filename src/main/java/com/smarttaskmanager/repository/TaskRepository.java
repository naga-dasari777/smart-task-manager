package com.smarttaskmanager.repository;

import com.smarttaskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Task Repository Interface
 * Extends JpaRepository for CRUD operations
 * Spring Data JPA automatically generates implementation at runtime
 * Provides custom query methods for task searching and filtering
 *
 * Key Benefits:
 * - No need to write SQL queries for basic CRUD
 * - Type-safe queries
 * - Automatic transaction management
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks by their status (PENDING or COMPLETED)
     * Uses method name convention for query generation
     *
     * @param status The task status to filter by
     * @return List of tasks with the specified status
     */
    List<Task> findByStatus(Task.TaskStatus status);

    /**
     * Find all tasks by priority level
     * Useful for priority-based filtering
     *
     * @param priority The priority level to filter by
     * @return List of tasks with the specified priority
     */
    List<Task> findByPriority(Task.Priority priority);

    /**
     * Search tasks by title or description using LIKE queries
     * Custom @Query annotation with JPQL
     *
     * @param keyword The search keyword
     * @return List of tasks containing keyword in title or description
     */
    @Query("SELECT t FROM Task t WHERE " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Find tasks by status and priority
     * Combined filtering for advanced search
     *
     * @param status The task status
     * @param priority The task priority
     * @return List of tasks matching both filters
     */
    List<Task> findByStatusAndPriority(Task.TaskStatus status, Task.Priority priority);

    /**
     * Count total number of pending tasks
     * Useful for dashboard statistics
     *
     * @return Number of pending tasks
     */
    long countByStatus(Task.TaskStatus status);
}
