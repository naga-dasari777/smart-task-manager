package com.smarttaskmanager.exception;

import com.smarttaskmanager.controller.TaskController;
import com.smarttaskmanager.model.TaskDTO;
import com.smarttaskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void handleResourceNotFoundException_returns404() throws Exception {
        when(taskService.getTaskById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 999"));

        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Task not found with id: 999")))
                .andExpect(jsonPath("$.path", containsString("/tasks/999")));
    }

    @Test
    void handleValidationException_returns400() throws Exception {
        String invalidJson = "{\"title\":\"\"}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    void handleIllegalArgumentException_returns400() throws Exception {
        when(taskService.getTasksByStatus("INVALID"))
                .thenThrow(new IllegalArgumentException("Invalid status: 'INVALID'. Accepted values: PENDING, COMPLETED"));

        mockMvc.perform(get("/tasks/filter/status").param("status", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("INVALID")))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void handleGenericException_returns500() throws Exception {
        when(taskService.getTaskById(anyLong()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.success", is(false)));
    }
}
