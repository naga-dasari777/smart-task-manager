package com.smarttaskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smarttaskmanager.exception.ResourceNotFoundException;
import com.smarttaskmanager.model.TaskDTO;
import com.smarttaskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private ObjectMapper objectMapper;
    private TaskDTO sampleTask;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleTask = TaskDTO.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .priority("HIGH")
                .status("PENDING")
                .dueDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // --- GET /tasks ---

    @Test
    void getAllTasks_returnsOkWithTasks() throws Exception {
        List<TaskDTO> tasks = Arrays.asList(sampleTask);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title", is("Test Task")));
    }

    @Test
    void getAllTasks_emptyList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.total", is(0)))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    // --- GET /tasks/{id} ---

    @Test
    void getTaskById_found() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(sampleTask);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Test Task")))
                .andExpect(jsonPath("$.data.priority", is("HIGH")));
    }

    @Test
    void getTaskById_notFound() throws Exception {
        when(taskService.getTaskById(99L))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 99"));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    // --- POST /tasks ---

    @Test
    void createTask_valid() throws Exception {
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(sampleTask);

        TaskDTO newTask = TaskDTO.builder()
                .title("Test Task")
                .description("Test Description")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Task created successfully")))
                .andExpect(jsonPath("$.data.title", is("Test Task")));
    }

    @Test
    void createTask_blankTitle_returnsBadRequest() throws Exception {
        TaskDTO invalid = TaskDTO.builder().title("").build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // --- PUT /tasks/{id} ---

    @Test
    void updateTask_valid() throws Exception {
        TaskDTO updated = TaskDTO.builder()
                .id(1L)
                .title("Updated Task")
                .description("Updated Desc")
                .priority("LOW")
                .status("COMPLETED")
                .build();

        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Task updated successfully")))
                .andExpect(jsonPath("$.data.title", is("Updated Task")));
    }

    @Test
    void updateTask_notFound() throws Exception {
        when(taskService.updateTask(eq(99L), any(TaskDTO.class)))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 99"));

        TaskDTO dto = TaskDTO.builder().title("Update").build();

        mockMvc.perform(put("/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // --- DELETE /tasks/{id} ---

    @Test
    void deleteTask_success() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Task deleted successfully")));
    }

    @Test
    void deleteTask_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("Task not found with id: 99"))
                .when(taskService).deleteTask(99L);

        mockMvc.perform(delete("/tasks/99"))
                .andExpect(status().isNotFound());
    }

    // --- PATCH /tasks/{id}/complete ---

    @Test
    void markTaskAsComplete_success() throws Exception {
        TaskDTO completedTask = TaskDTO.builder()
                .id(1L)
                .title("Test Task")
                .status("COMPLETED")
                .priority("HIGH")
                .build();

        when(taskService.markTaskAsComplete(1L)).thenReturn(completedTask);

        mockMvc.perform(patch("/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Task marked as completed")))
                .andExpect(jsonPath("$.data.status", is("COMPLETED")));
    }

    @Test
    void markTaskAsComplete_notFound() throws Exception {
        when(taskService.markTaskAsComplete(99L))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 99"));

        mockMvc.perform(patch("/tasks/99/complete"))
                .andExpect(status().isNotFound());
    }

    // --- GET /tasks/search/query ---

    @Test
    void searchTasks_found() throws Exception {
        when(taskService.searchTasks("Test")).thenReturn(Arrays.asList(sampleTask));

        mockMvc.perform(get("/tasks/search/query").param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.total", is(1)));
    }

    @Test
    void searchTasks_noResults() throws Exception {
        when(taskService.searchTasks("xyz")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tasks/search/query").param("keyword", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(0)));
    }

    // --- GET /tasks/filter/status ---

    @Test
    void filterByStatus_returnsFiltered() throws Exception {
        when(taskService.getTasksByStatus("PENDING")).thenReturn(Arrays.asList(sampleTask));

        mockMvc.perform(get("/tasks/filter/status").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.total", is(1)));
    }

    // --- GET /tasks/filter/priority ---

    @Test
    void filterByPriority_returnsFiltered() throws Exception {
        when(taskService.getTasksByPriority("HIGH")).thenReturn(Arrays.asList(sampleTask));

        mockMvc.perform(get("/tasks/filter/priority").param("priority", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.total", is(1)));
    }

    // --- GET /tasks/statistics ---

    @Test
    void getStatistics_returnsData() throws Exception {
        TaskService.Statistics stats = new TaskService.Statistics(10, 6, 4);
        when(taskService.getTaskStatistics()).thenReturn(stats);

        mockMvc.perform(get("/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(10)))
                .andExpect(jsonPath("$.data.completed", is(6)))
                .andExpect(jsonPath("$.data.pending", is(4)));
    }
}
