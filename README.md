# EduPro -- Course Management System

A Spring Boot web application for managing courses with role-based access control, built as a learning project to explore Spring MVC, Spring Security, JPA/Hibernate, Thymeleaf, and testing.

---

## Features

- **Course Management** -- Create, view, edit, and delete courses (ADMIN only)
- **User Registration** -- Self-service sign-up with uniqueness validation
- **Role-Based Access Control** -- ADMIN manages courses; students can browse
- **Spring Security** -- Form-based auth, CSRF protection, BCrypt passwords
- **Optimistic Locking** -- `@Version` prevents concurrent update conflicts
- **Audit Fields** -- Automatic `createdAt` / `updatedAt` timestamps
- **Dual Database** -- H2 (default, in-memory) for dev, Oracle for production
- **H2 Console** -- Built-in database browser at `/h2-console`
- **Thymeleaf Views** -- Natural HTML templates with auto CSRF and XSS protection
- **Flash Messages** -- Success/error notifications with auto-fade
- **Test Suite** -- Unit, controller, and repository tests

---

## Tech Stack

| Layer        | Technology                                            |
|-------------|------------------------------------------------------|
| Language    | Java 17                                               |
| Framework   | Spring Boot 3.5                                       |
| Web         | Spring MVC + **Thymeleaf**                            |
| Security    | Spring Security 6 (form login, role-based)            |
| Persistence | Spring Data JPA / Hibernate                           |
| Database    | H2 (dev), Oracle (prod)                               |
| Validation  | Bean Validation (Jakarta)                             |
| Testing     | JUnit 5, Mockito, MockMvc, @DataJpaTest              |
| Build       | Maven                                                 |
| Utilities   | Lombok                                                |

---

## Project Structure

```
src/main/java/com/eduproject/
├── EduProApplication.java              # Application entry point
├── config/
│   ├── SecurityConfig.java             # Security filter chain, CSRF, role rules
│   └── UserDetailService.java          # Custom UserDetailsService (DB-backed)
├── controller/
│   ├── HomeController.java             # Home page + login + logout handling
│   ├── CourseController.java           # CRUD endpoints for courses
│   └── UserController.java            # User registration
├── model/
│   ├── CourseEntity.java               # JPA entity with audit fields + @Version
│   ├── CourseDTO.java                  # Form-backing DTO with validation
│   ├── User.java                       # JPA entity implementing UserDetails
│   ├── UserRegistrationDTO.java        # Registration form DTO
│   └── Role.java                       # Enum: ADMIN, STUDENT, TEACHER
├── repository/
│   ├── CourseRepository.java           # Spring Data JPA repository
│   └── UserRepository.java            # findByUsername, existsByEmail, etc.
├── service/
│   ├── CourseService.java              # Service interface
│   ├── UserService.java               # Service interface
│   └── impl/
│       ├── CourseServiceImpl.java      # Business logic + DTO ↔ Entity mapping
│       └── UserServiceImpl.java        # Registration with BCrypt encoding
├── exception/
│   ├── CourseNotFoundException.java    # Custom runtime exception
│   └── GlobalExceptionHandler.java    # @ControllerAdvice error handling
└── runner/
    └── InsertDataInDB.java            # Seed data on startup

src/main/resources/
├── templates/                          # Thymeleaf templates
│   ├── fragments/header.html           # Shared navigation
│   ├── fragments/footer.html           # Shared footer
│   ├── home.html                       # Landing page
│   ├── auth/login.html                 # Login form
│   ├── course/list.html                # Course listing
│   ├── course/form.html                # Create/Edit form
│   ├── course/view.html                # Course detail view
│   ├── user/register.html              # Registration form
│   └── error/404.html, 500.html        # Error pages
├── static/css/style.css                # Stylesheet (annotated for learning)
├── static/js/app.js                    # JavaScript helpers
└── application.properties              # App configuration

src/test/java/com/eduproject/
├── service/
│   ├── CourseServiceImplTest.java      # Unit tests (Mockito)
│   └── UserServiceImplTest.java        # Unit tests (Mockito)
├── controller/
│   └── CourseControllerTest.java       # Web layer tests (@WebMvcTest)
└── repository/
    └── CourseRepositoryTest.java       # JPA tests (@DataJpaTest)
```

---

## Getting Started

### Prerequisites

- **Java 17** or later
- **Maven 3.8+**

### Run

```bash
mvn spring-boot:run
```

App starts at **http://localhost:8080** with H2 in-memory database and sample seed data.

### Run Tests

```bash
mvn test
```

### H2 Console

Navigate to **http://localhost:8080/h2-console** with:

| Setting    | Value                                                              |
|-----------|--------------------------------------------------------------------|
| JDBC URL  | `jdbc:h2:mem:edupro;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false`     |
| Username  | `sa`                                                               |
| Password  | *(empty)*                                                          |

---

## Default Users (Seed Data)

| Username  | Password     | Role       |
|-----------|-------------|------------|
| `admin`   | `admin123`  | ROLE_ADMIN |
| `student` | `student123`| ROLE_STUDENT |

---

## Version History

| Version | Tag | Highlights |
|---------|-----|-----------|
| v0.1.0 | First stable | JSP views, CRUD, Spring Security, H2/Oracle |
| **v0.2.0** | **Current** | **Thymeleaf migration, DTO redesign, test suite, security fixes** |

---

## Documentation

| File | Description |
|------|-------------|
| [V2_MIGRATION.md](V2_MIGRATION.md) | v0.2.0 changes: JSP→Thymeleaf, testing, model redesign, FAQ |
| [CHALLENGES.md](CHALLENGES.md) | 18 real problems faced with root cause analysis and fixes |
| [CONCEPTS_LEARNED.md](CONCEPTS_LEARNED.md) | Key concepts and patterns learned |
| [INTERVIEW_QNA.md](INTERVIEW_QNA.md) | Interview Q&A based on this project |
| [CODE_REVIEW.md](CODE_REVIEW.md) | Self-review: design decisions, limitations, improvements |

---

## Key Design Decisions

| Decision | Why |
|----------|-----|
| **Thymeleaf over JSP** | Natural HTML templates, auto CSRF/XSS, JAR-compatible, IDE preview |
| **Separate DTOs from Entities** | Clean boundary: form validation on DTO, JPA on entity |
| **UserRegistrationDTO** | Security: prevents role escalation (user can't set ADMIN) |
| **Three test layers** | Fast feedback: unit (ms), controller (s), repository (s) |
| **`@Version` optimistic locking** | Prevents silent data loss on concurrent edits |
| **`BindingResult.rejectValue()`** | Shows all validation errors at once (not one-at-a-time) |
| **Service interface + impl** | Spring convention, allows easy mocking in tests |

---

## Known Limitations

- **No pagination** -- Course list loads all records
- **No REST API** -- Server-rendered Thymeleaf only; no JSON endpoints
- **No database migration tool** -- Uses `ddl-auto=update` instead of Flyway
- **No CI/CD pipeline** -- Tests run locally only
- **No Docker** -- Manual deployment

---

## License

Learning/portfolio project. Free to reference for educational purposes.
