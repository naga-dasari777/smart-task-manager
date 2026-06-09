package com.smarttaskmanager.service;

import com.smarttaskmanager.exception.ResourceNotFoundException;
import com.smarttaskmanager.model.Task;
import com.smarttaskmanager.model.TaskDTO;
import com.smarttaskmanager.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Task Service Class
 * Contains all business logic for task management
 * Acts as intermediary between Controller and Repository layers
 * Handles data transformation between entities and DTOs
 *
 * Responsibilities:
 * - Task CRUD operations
 * - Business rule validation
 * - Data transformation
 * - Exception handling
 *
 * @Transactional annotation ensures atomic operations
 * @Slf4j provides logging capability
 */
@Slf4j
@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Retrieve all tasks from database
     * Logs the operation for monitoring
     *
     * @return List of all tasks as DTOs
     */
    public List<TaskDTO> getAllTasks() {
        log.info("Fetching all tasks");
        return taskRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific task by ID
     * Throws exception if task not found
     *
     * @param id Task identifier
     * @return Task DTO
     * @throws ResourceNotFoundException if task doesn't exist
     */
    public TaskDTO getTaskById(Long id) {
        log.info("Fetching task with id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found with id: " + id);
                });
        return convertToDTO(task);
    }

    /**
     * Create a new task
     * Validates input and sets default values
     *
     * @param taskDTO Task data to create
     * @return Created task DTO with generated ID
     */
    public TaskDTO createTask(TaskDTO taskDTO) {
        log.info("Creating new task with title: {}", taskDTO.getTitle());
        
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        
        // Set priority with default value if not provided
        if (taskDTO.getPriority() != null) {
            task.setPriority(parsePriority(taskDTO.getPriority()));
        } else {
            task.setPriority(Task.Priority.MEDIUM);
        }
        
        task.setDueDate(taskDTO.getDueDate());
        task.setStatus(Task.TaskStatus.PENDING);
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());
        return convertToDTO(savedTask);
    }

    /**
     * Update an existing task
     * Merges new data with existing task
     *
     * @param id Task identifier
     * @param taskDTO Updated task data
     * @return Updated task DTO
     * @throws ResourceNotFoundException if task doesn't exist
     */
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        log.info("Updating task with id: {}", id);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found with id: " + id);
                });
        
        // Update fields if provided
        if (taskDTO.getTitle() != null && !taskDTO.getTitle().isBlank()) {
            task.setTitle(taskDTO.getTitle());
        }
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getPriority() != null) {
            task.setPriority(parsePriority(taskDTO.getPriority()));
        }
        if (taskDTO.getDueDate() != null) {
            task.setDueDate(taskDTO.getDueDate());
        }
        if (taskDTO.getStatus() != null) {
            task.setStatus(parseStatus(taskDTO.getStatus()));
        }
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully with id: {}", id);
        return convertToDTO(updatedTask);
    }

    /**
     * Delete a task by ID
     * Removes task from database
     *
     * @param id Task identifier
     * @throws ResourceNotFoundException if task doesn't exist
     */
    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);
        
        if (!taskRepository.existsById(id)) {
            log.warn("Task not found with id: {}", id);
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        
        taskRepository.deleteById(id);
        log.info("Task deleted successfully with id: {}", id);
    }

    /**
     * Mark a task as completed
     * Updates task status to COMPLETED
     *
     * @param id Task identifier
     * @return Updated task DTO
     * @throws ResourceNotFoundException if task doesn't exist
     */
    public TaskDTO markTaskAsComplete(Long id) {
        log.info("Marking task as complete with id: {}", id);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found with id: " + id);
                });
        
        task.setStatus(Task.TaskStatus.COMPLETED);
        Task updatedTask = taskRepository.save(task);
        log.info("Task marked as complete with id: {}", id);
        return convertToDTO(updatedTask);
    }

    /**
     * Search tasks by keyword in title or description
     *
     * @param keyword Search term
     * @return List of matching tasks
     */
    public List<TaskDTO> searchTasks(String keyword) {
        log.info("Searching tasks with keyword: {}", keyword);
        return taskRepository.searchByKeyword(keyword)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filter tasks by status (PENDING or COMPLETED)
     *
     * @param status Task status to filter by
     * @return List of tasks with specified status
     */
    public List<TaskDTO> getTasksByStatus(String status) {
        log.info("Filtering tasks by status: {}", status);
        Task.TaskStatus taskStatus = parseStatus(status);
        return taskRepository.findByStatus(taskStatus)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filter tasks by priority
     *
     * @param priority Task priority to filter by
     * @return List of tasks with specified priority
     */
    public List<TaskDTO> getTasksByPriority(String priority) {
        log.info("Filtering tasks by priority: {}", priority);
        Task.Priority taskPriority = parsePriority(priority);
        return taskRepository.findByPriority(taskPriority)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get task statistics for dashboard
     * Returns counts of tasks by status
     *
     * @return Map containing statistics
     */
    public Object getTaskStatistics() {
        log.info("Fetching task statistics");
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByStatus(Task.TaskStatus.COMPLETED);
        long pendingTasks = taskRepository.countByStatus(Task.TaskStatus.PENDING);
        
        return new Statistics(totalTasks, completedTasks, pendingTasks);
    }

    /**
     * Convert Task entity to TaskDTO
     * Used for API responses to hide internal entity structure
     *
     * @param task Task entity
     * @return Task DTO
     */
    private TaskDTO convertToDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority().name())
                .dueDate(task.getDueDate())
                .status(task.getStatus().name())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private Task.Priority parsePriority(String priority) {
        try {
            return Task.Priority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid priority: '" + priority + "'. Accepted values: HIGH, MEDIUM, LOW");
        }
    }

    private Task.TaskStatus parseStatus(String status) {
        try {
            return Task.TaskStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status: '" + status + "'. Accepted values: PENDING, COMPLETED");
        }
    }

    /**
     * Inner class for statistics response
     */
    public static class Statistics {
        public long total;
        public long completed;
        public long pending;

        public Statistics(long total, long completed, long pending) {
            this.total = total;
            this.completed = completed;
            this.pending = pending;
        }

        public long getTotal() { return total; }
        public long getCompleted() { return completed; }
        public long getPending() { return pending; }
        public double getCompletionPercentage() {
            return total == 0 ? 0 : (completed * 100.0) / total;
        }
    }
}
