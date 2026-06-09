package com.smarttaskmanager.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskModelTest {

    // --- Task entity ---

    @Test
    void task_defaultValues() {
        Task task = new Task();

        assertEquals(Task.Priority.MEDIUM, task.getPriority());
        assertEquals(Task.TaskStatus.PENDING, task.getStatus());
        assertNull(task.getId());
        assertNull(task.getTitle());
    }

    @Test
    void task_settersAndGetters() {
        Task task = new Task();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime due = LocalDateTime.of(2026, 12, 31, 23, 59);

        task.setId(1L);
        task.setTitle("My Task");
        task.setDescription("Description");
        task.setPriority(Task.Priority.HIGH);
        task.setDueDate(due);
        task.setStatus(Task.TaskStatus.COMPLETED);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        assertEquals(1L, task.getId());
        assertEquals("My Task", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(Task.Priority.HIGH, task.getPriority());
        assertEquals(due, task.getDueDate());
        assertEquals(Task.TaskStatus.COMPLETED, task.getStatus());
        assertEquals(now, task.getCreatedAt());
        assertEquals(now, task.getUpdatedAt());
    }

    @Test
    void task_allArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(1L, "Title", "Desc", Task.Priority.LOW,
                now, Task.TaskStatus.PENDING, now, now);

        assertEquals(1L, task.getId());
        assertEquals("Title", task.getTitle());
        assertEquals(Task.Priority.LOW, task.getPriority());
    }

    @Test
    void task_equalsAndHashCode() {
        Task t1 = new Task();
        t1.setId(1L);
        t1.setTitle("A");
        t1.setPriority(Task.Priority.HIGH);
        t1.setStatus(Task.TaskStatus.PENDING);

        Task t2 = new Task();
        t2.setId(1L);
        t2.setTitle("A");
        t2.setPriority(Task.Priority.HIGH);
        t2.setStatus(Task.TaskStatus.PENDING);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void task_toString_containsFields() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test");

        String str = task.toString();
        assertTrue(str.contains("Test"));
        assertTrue(str.contains("1"));
    }

    // --- Priority enum ---

    @Test
    void priority_values() {
        Task.Priority[] values = Task.Priority.values();
        assertEquals(3, values.length);
        assertEquals(Task.Priority.LOW, Task.Priority.valueOf("LOW"));
        assertEquals(Task.Priority.MEDIUM, Task.Priority.valueOf("MEDIUM"));
        assertEquals(Task.Priority.HIGH, Task.Priority.valueOf("HIGH"));
    }

    @Test
    void priority_displayName() {
        assertEquals("Low", Task.Priority.LOW.getDisplayName());
        assertEquals("Medium", Task.Priority.MEDIUM.getDisplayName());
        assertEquals("High", Task.Priority.HIGH.getDisplayName());
    }

    // --- TaskStatus enum ---

    @Test
    void taskStatus_values() {
        Task.TaskStatus[] values = Task.TaskStatus.values();
        assertEquals(2, values.length);
        assertEquals(Task.TaskStatus.PENDING, Task.TaskStatus.valueOf("PENDING"));
        assertEquals(Task.TaskStatus.COMPLETED, Task.TaskStatus.valueOf("COMPLETED"));
    }

    @Test
    void taskStatus_displayName() {
        assertEquals("Pending", Task.TaskStatus.PENDING.getDisplayName());
        assertEquals("Completed", Task.TaskStatus.COMPLETED.getDisplayName());
    }

    // --- TaskDTO ---

    @Test
    void taskDTO_builder() {
        LocalDateTime now = LocalDateTime.now();
        TaskDTO dto = TaskDTO.builder()
                .id(1L)
                .title("DTO Title")
                .description("DTO Desc")
                .priority("HIGH")
                .status("PENDING")
                .dueDate(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("DTO Title", dto.getTitle());
        assertEquals("DTO Desc", dto.getDescription());
        assertEquals("HIGH", dto.getPriority());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    void taskDTO_noArgsConstructor() {
        TaskDTO dto = new TaskDTO();
        assertNull(dto.getId());
        assertNull(dto.getTitle());
    }

    @Test
    void taskDTO_allArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        TaskDTO dto = new TaskDTO(1L, "Title", "Desc", "LOW", now, "COMPLETED", now, now);

        assertEquals(1L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("LOW", dto.getPriority());
        assertEquals("COMPLETED", dto.getStatus());
    }

    @Test
    void taskDTO_equalsAndHashCode() {
        TaskDTO d1 = TaskDTO.builder().id(1L).title("A").build();
        TaskDTO d2 = TaskDTO.builder().id(1L).title("A").build();

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    // --- Statistics inner class ---

    @Test
    void statistics_getters() {
        com.smarttaskmanager.service.TaskService.Statistics stats =
                new com.smarttaskmanager.service.TaskService.Statistics(10, 6, 4);

        assertEquals(10, stats.getTotal());
        assertEquals(6, stats.getCompleted());
        assertEquals(4, stats.getPending());
        assertEquals(60.0, stats.getCompletionPercentage(), 0.01);
    }

    @Test
    void statistics_zeroDivision() {
        com.smarttaskmanager.service.TaskService.Statistics stats =
                new com.smarttaskmanager.service.TaskService.Statistics(0, 0, 0);

        assertEquals(0.0, stats.getCompletionPercentage(), 0.01);
    }
}
