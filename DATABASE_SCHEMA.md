# SQL Schema for Smart Task Manager

## Database Setup

Run the following SQL commands to create the database and tables:

```sql
-- Create Database
CREATE DATABASE IF NOT EXISTS smart_task_manager_db;
USE smart_task_manager_db;

-- Create Tasks Table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Unique task identifier',
    title VARCHAR(255) NOT NULL COMMENT 'Task title',
    description LONGTEXT COMMENT 'Detailed task description',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT 'Priority level: HIGH, MEDIUM, LOW',
    due_date DATETIME COMMENT 'Task due date and time',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Task status: PENDING, COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Task creation timestamp',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    
    -- Indexes for performance optimization
    INDEX idx_status (status) COMMENT 'Index on status for filtering',
    INDEX idx_priority (priority) COMMENT 'Index on priority for filtering',
    INDEX idx_created_at (created_at) COMMENT 'Index on created_at for sorting',
    INDEX idx_due_date (due_date) COMMENT 'Index on due_date for deadline tracking'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Stores all user tasks with priority and status tracking';

-- Display table structure
DESC tasks;

-- Display created table
SHOW TABLES;
```

## Sample Data (Optional)

Insert sample tasks to test the application:

```sql
-- Insert sample tasks
INSERT INTO tasks (title, description, priority, due_date, status) VALUES
(
    'Complete Project Documentation',
    'Write comprehensive documentation for the Smart Task Manager project including API endpoints, setup instructions, and usage guide.',
    'HIGH',
    DATE_ADD(NOW(), INTERVAL 2 DAY),
    'PENDING'
),
(
    'Setup Database Connection',
    'Configure MySQL database connection and create necessary tables for task management.',
    'HIGH',
    DATE_ADD(NOW(), INTERVAL 1 DAY),
    'COMPLETED'
),
(
    'Design Database Schema',
    'Design normalized database schema with proper indexes for optimal performance.',
    'HIGH',
    NOW(),
    'COMPLETED'
),
(
    'Implement Task Search Feature',
    'Add full-text search capability to search tasks by title and description keywords.',
    'MEDIUM',
    DATE_ADD(NOW(), INTERVAL 5 DAY),
    'PENDING'
),
(
    'Add Task Filtering',
    'Implement filters for task status and priority levels.',
    'MEDIUM',
    DATE_ADD(NOW(), INTERVAL 3 DAY),
    'PENDING'
),
(
    'Create REST API Endpoints',
    'Design and implement RESTful API endpoints for all CRUD operations on tasks.',
    'HIGH',
    DATE_ADD(NOW(), INTERVAL 1 DAY),
    'COMPLETED'
),
(
    'Build Frontend UI',
    'Create responsive HTML/CSS frontend with Bootstrap for task management interface.',
    'MEDIUM',
    DATE_ADD(NOW(), INTERVAL 2 DAY),
    'PENDING'
),
(
    'Implement Exception Handling',
    'Add global exception handling and proper error responses for API endpoints.',
    'MEDIUM',
    DATE_ADD(NOW(), INTERVAL 4 DAY),
    'PENDING'
),
(
    'Code Review and Cleanup',
    'Review code quality, add comments, and ensure ATS/project-review friendly formatting.',
    'LOW',
    DATE_ADD(NOW(), INTERVAL 6 DAY),
    'PENDING'
),
(
    'Performance Testing',
    'Test application performance with large datasets and optimize as needed.',
    'LOW',
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    'PENDING'
);

-- Verify inserted data
SELECT * FROM tasks ORDER BY created_at DESC;

-- Count tasks by status
SELECT status, COUNT(*) as count FROM tasks GROUP BY status;

-- Count tasks by priority
SELECT priority, COUNT(*) as count FROM tasks GROUP BY priority;
```

## Database Statistics

```sql
-- Get total number of tasks
SELECT COUNT(*) as total_tasks FROM tasks;

-- Get completed vs pending tasks
SELECT 
    status,
    COUNT(*) as task_count,
    ROUND((COUNT(*) / (SELECT COUNT(*) FROM tasks) * 100), 2) as percentage
FROM tasks
GROUP BY status;

-- Get tasks by priority
SELECT 
    priority,
    COUNT(*) as task_count
FROM tasks
GROUP BY priority
ORDER BY FIELD(priority, 'HIGH', 'MEDIUM', 'LOW');

-- Get overdue tasks
SELECT * FROM tasks 
WHERE due_date < NOW() AND status = 'PENDING'
ORDER BY due_date ASC;

-- Get tasks due today
SELECT * FROM tasks 
WHERE DATE(due_date) = DATE(NOW())
ORDER BY due_date ASC;
```

## Performance Optimization

### Indexes Explanation

1. **idx_status**: Speeds up filtering by task status (PENDING/COMPLETED)
2. **idx_priority**: Optimizes priority-based queries
3. **idx_created_at**: Enables fast sorting by creation date
4. **idx_due_date**: Helps with deadline tracking and overdue queries

### Query Performance Tips

- Always use indexed columns in WHERE clauses
- Use LIMIT in pagination queries
- Regular ANALYZE TABLE command to update index statistics
- Monitor slow query logs for optimization opportunities

```sql
-- Enable slow query log
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;

-- Analyze table for optimization
ANALYZE TABLE tasks;

-- Check table statistics
SHOW TABLE STATUS FROM smart_task_manager_db WHERE name = 'tasks';
```
