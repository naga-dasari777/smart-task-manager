/**
 * API Service Module
 * Handles all HTTP requests to the backend
 * Provides a clean abstraction layer for API calls
 */

const API_BASE_URL = '/api/tasks';

/**
 * Generic API request handler.
 * Centralizes fetch, error handling, and optional toast notifications.
 *
 * @param {string} url - Request URL
 * @param {Object} [options] - fetch options (method, headers, body)
 * @param {Object} [config] - Extra behaviour config
 * @param {string} [config.errorContext] - Human-readable context for console.error
 * @param {string} [config.errorToast] - Toast message shown on failure (omit to suppress)
 * @param {*} [config.fallback] - Value returned on failure (default: null)
 * @returns {Promise<Object>} Parsed JSON response body
 */
async function apiRequest(url, options = {}, config = {}) {
    const { errorContext = 'API request', errorToast, fallback = null } = config;
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            let serverMessage;
            try {
                const body = await response.json();
                serverMessage = body.message;
            } catch (_) { /* response not JSON */ }
            throw new Error(serverMessage || `${errorContext} failed`);
        }
        return await response.json();
    } catch (error) {
        console.error(`Error: ${errorContext}:`, error);
        if (errorToast) showToast(error.message || errorToast, 'danger');
        return fallback;
    }
}

/**
 * Fetch all tasks from the server
 * @returns {Promise<Array>} Array of task objects
 */
async function fetchAllTasks() {
    const data = await apiRequest(`${API_BASE_URL}`, {}, {
        errorContext: 'fetching tasks',
        errorToast: 'Error loading tasks',
        fallback: { data: [] }
    });
    return data.data || [];
}

/**
 * Fetch a single task by ID
 * @param {number} taskId - The task ID to fetch
 * @returns {Promise<Object>} Task object
 */
async function fetchTaskById(taskId) {
    const data = await apiRequest(`${API_BASE_URL}/${taskId}`, {}, {
        errorContext: 'fetching task',
        errorToast: 'Error loading task'
    });
    return data ? data.data : null;
}

/**
 * Create a new task
 * @param {Object} taskData - Task data object with title, description, priority, dueDate
 * @returns {Promise<Object>} Created task object
 */
async function createTask(taskData) {
    const data = await apiRequest(`${API_BASE_URL}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(taskData)
    }, {
        errorContext: 'creating task',
        errorToast: 'Error creating task'
    });
    if (data) showToast(data.message || 'Task created successfully!', 'success');
    return data ? data.data : null;
}

/**
 * Update an existing task
 * @param {number} taskId - The task ID to update
 * @param {Object} taskData - Updated task data
 * @returns {Promise<Object>} Updated task object
 */
async function updateTask(taskId, taskData) {
    const data = await apiRequest(`${API_BASE_URL}/${taskId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(taskData)
    }, {
        errorContext: 'updating task',
        errorToast: 'Error updating task'
    });
    if (data) showToast(data.message || 'Task updated successfully!', 'success');
    return data ? data.data : null;
}

/**
 * Delete a task
 * @param {number} taskId - The task ID to delete
 * @returns {Promise<boolean>} Success status
 */
async function deleteTask(taskId) {
    const data = await apiRequest(`${API_BASE_URL}/${taskId}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }
    }, {
        errorContext: 'deleting task',
        errorToast: 'Error deleting task'
    });
    if (data) showToast(data.message || 'Task deleted successfully!', 'success');
    return !!data;
}

/**
 * Mark a task as completed
 * @param {number} taskId - The task ID to complete
 * @returns {Promise<Object>} Updated task object
 */
async function markTaskComplete(taskId) {
    const data = await apiRequest(`${API_BASE_URL}/${taskId}/complete`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' }
    }, {
        errorContext: 'completing task',
        errorToast: 'Error completing task'
    });
    if (data) showToast(data.message || 'Task marked as completed!', 'success');
    return data ? data.data : null;
}

/**
 * Search tasks by keyword
 * @param {string} keyword - Search keyword
 * @returns {Promise<Array>} Array of matching tasks
 */
async function searchTasks(keyword) {
    if (!keyword.trim()) return fetchAllTasks();
    const data = await apiRequest(
        `${API_BASE_URL}/search/query?keyword=${encodeURIComponent(keyword)}`,
        {},
        { errorContext: 'searching tasks', fallback: { data: [] } }
    );
    return data.data || [];
}

/**
 * Filter tasks by status
 * @param {string} status - Task status (PENDING or COMPLETED)
 * @returns {Promise<Array>} Array of filtered tasks
 */
async function filterByStatus(status) {
    const data = await apiRequest(
        `${API_BASE_URL}/filter/status?status=${status}`,
        {},
        { errorContext: 'filtering by status', fallback: { data: [] } }
    );
    return data.data || [];
}

/**
 * Filter tasks by priority
 * @param {string} priority - Task priority (HIGH, MEDIUM, LOW)
 * @returns {Promise<Array>} Array of filtered tasks
 */
async function filterByPriority(priority) {
    const data = await apiRequest(
        `${API_BASE_URL}/filter/priority?priority=${priority}`,
        {},
        { errorContext: 'filtering by priority', fallback: { data: [] } }
    );
    return data.data || [];
}

/**
 * Fetch task statistics
 * @returns {Promise<Object>} Statistics object with total, completed, pending counts
 */
async function fetchStatistics() {
    const data = await apiRequest(`${API_BASE_URL}/statistics`, {}, {
        errorContext: 'fetching statistics',
        fallback: { data: { total: 0, completed: 0, pending: 0 } }
    });
    return data.data;
}
