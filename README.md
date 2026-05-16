# Smart Task Manager

A professional, full-stack task management web application built with Java Spring Boot, MySQL, and modern frontend technologies.

## Features

✅ Add, Edit, Delete Tasks
✅ Mark Tasks as Completed
✅ Set Task Priority (High, Medium, Low)
✅ Due Date Support
✅ Search Tasks
✅ Filter by Status (Completed/Pending)
✅ Task Statistics Dashboard
✅ Responsive UI with Bootstrap
✅ Toast Notifications
✅ Dark Mode Toggle

## Tech Stack

**Backend:**
- Java 11
- Spring Boot 2.7.15
- Spring Data JPA
- MySQL 8.0
- Maven

**Frontend:**
- HTML5
- CSS3
- Bootstrap 5
- JavaScript (Vanilla)
- Fetch API

## Project Structure

```
smart-task-manager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/smarttaskmanager/
│   │   │       ├── SmartTaskManagerApplication.java
│   │   │       ├── controller/
│   │   │       │   └── TaskController.java
│   │   │       ├── service/
│   │   │       │   └── TaskService.java
│   │   │       ├── repository/
│   │   │       │   └── TaskRepository.java
│   │   │       ├── model/
│   │   │       │   ├── Task.java
│   │   │       │   └── TaskDTO.java
│   │   │       └── exception/
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           └── ResourceNotFoundException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   ├── style.css
│   │       │   │   └── dark-mode.css
│   │       │   ├── js/
│   │       │   │   ├── app.js
│   │       │   │   ├── api.js
│   │       │   │   └── utils.js
│   │       │   └── index.html
│   │       └── templates/ (if using Thymeleaf)
│   └── test/
│       └── java/
│           └── com/smarttaskmanager/
│               └── SmartTaskManagerApplicationTests.java
├── pom.xml
├── README.md
└── .gitignore
```

## Installation & Setup

### Prerequisites
- Java 11 or higher
- MySQL Server
- Maven 3.6+
- Git

### Step-by-Step Setup

1. **Clone the repository**
```bash
git clone https://github.com/naga-dasari777/smart-task-manager.git
cd smart-task-manager
```

2. **Create MySQL Database**
```sql
CREATE DATABASE smart_task_manager_db;
USE smart_task_manager_db;
```

3. **Configure Database Connection**
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

4. **Build the Project**
```bash
mvn clean install
```

5. **Run the Application**
```bash
mvn spring-boot:run
```

6. **Access the Application**
Open your browser and navigate to:
```
http://localhost:8080/
```

## API Endpoints

### Task Management APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks/{id}` | Get task by ID |
| POST | `/api/tasks` | Create new task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| PATCH | `/api/tasks/{id}/complete` | Mark task as completed |
| GET | `/api/tasks/search?keyword=value` | Search tasks |
| GET | `/api/tasks/filter?status=PENDING` | Filter tasks by status |
| GET | `/api/tasks/statistics` | Get task statistics |

## Usage Guide

### Adding a Task
1. Click "Add New Task" button
2. Fill in task details (title, description, priority, due date)
3. Click "Save Task"

### Editing a Task
1. Click the "Edit" button on any task card
2. Modify the task details
3. Click "Update Task"

### Completing a Task
1. Click the "Mark Complete" button on the task
2. Task status will update to Completed

### Deleting a Task
1. Click the "Delete" button on the task
2. Confirm the deletion

### Searching Tasks
1. Use the search bar to find tasks by title or description
2. Results update in real-time

### Filtering Tasks
1. Use the filter dropdown to show:
   - All Tasks
   - Pending Tasks
   - Completed Tasks

## Database Schema

```sql
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description LONGTEXT,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    due_date DATETIME,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_created_at (created_at)
);
```

## Architecture Overview

### MVC Pattern
The application follows the Model-View-Controller (MVC) pattern:

- **Model**: Task entity with data annotations
- **View**: HTML pages with Bootstrap for responsive UI
- **Controller**: TaskController handling HTTP requests

### Layered Architecture

1. **Controller Layer**: Handles HTTP requests/responses
2. **Service Layer**: Contains business logic
3. **Repository Layer**: Data access using Spring Data JPA
4. **Model/Entity Layer**: Database entities and DTOs

## Code Quality

- ✅ Clean code principles
- ✅ Proper exception handling
- ✅ Meaningful variable names
- ✅ Code comments for complex logic
- ✅ RESTful API design
- ✅ Validation annotations
- ✅ Separation of concerns

## Interview Talking Points

1. **Architecture**: Explain how you used layered architecture (controller → service → repository)
2. **Spring Boot**: Discuss how Spring Boot simplified configuration and dependency injection
3. **Spring Data JPA**: Explain how JPA handles database operations without writing SQL
4. **REST APIs**: Describe RESTful principles used in the API design
5. **Frontend Integration**: Explain how JavaScript Fetch API connects frontend to backend
6. **Error Handling**: Discuss global exception handling strategy
7. **Database Design**: Explain normalization and indexing decisions
8. **Responsive Design**: Talk about Bootstrap usage for mobile responsiveness

## Future Enhancements

- User authentication and authorization
- Task categories/tags
- Task attachments
- Task reminders
- Collaborative task management
- Task history/audit logs

## License

MIT License - Feel free to use this project for learning and interview preparation.

## Support

For questions or issues, please open an issue on GitHub.

---

**Built with ❤️ for Campus Placements**