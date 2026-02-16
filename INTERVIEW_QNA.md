# Interview Q&A -- EduPro Course Management Project

> Ready-to-use answers with proper keywords for Spring Boot / Java interviews.  
> Each answer is structured using the **STAR format** (Situation, Task, Action, Result) where applicable.  
> Keywords that interviewers listen for are marked in **bold**.

---

## Table of Contents

**Project & Architecture**
1. [Walk me through your project](#q1-walk-me-through-your-project)
2. [Explain the architecture and layers](#q2-explain-the-architecture-and-layers)
3. [Why did you separate Entity from DTO?](#q3-why-did-you-separate-entity-from-dto)
4. [Why use an interface for the service layer?](#q4-why-use-an-interface-for-the-service-layer)

**Spring MVC**
5. [How does a request flow through your Spring MVC app?](#q5-how-does-a-request-flow-through-your-spring-mvc-app)
6. [What is the PRG pattern and why did you use it?](#q6-what-is-the-prg-pattern-and-why-did-you-use-it)
7. [How do you handle form validation?](#q7-how-do-you-handle-form-validation)
8. [What is @ControllerAdvice and how do you use it?](#q8-what-is-controlleradvice-and-how-do-you-use-it)

**Spring Security**
9. [How does Spring Security work in your application?](#q9-how-does-spring-security-work-in-your-application)
10. [What is CSRF and how do you handle it?](#q10-what-is-csrf-and-how-do-you-handle-it)
11. [Explain hasRole() vs hasAuthority()](#q11-explain-hasrole-vs-hasauthority)
12. [How did you implement authentication?](#q12-how-did-you-implement-authentication)

**JPA / Hibernate**
13. [What is optimistic locking and how did you implement it?](#q13-what-is-optimistic-locking-and-how-did-you-implement-it)
14. [What are the JPA entity lifecycle states?](#q14-what-are-the-jpa-entity-lifecycle-states)
15. [How do audit fields work in your entities?](#q15-how-do-audit-fields-work-in-your-entities)
16. [What is the N+1 query problem?](#q16-what-is-the-n1-query-problem)

**Design Decisions & Best Practices**
17. [Why constructor injection over field injection?](#q17-why-constructor-injection-over-field-injection)
18. [Why BCrypt for passwords?](#q18-why-bcrypt-for-passwords)
19. [What design patterns do you see in Spring Boot?](#q19-what-design-patterns-do-you-see-in-spring-boot)
20. [What is @Transactional and when should you use it?](#q20-what-is-transactional-and-when-should-you-use-it)

**Scalability & Production Readiness**
21. [How would you make this application production-ready?](#q21-how-would-you-make-this-application-production-ready)
22. [How would you add pagination?](#q22-how-would-you-add-pagination)
23. [What would you change if you rebuilt this project?](#q23-what-would-you-change-if-you-rebuilt-this-project)

**Behavioral / Challenge Questions**
24. [Tell me about a challenging bug you encountered](#q24-tell-me-about-a-challenging-bug-you-encountered)
25. [Tell me about a design decision you had to make](#q25-tell-me-about-a-design-decision-you-had-to-make)

---

## Q1: Walk me through your project

> **Best Answer:**

"EduPro is a **Spring Boot** campus course management system built with a **layered MVC architecture**. It supports **role-based access control** using **Spring Security** -- administrators can create, edit, and delete courses, while students and anonymous users can only view them.

The tech stack is **Spring Boot 3**, **Spring MVC** with JSP views, **Spring Data JPA** with Hibernate for persistence, **Spring Security** for authentication and authorization, and **Oracle/H2** as the database. I use **Lombok** to reduce boilerplate and **Bean Validation** (`jakarta.validation`) for input validation.

Key features include: **CRUD operations** with validation, **form reuse** (single JSP for create and edit), **optimistic locking** with `@Version`, **audit fields** (`createdDate`, `updatedDate`), a **custom UserDetailsService** for database-backed authentication, **CSRF protection**, and **centralized exception handling** with `@ControllerAdvice`."

---

## Q2: Explain the architecture and layers

> **Best Answer:**

"I follow the standard **three-tier layered architecture**:

1. **Presentation Layer** (Controller + Views): Controllers handle HTTP requests, validate input, and return view names. JSP pages render the UI.

2. **Service Layer**: Contains **business logic** and **transaction management**. Defined as interfaces with implementations for **loose coupling**. This layer converts between DTOs and entities.

3. **Persistence Layer** (Repository): Spring Data JPA repositories handle database operations. I use **derived query methods** like `findByTitle()` and `existsByEmail()`.

4. **Domain Layer** (Model): Contains JPA entities (`CourseEntity`, `User`) and DTOs (`CourseVO`). Entities are never exposed directly to the view layer -- DTOs are used instead.

Additionally, I have a **cross-cutting concerns layer**: `@ControllerAdvice` for global exception handling, Spring Security for authentication/authorization, and `@Transactional` for transaction management.

Data flows as: **Browser --> Controller --> Service --> Repository --> Database**, and each layer only talks to the one directly below it."

---

## Q3: Why did you separate Entity from DTO?

> **Best Answer:**

"I separate them for three reasons:

1. **Security**: The entity has fields like `@Version`, `createdDate`, and `updatedBy` that the user should never see or modify. The DTO only exposes user-editable fields.

2. **Validation scope**: The DTO (`CourseVO`) has form-specific validation annotations (`@NotBlank`, `@Size`). The entity has JPA annotations (`@Column`, `@Table`). Mixing them violates the **Single Responsibility Principle**.

3. **Decoupling**: If I change the database schema, I only modify the entity. If I change the form, I only modify the DTO. They evolve independently.

For conversion, I use `BeanUtils.copyProperties()` with field exclusion to map between them. For **create**, I copy DTO to a new entity. For **update**, I load the existing entity first (to preserve `@Version`), then copy only editable fields."

---

## Q4: Why use an interface for the service layer?

> **Best Answer:**

"There are four reasons:

1. **Abstraction and loose coupling**: Controllers depend on the `CourseService` interface, not the implementation. I can swap implementations without changing controllers.

2. **Testability**: In unit tests, I can mock the interface easily with Mockito.

3. **Spring proxy mechanism**: Spring AOP features like `@Transactional` work through **JDK dynamic proxies** (for interfaces) or **CGLIB proxies** (for classes). Interface-based proxying is the classic Spring pattern.

4. **Open/Closed Principle**: The system is open for extension (new implementations) but closed for modification (existing code doesn't change).

In this project, there's one implementation per interface. In larger systems, you might have `CourseServiceImpl` for production and `CourseServiceMockImpl` for testing or a different data source."

---

## Q5: How does a request flow through your Spring MVC app?

> **Best Answer:**

"When a user hits `/courses`:

1. **DispatcherServlet** receives the HTTP request (it's the **front controller**)
2. It consults **HandlerMapping** to find the right controller method
3. **Spring Security's FilterChain** intercepts first -- checks authentication and authorization
4. If authorized, the **controller method** executes (`CourseController.listCourses()`)
5. The controller calls the **service layer**, which calls the **repository**
6. Repository returns entities, service converts to DTOs, controller adds them to the **Model**
7. Controller returns a **logical view name** (`"course/course-list"`)
8. **ViewResolver** resolves it to `/WEB-INF/views/course/course-list.jsp`
9. The JSP is rendered with model data and sent as HTML response

Key Spring components involved: **DispatcherServlet**, **HandlerMapping**, **HandlerAdapter**, **ViewResolver**, and the **Security FilterChain**."

---

## Q6: What is the PRG pattern and why did you use it?

> **Best Answer:**

"**Post/Redirect/Get** is a web design pattern that prevents **duplicate form submissions**. Without it, if a user submits a form and hits refresh, the browser resubmits the POST -- creating duplicate records.

With PRG:
1. User submits POST
2. Server processes and responds with **HTTP 302 redirect**
3. Browser makes a **GET** request to the redirect URL
4. Refresh is now a safe GET

I use **`RedirectAttributes.addFlashAttribute()`** to pass success messages across the redirect. Flash attributes survive exactly one redirect and are then automatically cleaned up. This keeps URLs clean (no `?success=true` query params)."

---

## Q7: How do you handle form validation?

> **Best Answer:**

"I use a **multi-layered validation strategy**:

**Layer 1 -- Bean Validation**: Annotations like `@NotBlank`, `@Size`, `@Email` on the DTO fields. Triggered by `@Valid` on the controller parameter.

**Layer 2 -- Business validation**: Uniqueness checks (e.g., course title) using `BindingResult.rejectValue()`. I do this **after** `@Valid` but **before** `hasErrors()`, so all errors (validation + business) are collected and displayed together.

**Layer 3 -- Database constraints**: `unique = true`, `nullable = false` on `@Column`. This is the **last line of defense** against race conditions.

A critical rule I learned: **`BindingResult` must immediately follow the `@Valid` parameter**. Without it, Spring silently ignores validation failures. Also, for String fields, always use `@NotBlank` instead of `@NotNull` because HTML forms send empty strings, not null."

---

## Q8: What is @ControllerAdvice and how do you use it?

> **Best Answer:**

"`@ControllerAdvice` is a **centralized exception handling mechanism** that applies across all controllers. It's an implementation of the **cross-cutting concerns** pattern.

I have a `GlobalExceptionHandler` class annotated with `@ControllerAdvice` that contains `@ExceptionHandler` methods:

- `CourseNotFoundException` --> returns HTTP 404 with an error page
- Generic `Exception` --> returns HTTP 500 with a friendly error message

Without `@ControllerAdvice`, I'd have to add try-catch blocks in every controller method. With it, exception handling is **DRY** and **consistent** across the entire application. The `@ResponseStatus` annotation on each handler sets the correct HTTP status code."

---

## Q9: How does Spring Security work in your application?

> **Best Answer:**

"Spring Security works through a **SecurityFilterChain** -- a series of servlet filters that intercept every HTTP request before it reaches controllers.

My configuration:
- **Authentication**: Database-backed. I have a custom `UserDetailsService` that loads users from JPA repository. Spring Security calls `loadUserByUsername()` during form login.
- **Authorization**: URL-based using `requestMatchers()`. `/courses` is public. CRUD operations (`/courses/add`, `/courses/edit/**`, `/courses/delete/**`) require `ADMIN` role.
- **Password encoding**: BCrypt via `PasswordEncoder` bean.
- **CSRF protection**: Enabled by default. Every POST form includes a CSRF token.
- **Form login**: Custom login page at `/login` with plain HTML form (not Spring's `<form:form>`, which requires a model attribute).
- **Logout**: Custom handler that sets a flash message in the session and redirects to home.

I initially used **in-memory authentication** for prototyping, then migrated to **database-backed auth** with a registration flow."

---

## Q10: What is CSRF and how do you handle it?

> **Best Answer:**

"**Cross-Site Request Forgery** is an attack where a malicious site tricks a user's browser into making requests to your app using the user's existing session cookies.

Example: If a user is logged into my app and visits a malicious site, that site could have a hidden form that POSTs to `/courses/delete/1` -- and the browser would include the session cookie, making it look legitimate.

**Spring Security prevents this** by generating a unique **CSRF token per session**. Every state-changing request (POST, PUT, DELETE) must include this token. If it's missing or invalid, Spring returns **403 Forbidden**.

In JSP, I include it as a hidden field: `<input type='hidden' name='${_csrf.parameterName}' value='${_csrf.token}'/>`. GET requests are exempt because they should be **idempotent** (read-only)."

---

## Q11: Explain hasRole() vs hasAuthority()

> **Best Answer:**

"Both check user permissions, but they differ in how they match:

- **`hasRole("ADMIN")`** automatically prepends `ROLE_` -- so it checks for the authority `ROLE_ADMIN`
- **`hasAuthority("ADMIN")`** does an **exact match** -- checks for the authority `ADMIN`

In my application, `User.getAuthorities()` returns `ROLE_ADMIN` (with prefix). So I use `hasRole("ADMIN")` in my security config.

The convention is: use `hasRole()` when your authorities follow the `ROLE_` prefix convention (which is Spring's default). Use `hasAuthority()` when you have fine-grained permissions like `READ_COURSES`, `WRITE_COURSES`."

---

## Q12: How did you implement authentication?

> **Best Answer:**

"I implemented **form-based authentication** backed by a database:

1. **User entity** implements `UserDetails` -- it provides `getUsername()`, `getPassword()`, `getAuthorities()`, and account status methods.

2. **Custom `UserDetailsService`** -- loads user by username from `UserRepository`. Returns the `User` entity (which IS a `UserDetails`). If not found, throws `UsernameNotFoundException`.

3. **Password encoding** -- `BCryptPasswordEncoder` bean. Passwords are hashed on registration. Spring Security automatically calls `matches()` during login.

4. **Security config** -- `formLogin()` with a custom login page. Spring Security handles the actual authentication flow (form submission, credential matching, session creation).

5. **Registration** -- Separate flow via `UserController`. User self-registers with a role (currently unrestricted -- in production, I'd default to STUDENT).

Spring auto-detects the `UserDetailsService` bean. I didn't need to wire it explicitly."

---

## Q13: What is optimistic locking and how did you implement it?

> **Best Answer:**

"**Optimistic locking** is a concurrency control strategy that assumes conflicts are rare. Instead of locking database rows (pessimistic), it detects conflicts at commit time.

I implement it with **`@Version`** on the entity:

```java
@Version
private Integer version;
```

Hibernate includes the version in every UPDATE:
```sql
UPDATE courses SET title = ?, version = 4 WHERE id = 100040 AND version = 3
```

If another user already changed the record (version is now 4), the WHERE clause matches 0 rows. Hibernate throws **`OptimisticLockException`**, and the second user's update is rejected.

The key lesson I learned: **never expose `@Version` in the DTO**. When updating, I load the managed entity from the database (which has the correct version), then copy editable fields onto it. Creating a new entity with `version = null` causes a `Detached entity with uninitialized version` error."

---

## Q14: What are the JPA entity lifecycle states?

> **Best Answer:**

"JPA entities have four states:

| State | In Context? | In DB? | Description |
|-------|------------|--------|-------------|
| **New/Transient** | No | No | Just created with `new`. Not yet persisted. |
| **Managed/Persistent** | Yes | Yes | Attached to a persistence context. Changes are auto-tracked (dirty checking). |
| **Detached** | No | Yes | Was managed, but persistence context closed (e.g., after transaction ends). |
| **Removed** | Yes | Pending delete | Scheduled for deletion. |

In my project, I hit an issue where I created a new entity with an existing ID and null version. Hibernate classified it as **detached with uninitialized version** and refused to save. The fix was to load the managed entity first via `findById()`, preserving its version."

---

## Q15: How do audit fields work in your entities?

> **Best Answer:**

"I use Hibernate's **`@CreationTimestamp`** and **`@UpdateTimestamp`** for automatic timestamping:

- `createdDate` is annotated with `@CreationTimestamp` and `@Column(updatable = false)` -- set once on INSERT, never overwritten.
- `updatedDate` has `@UpdateTimestamp` and `@Column(insertable = false)` -- null on first INSERT, updated on every modification.
- `createdBy` and `updatedBy` are manual strings (could be automated with Spring Data's `@CreatedBy`/`@LastModifiedBy` with `AuditorAware`).

The `insertable` and `updatable` attributes on `@Column` control whether Hibernate includes the column in INSERT/UPDATE SQL statements."

---

## Q16: What is the N+1 query problem?

> **Best Answer:**

"The **N+1 problem** occurs when fetching a list of N entities triggers N additional queries to load related entities.

Example: If I had a `Course` entity with a lazy-loaded `@ManyToOne Instructor` field:
- Query 1: `SELECT * FROM courses` (returns 100 courses)
- Queries 2-101: `SELECT * FROM users WHERE id = ?` (one per course to load instructor)

That's 101 queries instead of 1 or 2.

**Solutions:**
- **`@EntityGraph`** or **`JOIN FETCH`** in JPQL to eagerly load in one query
- **Batch fetching** (`@BatchSize`)
- **DTO projection** -- query only the fields you need, skipping relationships entirely

In my current project, there are no entity relationships yet, so I haven't hit this. But it's one of the first things I'd watch for when adding enrollments."

---

## Q17: Why constructor injection over field injection?

> **Best Answer:**

"I prefer **constructor injection** for four reasons:

1. **Immutability**: Dependencies can be declared `final`. Once set, they can't change.
2. **Testability**: I can pass mocks directly in the constructor during unit tests. No reflection, no Spring context needed.
3. **Fail-fast**: If a dependency is missing, the application fails at **startup** (compilation error). With field injection, you get a `NullPointerException` at **runtime**.
4. **Spring's official recommendation**: Since Spring 4.3, constructor injection is the recommended approach.

I use Lombok's **`@RequiredArgsConstructor`** to avoid writing the constructor manually. It generates a constructor for all `final` fields, and Spring auto-injects them."

---

## Q18: Why BCrypt for passwords?

> **Best Answer:**

"BCrypt is the industry standard for password hashing because:

1. **One-way**: You can't reverse a hash to get the original password.
2. **Per-password salt**: Even identical passwords produce different hashes, preventing **rainbow table attacks**.
3. **Work factor**: BCrypt has a configurable cost factor. As hardware gets faster, you increase the cost to keep brute-force attacks slow.
4. **Timing-safe comparison**: `BCryptPasswordEncoder.matches()` is resistant to **timing attacks**.

I use `BCryptPasswordEncoder` which is Spring Security's default. The hash includes the algorithm identifier, cost factor, and salt -- all stored in one string. I encode on registration and Spring Security verifies during login automatically."

---

## Q19: What design patterns do you see in Spring Boot?

> **Best Answer:**

"Several patterns are present:

| Pattern | Where |
|---------|-------|
| **Front Controller** | `DispatcherServlet` -- single entry point for all requests |
| **MVC** | Controller-Service-View separation |
| **Dependency Injection / IoC** | `@Autowired`, constructor injection |
| **Proxy** | `@Transactional`, Spring Security filters (AOP proxies) |
| **Template Method** | `JpaRepository` provides default implementations |
| **Repository** | `CourseRepository`, `UserRepository` abstract data access |
| **Strategy** | `PasswordEncoder` interface with `BCryptPasswordEncoder` implementation |
| **Builder** | Lombok `@Builder` on entities |
| **Chain of Responsibility** | Security filter chain |
| **Factory** | `SecurityFilterChain` bean creation |
| **Observer** | Spring events, `@EventListener` |
| **Singleton** | All Spring beans are singleton by default |

The one I used most intentionally is the **Service-Repository pattern** -- service interfaces with implementations for loose coupling and testability."

---

## Q20: What is @Transactional and when should you use it?

> **Best Answer:**

"**`@Transactional`** wraps a method in a database transaction. If the method completes normally, the transaction is **committed**. If any exception is thrown, it's **rolled back** (by default, for unchecked exceptions).

**When to use it:**
- Any service method that **writes** to the database (create, update, delete)
- Any method that performs **multiple database operations** that should be atomic

**Best practice:**
- Set `@Transactional(readOnly = true)` at the **class level** for services (optimizes read queries)
- Override with `@Transactional` (without readOnly) on specific **write methods**

**`readOnly = true` benefits:**
- Hibernate skips **dirty checking** (performance)
- Database driver may optimize for reads
- Acts as self-documenting code
- Prevents accidental writes

Without `@Transactional`, each repository call runs in its own mini-transaction. If you save entity A and then saving entity B fails, entity A is already committed -- leaving inconsistent data."

---

## Q21: How would you make this application production-ready?

> **Best Answer:**

"Several improvements:

**Database:**
- Replace `ddl-auto=update` with **Flyway** or **Liquibase** for versioned migrations
- Configure **HikariCP** connection pool settings
- Add database **indexes** on frequently queried columns

**API:**
- Add a **REST API layer** alongside (or replacing) JSP views
- Add **pagination** using Spring Data's `Pageable`
- Add response **caching** with `@Cacheable`

**Security:**
- Restrict role selection during registration (default to STUDENT)
- Add **rate limiting** on login attempts
- Configure **CORS** if adding a frontend
- Use **environment variables** for credentials (not hardcoded)

**Observability:**
- Add **Spring Boot Actuator** for health checks and metrics
- Structured **logging** with correlation IDs
- Integrate with monitoring (Prometheus/Grafana)

**Testing:**
- Unit tests for services (Mockito)
- Controller tests with MockMvc
- Repository tests with `@DataJpaTest`
- Integration tests

**Deployment:**
- **Dockerize** the application
- Use **Spring profiles** (dev, staging, prod)
- Externalize configuration with environment variables or Spring Cloud Config"

---

## Q22: How would you add pagination?

> **Best Answer:**

"Spring Data makes it simple:

**Repository** -- already supports it through `JpaRepository`:
```java
Page<CourseEntity> findAll(Pageable pageable);
```

**Service:**
```java
public Page<CourseVO> getCourses(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("title"));
    return courseRepository.findAll(pageable).map(this::convertEntityToVO);
}
```

**Controller:**
```java
@GetMapping
public String listCourses(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          Model model) {
    Page<CourseVO> coursePage = courseService.getCourses(page, size);
    model.addAttribute("courses", coursePage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", coursePage.getTotalPages());
    return "course/course-list";
}
```

`Page<T>` gives you `getContent()`, `getTotalPages()`, `getTotalElements()`, `hasNext()`, `hasPrevious()` -- everything needed for pagination UI."

---

## Q23: What would you change if you rebuilt this project?

> **Best Answer:**

"Five key changes:

1. **Thymeleaf or REST API instead of JSP**: JSP is legacy. Thymeleaf integrates better with Spring Boot, or I'd build a REST API with a React frontend.

2. **Separate User entity from UserDetails**: My `User` entity implements `UserDetails`, mixing persistence and security concerns. I'd create a `UserPrincipal` wrapper class instead.

3. **Add proper tests from day one**: Unit tests for services with Mockito, controller tests with `MockMvc`, repository tests with `@DataJpaTest`. Tests are the first thing interviewers look at.

4. **Use Flyway for database migrations**: `ddl-auto=update` works for development but is dangerous in production. Flyway provides versioned, repeatable migrations.

5. **Proper DTO separation for User**: I use the JPA entity directly in controllers for User (but have a DTO for Course). Inconsistent -- I'd create `UserRegistrationDTO` and `UserResponseDTO`."

---

## Q24: Tell me about a challenging bug you encountered

> **Best Answer (STAR format):**

"**Situation:** I was implementing the update feature for courses. The same form is reused for create and edit.

**Task:** When updating a course, Hibernate threw `Detached entity with generated id has an uninitialized version value 'null'`.

**Action:** I investigated and found the root cause: for updates, I was creating a **new** `CourseEntity` and copying fields from the DTO. This new entity had the correct ID but `@Version` was `null`. Hibernate saw an entity with an existing ID but no version history -- it classified it as a **detached entity with no version**, which violates optimistic locking safety.

The fix was to **load the existing entity from the database first** using `findById()`, which gives a **managed entity** with the correct version. Then I copied only user-editable fields onto it using `BeanUtils.copyProperties()` with field exclusion (skipping `id`, `version`, `createdDate`).

**Result:** The update works correctly, optimistic locking is preserved, and I learned the critical distinction between JPA entity states: **New** (null ID, null version = OK), **Managed** (non-null ID, non-null version = OK), **Detached with null version = ERROR**."

---

## Q25: Tell me about a design decision you had to make

> **Best Answer (STAR format):**

"**Situation:** I needed to validate that a course title is unique before saving, and also handle cases where both username and email might be duplicated during user registration.

**Task:** Initially I used a `throw` exception approach for uniqueness checks. But this only showed the first error -- if both username and email were duplicates, the user would fix one, submit, then see the second error. Poor UX.

**Action:** I switched to **`BindingResult.rejectValue()`** in the controller layer. Instead of throwing exceptions, I collected all errors into the `BindingResult`:

```java
if (userService.existsByEmail(user.getEmail())) {
    result.rejectValue("email", null, "Email already exists");
}
if (userService.existsByUsername(user.getUsername())) {
    result.rejectValue("username", null, "Username already exists");
}
```

I placed these checks **after** `@Valid` runs but **before** `hasErrors()`, so Bean Validation errors and uniqueness errors are all displayed together.

**Result:** Users see all errors in one submission. No unnecessary exceptions in the normal flow. Error messages appear next to the correct form field. This approach also avoids the `NoSuchMessageException` issue I had when using error codes without a `messages.properties` file."

---

## Rapid-Fire Answers (30-Second Responses)

### What's the difference between @Controller and @RestController?

> "`@RestController` combines `@Controller` + `@ResponseBody`. Every method returns data directly as JSON/XML instead of a view name. I use `@Controller` because I return JSP view names."

### What's the difference between @RequestParam and @PathVariable?

> "`@PathVariable` extracts from the URL path (`/courses/5` --> `5`). `@RequestParam` extracts from query parameters (`/courses?page=2` --> `2`). I use `@PathVariable` for resource IDs and `@RequestParam` for filters/pagination."

### What's the difference between PUT and PATCH?

> "`PUT` replaces the entire resource. `PATCH` partially updates specific fields. In my JSP-based app I use `POST` for both create and update (HTML forms only support GET/POST), but in a REST API I'd use `POST` for create, `PUT` for full update, and `PATCH` for partial update."

### What happens if two users update the same course at the same time?

> "I use `@Version` for **optimistic locking**. Both users load version 3. User A saves first -- version becomes 4. When User B tries to save with version 3, Hibernate's `WHERE version = 3` matches 0 rows and throws `OptimisticLockException`. The second user must reload and retry."

### How does Spring Boot auto-configuration work?

> "Spring Boot scans the classpath for libraries and auto-configures beans. For example, adding `spring-boot-starter-data-jpa` auto-configures a `DataSource`, `EntityManagerFactory`, and `TransactionManager`. It uses `@Conditional` annotations internally -- beans are only created if certain conditions are met (class on classpath, property set, no user-defined bean, etc.)."

### What is Spring Boot Starter?

> "A **starter** is a pre-packaged set of dependencies. `spring-boot-starter-web` includes Spring MVC, embedded Tomcat, and Jackson. `spring-boot-starter-data-jpa` includes Hibernate, Spring Data JPA, and HikariCP. Starters eliminate manual dependency management."

### What is the difference between `findById()` and `getById()`?

> "`findById()` returns `Optional<T>` and immediately hits the database. `getReferenceById()` (formerly `getById()`) returns a **lazy proxy** -- no database hit until you access a field. Use `findById()` when you need the data immediately; `getReferenceById()` when you just need a reference for setting relationships."

### What is Spring Data JPA's derived query method?

> "Spring Data generates SQL from method names. `findByTitle(String title)` becomes `SELECT * FROM courses WHERE title = ?`. `existsByEmail(String email)` becomes `SELECT COUNT(*) > 0 WHERE email = ?`. No implementation needed -- Spring generates it at runtime from the method signature."
