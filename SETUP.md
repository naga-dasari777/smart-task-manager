# Setup Instructions for Smart Task Manager

## Prerequisites

- **Java JDK 11** or higher
- **MySQL Server** (8.0 or higher)
- **Maven 3.6+**
- **Git**
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code with Java extensions)
- **Postman** (for API testing, optional)

## Step 1: Clone the Repository

```bash
# Clone the repository
git clone https://github.com/naga-dasari777/smart-task-manager.git

# Navigate to project directory
cd smart-task-manager
```

## Step 2: Create MySQL Database

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE smart_task_manager_db;
USE smart_task_manager_db;
```

Or run the SQL schema file:

```bash
mysql -u root -p smart_task_manager_db < DATABASE_SCHEMA.md
```

## Step 3: Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/smart_task_manager_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Note**: Replace `your_mysql_password` with your actual MySQL password.

## Step 4: Build the Project

```bash
# Clean and install dependencies
mvn clean install

# Or just clean and compile
mvn clean compile
```

This command will:
- Download all dependencies from Maven Central Repository
- Compile Java source files
- Package the application

## Step 5: Run the Application

```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Run the JAR file
java -jar target/smart-task-manager-1.0.0.jar
```

You should see output similar to:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/

Smart Task Manager Started!
Server is running on http://localhost:8080
```

## Step 6: Access the Application

Open your browser and navigate to:

```
http://localhost:8080
```

You should see the Smart Task Manager dashboard.

## Step 7: Test the API (Optional)

Use Postman or curl to test the API endpoints:

### Get All Tasks

```bash
curl -X GET http://localhost:8080/api/tasks
```

### Create a New Task

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Task",
    "description": "This is my first task",
    "priority": "HIGH",
    "dueDate": "2026-05-20T10:00:00"
  }'
```

### Get Task Statistics

```bash
curl -X GET http://localhost:8080/api/tasks/statistics
```

## Troubleshooting

### MySQL Connection Error

**Error**: `java.sql.SQLException: Access denied for user 'root'@'localhost'`

**Solution**: Check your MySQL password in `application.properties`

### Port Already in Use

**Error**: `Address already in use`

**Solution**: Change the port in `application.properties`:

```properties
server.port=8081
```

### Maven Build Failure

**Error**: `[ERROR] FATAL ERROR in native method`

**Solution**: Clear Maven cache and rebuild:

```bash
rm -rf ~/.m2/repository
mvn clean install
```

### No Tables in Database

**Solution**: Spring Boot will auto-create tables when `spring.jpa.hibernate.ddl-auto=update` is set.

If not, manually run the SQL schema from `DATABASE_SCHEMA.md`

## Project Structure

```
smart-task-manager/
├── src/
│   ├── main/
│   │   ├── java/com/smarttaskmanager/
│   │   │   ├── SmartTaskManagerApplication.java    (Entry point)
│   │   │   ├── controller/
│   │   │   │   └── TaskController.java             (REST endpoints)
│   │   │   ├── service/
│   │   │   │   └── TaskService.java                (Business logic)
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java             (Database access)
│   │   │   ├── model/
│   │   │   │   ├── Task.java                       (Entity)
│   │   │   │   └── TaskDTO.java                    (DTO)
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java     (Error handling)
│   │   │       └── ResourceNotFoundException.java  (Custom exception)
│   │   └── resources/
│   │       ├── application.properties              (Configuration)
│   │       └── static/
│   │           ├── index.html                      (Main page)
│   │           ├── js/
│   │           │   ├── app.js                      (Main logic)
│   │           │   ├── api.js                      (API calls)
│   │           │   └── utils.js                    (Helper functions)
│   │           └── css/
│   │               ├── style.css                   (Main styles)
│   │               └── dark-mode.css               (Dark mode)
│   └── test/
│       └── java/com/smarttaskmanager/              (Unit tests)
├── pom.xml                                          (Maven configuration)
├── README.md                                        (Project documentation)
├── DATABASE_SCHEMA.md                               (Database setup)
└── SETUP.md                                         (This file)
```

## Next Steps

1. **Explore the Application**: Add some tasks, edit them, mark as complete
2. **Test API Endpoints**: Use Postman to test all REST endpoints
3. **Review Code**: Understand the architecture and design patterns
4. **Customize**: Add your own features or styling
5. **Deploy**: Deploy to production (Heroku, AWS, Azure, etc.)

## IDE Setup

### IntelliJ IDEA

1. Open project: File → Open → Select project folder
2. Maven should auto-detect pom.xml
3. Right-click pom.xml → Run Maven → clean install
4. Run application: Right-click SmartTaskManagerApplication.java → Run

### VS Code

1. Install Extension Pack for Java
2. Install Spring Boot Extension Pack
3. Open folder in VS Code
4. Click Run button on main() method

### Eclipse

1. File → Import → Existing Maven Projects
2. Select project folder
3. Right-click → Run As → Maven install
4. Run application: Right-click → Run As → Spring Boot App

## Performance Tips

- Enable database connection pooling
- Add caching layer for frequently accessed data
- Implement pagination for large datasets
- Use database indexes effectively
- Monitor application logs for bottlenecks

## Support

For issues or questions:

1. Check GitHub Issues
2. Review documentation in README.md
3. Check application logs in `log/` folder
4. Verify database connection
5. Ensure all dependencies are installed

---

**Happy coding! 🚀**
