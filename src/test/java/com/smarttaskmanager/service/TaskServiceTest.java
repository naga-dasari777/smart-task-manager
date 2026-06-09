package com.smarttaskmanager.service;

import com.smarttaskmanager.exception.ResourceNotFoundException;
import com.smarttaskmanager.model.Task;
import com.smarttaskmanager.model.TaskDTO;
import com.smarttaskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private TaskDTO sampleTaskDTO;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Test Task");
        sampleTask.setDescription("Test Description");
        sampleTask.setPriority(Task.Priority.HIGH);
        sampleTask.setStatus(Task.TaskStatus.PENDING);
        sampleTask.setDueDate(LocalDateTime.of(2026, 12, 31, 23, 59));
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());

        sampleTaskDTO = TaskDTO.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .priority("HIGH")
                .status("PENDING")
                .dueDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                .build();
    }

    // --- getAllTasks ---

    @Test
    void getAllTasks_returnsList() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(sampleTask));

        List<TaskDTO> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        verify(taskRepository).findAll();
    }

    @Test
    void getAllTasks_emptyList() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        List<TaskDTO> result = taskService.getAllTasks();

        assertTrue(result.isEmpty());
    }

    // --- getTaskById ---

    @Test
    void getTaskById_found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskDTO result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals("HIGH", result.getPriority());
    }

    @Test
    void getTaskById_notFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(99L));
    }

    // --- createTask ---

    @Test
    void createTask_withPriority() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskDTO result = taskService.createTask(sampleTaskDTO);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_withoutPriority_defaultsMedium() {
        TaskDTO dto = TaskDTO.builder().title("No Priority Task").build();
        Task savedTask = new Task();
        savedTask.setId(2L);
        savedTask.setTitle("No Priority Task");
        savedTask.setPriority(Task.Priority.MEDIUM);
        savedTask.setStatus(Task.TaskStatus.PENDING);
        savedTask.setCreatedAt(LocalDateTime.now());
        savedTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.createTask(dto);

        assertEquals("MEDIUM", result.getPriority());
    }

    @Test
    void createTask_statusAlwaysPending() {
        TaskDTO dto = TaskDTO.builder().title("New").priority("LOW").build();
        Task savedTask = new Task();
        savedTask.setId(3L);
        savedTask.setTitle("New");
        savedTask.setPriority(Task.Priority.LOW);
        savedTask.setStatus(Task.TaskStatus.PENDING);
        savedTask.setCreatedAt(LocalDateTime.now());
        savedTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.createTask(dto);

        assertEquals("PENDING", result.getStatus());
    }

    // --- updateTask ---

    @Test
    void updateTask_allFields() {
        TaskDTO updateDTO = TaskDTO.builder()
                .title("Updated Title")
                .description("Updated Desc")
                .priority("LOW")
                .dueDate(LocalDateTime.of(2027, 1, 1, 0, 0))
                .status("COMPLETED")
                .build();

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Desc");
        updatedTask.setPriority(Task.Priority.LOW);
        updatedTask.setStatus(Task.TaskStatus.COMPLETED);
        updatedTask.setDueDate(LocalDateTime.of(2027, 1, 1, 0, 0));
        updatedTask.setCreatedAt(LocalDateTime.now());
        updatedTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskDTO result = taskService.updateTask(1L, updateDTO);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("LOW", result.getPriority());
        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void updateTask_partialUpdate_titleOnly() {
        TaskDTO updateDTO = TaskDTO.builder().title("New Title").build();

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("Test Description");
        updatedTask.setPriority(Task.Priority.HIGH);
        updatedTask.setStatus(Task.TaskStatus.PENDING);
        updatedTask.setCreatedAt(LocalDateTime.now());
        updatedTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskDTO result = taskService.updateTask(1L, updateDTO);

        assertEquals("New Title", result.getTitle());
        assertEquals("HIGH", result.getPriority());
    }

    @Test
    void updateTask_blankTitle_notUpdated() {
        TaskDTO updateDTO = TaskDTO.builder().title("   ").build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskDTO result = taskService.updateTask(1L, updateDTO);

        assertEquals("Test Task", result.getTitle());
    }

    @Test
    void updateTask_notFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(99L, sampleTaskDTO));
    }

    // --- deleteTask ---

    @Test
    void deleteTask_exists() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        assertDoesNotThrow(() -> taskService.deleteTask(1L));
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_notFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(99L));
        verify(taskRepository, never()).deleteById(anyLong());
    }

    // --- markTaskAsComplete ---

    @Test
    void markTaskAsComplete_success() {
        Task completedTask = new Task();
        completedTask.setId(1L);
        completedTask.setTitle("Test Task");
        completedTask.setPriority(Task.Priority.HIGH);
        completedTask.setStatus(Task.TaskStatus.COMPLETED);
        completedTask.setCreatedAt(LocalDateTime.now());
        completedTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(completedTask);

        TaskDTO result = taskService.markTaskAsComplete(1L);

        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void markTaskAsComplete_notFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.markTaskAsComplete(99L));
    }

    // --- searchTasks ---

    @Test
    void searchTasks_found() {
        when(taskRepository.searchByKeyword("Test")).thenReturn(Arrays.asList(sampleTask));

        List<TaskDTO> result = taskService.searchTasks("Test");

        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
    }

    @Test
    void searchTasks_noResults() {
        when(taskRepository.searchByKeyword("xyz")).thenReturn(Collections.emptyList());

        List<TaskDTO> result = taskService.searchTasks("xyz");

        assertTrue(result.isEmpty());
    }

    // --- getTasksByStatus ---

    @Test
    void getTasksByStatus_pending() {
        when(taskRepository.findByStatus(Task.TaskStatus.PENDING))
                .thenReturn(Arrays.asList(sampleTask));

        List<TaskDTO> result = taskService.getTasksByStatus("PENDING");

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    @Test
    void getTasksByStatus_caseInsensitive() {
        when(taskRepository.findByStatus(Task.TaskStatus.COMPLETED))
                .thenReturn(Collections.emptyList());

        List<TaskDTO> result = taskService.getTasksByStatus("completed");

        assertTrue(result.isEmpty());
    }

    // --- getTasksByPriority ---

    @Test
    void getTasksByPriority_high() {
        when(taskRepository.findByPriority(Task.Priority.HIGH))
                .thenReturn(Arrays.asList(sampleTask));

        List<TaskDTO> result = taskService.getTasksByPriority("HIGH");

        assertEquals(1, result.size());
    }

    @Test
    void getTasksByPriority_caseInsensitive() {
        when(taskRepository.findByPriority(Task.Priority.LOW))
                .thenReturn(Collections.emptyList());

        List<TaskDTO> result = taskService.getTasksByPriority("low");

        assertTrue(result.isEmpty());
    }

    // --- getTaskStatistics ---

    @Test
    void getTaskStatistics_withTasks() {
        when(taskRepository.count()).thenReturn(10L);
        when(taskRepository.countByStatus(Task.TaskStatus.COMPLETED)).thenReturn(6L);
        when(taskRepository.countByStatus(Task.TaskStatus.PENDING)).thenReturn(4L);

        Object result = taskService.getTaskStatistics();

        assertNotNull(result);
        assertInstanceOf(TaskService.Statistics.class, result);
        TaskService.Statistics stats = (TaskService.Statistics) result;
        assertEquals(10L, stats.getTotal());
        assertEquals(6L, stats.getCompleted());
        assertEquals(4L, stats.getPending());
        assertEquals(60.0, stats.getCompletionPercentage(), 0.01);
    }

    @Test
    void getTaskStatistics_noTasks() {
        when(taskRepository.count()).thenReturn(0L);
        when(taskRepository.countByStatus(Task.TaskStatus.COMPLETED)).thenReturn(0L);
        when(taskRepository.countByStatus(Task.TaskStatus.PENDING)).thenReturn(0L);

        TaskService.Statistics stats = (TaskService.Statistics) taskService.getTaskStatistics();

        assertEquals(0L, stats.getTotal());
        assertEquals(0.0, stats.getCompletionPercentage(), 0.01);
    }
}
