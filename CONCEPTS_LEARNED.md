# Concepts Learned & Patterns Applied

> Everything I learned while building this project -- organized by topic, with interview-ready explanations.  
> Each section explains the **what**, **why**, and **when to use** so you can answer confidently.

---

## Table of Contents

1. [Post/Redirect/Get (PRG) Pattern](#1-postredirectget-prg-pattern)
2. [Redirect vs Forward vs Returning a View](#2-redirect-vs-forward-vs-returning-a-view)
3. [RedirectAttributes & Flash Attributes](#3-redirectattributes--flash-attributes)
4. [Bean Validation in Spring MVC](#4-bean-validation-in-spring-mvc)
5. [BindingResult & How Spring MVC Forms Work](#5-bindingresult--how-spring-mvc-forms-work)
6. [Uniqueness Validation (Database-Level Checks)](#6-uniqueness-validation-database-level-checks)
7. [Custom Bean Validation Annotation](#7-custom-bean-validation-annotation)
8. [Optimistic Locking with @Version](#8-optimistic-locking-with-version)
9. [Entity vs DTO Separation](#9-entity-vs-dto-separation)
10. [Spring Security Fundamentals](#10-spring-security-fundamentals)
11. [CSRF Protection](#11-csrf-protection)
12. [Spring Security JSP Taglib (`<sec:authorize>`)](#12-spring-security-jsp-taglib-secauthorize)
13. [Logout Configuration](#13-logout-configuration)
14. [Hibernate DDL Hints from Annotations](#14-hibernate-ddl-hints-from-annotations)
15. [JSP Form Tag Gotchas](#15-jsp-form-tag-gotchas)
16. [Enum Handling in Forms](#16-enum-handling-in-forms)
17. [Lombok @Builder on Constructor vs Class](#17-lombok-builder-on-constructor-vs-class)
18. [Spring Data JPA Query Method Naming](#18-spring-data-jpa-query-method-naming)
19. [Error Handling with @ControllerAdvice](#19-error-handling-with-controlleradvice)
20. [Custom Exception with HTTP Status](#20-custom-exception-with-http-status)
21. [@Transactional and readOnly](#21-transactional-and-readonly)
22. [HTTP DELETE Best Practice](#22-http-delete-best-practice)
23. [Two Types of "Not Found" Scenarios](#23-two-types-of-not-found-scenarios)
24. [@ModelAttribute at Method Level (Common Attributes)](#24-modelattribute-at-method-level-common-attributes)
25. [Reusing One JSP Form for Create & Edit (Dynamic Forms)](#25-reusing-one-jsp-form-for-create--edit-dynamic-forms)
26. [CommandLineRunner for Data Seeding](#26-commandlinerunner-for-data-seeding)
27. [BeanUtils.copyProperties with Field Exclusion](#27-beanutilscopyproperties-with-field-exclusion)
28. [Audit Fields: @CreationTimestamp & @UpdateTimestamp](#28-audit-fields-creationtimestamp--updatetimestamp)
29. [Constructor Injection via @RequiredArgsConstructor](#29-constructor-injection-via-requiredargsconstructor)
30. [Password Encoding with BCrypt](#30-password-encoding-with-bcrypt)
31. [Optional Handling Patterns in Java](#31-optional-handling-patterns-in-java)
32. [JSP Fragments for View Reuse](#32-jsp-fragments-for-view-reuse)

---

## 1. Post/Redirect/Get (PRG) Pattern

### What Is It?

A web design pattern that prevents duplicate form submissions on browser refresh.

### The Problem It Solves

Without PRG:
1. User submits a form (POST)
2. Server processes and returns a view
3. User hits refresh --> **browser resubmits the POST** --> duplicate record

With PRG:
1. User submits a form (POST)
2. Server processes it and responds with **HTTP 302 redirect**
3. Browser performs a **GET** to the redirect URL
4. User hits refresh --> **safe GET request**, no duplicate

### Implementation

```java
@PostMapping
public String registerCourse(@Valid @ModelAttribute("course") CourseVO course,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
        return "course/course-form";   // NOT a redirect -- preserve errors
    }
    courseService.registerNewCourse(course);
    redirectAttributes.addFlashAttribute("successMessage", "Course registered!");
    return "redirect:/courses";        // PRG -- redirect to GET
}
```

### Interview Answer

> "I used the Post/Redirect/Get pattern to prevent duplicate form submissions. After a successful POST, the server responds with a 302 redirect. The browser then makes a GET request, so refreshing the page doesn't resubmit the form."

---

## 2. Redirect vs Forward vs Returning a View

### Quick Comparison

| Action | What Happens | URL Changes? | Request Data Preserved? |
|--------|-------------|--------------|------------------------|
| `return "viewName"` | Server renders the view within the same request | No | Yes |
| `return "redirect:/url"` | Server sends 302, browser makes new GET | **Yes** | **No** (new request) |
| `return "forward:/url"` | Server internally forwards to another handler | No | Yes |

### When To Use Each

| Situation | Use |
|-----------|-----|
| Validation errors -- redisplay form | `return "course/course-form"` (view name) |
| After successful POST | `return "redirect:/courses"` (PRG pattern) |
| Internal rerouting | `return "forward:/other-handler"` (rare) |

### Why Validation Errors Need View Name, Not Redirect

```java
// CORRECT: Returns view -- BindingResult and user input are preserved
if (bindingResult.hasErrors()) {
    return "user/user-form";
}

// WRONG: Redirect loses everything -- errors disappear, fields reset
if (bindingResult.hasErrors()) {
    return "redirect:/users/register";
}
```

A redirect creates a **new HTTP request**. `BindingResult`, model attributes, and user input only exist for the current request -- they are lost on redirect.

---

## 3. RedirectAttributes & Flash Attributes

### Three Ways to Pass Data After Redirect

**Option 1: Query parameter (simple flag)**
```java
return "redirect:/courses?success";
// JSP: <c:if test="${param.success != null}">Success!</c:if>
```

**Option 2: Query parameter with value**
```java
return "redirect:/courses?message=success";
// JSP: <c:if test="${param.message == 'success'}">Success!</c:if>
```

**Option 3: Flash Attributes (best practice)**
```java
redirectAttributes.addFlashAttribute("successMessage", "Course registered!");
return "redirect:/courses";
// JSP: <c:if test="${not empty successMessage}">${successMessage}</c:if>
```

### Why Flash Attributes Are Best

- No ugly query parameters in the URL
- Message survives **exactly one redirect**, then auto-removed
- Works with any data type, not just strings
- This is what production apps use

---

## 4. Bean Validation in Spring MVC

### Annotation Comparison

| Annotation    | `null` | `""` (empty) | `"  "` (whitespace) | Use For |
|--------------|--------|-------------|---------------------|---------|
| `@NotNull`   | Fail   | **Pass**    | **Pass**            | Non-string fields (Integer, Enum) |
| `@NotBlank`  | Fail   | Fail        | Fail                | **Required text inputs** |
| `@NotEmpty`  | Fail   | Fail        | **Pass**            | Collections, arrays |
| `@Size`      | Pass   | Depends     | Depends             | Length limits |
| `@Email`     | Pass   | Pass        | Fail                | Email format |

### Key Rule

**HTML forms submit empty fields as `""`, not `null`.** Always use `@NotBlank` for required String fields.

### Common Mistake

```java
@NotNull(message = "Role is required")
private Role role;  // CORRECT: @NotNull for non-String types

@NotBlank(message = "Role is required")
private Role role;  // WRONG: @NotBlank only works on Strings
```

---

## 5. BindingResult & How Spring MVC Forms Work

### What Spring Does Automatically

When a controller method has `@Valid @ModelAttribute("course") CourseVO course`:

1. Spring creates a `CourseVO` instance
2. Binds form field values to it
3. Runs Bean Validation (`@Valid`)
4. Puts **both** into the model:
   - `course` (the object with user's input)
   - `BindingResult.course` (the validation errors)

### Critical Rules

1. **`BindingResult` must immediately follow the `@Valid` parameter** -- no other parameters in between
2. **The model attribute name must match everywhere:**
   - `@ModelAttribute("course")` in controller
   - `model.addAttribute("course", ...)` in GET handler
   - `<form:form modelAttribute="course">` in JSP
3. You do NOT need to manually re-add the course to the model on error -- Spring already did it

### Why You Don't Need `model.addAttribute("course", course)` on Error

```java
if (bindingResult.hasErrors()) {
    // The course object is ALREADY in the model because of @ModelAttribute("course")
    // BindingResult is ALREADY in the model
    // Just add extra attributes needed by the view
    model.addAttribute("pageHeading", "Create New Course");
    return "course/course-form";
}
```

---

## 6. Uniqueness Validation (Database-Level Checks)

### Why Standard Annotations Aren't Enough

`@NotBlank`, `@Size`, `@Email` are all **in-memory checks**. They cannot query the database. For uniqueness (e.g., "title must not already exist"), you need an explicit database check.

### Approach: Service-Layer Check with BindingResult

```java
// In controller -- BEFORE checking bindingResult.hasErrors()
if (courseService.existsByTitle(course.getTitle())) {
    bindingResult.rejectValue("title", null, "Course title already exists");
}

if (bindingResult.hasErrors()) {
    return "course/course-form";
}
```

**Why after `@Valid` but before `hasErrors()`?** Because this way, both Bean Validation errors AND uniqueness errors are collected together and displayed at once.

### Important

Even with these checks, **always enforce uniqueness at the database level** (unique constraints). Validation is the user-friendly first line of defense. The database constraint protects against race conditions.

---

## 7. Custom Bean Validation Annotation

### When To Use

When you want uniqueness checks to integrate directly with `@Valid` so they fire automatically.

### Step 1: Create the Annotation

```java
@Documented
@Constraint(validatedBy = UniqueCourseTitleValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCourseTitle {
    String message() default "Course title already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### Step 2: Create the Validator

```java
@Component
public class UniqueCourseTitleValidator
        implements ConstraintValidator<UniqueCourseTitle, String> {

    @Autowired
    private CourseService courseService;

    @Override
    public boolean isValid(String title, ConstraintValidatorContext context) {
        if (title == null || title.isBlank()) return true; // let @NotBlank handle this
        return !courseService.existsByTitle(title);
    }
}
```

### Step 3: Use It

```java
@NotBlank
@Size(max = 100)
@UniqueCourseTitle
private String title;
```

### Trade-offs

| Approach | Pros | Cons |
|----------|------|------|
| Service-layer check | Simple, quick to implement | Not reusable across forms |
| Custom annotation | Integrated with `@Valid`, reusable | More setup, needs Spring DI |

---

## 8. Optimistic Locking with @Version

### What It Is

A concurrency control mechanism that prevents lost updates when multiple users edit the same record.

### How It Works

```java
@Version
private Integer version;
```

Hibernate adds a `WHERE version = ?` clause to UPDATE statements:

```sql
UPDATE courses SET title = 'New Title', version = 4
WHERE id = 100040 AND version = 3
```

If another user already updated (version is now 4), this UPDATE affects 0 rows and Hibernate throws `OptimisticLockException`.

### Key Rule

**Never expose `@Version` in the DTO/VO.** The version is a persistence concern, not a UI concern. When updating:

1. Load the entity from the database (version is loaded)
2. Copy user-editable fields onto it (skip version)
3. Save it (Hibernate uses the loaded version)

### Interview Answer

> "I used `@Version` for optimistic locking. Hibernate includes the version in the UPDATE WHERE clause. If two users try to update the same record concurrently, the second one gets an `OptimisticLockException` because the version no longer matches. This prevents lost updates without database-level locking."

---

## 9. Entity vs DTO Separation

### Why Separate?

| Layer | Object | Purpose |
|-------|--------|---------|
| Controller/View | DTO (CourseVO) | Form binding, validation, user-facing data |
| Service/Repository | Entity (CourseEntity) | Database mapping, JPA annotations, audit fields |

### Benefits

- Entity can have fields the user should never see (`version`, `createdDate`, `updatedBy`)
- DTO can have validation annotations specific to the form
- Changing the form doesn't require changing the database schema
- Security: Entity fields like `version` are never exposed to the client

### Conversion Pattern

```java
// Entity to DTO
private CourseVO convertEntityToVO(CourseEntity entity) {
    CourseVO vo = new CourseVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
}

// DTO to Entity (for CREATE)
private CourseEntity convertVOToEntity(CourseVO vo) {
    CourseEntity entity = new CourseEntity();
    BeanUtils.copyProperties(vo, entity);
    return entity;
}

// DTO to Entity (for UPDATE -- load first, then copy)
private CourseEntity convertVOToEntityForUpdate(CourseVO vo) {
    CourseEntity entity = courseRepository.findById(vo.getId())
        .orElseThrow(() -> new CourseNotFoundException("Not found"));
    BeanUtils.copyProperties(vo, entity, "id", "version", "createdBy", "createdDate");
    return entity;
}
```

---

## 10. Spring Security Fundamentals

### Security Filter Chain

A sequence of filters that intercept HTTP requests **before** they reach controllers. Requests failing security checks are rejected; valid ones proceed.

### My Configuration Breakdown

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/courses").permitAll()              // anyone can view list
            .requestMatchers("/courses/edit/**").hasRole("ADMIN") // only admin can edit
            .requestMatchers("/courses/add").hasRole("ADMIN")     // only admin can add
            .requestMatchers("/courses/delete/**").hasRole("ADMIN")
            .anyRequest().permitAll()
        )
        .formLogin(form -> form
            .loginPage("/login")    // custom login page
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .permitAll()
        )
        .build();
}
```

### `hasRole()` vs `hasAuthority()`

```java
hasRole("ADMIN")      // checks for authority "ROLE_ADMIN" (auto-prefixes ROLE_)
hasAuthority("ADMIN") // checks for authority "ADMIN" (exact match, no prefix)
```

My `User` entity returns authorities with the `ROLE_` prefix:

```java
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role));
}
```

So `hasRole("ADMIN")` works correctly.

---

## 11. CSRF Protection

### What Is CSRF?

Cross-Site Request Forgery -- an attack where a malicious website tricks a logged-in user's browser into making requests to your app.

### How Spring Security Prevents It

- Server generates a unique token per session
- Every state-changing request (POST/PUT/DELETE) must include this token
- Requests without a valid token are rejected with 403

### Adding CSRF Token in JSP Forms

```jsp
<!-- Manual approach -->
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

<!-- Spring tag approach (cleaner) -->
<spring:csrfInput />
```

### Important

- GET requests don't need CSRF (they are read-only)
- Spring Security enables CSRF by default -- don't disable it
- When using Thymeleaf, CSRF is handled automatically in `<form>` tags

---

## 12. Spring Security JSP Taglib (`<sec:authorize>`)

### Setup Required

1. Add the dependency (not included by default, even with Spring Security):

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-taglibs</artifactId>
</dependency>
```

2. Declare the taglib in JSP:

```jsp
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
```

### Usage -- Conditional UI Based on Auth State

```jsp
<!-- Show logout button only for logged-in users -->
<sec:authorize access="isAuthenticated()">
    <form action="${pageContext.request.contextPath}/logout" method="POST">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <button type="submit">Logout</button>
    </form>
</sec:authorize>

<!-- Show login/register links only for anonymous users -->
<sec:authorize access="isAnonymous()">
    <a href="${pageContext.request.contextPath}/login">Login</a>
    <a href="${pageContext.request.contextPath}/users/register">Register</a>
</sec:authorize>
```

### Common Issues

| Problem | Cause | Fix |
|---------|-------|-----|
| `Cannot find tag library descriptor` | Missing `spring-security-taglibs` dependency | Add the Maven/Gradle dependency |
| Tags work in compile but fail at runtime | Version mismatch between security modules | Ensure all Spring Security modules use the same version |
| Tags not rendering at all | Missing `tomcat-embed-jasper` in Spring Boot 3 | Add `tomcat-embed-jasper` dependency |

---

## 13. Logout Configuration

### What Happens on Logout

1. User sends `POST /logout` with CSRF token
2. Spring Security invalidates the HTTP session
3. Clears the `SecurityContext` (authentication object)
4. Deletes remember-me cookies (if any)
5. Redirects to configured URL

### My Implementation (Clean URL, No Query Params)

```java
.logout(logoutConfig -> logoutConfig
    .logoutUrl("/logout")
    .logoutSuccessHandler((request, response, authentication) -> {
        request.getSession().setAttribute("logoutMessage",
            "You have been logged out successfully.");
        response.sendRedirect("/");
    })
    .permitAll()
)
```

### Common Mistakes

| Mistake | Why It Fails |
|---------|-------------|
| `<a href="/logout">Logout</a>` | Logout requires POST by default (CSRF) |
| Missing CSRF token in logout form | Results in 403 Forbidden |
| Forgetting `.permitAll()` on logout | Logout URL may become inaccessible |

---

## 14. Hibernate DDL Hints from Annotations

### How Column Size Gets Determined

```java
@NotBlank
@Size(max = 50)
@Column(nullable = false)
private String firstName;
// Generates: first_name varchar2(50) not null
```

### Priority Rules

| If Present | Column Length Comes From |
|------------|------------------------|
| `@Column(length = 100)` | `@Column` wins |
| `@Size(max = 50)` only | Hibernate uses `@Size(max)` as DDL hint |
| Neither | Default 255 |

### Important Distinction

- `@Size` is a **Bean Validation** annotation (validates in Java, before persistence)
- `@Column(length)` is a **JPA** annotation (defines database schema)
- `@Size` does NOT enforce at database level -- only Hibernate uses it as a DDL hint during schema generation

---

## 15. JSP Form Tag Gotchas

### `<form:label>` Uses `path`, Not `for`

```jsp
<!-- WRONG: -->
<frm:label for="title">Title</frm:label>  <!-- causes 500 error -->

<!-- CORRECT: -->
<frm:label path="title">Title</frm:label>
```

### `<form:hidden>` vs Hidden `<form:input>`

```jsp
<!-- WRONG: -->
<frm:input path="id" hidden="true" />

<!-- CORRECT: -->
<frm:hidden path="id" />
```

### Mixing JSTL and Scriptlets

```jsp
<!-- WRONG: Don't mix JSTL with scriptlets -->
<c:when test="${isEditMode}">
    <%= path="" %>
</c:when>

<!-- CORRECT: Use only JSTL/EL -->
<c:when test="${isEditMode}">
    <c:url value='/courses/${course.id}' var='formActionUrl'/>
</c:when>
```

### Global Validation Errors

```jsp
<!-- Show ALL validation errors at once (above the form) -->
<frm:errors path="*" cssClass="error"/>
```

---

## 16. Enum Handling in Forms

### Passing Enum Values to JSP

```java
// Controller
model.addAttribute("roles", Role.values());
```

`Role.values()` returns a `Role[]` array. JSTL `<c:forEach>` can iterate over arrays directly -- no conversion to `List` needed.

### Rendering a Dropdown

```jsp
<frm:select path="role">
    <frm:option value="" label="-- Select Role --"/>
    <c:forEach var="role" items="${roles}">
        <frm:option value="${role}" label="${role}"/>
    </c:forEach>
</frm:select>
```

`${role}` prints the enum constant name (e.g., `ADMIN`) because `enum.toString()` returns the name by default.

---

## 17. Lombok @Builder on Constructor vs Class

### Why Put @Builder on a Constructor?

When `@Builder` is on the class, it includes ALL fields -- including auto-generated ones like `id`, `version`, and `createdDate` that should not be set manually.

```java
// BETTER: @Builder on a specific constructor limits which fields are in the builder
@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue
    private Long id;         // excluded from builder

    private String name;
    private String email;

    @Version
    private Integer version; // excluded from builder

    @Builder
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

// Usage: only name and email are settable
User user = User.builder().name("John").email("john@mail.com").build();
```

---

## 18. Spring Data JPA Query Method Naming

### `findBy` vs `existsBy`

| Method Signature | Return Type | Purpose |
|-----------------|-------------|---------|
| `findByTitle(String title)` | `Optional<CourseEntity>` | Fetch the entity |
| `existsByTitle(String title)` | `boolean` | Check existence only |
| `existsByEmail(String email)` | `boolean` | Check existence only |

### Important

`findBy` returns an entity (or `Optional`), not a `boolean`. If you only need to check if something exists, use `existsBy` -- it's more efficient (generates `SELECT COUNT(*)` or `SELECT 1` instead of loading the full entity).

---

## 19. Error Handling with @ControllerAdvice

### What It Is

A centralized exception handler that catches exceptions thrown from any controller.

### My Implementation

```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CourseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCourseNotFound(CourseNotFoundException ex, Model model) {
        log.error(ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Something went wrong.");
        return "error/500";
    }
}
```

### How It Works

1. Controller throws `CourseNotFoundException`
2. Spring looks for a matching `@ExceptionHandler` in `@ControllerAdvice` classes
3. The handler sets the HTTP status, adds error info to model, and returns a view

---

## 20. Custom Exception with HTTP Status

### Option 1: `@ResponseStatus` on the Exception Class

```java
@ResponseStatus(HttpStatus.CONFLICT) // 409
public class UserNameAlreadyExistsException extends RuntimeException {
    public UserNameAlreadyExistsException(String message) {
        super(message);  // IMPORTANT: always pass message to super()
    }
}
```

### Option 2: Handle in `@ControllerAdvice` (more control)

```java
@ExceptionHandler(UserNameAlreadyExistsException.class)
@ResponseStatus(HttpStatus.CONFLICT)
public String handleUserExists(UserNameAlreadyExistsException ex, Model model) {
    model.addAttribute("errorMessage", ex.getMessage());
    return "error/user_error";
}
```

### Common Mistake

```java
// WRONG: Message is lost
public UserNameAlreadyExistsException(String message) {
    // forgot to call super(message)
}

// CORRECT: Message is preserved
public UserNameAlreadyExistsException(String message) {
    super(message);
}
```

---

## 21. @Transactional and readOnly

### What `@Transactional` Does

Wraps the method in a database transaction. If any step fails, all changes are rolled back.

### Best Practice: Class-Level readOnly + Method-Level Override

```java
@Service
@Transactional(readOnly = true)  // default: read-only for all methods
public class CourseServiceImpl implements CourseService {

    @Transactional  // override: this method writes to DB
    public String registerNewCourse(CourseVO courseVo) { ... }

    // This inherits readOnly = true (no override needed)
    public List<CourseVO> getAllCourses() { ... }
}
```

### Why `readOnly = true`?

- Tells Hibernate not to dirty-check entities (performance boost)
- Some databases optimize read-only transactions
- Prevents accidental writes in query methods
- Acts as documentation: "this method doesn't modify data"

---

## 22. HTTP DELETE Best Practice

### Use POST (Not GET) for Delete Actions

```java
// Controller
@PostMapping("/delete/{courseId}")
public String deleteCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
    courseService.deleteCourseById(courseId);
    redirectAttributes.addFlashAttribute("successMessage", "Course deleted!");
    return "redirect:/courses";
}
```

```jsp
<!-- JSP: Use a form with POST, not a link -->
<form action="/courses/delete/${course.id}" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <button type="submit">Delete</button>
</form>
```

### Why Not GET?

- GET should be idempotent and safe (no side effects)
- A `<a href="/delete/5">` link could be accidentally triggered by bots, prefetchers, or bookmarks
- CSRF protection only applies to POST/PUT/DELETE, not GET
- Using POST prevents accidental deletes

---

## 23. Two Types of "Not Found" Scenarios

### Scenario A: User Enters Invalid URL Directly

Example: `/courses/99999` where course 99999 doesn't exist.

**Correct response:** HTTP 404 -- the resource genuinely doesn't exist.

```java
@GetMapping("/{id}")
public String viewCourse(@PathVariable long id, Model model) {
    CourseVO course = courseService.getCourseById(id); // throws CourseNotFoundException
    model.addAttribute("course", course);
    return "course/course-view";
}
```

The `GlobalExceptionHandler` catches `CourseNotFoundException` and returns a 404 page.

### Scenario B: Business Logic Failure in a Flow

Example: User clicks "Delete" but the course was already deleted by someone else.

**Correct response:** Redirect back with a user-friendly message, not a 404 page.

```java
@PostMapping("/delete/{courseId}")
public String deleteCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
    try {
        courseService.deleteCourseById(courseId);
        redirectAttributes.addFlashAttribute("successMessage", "Course deleted!");
    } catch (CourseNotFoundException e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Course not found or already deleted.");
    }
    return "redirect:/courses";
}
```

### Interview-Ready Answer

> "I handle two types of 'not found' differently. When a user directly accesses a non-existent resource URL, I return a proper HTTP 404. But when a business operation fails mid-flow (like deleting something already deleted), I redirect back to the list with a flash message -- better UX than showing an error page."

---

## 24. @ModelAttribute at Method Level (Common Attributes)

### What It Does

When `@ModelAttribute` is on a **method** (not a parameter), that method runs **before every handler** in the controller. It's used to add shared data to the model.

### My Implementation

```java
@Controller
@RequestMapping("/users")
public class UserController {

    @ModelAttribute  // runs before EVERY handler in this controller
    public void addCommonAttributes(Model model) {
        model.addAttribute("roles", Role.values());
        model.addAttribute("pageTitle", "User Registration");
        model.addAttribute("isEdit", false);
        model.addAttribute("submitButtonLabel", "Register");
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        // "roles", "pageTitle", "isEdit", "submitButtonLabel" are ALREADY in the model
        model.addAttribute("user", new User());
        return "user/user-form";
    }
}
```

### Why This Is Useful

- Eliminates duplicate `model.addAttribute()` calls across multiple handlers
- Keeps common view data in one place (DRY principle)
- If you add a new handler, it automatically gets the common attributes

### Interview Answer

> "I used `@ModelAttribute` at the method level to inject shared data (like enum values for dropdowns and page titles) into the model before every handler method in the controller. This follows the DRY principle -- I define it once and every endpoint gets it automatically."

---

## 25. Reusing One JSP Form for Create & Edit (Dynamic Forms)

### The Pattern

Instead of two separate JSPs (`add-course.jsp` and `edit-course.jsp`), I used **one form** with dynamic behavior based on flags:

**Controller sets the mode:**
```java
// ADD mode
model.addAttribute("course", new CourseVO());       // empty object
model.addAttribute("pageHeading", "Create New Course");
model.addAttribute("isEditMode", false);

// EDIT mode
model.addAttribute("course", courseService.getCourseById(id)); // loaded object
model.addAttribute("pageHeading", "Edit Course");
model.addAttribute("isEditMode", true);
```

**JSP adapts dynamically:**
```jsp
<h2>${pageHeading}</h2>

<!-- Dynamic form action URL -->
<c:choose>
    <c:when test="${isEditMode}">
        <c:url value='/courses/${course.id}' var='formActionUrl'/>
    </c:when>
    <c:otherwise>
        <c:url value='/courses' var='formActionUrl'/>
    </c:otherwise>
</c:choose>

<frm:form method="post" action="${formActionUrl}" modelAttribute="course">
    <frm:hidden path="id"/>  <!-- null for create, populated for edit -->
    ...
    <input type="submit" value="${submitButtonLabel}"/>
</frm:form>
```

### Benefits

- Single source of truth for form layout
- Change the form in one place, affects both create and edit
- Less JSP files to maintain

### Pitfall I Encountered

Uniqueness validation broke during edits because it found the course being edited itself. Fixed with an "exclude current ID" check. (See Challenge #4.)

---

## 26. CommandLineRunner for Data Seeding

### What It Is

`CommandLineRunner` is a Spring Boot interface that runs code **after** the application context is fully initialized. Used for startup tasks like seeding test data.

### My Implementation

```java
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        courseRepository.saveAll(List.of(
            CourseEntity.builder().title("Java Programming")
                .description("Learn Java").durationInHours(40).build(),
            CourseEntity.builder().title("Spring Boot")
                .description("Master Spring Boot").durationInHours(30).build()
        ));

        userRepository.saveAll(List.of(
            User.builder().username("admin")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin").email("admin@mail.com")
                .role(Role.ADMIN).build()
        ));
    }
}
```

### Best Practice: Make It Idempotent

```java
@Override
public void run(String... args) {
    if (courseRepository.count() == 0) {
        // seed only if empty
    }
}
```

Or use `@Profile("dev")` so it only runs in development.

### Interview Answer

> "`CommandLineRunner` runs after Spring Boot fully initializes. I used it to seed sample data for development. In production, you'd use database migration tools like Flyway or Liquibase instead."

---

## 27. BeanUtils.copyProperties with Field Exclusion

### What It Does

`BeanUtils.copyProperties(source, target, "field1", "field2")` copies all matching properties from source to target, **except** the listed fields.

### Why I Needed It

When updating a course, I need to copy user-editable fields from the DTO to the entity, but **preserve** database-managed fields:

```java
// Copy everything EXCEPT id, version, createdBy, createdDate
BeanUtils.copyProperties(courseVO, courseEntity, "id", "createdBy", "createdDate", "version");
```

Without exclusion, `BeanUtils` would overwrite `version` with `null` (since the DTO doesn't have it), causing the Hibernate `@Version` error.

### Limitation

The excluded field names are **strings** -- no compile-time safety. A typo like `"versoin"` would silently fail. For production code, consider using MapStruct (compile-time safe mapper) instead.

---

## 28. Audit Fields: @CreationTimestamp & @UpdateTimestamp

### What They Do

Hibernate annotations that automatically set timestamps on entity lifecycle events:

```java
@CreationTimestamp
@Column(updatable = false)
private LocalDateTime createdDate;   // set once on INSERT, never changed

@UpdateTimestamp
@Column(insertable = false)
private LocalDateTime updatedDate;   // null on INSERT, updated on every UPDATE
```

### Key Difference from Spring Data's Annotations

| Annotation | Library | When It Fires |
|-----------|---------|---------------|
| `@CreationTimestamp` | Hibernate | On INSERT (Hibernate event) |
| `@UpdateTimestamp` | Hibernate | On UPDATE (Hibernate event) |
| `@CreatedDate` | Spring Data | Needs `@EnableJpaAuditing` + `AuditorAware` |
| `@LastModifiedDate` | Spring Data | Needs `@EnableJpaAuditing` + `AuditorAware` |

I used Hibernate's annotations because they're simpler (no extra config). Spring Data's annotations are more powerful when you also need `@CreatedBy` / `@LastModifiedBy` with user tracking.

### Interview Answer

> "I used Hibernate's `@CreationTimestamp` and `@UpdateTimestamp` for automatic audit fields. Combined with `@Column(updatable = false)` and `@Column(insertable = false)`, the created timestamp is set once and never overwritten, while the updated timestamp is set on every modification."

---

## 29. Constructor Injection via @RequiredArgsConstructor

### The Pattern

```java
@Service
@RequiredArgsConstructor  // Lombok generates constructor for all final fields
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;  // injected via constructor
}
```

### Why Constructor Injection Is Preferred Over @Autowired

| Feature | Constructor Injection | `@Autowired` Field Injection |
|---------|----------------------|------------------------------|
| Immutability | Fields can be `final` | Fields cannot be `final` |
| Testability | Easy to pass mocks in tests | Requires reflection or Spring context |
| Required deps | Compile-time error if missing | Runtime error (NullPointerException) |
| Spring recommendation | **Officially recommended** | Discouraged since Spring 4.3 |

### Common Mistake

```java
@RequiredArgsConstructor
public class MyService {
    @Autowired                          // REDUNDANT -- remove this
    private final CourseRepository repo; // @RequiredArgsConstructor handles injection
}
```

`@RequiredArgsConstructor` generates the constructor. Spring auto-injects via constructor (since Spring 4.3, if there's only one constructor, `@Autowired` is optional).

### Interview Answer

> "I use constructor injection via Lombok's `@RequiredArgsConstructor`. It generates a constructor for all `final` fields, making dependencies immutable and easy to test. This is Spring's officially recommended approach over field injection with `@Autowired`."

---

## 30. Password Encoding with BCrypt

### Why Not Store Plain Text

- If the database is breached, all passwords are exposed
- Hashing is one-way -- you can verify but not reverse
- BCrypt adds a random salt per password, so identical passwords have different hashes

### My Implementation

**Bean definition:**
```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Encoding on registration:**
```java
public String registerUser(User user) {
    user.setPassword(encoder.encode(user.getPassword())); // hash before save
    return userRepository.save(user).getFullName();
}
```

**Verification (handled by Spring Security):**
Spring Security automatically calls `passwordEncoder.matches(rawPassword, encodedPassword)` during login. You don't write this code yourself.

### Interview Answer

> "I used BCrypt for password hashing. It's a one-way hash with per-password salting, so even identical passwords produce different hashes. I encode at registration and Spring Security automatically verifies during login using `PasswordEncoder.matches()`."

---

## 31. Optional Handling Patterns in Java

### Patterns I Used

**Pattern 1: `orElseThrow()` -- fail fast if not found**
```java
CourseEntity entity = courseRepository.findById(id)
    .orElseThrow(() -> new CourseNotFoundException("Course not found: " + id));
```

**Pattern 2: `map()` + `orElseThrow()` -- transform then fail**
```java
CourseVO vo = courseRepository.findById(courseId)
    .map(this::convertEntityToVO)
    .orElseThrow(() -> new CourseNotFoundException("Not found"));
```

**Pattern 3: `isPresent()` + `get()` -- conditional logic**
```java
Optional<CourseEntity> courseWithTitle = courseRepository.findByTitle(title);
return courseWithTitle.isPresent() && !courseWithTitle.get().getId().equals(id);
```

### What NOT to Do

```java
// NEVER call .get() without checking first
return repository.findByUsername(username).get(); // throws NoSuchElementException

// CORRECT:
return repository.findByUsername(username)
    .orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));
```

---

## 32. JSP Fragments for View Reuse

### The Pattern

Instead of duplicating HTML boilerplate in every JSP, extract common parts:

**`header.jsp`** -- contains `<html>`, `<head>`, CSS, navigation
```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle != null ? pageTitle : 'Course Management'}</title>
    <style>/* shared CSS */</style>
</head>
<body>
<header>
    <nav>...</nav>
</header>
```

**`footer.jsp`** -- closes the HTML
```jsp
<footer>...</footer>
</body>
</html>
```

**Every page includes them:**
```jsp
<%@ include file="../fragments/header.jsp" %>

<div class="container">
    <!-- page-specific content -->
</div>

<%@ include file="../fragments/footer.jsp" %>
```

### Benefits

- Change navigation once, affects all pages
- Consistent look and feel
- Less code duplication

### Equivalent in Modern Frameworks

| Technology | Layout Mechanism |
|-----------|-----------------|
| JSP | `<%@ include file="..." %>` |
| Thymeleaf | `th:replace="fragments/header"` (more powerful) |
| React | Component composition (`<Layout><Page/></Layout>`) |

---

## Quick Revision: Key Concepts in One Line Each

| Concept | One-Liner |
|---------|-----------|
| PRG Pattern | POST redirects to GET to prevent duplicate submissions on refresh |
| `@NotBlank` vs `@NotNull` | `@NotBlank` rejects `""` and `" "`, `@NotNull` only rejects `null` |
| `BindingResult` | Must immediately follow `@Valid` parameter; holds validation errors |
| Flash Attributes | Survive exactly one redirect, then auto-removed |
| `@Version` | Optimistic locking -- Hibernate adds `WHERE version = ?` to UPDATE |
| CSRF Token | Required in every POST/PUT/DELETE form to prevent cross-site forgery |
| `hasRole("ADMIN")` | Checks for authority `ROLE_ADMIN` (auto-prefixes `ROLE_`) |
| `@ControllerAdvice` | Centralized exception handling across all controllers |
| `@Transactional(readOnly=true)` | Optimizes read queries; prevents accidental writes |
| Entity vs DTO | Entity = database; DTO = user-facing. Never expose entities to views. |
| `@ModelAttribute` on method | Runs before every handler in the controller; adds shared model data |
| Dynamic form (one JSP) | Use flags (`isEditMode`) and conditional action URLs for create/edit |
| `CommandLineRunner` | Runs after Spring Boot starts; use for dev seed data with idempotency check |
| `BeanUtils.copyProperties` | Third argument excludes fields; critical for preserving `@Version` on updates |
| `@CreationTimestamp` | Hibernate sets timestamp on INSERT; pair with `@Column(updatable = false)` |
| Constructor injection | Use `@RequiredArgsConstructor` + `final` fields; officially recommended over `@Autowired` |
| BCrypt | One-way hash with per-password salt; Spring Security auto-verifies during login |
| `Optional.orElseThrow()` | Fail fast with a meaningful exception instead of calling `.get()` |
| JSP fragments | `<%@ include file="..." %>` for shared header/footer across all pages |
