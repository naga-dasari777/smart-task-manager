package com.smarttaskmanager.controller;

import com.smarttaskmanager.model.TaskDTO;
import com.smarttaskmanager.service.TaskService;
import com.smarttaskmanager.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Task Controller Class
 * Handles all HTTP requests related to task management
 * REST API endpoints for CRUD operations
 *
 * Request Flow:
 * HTTP Request → Controller → Service → Repository → Database
 * Database → Repository → Service → Controller → HTTP Response
 *
 * REST API Conventions Used:
 * - GET: Retrieve resources
 * - POST: Create resources
 * - PUT/PATCH: Update resources
 * - DELETE: Remove resources
 *
 * @RestController: Combines @Controller and @ResponseBody
 * @RequestMapping: Base path for all endpoints in this controller
 * @Slf4j: Provides logging capability
 */
@Slf4j
@RestController
@RequestMapping("/tasks")
@Validated
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Get all tasks
     * GET /api/tasks
     * Returns list of all tasks in system
     *
     * @return ResponseEntity containing list of TaskDTOs
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTasks() {
        log.info("Request received to fetch all tasks");
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(ApiResponse.successList(tasks));
    }

    /**
     * Get task by ID
     * GET /api/tasks/{id}
     * Returns specific task details
     *
     * @param id Task identifier
     * @return ResponseEntity containing TaskDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTaskById(@PathVariable Long id) {
        log.info("Request received to fetch task with id: {}", id);
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    /**
     * Create new task
     * POST /api/tasks
     * Accepts TaskDTO in request body
     *
     * @param taskDTO Task data to create
     * @return ResponseEntity with created task and 201 status
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        log.info("Request received to create task with title: {}", taskDTO.getTitle());
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdTask, "Task created successfully"));
    }

    /**
     * Update existing task
     * PUT /api/tasks/{id}
     * Replaces task data with provided values
     *
     * @param id Task identifier
     * @param taskDTO Updated task data
     * @return ResponseEntity with updated task
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        log.info("Request received to update task with id: {}", id);
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedTask, "Task updated successfully"));
    }

    /**
     * Delete task
     * DELETE /api/tasks/{id}
     * Removes task from database
     *
     * @param id Task identifier
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable Long id) {
        log.info("Request received to delete task with id: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Task deleted successfully"));
    }

    /**
     * Mark task as completed
     * PATCH /api/tasks/{id}/complete
     * Updates task status to COMPLETED
     *
     * @param id Task identifier
     * @return ResponseEntity with updated task
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> markTaskAsComplete(@PathVariable Long id) {
        log.info("Request received to mark task as complete with id: {}", id);
        TaskDTO completedTask = taskService.markTaskAsComplete(id);
        return ResponseEntity.ok(ApiResponse.success(completedTask, "Task marked as completed"));
    }

    /**
     * Search tasks by keyword
     * GET /api/tasks/search?keyword=value
     * Searches task title and description
     *
     * @param keyword Search term
     * @return ResponseEntity with matching tasks
     */
    @GetMapping("/search/query")
    public ResponseEntity<Map<String, Object>> searchTasks(@RequestParam String keyword) {
        log.info("Request received to search tasks with keyword: {}", keyword);
        List<TaskDTO> tasks = taskService.searchTasks(keyword);
        return ResponseEntity.ok(ApiResponse.successList(tasks));
    }

    /**
     * Filter tasks by status
     * GET /api/tasks/filter/status?status=PENDING
     * Returns tasks with specified status
     *
     * @param status Task status (PENDING or COMPLETED)
     * @return ResponseEntity with filtered tasks
     */
    @GetMapping("/filter/status")
    public ResponseEntity<Map<String, Object>> filterByStatus(@RequestParam String status) {
        log.info("Request received to filter tasks by status: {}", status);
        List<TaskDTO> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(ApiResponse.successList(tasks));
    }

    /**
     * Filter tasks by priority
     * GET /api/tasks/filter/priority?priority=HIGH
     * Returns tasks with specified priority
     *
     * @param priority Task priority (HIGH, MEDIUM, LOW)
     * @return ResponseEntity with filtered tasks
     */
    @GetMapping("/filter/priority")
    public ResponseEntity<Map<String, Object>> filterByPriority(@RequestParam String priority) {
        log.info("Request received to filter tasks by priority: {}", priority);
        List<TaskDTO> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(ApiResponse.successList(tasks));
    }

    /**
     * Get task statistics
     * GET /api/tasks/statistics
     * Returns counts of tasks by status
     *
     * @return ResponseEntity with statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("Request received to fetch task statistics");
        TaskService.Statistics statistics = taskService.getTaskStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
}
