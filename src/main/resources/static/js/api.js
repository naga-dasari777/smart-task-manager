/**
 * API Service Module
 * Handles all HTTP requests to the backend
 * Provides a clean abstraction layer for API calls
 */

const API_BASE_URL = '/api/tasks';

async function parseErrorMessage(response, fallback) {
    try {
        const body = await response.json();
        return body.message || fallback;
    } catch {
        return fallback;
    }
}

/**
 * Fetch all tasks from the server
 * @returns {Promise<Array>} Array of task objects
 */
async function fetchAllTasks() {
    try {
        const response = await fetch(`${API_BASE_URL}`);
        if (!response.ok) throw new Error(await parseErrorMessage(response, 'Failed to fetch tasks'));
        const data = await response.json();
        return data.data || [];
    } catch (error) {
        console.error('Error fetching tasks:', error);
        showToast('Error loading tasks', 'danger');
        return [];
    }
}

/**
 * Fetch a single task by ID
 * @param {number} taskId - The task ID to fetch
 * @returns {Promise<Object>} Task object
 */
async function fetchTaskById(taskId) {
    try {
        const response = await fetch(`${API_BASE_URL}/${taskId}`);
        if (!response.ok) throw new Error(await parseErrorMessage(response, 'Task not found'));
        const data = await response.json();
        return data.data;
    } catch (error) {
        console.error('Error fetching task:', error);
        showToast('Error loading task', 'danger');
        return null;
    }
}

/**
 * Create a new task
 * @param {Object} taskData - Task data object with title, description, priority, dueDate
 * @returns {Promise<Object>} Created task object
 */
async function createTask(taskData) {
    try {
        const response = await fetch(`${API_BASE_URL}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData)
        });
        if (!response.ok) throw new Error(await parseErrorMessage(response, 'Failed to create task'));
        const data = await response.json();
        showToast(data.message || 'Task created successfully!', 'success');
        return data.data;
    } catch (error) {
        console.error('Error creating task:', error);
        showToast(error.message || 'Error creating task', 'danger');
        return null;
    }
}

/**
 * Update an existing task
 * @param {number} taskId - The task ID to update
 * @param {Object} taskData - Updated task data
 * @returns {Promise<Object>} Updated task object
 */
async function updateTask(taskId, taskData) {
    try {
        const response = await fetch(`${API_BASE_URL}/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData)
        });
        if (!response.ok) throw new Error(await parseErrorMessage(response, 'Failed to update task'));
        const data = await response.json();
        showToast(data.message || 'Task updated successfully!', 'success');
        return data.data;
    } catch (error) {
        console.error('Error updating task:', error);
        showToast(error.message || 'Error updating task', 'danger');
        return null;
    }
}

/**
 * Delete a task
 * @param {number} taskId - The task ID to delete
 * @returns {Promise<boolean>} Success status
 */
async function deleteTask(taskId) {
    try {
        const response = await fetch(`${API_BASE_URL}/${taskId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        if (!response.ok) throw new Error(await parseErrorMessage(response, 'Failed to delete task'));
        const data = await response.json();
        showToast(data.message || 'Task deleted successfully!', 'success');
        return true;
    } catch (error) {
        console.error('Error deleting task:', error);
        showToast(error.message || 'Error deleting task', 'danger');
        return false;
    }
}

/**
 * Mark a task as completed
 * @param {number} taskId - The task ID to complete
 * @returns {Promise<Object>} Updated task object
 */
async function markTaskComplete(taskId) {
    try {
        const response = await fetch(`${API_BASE_URL}/${taskId}/complete`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        if (!response.ok) throw new Error(await parseErrorMessage(response, 'Failed to complete task'));
        const data = await response.json();
        showToast(data.message || 'Task marked as completed!', 'success');
        return data.data;
    } catch (error) {
        console.error('Error completing task:', error);
        showToast(error.message || 'Error completing task', 'danger');
        return null;
    }
}

/**
 * Search tasks by keyword
 * @param {string} keyword - Search keyword
 * @returns {Promise<Array>} Array of matching tasks
 */
async function searchTasks(keyword) {
    if (!keyword.trim()) return fetchAllTasks();
    
    try {
        const response = await fetch(`${API_BASE_URL}/search/query?keyword=${encodeURIComponent(keyword)}`);
        if (!response.ok) throw new Error(await parseErrorMessage(response, 'Search failed'));
        const data = await response.json();
        return data.data || [];
    } catch (error) {
        console.error('Error searching tasks:', error);
        showToast(error.message || 'Error searching tasks', 'danger');
        return [];
    }
}

/**
 * Filter tasks by status
 * @param {string} status - Task status (PENDING or COMPLETED)
 * @returns {Promise<Array>} Array of filtered tasks
 */
async function filterByStatus(status) {
    try {
        const response = await fetch(`${API_BASE_URL}/filter/status?status=${status}`);
        if (!response.ok) throw new Error(await parseErrorMessage(response, 'Filter failed'));
        const data = await response.json();
        return data.data || [];
    } catch (error) {
        console.error('Error filtering by status:', error);
        showToast(error.message || 'Error filtering tasks by status', 'danger');
        return [];
    }
}

/**
 * Filter tasks by priority
 * @param {string} priority - Task priority (HIGH, MEDIUM, LOW)
 * @returns {Promise<Array>} Array of filtered tasks
 */
async function filterByPriority(priority) {
    try {
        const response = await fetch(`${API_BASE_URL}/filter/priority?priority=${priority}`);
        if (!response.ok) throw new Error(await parseErrorMessage(response, 'Filter failed'));
        const data = await response.json();
        return data.data || [];
    } catch (error) {
        console.error('Error filtering by priority:', error);
        showToast(error.message || 'Error filtering tasks by priority', 'danger');
        return [];
    }
}

/**
 * Fetch task statistics
 * @returns {Promise<Object>} Statistics object with total, completed, pending counts
 */
async function fetchStatistics() {
    try {
        const response = await fetch(`${API_BASE_URL}/statistics`);
        if (!response.ok) throw new Error(await parseErrorMessage(response, 'Failed to fetch statistics'));
        const data = await response.json();
        return data.data;
    } catch (error) {
        console.error('Error fetching statistics:', error);
        showToast(error.message || 'Error loading statistics', 'danger');
        return { total: 0, completed: 0, pending: 0 };
    }
}
