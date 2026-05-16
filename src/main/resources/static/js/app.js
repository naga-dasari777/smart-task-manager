/**
 * Main Application Module
 * Orchestrates the entire task management application
 * Handles user interactions and manages application state
 */

let currentEditingTaskId = null;
let allTasks = [];
let filteredTasks = [];

/**
 * Initialize application on DOM load
 */
document.addEventListener('DOMContentLoaded', () => {
    console.log('Smart Task Manager Application Initialized');
    
    // Initialize dark mode
    initializeDarkMode();
    
    // Load initial data
    loadTasks();
    loadStatistics();
    
    // Setup event listeners
    setupEventListeners();
});

/**
 * Setup all event listeners
 */
function setupEventListeners() {
    // Add Task Button
    document.getElementById('addTaskBtn').addEventListener('click', openAddTaskModal);
    
    // Save Task Button
    document.getElementById('saveTaskBtn').addEventListener('click', handleSaveTask);
    
    // Task Modal Close
    const taskModal = document.getElementById('taskModal');
    taskModal.addEventListener('hidden.bs.modal', resetTaskForm);
    
    // Search Input
    document.getElementById('searchInput').addEventListener('input', debounce(handleSearch, 300));
    document.getElementById('clearSearchBtn').addEventListener('click', clearSearch);
    
    // Status Filter
    document.querySelectorAll('input[name="statusFilter"]').forEach(radio => {
        radio.addEventListener('change', handleStatusFilter);
    });
    
    // Priority Filter
    document.getElementById('priorityFilter').addEventListener('change', handlePriorityFilter);
    
    // Dark Mode Toggle
    document.getElementById('darkModeToggle').addEventListener('click', () => {
        toggleDarkMode();
        updateDarkModeIcon();
    });
}

/**
 * Load all tasks from server and render them
 */
async function loadTasks() {
    showLoadingSpinner();
    allTasks = await fetchAllTasks();
    filteredTasks = [...allTasks];
    renderTasks(filteredTasks);
    hideLoadingSpinner();
}

/**
 * Render tasks to the DOM
 * @param {Array} tasks - Array of task objects to render
 */
function renderTasks(tasks) {
    const tasksContainer = document.getElementById('tasksContainer');
    tasksContainer.innerHTML = '';
    
    if (tasks.length === 0) {
        showEmptyState();
        return;
    }
    
    hideEmptyState();
    
    tasks.forEach(task => {
        const taskCard = createTaskCard(task);
        tasksContainer.appendChild(taskCard);
    });
}

/**
 * Create a task card DOM element
 * @param {Object} task - Task object
 * @returns {HTMLElement} Task card element
 */
function createTaskCard(task) {
    const col = document.createElement('div');
    col.className = 'col-md-6 col-lg-4';
    
    const isOverdueTask = isOverdue(task.dueDate, task.status);
    const cardClasses = `card shadow-sm h-100 border-0 ${isOverdueTask ? 'border-danger border-2' : ''}`;
    
    col.innerHTML = `
        <div class="${cardClasses}">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <div style="flex: 1;">
                        <h5 class="card-title ${task.status === 'COMPLETED' ? 'text-decoration-line-through text-muted' : ''}">
                            ${escapeHtml(task.title)}
                        </h5>
                        <p class="card-text text-muted small mb-2">
                            ${task.description ? escapeHtml(task.description.substring(0, 100)) + (task.description.length > 100 ? '...' : '') : 'No description'}
                        </p>
                    </div>
                </div>
                
                <div class="mb-3">
                    <div class="d-flex gap-2 flex-wrap">
                        ${getPriorityBadge(task.priority)}
                        ${getStatusBadge(task.status)}
                    </div>
                </div>
                
                <div class="mb-3">
                    <small class="text-muted">
                        <i class="fas fa-calendar me-1"></i>
                        ${formatDate(task.dueDate)}
                    </small>
                </div>
                
                <div class="d-flex gap-2">
                    ${task.status === 'PENDING' ? `
                        <button class="btn btn-sm btn-success flex-grow-1" onclick="handleCompleteTask(${task.id})" title="Mark as completed">
                            <i class="fas fa-check me-1"></i>Complete
                        </button>
                    ` : `
                        <button class="btn btn-sm btn-warning flex-grow-1" onclick="handleRevertTask(${task.id})" title="Revert to pending">
                            <i class="fas fa-undo me-1"></i>Undo
                        </button>
                    `}
                    <button class="btn btn-sm btn-primary" onclick="openEditTaskModal(${task.id})" title="Edit task">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="handleDeleteTask(${task.id})" title="Delete task">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        </div>
    `;
    
    return col;
}

/**
 * Load and update task statistics
 */
async function loadStatistics() {
    const stats = await fetchStatistics();
    document.getElementById('totalTasks').textContent = stats.total || 0;
    document.getElementById('completedTasks').textContent = stats.completed || 0;
    document.getElementById('pendingTasks').textContent = stats.pending || 0;
    
    const percentage = stats.getCompletionPercentage ? stats.getCompletionPercentage() : 0;
    document.getElementById('completionBar').style.width = percentage + '%';
    document.getElementById('completionPercentage').textContent = percentage.toFixed(0) + '% Complete';
}

/**
 * Open modal for adding a new task
 */
function openAddTaskModal() {
    currentEditingTaskId = null;
    resetTaskForm();
    document.getElementById('modalTitle').textContent = 'Add New Task';
    const modal = new bootstrap.Modal(document.getElementById('taskModal'));
    modal.show();
}

/**
 * Open modal for editing an existing task
 * @param {number} taskId - Task ID to edit
 */
async function openEditTaskModal(taskId) {
    currentEditingTaskId = taskId;
    const task = allTasks.find(t => t.id === taskId);
    
    if (task) {
        document.getElementById('taskTitle').value = task.title;
        document.getElementById('taskDescription').value = task.description || '';
        document.getElementById('taskPriority').value = task.priority;
        document.getElementById('taskDueDate').value = formatDateForInput(task.dueDate);
        
        document.getElementById('modalTitle').textContent = 'Edit Task';
        const modal = new bootstrap.Modal(document.getElementById('taskModal'));
        modal.show();
    }
}

/**
 * Handle saving task (create or update)
 */
async function handleSaveTask() {
    const title = document.getElementById('taskTitle').value.trim();
    const description = document.getElementById('taskDescription').value.trim();
    const priority = document.getElementById('taskPriority').value;
    const dueDate = document.getElementById('taskDueDate').value;
    
    // Validation
    if (!title) {
        document.getElementById('titleError').textContent = 'Task title is required';
        return;
    }
    
    const taskData = {
        title,
        description: description || null,
        priority,
        dueDate: dueDate ? new Date(dueDate).toISOString() : null
    };
    
    let result;
    if (currentEditingTaskId) {
        // Update existing task
        result = await updateTask(currentEditingTaskId, taskData);
    } else {
        // Create new task
        result = await createTask(taskData);
    }
    
    if (result) {
        bootstrap.Modal.getInstance(document.getElementById('taskModal')).hide();
        await loadTasks();
        await loadStatistics();
    }
}

/**
 * Handle task completion
 * @param {number} taskId - Task ID to complete
 */
async function handleCompleteTask(taskId) {
    const result = await markTaskComplete(taskId);
    if (result) {
        await loadTasks();
        await loadStatistics();
    }
}

/**
 * Handle reverting completed task to pending
 * @param {number} taskId - Task ID to revert
 */
async function handleRevertTask(taskId) {
    const task = allTasks.find(t => t.id === taskId);
    if (task) {
        const taskData = {
            ...task,
            status: 'PENDING'
        };
        const result = await updateTask(taskId, taskData);
        if (result) {
            await loadTasks();
            await loadStatistics();
        }
    }
}

/**
 * Handle task deletion with confirmation
 * @param {number} taskId - Task ID to delete
 */
function handleDeleteTask(taskId) {
    if (confirm('Are you sure you want to delete this task? This action cannot be undone.')) {
        deleteTaskAndRefresh(taskId);
    }
}

/**
 * Delete task and refresh UI
 * @param {number} taskId - Task ID to delete
 */
async function deleteTaskAndRefresh(taskId) {
    const result = await deleteTask(taskId);
    if (result) {
        await loadTasks();
        await loadStatistics();
    }
}

/**
 * Handle search input
 * @param {Event} event - Input event
 */
async function handleSearch(event) {
    const keyword = event.target.value.trim();
    
    if (!keyword) {
        filteredTasks = [...allTasks];
        renderTasks(filteredTasks);
        return;
    }
    
    showLoadingSpinner();
    filteredTasks = await searchTasks(keyword);
    renderTasks(filteredTasks);
    hideLoadingSpinner();
}

/**
 * Clear search input and reset tasks
 */
function clearSearch() {
    document.getElementById('searchInput').value = '';
    document.getElementById('searchInput').focus();
    filteredTasks = [...allTasks];
    renderTasks(filteredTasks);
}

/**
 * Handle status filter change
 */
async function handleStatusFilter(event) {
    const status = event.target.value;
    
    if (status === 'all') {
        filteredTasks = [...allTasks];
    } else {
        filteredTasks = allTasks.filter(task => task.status === status);
    }
    
    renderTasks(filteredTasks);
}

/**
 * Handle priority filter change
 */
async function handlePriorityFilter(event) {
    const priority = event.target.value;
    
    if (priority === 'all') {
        filteredTasks = [...allTasks];
    } else {
        filteredTasks = allTasks.filter(task => task.priority === priority);
    }
    
    renderTasks(filteredTasks);
}

/**
 * Reset task form
 */
function resetTaskForm() {
    document.getElementById('taskForm').reset();
    document.getElementById('titleError').textContent = '';
    currentEditingTaskId = null;
    document.getElementById('taskPriority').value = 'MEDIUM';
}

/**
 * Escape HTML special characters to prevent XSS
 * @param {string} text - Text to escape
 * @returns {string} Escaped text
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Debounce function to limit function calls
 * @param {Function} func - Function to debounce
 * @param {number} wait - Wait time in milliseconds
 * @returns {Function} Debounced function
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}
