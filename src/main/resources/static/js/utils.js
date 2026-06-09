/**
 * Utility Functions Module
 * Contains helper functions for UI and common operations
 */

/**
 * Show a toast notification
 * @param {string} message - Toast message
 * @param {string} type - Bootstrap alert type (success, danger, warning, info)
 * @param {number} duration - Duration in milliseconds (default: 3000)
 */
function showToast(message, type = 'info', duration = 3000) {
    const toastContainer = document.getElementById('toastContainer');
    const toastId = `toast-${Date.now()}`;

    const toastElement = document.createElement('div');
    toastElement.id = toastId;
    toastElement.className = 'toast';
    toastElement.setAttribute('role', 'alert');
    toastElement.setAttribute('aria-live', 'assertive');
    toastElement.setAttribute('aria-atomic', 'true');

    const header = document.createElement('div');
    header.className = `toast-header bg-${type} text-white border-0`;
    header.innerHTML = `<i class="fas fa-${getToastIcon(type)} me-2"></i><strong class="me-auto">${getToastTitle(type)}</strong><button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast"></button>`;

    const body = document.createElement('div');
    body.className = 'toast-body';
    body.textContent = message;

    toastElement.appendChild(header);
    toastElement.appendChild(body);
    toastContainer.appendChild(toastElement);

    const toast = new bootstrap.Toast(toastElement);
    toast.show();

    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}

/**
 * Get toast icon based on type
 * @param {string} type - Toast type
 * @returns {string} Font Awesome icon name
 */
function getToastIcon(type) {
    const icons = {
        'success': 'check-circle',
        'danger': 'exclamation-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

/**
 * Get toast title based on type
 * @param {string} type - Toast type
 * @returns {string} Toast title
 */
function getToastTitle(type) {
    const titles = {
        'success': 'Success',
        'danger': 'Error',
        'warning': 'Warning',
        'info': 'Info'
    };
    return titles[type] || 'Info';
}

/**
 * Format a date string to readable format
 * @param {string} dateString - ISO date string
 * @returns {string} Formatted date
 */
function formatDate(dateString) {
    if (!dateString) return 'No due date';
    
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = date - now;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) {
        return `Overdue by ${Math.abs(diffDays)} days`;
    } else if (diffDays === 0) {
        return 'Due today';
    } else if (diffDays === 1) {
        return 'Due tomorrow';
    } else if (diffDays <= 7) {
        return `Due in ${diffDays} days`;
    }
    
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

/**
 * Get priority badge HTML
 * @param {string} priority - Priority level (HIGH, MEDIUM, LOW)
 * @returns {string} HTML badge
 */
function getPriorityBadge(priority) {
    const badges = {
        'HIGH': '<span class="badge bg-danger"><i class="fas fa-arrow-up me-1"></i>High</span>',
        'MEDIUM': '<span class="badge bg-warning text-dark"><i class="fas fa-minus me-1"></i>Medium</span>',
        'LOW': '<span class="badge bg-info"><i class="fas fa-arrow-down me-1"></i>Low</span>'
    };
    return badges[priority] || '<span class="badge bg-secondary">Unknown</span>';
}

/**
 * Get status badge HTML
 * @param {string} status - Task status (PENDING or COMPLETED)
 * @returns {string} HTML badge
 */
function getStatusBadge(status) {
    if (status === 'COMPLETED') {
        return '<span class="badge bg-success"><i class="fas fa-check-circle me-1"></i>Completed</span>';
    }
    return '<span class="badge bg-warning text-dark"><i class="fas fa-clock me-1"></i>Pending</span>';
}

/**
 * Check if a task is overdue
 * @param {string} dueDate - Task due date
 * @param {string} status - Task status
 * @returns {boolean} True if task is overdue
 */
function isOverdue(dueDate, status) {
    if (!dueDate || status === 'COMPLETED') return false;
    return new Date(dueDate) < new Date();
}

/**
 * Format datetime for input field
 * @param {string} dateString - ISO date string
 * @returns {string} Formatted datetime for input
 */
function formatDateForInput(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

/**
 * Show loading spinner
 */
function showLoadingSpinner() {
    document.getElementById('loadingSpinner').classList.remove('d-none');
}

/**
 * Hide loading spinner
 */
function hideLoadingSpinner() {
    document.getElementById('loadingSpinner').classList.add('d-none');
}

/**
 * Show empty state message
 */
function showEmptyState() {
    document.getElementById('emptyState').classList.remove('d-none');
    document.getElementById('tasksContainer').innerHTML = '';
}

/**
 * Hide empty state message
 */
function hideEmptyState() {
    document.getElementById('emptyState').classList.add('d-none');
}

/**
 * Toggle dark mode
 */
function toggleDarkMode() {
    document.documentElement.setAttribute('data-bs-theme', 
        document.documentElement.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark'
    );
    localStorage.setItem('theme', document.documentElement.getAttribute('data-bs-theme'));
}

/**
 * Initialize dark mode from localStorage
 */
function initializeDarkMode() {
    const theme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-bs-theme', theme);
    updateDarkModeIcon();
}

/**
 * Update dark mode button icon
 */
function updateDarkModeIcon() {
    const isDark = document.documentElement.getAttribute('data-bs-theme') === 'dark';
    const icon = document.querySelector('#darkModeToggle i');
    if (isDark) {
        icon.classList.remove('fa-moon');
        icon.classList.add('fa-sun');
    } else {
        icon.classList.remove('fa-sun');
        icon.classList.add('fa-moon');
    }
}
