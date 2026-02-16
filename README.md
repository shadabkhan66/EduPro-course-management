# EduPro -- Course Management System

A Spring Boot web application for managing courses with role-based access control, built as a learning project to explore Spring MVC, Spring Security, JPA/Hibernate, and JSP-based views.

---

## Features

- **Course Management** -- Create, view, edit, and delete courses (ADMIN only)
- **User Registration** -- Self-service sign-up with uniqueness validation for username and email
- **Role-Based Access Control** -- ADMIN users manage courses; regular users can browse
- **Spring Security** -- Form-based authentication, CSRF protection, BCrypt password encoding
- **Optimistic Locking** -- `@Version` field prevents concurrent update conflicts
- **Audit Fields** -- Automatic `createdAt` / `updatedAt` timestamps on entities
- **Dual Database Support** -- H2 (default, in-memory) for development, Oracle for production
- **H2 Console** -- Built-in database browser at `/h2-console` for development
- **Shared Layout** -- Common header/footer fragments with responsive navigation
- **Flash Messages** -- Success/error notifications with auto-fade

---

## Tech Stack

| Layer        | Technology                                      |
|-------------|------------------------------------------------|
| Language    | Java 17                                         |
| Framework   | Spring Boot 3.5                                 |
| Web         | Spring MVC + JSP (JSTL, Spring Form Tags)       |
| Security    | Spring Security 6 (form login, role-based)      |
| Persistence | Spring Data JPA / Hibernate                     |
| Database    | H2 (dev), Oracle (prod)                         |
| Validation  | Bean Validation (Jakarta)                       |
| Build       | Maven                                           |
| Utilities   | Lombok                                          |

---

## Project Structure

```
src/main/java/com/eduproject/
├── EduProApplication.java              # Application entry point
├── config/
│   ├── SecurityConfig.java             # Security filter chain, CSRF, role rules
│   └── UserDetailService.java          # Custom UserDetailsService (DB-backed)
├── controller/
│   ├── HomeController.java             # Home page + logout message handling
│   ├── CourseController.java           # CRUD endpoints for courses
│   └── UserController.java            # User registration
├── model/
│   ├── CourseEntity.java               # JPA entity with audit fields + @Version
│   ├── CourseVO.java                   # Form-backing object with validation
│   ├── User.java                       # JPA entity implementing UserDetails
│   └── Role.java                       # Enum: ROLE_USER, ROLE_ADMIN
├── repository/
│   ├── CourseRepository.java           # Spring Data JPA repository
│   └── UserRepository.java            # findByUsername, existsByEmail, etc.
├── service/
│   ├── CourseService.java              # Service interface
│   ├── UserService.java               # Service interface
│   └── impl/
│       ├── CourseServiceImpl.java      # Business logic + VO <-> Entity mapping
│       └── UserServiceImpl.java        # Registration + uniqueness checks
├── exception/
│   ├── CourseNotFoundException.java    # Custom runtime exception
│   └── GlobalExceptionHandler.java    # @ControllerAdvice error handling
└── runner/
    └── InsertDataInDB.java            # Seed data on startup (CommandLineRunner)

src/main/webapp/
├── css/style.css                       # External stylesheet (commented for learning)
├── js/app.js                           # External JS (confirmation dialogs, flash msgs)
└── WEB-INF/views/
    ├── fragments/header.jsp            # Shared navigation + security-aware links
    ├── fragments/footer.jsp            # Shared footer + JS includes
    ├── home/home.jsp                   # Landing page with course count
    ├── auth/login.jsp                  # Login form (plain HTML for Spring Security)
    ├── course/course-list.jsp          # Course listing table
    ├── course/course-form.jsp          # Create/Edit form (shared)
    ├── course/course-view.jsp          # Course detail view
    ├── user/user-form.jsp              # Registration form
    └── error/404.jsp, 500.jsp          # Custom error pages
```

---

## Getting Started

### Prerequisites

- **Java 17** or later
- **Maven 3.8+**

### Run with H2 (default)

```bash
mvn spring-boot:run
```

The app starts at **http://localhost:8080** with an in-memory H2 database. Seed data (sample courses + admin user) is inserted automatically on startup.

### Access H2 Console

Navigate to **http://localhost:8080/h2-console** and connect with:

| Setting     | Value                                               |
|------------|-----------------------------------------------------|
| JDBC URL   | `jdbc:h2:mem:edupro;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false` |
| Username   | `sa`                                                |
| Password   | *(leave empty)*                                     |

### Run with Oracle

1. Copy `application-oracle.properties.example` to `application-oracle.properties`
2. Fill in your Oracle credentials
3. Switch the active profile:

```properties
# In application.properties
spring.profiles.active=oracle
```

4. Run: `mvn spring-boot:run`

---

## Default Users (Seed Data)

| Username | Password  | Role       |
|----------|-----------|------------|
| `admin`  | `admin123`| ROLE_ADMIN |
| `user`   | `user123` | ROLE_USER  |

---

## Key Design Decisions

| Decision | Why |
|----------|-----|
| **VO (Value Object) separate from Entity** | Keeps validation on the form-backing object; entity stays clean for JPA |
| **Service interface + impl** | Follows Spring convention; allows swapping implementations |
| **`@Version` for optimistic locking** | Prevents lost updates when two users edit the same course |
| **`BeanUtils.copyProperties` with exclusions** | Safely copies VO fields to entity while preserving audit/version fields |
| **`BindingResult.rejectValue()` for uniqueness** | Shows all validation errors at once instead of one-at-a-time exceptions |
| **Plain HTML form for login** | Spring Security's login endpoint doesn't provide a model attribute for `<form:form>` |
| **Custom `logoutSuccessHandler`** | Clean URL (`/`) with session-based flash message instead of `?logout` parameter |
| **`readOnly = true` on read operations** | Performance optimization -- Hibernate skips dirty checking |

---

## Documentation

This project includes detailed learning documentation:

| File | Description |
|------|-------------|
| [CHALLENGES.md](CHALLENGES.md) | 18 real problems faced during development, with root cause analysis and fixes |
| [CONCEPTS_LEARNED.md](CONCEPTS_LEARNED.md) | Key concepts and patterns learned while building the project |
| [INTERVIEW_QNA.md](INTERVIEW_QNA.md) | Interview questions and answers based on this project |
| [CODE_REVIEW.md](CODE_REVIEW.md) | Self-review highlighting design decisions, limitations, and improvements |

---

## Known Limitations

- **No pagination** -- Course list loads all records (fine for a POC, not for production)
- **No caching** -- Every request hits the database
- **No REST API** -- Server-rendered JSP only; no JSON endpoints
- **Minimal tests** -- Only a context load test; needs unit and integration tests
- **No database migration tool** -- Uses `ddl-auto=update` instead of Flyway/Liquibase
- **JSP technology** -- Legacy view technology; modern Spring apps use Thymeleaf or a frontend framework

---

## License

This is a learning/portfolio project. Feel free to reference it for educational purposes.
