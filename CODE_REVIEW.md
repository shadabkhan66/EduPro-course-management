# EduPro - Comprehensive Code Review

> **Purpose:** Honest review for learning, interview prep, and portfolio improvement.  
> **Reviewer note:** This project shows solid effort and many good instincts. The issues below are meant to help you grow -- every professional developer goes through this process.

---

## TABLE OF CONTENTS

1. [What You Did Well](#1-what-you-did-well)
2. [Naming Convention Issues](#2-naming-convention-issues)
3. [Design & Architecture Problems](#3-design--architecture-problems)
4. [Bugs & Potential Crashes](#4-bugs--potential-crashes)
5. [Security Concerns](#5-security-concerns)
6. [Scalability Limitations](#6-scalability-limitations)
7. [Testing Gaps](#7-testing-gaps)
8. [Convention Violations](#8-convention-violations)
9. [JSP / View Layer Issues](#9-jsp--view-layer-issues)
10. [Summary: What To Fix First](#10-summary-what-to-fix-first)

---

## 1. What You Did Well

Before the issues -- credit where it's due:

| Good Practice | Where |
|---|---|
| Service interface + implementation pattern | `CourseService` / `CourseServiceImpl` |
| Separation of Entity vs DTO (for Course) | `CourseEntity` vs `CourseVO` |
| `@Valid` for input validation | Controllers |
| CSRF protection in forms | JSP forms |
| Spring Security with role-based access | `AuthorizeUrlsSecurityConfig` |
| `@Version` for optimistic locking | `CourseEntity`, `User` |
| Audit fields (`createdDate`, `updatedDate`) | Both entities |
| Custom exception classes | `CourseNotFoundException`, etc. |
| Flash attributes for PRG pattern | `RedirectAttributes` in controllers |
| Builder pattern on entities | `CourseEntity`, `User` |
| `.gitignore` excludes secrets properly | `application.properties` is gitignored |
| `application.properties.example` pattern | Helps new developers set up |
| `BigDecimal` for money (`fees`) | Correct -- never use `double` for currency |

---

## 2. Naming Convention Issues

### 2.1 Package `service/imp` should be `service/impl`

- **Current:** `com.eduproject.service.imp`
- **Should be:** `com.eduproject.service.impl`
- **Why:** `impl` is the universal Java convention. Every Spring project, tutorial, and enterprise codebase uses `impl`. An interviewer will notice this immediately.

### 2.2 `CourseVO` should be `CourseDTO` (or `CourseRequest`/`CourseResponse`)

- **Current:** `CourseVO` (Value Object)
- **Should be:** `CourseDTO` or `CourseRequest`
- **Why:** A Value Object (VO) is **immutable** by definition (no setters, equals/hashCode based on all fields). Your class has `@Setter` and is mutable -- it's a DTO (Data Transfer Object). In interviews, calling a mutable DTO a "VO" will raise a red flag that you don't know the DDD distinction.

### 2.3 `UserNameAlreadyExists` -- two problems

- **Current:** `UserNameAlreadyExists`
- **Should be:** `UsernameAlreadyExistsException`
- **Why:** (a) Missing the `Exception` suffix -- Java convention for all exception classes. (b) "Username" is one word, not two (matching `User.username` field).

### 2.4 `InsertDataInOracleDB` is misleading

- **Current:** `InsertDataInOracleDB`
- **Should be:** `DataSeeder` or `DataInitializer`
- **Why:** The class uses Spring Data JPA -- it works with H2, PostgreSQL, MySQL, anything. Hardcoding "OracleDB" in the name is incorrect and will confuse readers. Also, class names should not describe implementation details.

### 2.5 `encodePass()` bean method

- **Current:** `@Bean PasswordEncoder encodePass()`
- **Should be:** `@Bean PasswordEncoder passwordEncoder()`
- **Why:** Bean method names should describe **what they return**, not what they do. Spring registers this as a bean named "encodePass" which is non-standard.

### 2.6 Typo: `getNumberOfAvailabelCourses`

- **Current:** `getNumberOfAvailabelCourses` (typo: "Availabel")
- **Should be:** `getAvailableCourseCount()` or `countCourses()`
- **Why:** Typos in method names are visible in your public Git repo. Keep names concise too -- `getNumberOfAvailableCourses` is verbose.

### 2.7 `UserDetailService` -- missing "s"

- **Current:** `UserDetailService`
- **Should be:** `CustomUserDetailsService`
- **Why:** Spring's interface is `UserDetailsService` (plural "Details"). Your class name drops the "s", which looks like a typo. Adding "Custom" prefix is the standard convention to differentiate from Spring's own.

### 2.8 `doesUniqueEmailExists` / `doesUniqueUsernameExists`

- **Current:** `doesUniqueEmailExists(String email)`
- **Should be:** `isEmailTaken(String email)` or simply `existsByEmail(String email)`
- **Why:** "doesUniqueEmailExists" is grammatically confusing -- does the unique email exist? Boolean methods should start with `is`, `has`, `exists`, or `can`.

### 2.9 `AuthorizeUrlsSecurityConfig`

- **Current:** `AuthorizeUrlsSecurityConfig`
- **Should be:** `SecurityConfig`
- **Why:** Overly specific. When you add CORS config, method security, or OAuth2 later, the name becomes misleading. Keep it simple.

### 2.10 Test class name

- **Current:** `SbMvc06SimpleCrudEduProApplicationTests`
- **Should be:** `EduProApplicationTests`
- **Why:** "SbMvc06" looks like leftover tutorial naming and makes the project look like a copy-paste job.

---

## 3. Design & Architecture Problems

### 3.1 CRITICAL: `@Autowired` + `@RequiredArgsConstructor` is redundant

```java
// WRONG: You're doing this in multiple classes
@RequiredArgsConstructor
public class InsertDataInOracleDB implements CommandLineRunner {
    @Autowired
    private final CourseRepository courseRepository;
    @Autowired
    private final UserRepository userRepository;
}
```

```java
// CORRECT: Pick ONE approach. Constructor injection via Lombok is preferred:
@RequiredArgsConstructor
public class InsertDataInOracleDB implements CommandLineRunner {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
}
```

**Why:** `@RequiredArgsConstructor` generates a constructor with all `final` fields, and Spring auto-injects via constructor. Adding `@Autowired` is redundant and shows misunderstanding of how DI works. **An interviewer will ask about this.**

### 3.2 CRITICAL: `User` entity implements `UserDetails` directly

```java
// CURRENT: Entity + Security concern mixed
@Entity
public class User implements UserDetails { ... }
```

```java
// BETTER: Separate concerns
@Entity
public class User { /* pure JPA entity */ }

// In a separate file:
public class UserPrincipal implements UserDetails {
    private final User user;
    // delegate methods to user
}
```

**Why:** This violates the **Single Responsibility Principle**. Your JPA entity now depends on Spring Security interfaces. If you ever:
- Need a REST API that serializes User
- Switch to OAuth2/JWT
- Need different UserDetails logic

...you'll have to modify the entity. Entity should only represent the database table.

### 3.3 No DTO for User -- inconsistent with Course pattern

- `Course` has: `CourseEntity` (entity) + `CourseVO` (DTO) -- **good**
- `User` has: `User` (entity used directly in controller) -- **bad**

**Should have:** `UserRegistrationDTO` for the registration form, keeping the `User` entity internal to the service/repository layer. Right now, validation annotations for the form (`@NotBlank`, `@Size`) are on the JPA entity, which couples your form validation to your database schema.

### 3.4 `registerNewCourse()` handles both create AND update

```java
// CURRENT: Confusing dual-purpose method
public String registerNewCourse(CourseVO courseVo) {
    if (courseVo.getId() != null) {
        return this.courseRepository.save(convertVOToEntityForUpdate(courseVo)).getTitle();
    }
    return this.courseRepository.save(convertVOToEntity(courseVo)).getTitle();
}
```

**Why this is bad:** The method is named "register**New**Course" but it also updates existing courses. This violates the **Single Responsibility Principle** and is confusing to read. Have separate `createCourse()` and `updateCourse()` methods.

### 3.5 `updateCourseDetails()` makes duplicate DB calls

```java
// CURRENT: TWO findById calls for the same entity
public void updateCourseDetails(CourseVO courseVo) {
    // Call 1: just to check existence
    this.courseRepository.findById(courseVo.getId())
        .orElseThrow(() -> new CourseNotFoundException(...));
    // Call 2: inside convertVOToEntityForUpdate, findById AGAIN
    this.courseRepository.save(convertVOToEntityForUpdate(courseVo));
}
```

**Should be:** One `findById`, reuse the result.

### 3.6 No `@Transactional` on service methods

None of your service methods have `@Transactional`. Any method that writes to the database should be wrapped in a transaction.

```java
@Service
@Transactional(readOnly = true) // default for the class
public class CourseServiceImpl implements CourseService {

    @Transactional // override for write methods
    public String registerNewCourse(CourseVO courseVo) { ... }
}
```

**Why:** Without this, each `repository.save()` and `repository.findById()` runs in its own transaction. If you have multiple DB operations in one method and the second fails, the first is already committed -- leaving inconsistent data.

### 3.7 `GlobalExceptionHandler` is empty

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    // completely empty
}
```

**Why this matters:** You created custom exceptions (`CourseNotFoundException`, `EmailAlreadyExistsException`) but never handle them centrally. Right now, `CourseNotFoundException` would return a 500 error. Add proper `@ExceptionHandler` methods.

### 3.8 `AuthController` is an empty shell

```java
@Controller
public class AuthController {
    public AuthController() {
        log.info("AuthController instance created");
    }
}
```

This has no endpoints and serves no purpose. It should either be deleted or contain the `/login` and `/logout` endpoints currently in `HomeController`.

### 3.9 `model` package mixes Entities, DTOs, and Enums

- **Current:** Everything in `com.eduproject.model`
- **Should be:**
  - `com.eduproject.entity` -- `CourseEntity`, `User`
  - `com.eduproject.dto` -- `CourseDTO`, `UserRegistrationDTO`
  - `com.eduproject.enums` -- `Role`

**Why:** As the project grows, having 20+ classes in one package becomes unmanageable. Clear separation also signals to interviewers that you understand layered architecture.

### 3.10 Data seeder runs on EVERY startup with no idempotency

```java
// CURRENT: Always inserts, will crash on second run due to unique constraints
@Override
public void run(String... args) throws Exception {
    this.courseRepository.saveAll(List.of(...));
    this.userRepository.saveAll(List.of(...));
}
```

```java
// BETTER: Check first, or use a profile
@Override
public void run(String... args) {
    if (courseRepository.count() == 0) {
        // seed data
    }
}
```

Or use `@Profile("dev")` so it only runs in development.

### 3.11 Mixed ID generation strategies (SEQUENCE vs IDENTITY)

- `CourseEntity` uses `@GeneratedValue(strategy = GenerationType.SEQUENCE)`
- `User` uses `@GeneratedValue(strategy = GenerationType.IDENTITY)`

**Why this matters:** These have different performance characteristics and database compatibility. SEQUENCE allows batch inserts (better performance). IDENTITY forces one-by-one inserts. Pick one strategy and be consistent, or document why they differ.

### 3.12 No relationship between User and Course

In a "course management system," you'd expect:
- A `User` (STUDENT) can enroll in many `Courses`
- A `Course` has one instructor (`User` with TEACHER role)

There are no `@ManyToMany`, `@OneToMany`, or `@ManyToOne` relationships. The domain model is incomplete. For interview purposes, even if you don't implement enrollment, **mention it in your README** as a planned feature.

---

## 4. Bugs & Potential Crashes

### 4.1 CRASH: `UserDetailService.loadUserByUsername()` calls `.get()` without check

```java
// CURRENT: Will throw NoSuchElementException (not UsernameNotFoundException)
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return this.userRepository.findByUsername(username).get(); // DANGEROUS
}
```

```java
// CORRECT:
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return this.userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
}
```

**Why:** `.get()` on an empty `Optional` throws `NoSuchElementException`, which Spring Security doesn't handle gracefully. The contract requires throwing `UsernameNotFoundException`.

### 4.2 BUG: Exception classes don't pass message to super

```java
// CURRENT:
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String emailAlreadyExists) {
        // message is LOST -- never passed to super()
    }
}

// CORRECT:
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
```

Same issue exists in `UserNameAlreadyExists`. The error message is silently discarded.

### 4.3 BUG: `CourseVO` has `@DecimalMin` on `durationInHours`

```java
// CURRENT: Two conflicting annotations on durationInHours
@Min(value = 1, message = "Duration must be at least 1 hour")
@DecimalMin(value = "0.0", message = "Fees must be non-negative")  // <-- This belongs on fees!
private Integer durationInHours;
```

The `@DecimalMin` annotation with the "Fees" message is placed on the wrong field. It should be on `fees`.

### 4.4 Unused exceptions: `EmailAlreadyExistsException` and `UserNameAlreadyExists`

These are declared but **never thrown** anywhere. In `UserController`, uniqueness is checked via `BindingResult.rejectValue()` instead. Either use the exceptions or delete them.

---

## 5. Security Concerns

### 5.1 CRITICAL: Password logged in plain text

```java
// UserServiceImpl.java
public String registerUser(User user) {
    log.info("Registering user: {}", user);  // Logs ENTIRE user object
    user.setPassword(this.encoder.encode(user.getPassword()));
    ...
}
```

`User` has `@ToString` (Lombok), which includes the `password` field. This means the raw password is printed to logs BEFORE encoding. 

**Fix:** Add `@ToString.Exclude` on the password field, or use `@ToString(exclude = "password")`.

### 5.2 Anyone can register as ADMIN

```java
// user-form.jsp: Role dropdown includes ALL roles
<c:forEach var="role" items="${roles}">
    <frm:option value="${role}" label="${role}"/>
</c:forEach>
```

A user can select "ADMIN" during self-registration. New users should default to STUDENT. Only existing ADMINs should assign roles.

### 5.3 Hardcoded credentials in data seeder

```java
User.builder().username("user").password(passwordEncoder.encode("user123"))...
```

Even for seed data, passwords like "user123" and "king123" in source code are a bad look. Use environment variables or a separate data SQL file.

### 5.4 No `@ResponseStatus` on `CourseNotFoundException`

When `CourseNotFoundException` is thrown and not caught, it results in HTTP 500. It should return 404:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CourseNotFoundException extends RuntimeException { ... }
```

---

## 6. Scalability Limitations

### 6.1 No pagination -- loads ALL records

```java
// CURRENT: Fetches every course in the database
public List<CourseVO> getAllTheAvailableCourses() {
    return this.courseRepository.findAll().stream()...
}
```

**Should be:**
```java
Page<CourseVO> getCourses(Pageable pageable);
```

With 10,000 courses, the current code will load them all into memory. Use Spring Data's `Pageable` + `Page<T>`.

### 6.2 No caching

Frequently accessed data (course list, course count) could be cached with `@Cacheable` from Spring Cache.

### 6.3 No database migration tool

Using `spring.jpa.hibernate.ddl-auto=update` is not production-safe. Hibernate can add columns but can't:
- Rename columns
- Drop columns safely  
- Migrate data

**Should use:** Flyway or Liquibase for versioned, repeatable migrations. This is a very common interview question.

### 6.4 No connection pool configuration

No HikariCP settings (max pool size, timeout, etc.) configured. Defaults may not suit production loads.

### 6.5 No API layer (REST)

The entire app is server-rendered JSP. For scalability, the industry standard is:
- **Backend:** REST API (JSON) with Spring Boot
- **Frontend:** React, Angular, or even Thymeleaf

JSP is largely considered legacy. For a portfolio project, having a REST API alongside (or instead of) JSP would be much more impressive.

---

## 7. Testing Gaps

### 7.1 Only one test exists, and it tests nothing

```java
@SpringBootTest
class SbMvc06SimpleCrudEduProApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

**For a portfolio project, you should have:**
- Unit tests for `CourseServiceImpl` (using Mockito)
- Unit tests for `UserServiceImpl`
- Controller tests with `@WebMvcTest` + `MockMvc`
- Repository tests with `@DataJpaTest`

Even 5-6 meaningful tests would dramatically improve the impression this project makes.

### 7.2 Test class name is from a tutorial

`SbMvc06SimpleCrudEduProApplicationTests` -- rename to `EduProApplicationTests`.

---

## 8. Convention Violations

### 8.1 Unused imports in `UserRepository`

```java
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
```

These are imported but never used. Clean up imports.

### 8.2 `TODO Auto-generated` left in code

```java
// AuthController.java
public AuthController() {
    // TODO Auto-generated constructor stub
    log.info("AuthController instance created");
}
```

IDE-generated TODO comments should be removed before committing. They signal incomplete work.

### 8.3 Commented-out code checked into Git

```java
// AuthorizeUrlsSecurityConfig.java
/*  @Bean
    UserDetailsService userStore(PasswordEncoder encoder) { ... }
*/
```

Dead code should be deleted, not commented out. Git history preserves it if needed.

### 8.4 Typo in log message

```java
// InsertDataInOracleDB.java
saveAllUsers.forEach(u -> log.info("Saved user with courese name: {} ", u.getUsername()));
//                                              ^^^^^^^ typo: "courese" should be "course"
//                                    Also, it's a USER, not a course
```

### 8.5 Missing `application.properties` in `src/main/resources`

The app has no `application.properties` or `application.yml` in `src/main/resources`. Configuration is only in root-level `application.properties.example`. You should have:
- `src/main/resources/application.properties` (with `${ENV_VAR}` placeholders) OR
- `src/main/resources/application.yml` with profile-based config (`application-dev.yml`, `application-prod.yml`)

### 8.6 README is one sentence

```markdown
# EduPro-course-management
take time to change view name all the place while refactoreing...
```

For a portfolio project, your README should include:
- Project description
- Tech stack
- How to run
- Screenshots
- Architecture overview
- API endpoints (if any)
- What you learned

The README is the **first thing** interviewers and recruiters see.

---

## 9. JSP / View Layer Issues

### 9.1 `user-form.jsp` has its own HTML structure

All other JSPs use `header.jsp` / `footer.jsp` fragments, but `user-form.jsp` has its own `<html>`, `<head>`, `<style>`, and `<body>` tags. This means it has completely different styling and no navigation header.

### 9.2 Inline CSS everywhere

All CSS is in `header.jsp` inline `<style>` block. Should be in a separate `.css` file under `/static/css/`.

### 9.3 JSP is legacy technology

JSP is rarely used in new projects (since ~2015). For a modern Spring Boot portfolio:
- **Minimum:** Use **Thymeleaf** (Spring Boot's default template engine)
- **Better:** Build a **REST API** + separate frontend (React/Angular/Vue)

If an interviewer asks "Why JSP?", having a clear answer ("I wanted to learn the traditional MVC approach before moving to modern alternatives") is acceptable. But you should know it's not current practice.

### 9.4 XSS vulnerability in JSPs

```jsp
<p>${logoutMessage}</p>
<p class="info">${message}</p>
```

Using `${}` without `<c:out>` or `fn:escapeXml()` is vulnerable to XSS. Use:
```jsp
<p><c:out value="${logoutMessage}"/></p>
```

---

## 10. Summary: What To Fix First

### Priority 1 -- Will crash / security risk
| # | Issue | File |
|---|---|---|
| 1 | `.get()` without check in `loadUserByUsername` | `UserDetailService.java` |
| 2 | Password logged in plain text (`@ToString` on User) | `User.java` |
| 3 | Exception classes don't pass message to `super()` | `EmailAlreadyExistsException.java`, `UserNameAlreadyExists.java` |
| 4 | Anyone can register as ADMIN | `user-form.jsp` |

### Priority 2 -- Interviewer will notice immediately
| # | Issue | Fix |
|---|---|---|
| 5 | `@Autowired` + `@RequiredArgsConstructor` together | Remove `@Autowired` |
| 6 | Package `imp` -> `impl` | Rename package |
| 7 | Test class name from tutorial | Rename to `EduProApplicationTests` |
| 8 | Empty `GlobalExceptionHandler` | Add `@ExceptionHandler` methods |
| 9 | Empty `AuthController` | Delete or add endpoints |
| 10 | No `@Transactional` anywhere | Add to service methods |
| 11 | README is basically empty | Write a proper README |

### Priority 3 -- Shows maturity
| # | Issue | Fix |
|---|---|---|
| 12 | Add 5-6 unit tests | Mockito + MockMvc |
| 13 | Add pagination | `Pageable` in repository/service |
| 14 | Separate User entity from UserDetails | Create `UserPrincipal` wrapper |
| 15 | Create proper DTOs for User | `UserRegistrationDTO` |
| 16 | Add Flyway/Liquibase migrations | Replace `ddl-auto=update` |
| 17 | Split `model` package | `entity`, `dto`, `enums` |
| 18 | Fix all naming issues | See Section 2 |

---

## Interview Questions This Project Might Trigger

If you show this project, be ready for:

1. "Why did you use `@Autowired` with `@RequiredArgsConstructor`?" -- Know that it's redundant
2. "What's the difference between VO and DTO?" -- Know that VO is immutable
3. "Why JSP instead of Thymeleaf or a REST API?" -- Have a thoughtful answer
4. "How would you add pagination?" -- Know Spring Data's `Pageable`
5. "Why `ddl-auto=update`? What would you use in production?" -- Say Flyway/Liquibase
6. "Where are your tests?" -- This is the #1 thing interviewers look for
7. "How do you handle transactions?" -- Know `@Transactional`
8. "What happens if two users update the same course?" -- You have `@Version` (good!), explain optimistic locking

---

> **Bottom line:** The foundation is solid -- proper layering, validation, security basics, and audit fields. The main gaps are: no tests, naming inconsistencies, a few bugs, and some design patterns that need refinement. Fix the Priority 1 items immediately, then work through Priority 2 and 3 systematically. This project can become a strong portfolio piece.
