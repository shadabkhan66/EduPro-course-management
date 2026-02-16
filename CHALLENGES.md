# Challenges & Problems Faced During Development

> Real problems I encountered while building this project, how I debugged them, and what I learned.  
> Structured as interview Q&A -- perfect for "Tell me about a challenge you faced" questions.

---

## Table of Contents

1. [Hibernate @Version Error on Update](#1-hibernate-version-error-on-update)
2. [Validation Not Stopping Database Calls](#2-validation-not-stopping-database-calls)
3. [Uniqueness Validation -- Showing All Errors at Once](#3-uniqueness-validation----showing-all-errors-at-once)
4. [Same Form for Create & Edit -- Title Uniqueness Clash](#4-same-form-for-create--edit----title-uniqueness-clash)
5. [CSRF 403 Forbidden on POST Requests](#5-csrf-403-forbidden-on-post-requests)
6. [Spring Security 403 Instead of Login Redirect](#6-spring-security-403-instead-of-login-redirect)
7. [DispatcherServlet URL Mapping Confusion](#7-dispatcherservlet-url-mapping-confusion)
8. [Double Context Path in `<c:url>`](#8-double-context-path-in-curl)
9. [BindingResult / Command Bean Error on Login JSP](#9-bindingresult--command-bean-error-on-login-jsp)
10. [Spring Form Error Code Resolution Failure (500 Error)](#10-spring-form-error-code-resolution-failure-500-error)
11. [Browser Auto-filling Registration Form with Login Credentials](#11-browser-auto-filling-registration-form-with-login-credentials)
12. [Logout Message Without Query Parameters](#12-logout-message-without-query-parameters)
13. [JSP Error Page: `${exception}` is null in Spring Boot](#13-jsp-error-page-exception-is-null-in-spring-boot)
14. [Moving from In-Memory Auth to Database-Backed UserDetailsService](#14-moving-from-in-memory-auth-to-database-backed-userdetailsservice)
15. [@PathVariable vs Hidden Form Field -- ID Mismatch Security Risk](#15-pathvariable-vs-hidden-form-field----id-mismatch-security-risk)
16. [Audit Column Configuration: `insertable` and `updatable`](#16-audit-column-configuration-insertable-and-updatable)
17. [H2 Console 403 Forbidden -- Spring Security Blocking Dev Tools](#17-h2-console-403-forbidden----spring-security-blocking-dev-tools)
18. [Delete Not Working -- `@Transactional(readOnly = true)` on Write Operation](#18-delete-not-working----transactionalreadonly--true-on-write-operation)

---

## 1. Hibernate @Version Error on Update

### The Error

```
Detached entity with generated id has an uninitialized version value 'null'
```

### What Happened

When updating a course, I created a **new** `CourseEntity` object and copied properties from the VO using `BeanUtils.copyProperties()`. The new entity had the correct `id` but `version` was `null`.

### Why Hibernate Refused

Hibernate classifies entities by their state:

| Entity State | ID       | Version   | Allowed |
|-------------|----------|-----------|---------|
| **New**     | `null`   | `null`    | Yes     |
| **Managed** | non-null | non-null  | Yes     |
| **Detached**| non-null | `null`    | **No**  |

My entity fell into the **illegal "detached with null version"** state. Hibernate uses `@Version` for optimistic locking -- it needs the version number to generate the correct `WHERE` clause:

```sql
UPDATE courses SET title = ?, version = version + 1
WHERE id = 100040 AND version = 3
```

If `version` is `null`, Hibernate cannot do this safely, so it refuses.

### Root Cause

```java
// WRONG: Creates a NEW entity for an UPDATE -- version is lost
private CourseEntity convertVOToEntity(CourseVO courseVO) {
    CourseEntity courseEntity = new CourseEntity();       // version = null
    BeanUtils.copyProperties(courseVO, courseEntity);     // VO has no version
    return courseEntity;                                  // entity has id but no version
}
```

### The Fix

**Golden Rule: CREATE = new entity, UPDATE = load entity first.**

```java
// CORRECT: Load the managed entity, then overwrite fields
private CourseEntity convertVOToEntityForUpdate(CourseVO courseVO) {
    CourseEntity entity = courseRepository.findById(courseVO.getId())
        .orElseThrow(() -> new CourseNotFoundException("Not found"));
    // Exclude id, version, and audit fields from copy
    BeanUtils.copyProperties(courseVO, entity, "id", "createdBy", "createdDate", "version");
    return entity;  // version is preserved from the database
}
```

### Interview-Ready Answer

> "I ran into a Hibernate error where updating a course threw `Detached entity with uninitialized version`. The issue was that I was creating a new entity object for an update, which lost the `@Version` field. The fix was to load the existing entity from the database first (so Hibernate tracks it as a managed entity with the correct version), then copy only the user-editable fields onto it. This preserves optimistic locking."

---

## 2. Validation Not Stopping Database Calls

### The Error

```
ORA-01400: cannot insert NULL into COURSES.COURSE_TITLE
```

### What Happened

I used `@Valid` on the controller method but validation errors were not stopping the flow. The request went straight to the service layer and Hibernate tried to insert a `null` title.

### Root Cause (Two Issues)

**Issue A: Missing `BindingResult` parameter**

```java
// WRONG: No BindingResult -- Spring has nowhere to store errors
@PostMapping
public String register(@Valid @ModelAttribute CourseVO course, Model model) { ... }
```

Without `BindingResult`, Spring has nowhere to store validation errors, so it silently ignores them and continues execution.

**Issue B: `@NotNull` instead of `@NotBlank`**

```java
@NotNull   // allows empty string ""
private String title;
```

HTML forms submit empty fields as `""` (empty string), not `null`. So `@NotNull` passes even when the user leaves the field blank.

### The Fix

```java
// CORRECT: BindingResult MUST immediately follow the @Valid parameter
@PostMapping
public String register(
    @Valid @ModelAttribute("course") CourseVO course,
    BindingResult bindingResult,    // <-- catches validation errors
    Model model) {

    if (bindingResult.hasErrors()) {
        return "course/course-form";  // re-display form with errors
    }
    // ... proceed with save
}
```

And use `@NotBlank` for required text fields:

```java
@NotBlank(message = "Course title is required")  // rejects null AND ""
private String title;
```

### Key Takeaways

| Annotation  | null | "" (empty) | "  " (whitespace) |
|-------------|------|------------|---------------------|
| `@NotNull`  | Fail | **Pass**   | **Pass**            |
| `@NotBlank` | Fail | Fail       | Fail                |
| `@Size`     | Pass | Pass       | Depends on min      |

### Interview-Ready Answer

> "I had an issue where Bean Validation wasn't stopping invalid data from hitting the database. Two problems: first, I forgot to add `BindingResult` after the `@Valid` parameter -- without it, Spring ignores validation failures. Second, I was using `@NotNull` on String fields, but HTML forms send empty strings, not null. Switching to `@NotBlank` fixed it. The lesson is: `BindingResult` must immediately follow the `@Valid` parameter, and `@NotBlank` is correct for required text inputs."

---

## 3. Uniqueness Validation -- Showing All Errors at Once

### The Problem

Both username and email have unique constraints. Initially I checked uniqueness by throwing exceptions:

```java
// WRONG: Only first error is shown
if (userRepository.existsByEmail(user.getEmail())) {
    throw new EmailAlreadyExistsException("Email already exists");
}
if (userRepository.existsByUsername(user.getUsername())) {
    throw new UserNameAlreadyExists("Username already exists");
}
```

If both username AND email were duplicates, only the first exception fired. The user had to fix one, submit again, then see the second error. Bad UX.

### The Fix

Use `BindingResult.rejectValue()` in the controller instead of throwing exceptions:

```java
if (userService.doesUniqueEmailExists(user.getEmail())) {
    result.rejectValue("email", null, "Email already exists");
}
if (userService.doesUniqueUsernameExists(user.getUsername())) {
    result.rejectValue("username", null, "Username already exists");
}
if (result.hasErrors()) {
    return "user/user-form";  // both errors shown simultaneously
}
```

### Why This Is Better

- Both errors display at the same time
- No unnecessary exceptions in the normal flow
- User input is preserved in the form
- Error messages appear next to the correct field via `<form:errors path="username"/>`

### Interview-Ready Answer

> "For uniqueness checks like duplicate email and username, I initially threw exceptions, but that only showed one error at a time. I switched to `BindingResult.rejectValue()` which lets me collect all errors before returning the form, so the user sees every problem in one submission."

---

## 4. Same Form for Create & Edit -- Title Uniqueness Clash

### The Problem

I reused one JSP form for both creating and editing courses. The title uniqueness check always rejected edits because "the title already exists" -- it was finding **the course being edited itself**.

### The Fix

For edits, check if the title exists **excluding the current course's ID**:

```java
// During EDIT: exclude the current course from the uniqueness check
if (courseService.existsByTitleExcludingCurrentCourseTitle(courseVo.getTitle(), courseVo.getId())) {
    bindingResult.rejectValue("title", "error.course", "Course title already exists");
}
```

```java
// Service implementation
public boolean existsByTitleExcludingCurrentCourseTitle(String title, Long id) {
    Optional<CourseEntity> courseWithTitle = courseRepository.findByTitle(title);
    return courseWithTitle.isPresent() && !courseWithTitle.get().getId().equals(id);
}
```

### Interview-Ready Answer

> "When reusing a single form for create and edit, the uniqueness check for course title would always fail during edits because it found the course itself. I solved this by adding an 'exclude by ID' check -- if the matching title belongs to the same course being edited, it's allowed."

---

## 5. CSRF 403 Forbidden on POST Requests

### The Error

```
403 Forbidden
```

On every POST request (create, edit, delete course).

### Why It Happened

Spring Security enables CSRF protection by default. Every state-changing request (POST, PUT, DELETE) must include a valid CSRF token. My forms didn't have one.

### The Fix

Add the CSRF token as a hidden field in every form:

```jsp
<form method="post" action="/courses/delete/${course.id}">
    <input type="hidden"
           name="${_csrf.parameterName}"
           value="${_csrf.token}" />
    <button type="submit">Delete</button>
</form>
```

Spring Security exposes `_csrf` automatically to JSPs:
- `${_csrf.parameterName}` --> usually `_csrf`
- `${_csrf.token}` --> the actual token value

### Important Notes

| HTTP Method | CSRF Required? |
|-------------|----------------|
| GET         | No (read-only) |
| POST        | **Yes**        |
| PUT         | **Yes**        |
| DELETE      | **Yes**        |

### Alternative: Use Spring's `<spring:csrfInput />` tag

```jsp
<form method="post" action="/courses/delete/${course.id}">
    <spring:csrfInput />
    <button type="submit">Delete</button>
</form>
```

Cleaner -- Spring automatically inserts the hidden input.

### Interview-Ready Answer

> "I got 403 Forbidden on all POST requests because Spring Security's CSRF protection was enabled by default, and my JSP forms weren't including the CSRF token. The fix was adding `<input type='hidden' name='${_csrf.parameterName}' value='${_csrf.token}' />` to every form. CSRF prevents cross-site request forgery by ensuring the request originates from our own pages."

---

## 6. Spring Security 403 Instead of Login Redirect

### The Problem

I restricted `/courses/**` to `ADMIN` role but didn't enable `formLogin()`. When unauthenticated users visited `/courses`, they got **403 Forbidden** instead of being redirected to a login page.

### Why It Happened

Without an authentication mechanism configured (like `formLogin()` or `httpBasic()`), Spring Security has no way to authenticate the user. It can't redirect to login because no login mechanism exists. So it directly rejects with 403.

### The Fix

Enable form login in the security configuration:

```java
.formLogin(formLoginConfig -> formLoginConfig
    .loginPage("/login")
    .permitAll()
)
```

### Also Important: `hasRole()` vs `hasAuthority()`

```java
hasRole("ADMIN")      // checks for authority "ROLE_ADMIN" (adds prefix)
hasAuthority("ADMIN") // checks for authority "ADMIN" (exact match)
```

If your authorities are stored as `ROLE_ADMIN`, use `hasRole("ADMIN")`.

### HTTP Status Codes to Remember

| Code | Meaning                          |
|------|----------------------------------|
| 401  | **Not authenticated** (not logged in) |
| 403  | **Authenticated but no permission**   |

### Interview-Ready Answer

> "Initially I got 403 instead of a login redirect because I configured authorization rules without enabling an authentication mechanism. Spring Security couldn't redirect to login because `formLogin()` wasn't configured. After enabling it, unauthenticated users are redirected to `/login` automatically."

---

## 7. DispatcherServlet URL Mapping Confusion

### The Problem

`POST /courses/add` was being routed to `@PostMapping("/{courseId}")` instead of the intended handler.

### Why It Happened

Spring's `DispatcherServlet` matched `/courses/add` against `/{courseId}` because `add` looked like a valid path variable value. URL patterns with path variables are greedy matchers.

### The Fix

Use more specific URL patterns and ensure `POST /courses` (without a path variable) is used for creation, while `POST /courses/{courseId}` is used for updates.

### Interview-Ready Answer

> "I had a routing issue where `/courses/add` was being matched by `/{courseId}` because Spring treated 'add' as a path variable value. I restructured my URL patterns so create uses `POST /courses` and update uses `POST /courses/{id}`, avoiding ambiguity."

---

## 8. Double Context Path in `<c:url>`

### The Problem

Links were generating double context paths like `/myapp/myapp/courses/1`, causing 404 errors.

### Why It Happened

`<c:url>` automatically prepends the context path. I was also manually adding `${pageContext.request.contextPath}`:

```jsp
<!-- WRONG: Double context path -->
action="${pageContext.request.contextPath}/${formActionUrl}"
```

### The Fix

Use only one approach -- let `<c:url>` handle it:

```jsp
<c:url value='/courses/${course.id}' var='formActionUrl'/>

<!-- CORRECT: <c:url> already includes the context path -->
<form:form action="${formActionUrl}" ...>
```

### Interview-Ready Answer

> "`<c:url>` automatically includes the application's context path, so combining it with `${pageContext.request.contextPath}` creates a double-prefixed URL. The fix is to use one or the other, not both."

---

## 9. BindingResult / Command Bean Error on Login JSP

### The Error

```
500 Internal Server Error
Neither BindingResult nor plain target object for bean name 'command' available as request attribute
```

### What Happened

My custom `login.jsp` used Spring MVC's `<form:form>` tag, which requires a model attribute (backing bean). Spring Security's login endpoint doesn't provide one.

### Why It Failed

- `<form:form>` needs a backing object in the model (default name: `command`)
- Spring Security's `/login` processing doesn't set up any model attributes
- When JSP renders, it can't find the bean and throws an error

### The Fix

Use a plain HTML `<form>` for the login page, not Spring's `<form:form>`:

```jsp
<!-- CORRECT: Plain HTML form for Spring Security login -->
<form method="post" action="${pageContext.request.contextPath}/login">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <input type="text" name="username" required />
    <input type="password" name="password" required />
    <input type="submit" value="Login" />
</form>
```

### Interview-Ready Answer

> "My login page threw a 500 error because I used Spring's `<form:form>` tag, which requires a model attribute. Spring Security's login mechanism doesn't provide one. The fix was to use a plain HTML `<form>` with `name='username'` and `name='password'` -- which is what Spring Security expects."

---

## 10. Spring Form Error Code Resolution Failure (500 Error)

### The Error

```
No message found under code 'error.user.user.email' for locale 'en_US'
```

### What Happened

When rejecting a value with `rejectValue()`, I passed an error code without having a `messages.properties` file:

```java
// PROBLEMATIC: Spring tries to look up "error.user" in messages.properties
result.rejectValue("email", "error.user", e.getMessage());
```

Spring tried to resolve the code `error.user.user.email` in `messages.properties` and threw `NoSuchMessageException` when it wasn't found.

### The Fix

Pass `null` as the error code to use the default message directly:

```java
// CORRECT: null = skip code lookup, use the message directly
result.rejectValue("email", null, "Email already exists");
result.rejectValue("username", null, "Username already exists");
```

Or define the codes in `src/main/resources/messages.properties`:

```properties
error.user.username=Username already exists
error.user.email=Email already exists
```

### Interview-Ready Answer

> "I got a 500 error because `rejectValue()` tries to resolve the error code from a message source. Without `messages.properties`, this fails. The quick fix is passing `null` as the error code, which tells Spring to use the default message string directly."

---

## 11. Browser Auto-filling Registration Form with Login Credentials

### The Problem

After logging in, the browser auto-filled the registration form's username and password fields with saved login credentials. The password field appeared empty visually but contained saved data.

### Why It Happens

Browsers remember credentials per domain and auto-fill any form with matching `name="username"` and `name="password"` fields.

### Possible Fixes

- Use `autocomplete="off"` on the form or input fields
- Use `autocomplete="new-password"` specifically on the password field
- Use different field names (though this complicates Spring Security integration)

---

## 12. Logout Message Without Query Parameters

### The Problem

The default `logoutSuccessUrl("/?logout")` puts an ugly `?logout` parameter in the URL.

### The Fix

Use a custom `logoutSuccessHandler` with session attributes:

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

Controller reads and removes the message:

```java
@GetMapping("/")
public String home(HttpServletRequest request, Model model) {
    if (request.getSession().getAttribute("logoutMessage") != null) {
        model.addAttribute("logoutMessage",
            request.getSession().getAttribute("logoutMessage"));
        request.getSession().removeAttribute("logoutMessage");
    }
    return "home/home";
}
```

**Result:** Clean URL (`/`) with a one-time message.

---

## 13. JSP Error Page: `${exception}` is null in Spring Boot

### The Problem

In my error JSP, `${exception}` was always `null` even though `isErrorPage="true"` was set.

### Why It Happens

- On **external Tomcat**, `isErrorPage="true"` makes the `exception` implicit variable available automatically
- On **Spring Boot embedded Tomcat**, the default `BasicErrorController` handles errors differently and does not inject the `exception` implicit variable

### The Fix

**Option A: Use `requestScope` in JSP**

```jsp
${requestScope['javax.servlet.error.exception'].class.name}
${requestScope['javax.servlet.error.exception'].message}
```

**Option B: Custom Error Controller (cleaner)**

```java
@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Throwable exception = (Throwable) request.getAttribute(
            RequestDispatcher.ERROR_EXCEPTION);
        if (exception != null) {
            model.addAttribute("exception", exception);
        }
        return "error/404";
    }
}
```

### Interview-Ready Answer

> "In Spring Boot, the `${exception}` JSP implicit variable doesn't work like it does on standalone Tomcat, because Spring Boot's `BasicErrorController` intercepts errors first. The fix is to either access the exception via `requestScope['javax.servlet.error.exception']` or create a custom `ErrorController` that passes the exception to the model."

---

## 14. Moving from In-Memory Auth to Database-Backed UserDetailsService

### The Journey

The commented-out code in `AuthorizeUrlsSecurityConfig` shows I initially used `InMemoryUserDetailsManager`:

```java
// BEFORE: Hardcoded users in memory
@Bean
UserDetailsService userStore(PasswordEncoder encoder) {
    var user1 = User.withUsername("user").password(encoder.encode("user123")).roles("USER").build();
    var user2 = User.withUsername("admin").password(encoder.encode("admin123")).roles("ADMIN").build();
    return new InMemoryUserDetailsManager(Arrays.asList(user1, user2));
}
```

### Why I Migrated

- In-memory users are lost on every restart
- Cannot register new users at runtime
- Not suitable for any real application
- Needed database-backed authentication with a registration flow

### What I Had to Do

1. Created a `User` entity implementing `UserDetails`
2. Created `UserRepository` with `findByUsername()`
3. Created a custom `UserDetailsService` implementation:

```java
@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
```

4. Spring auto-detects this `@Service` bean and uses it instead of `InMemoryUserDetailsManager`
5. Created `UserController` + registration form for self-service sign-up
6. Used `PasswordEncoder` (BCrypt) to hash passwords before saving

### Key Insight

Spring Security auto-discovers any `UserDetailsService` bean. You don't need to explicitly wire it into the security config -- just declare it as a `@Service` and Spring Boot does the rest.

### Interview-Ready Answer

> "I started with `InMemoryUserDetailsManager` for rapid prototyping, then migrated to a database-backed `UserDetailsService`. I created a custom implementation that loads users from JPA repository, and Spring Security auto-detected it. I also implemented a registration flow with BCrypt password encoding. The migration was smooth because Spring Security is designed to be pluggable -- you just swap the `UserDetailsService` implementation."

---

## 15. @PathVariable vs Hidden Form Field -- ID Mismatch Security Risk

### The Problem

In my edit handler, the course ID comes from both the URL path and the form body:

```java
@PostMapping("/{courseId}")
public String editCourseForm(
    @PathVariable Long courseId,              // from URL
    @ModelAttribute("course") CourseVO courseVo, // courseVo.getId() from hidden form field
    ...) {
    // I used courseVo.getId() but never validated it against courseId
}
```

### Why This Is Risky

A malicious user could:
1. Open the edit form for course 100 (`/courses/edit/100`)
2. Tamper with the hidden `id` field in the form to say `200`
3. Submit to `POST /courses/100` but the body says `id=200`
4. The service updates course 200 instead of 100

### The Fix

Validate that the URL ID matches the form ID:

```java
@PostMapping("/{courseId}")
public String editCourseForm(
    @PathVariable Long courseId,
    @Valid @ModelAttribute("course") CourseVO courseVo, ...) {

    if (!courseId.equals(courseVo.getId())) {
        throw new IllegalArgumentException("Course ID mismatch between URL and form");
    }
    // proceed safely
}
```

Or simply ignore the form's ID and always use the `@PathVariable`:

```java
courseVo.setId(courseId); // trust the URL, not the form
```

### Interview-Ready Answer

> "I noticed a potential security issue where the course ID in the URL path could differ from the hidden form field. A malicious user could tamper with the form data. The fix is to either validate both match, or always trust the `@PathVariable` from the URL and overwrite the form's ID."

---

## 16. Audit Column Configuration: `insertable` and `updatable`

### The Problem

When configuring audit fields (`createdDate`, `updatedDate`, `createdBy`, `updatedBy`), I needed to control which fields are set on INSERT vs UPDATE.

### What `insertable` and `updatable` Mean

```java
@Column(updatable = false)        // set on INSERT, never changed on UPDATE
private LocalDateTime createdDate;

@Column(insertable = false)       // NOT set on INSERT, only set on UPDATE
private LocalDateTime updatedDate;
```

| Attribute | INSERT | UPDATE |
|-----------|--------|--------|
| `insertable = true, updatable = true` (default) | Included | Included |
| `updatable = false` | Included | **Excluded** |
| `insertable = false` | **Excluded** | Included |
| `insertable = false, updatable = false` | Excluded | Excluded |

### My Audit Field Design

```java
@CreationTimestamp
@Column(updatable = false)          // set once at creation, never modified
private LocalDateTime createdDate;

@UpdateTimestamp
@Column(insertable = false)         // null on first insert, set on every update
private LocalDateTime updatedDate;
```

### Interview-Ready Answer

> "I used `@Column(updatable = false)` on `createdDate` so Hibernate never overwrites it on updates, and `@Column(insertable = false)` on `updatedDate` so it's null on creation and only populated on subsequent updates. Combined with `@CreationTimestamp` and `@UpdateTimestamp`, this gives automatic audit tracking."

---

## 17. H2 Console 403 Forbidden -- Spring Security Blocking Dev Tools

### The Error

```
403 Forbidden
```

When navigating to `http://localhost:8080/h2-console`, the page either shows a blank white screen or a 403 error. Even after login, the console loads but the internal frame content is blocked.

### Why It Happened (Three Separate Issues)

The H2 console is a web-based tool embedded in the application. Spring Security blocks it in **three** different ways:

| # | Issue | What It Blocks |
|---|-------|---------------|
| 1 | **URL Authorization** | `/h2-console/**` is not explicitly permitted, so depending on config it may require authentication |
| 2 | **CSRF Protection** | The H2 console makes internal POST requests (login, execute SQL) without CSRF tokens |
| 3 | **Frame Options** | The H2 console UI uses `<iframe>` internally, but Spring Security sends `X-Frame-Options: DENY` header by default |

### Debugging Journey

**Step 1:** Added `/h2-console/**` to `.permitAll()` -- the initial page loaded, but login inside the console returned 403.

**Step 2:** Realized the console's internal form doesn't include CSRF tokens (it's a third-party embedded tool). Disabled CSRF for H2 console paths.

**Step 3:** Console logged in, but the main content area was blank -- browser console showed `Refused to display in a frame because 'X-Frame-Options' is set to 'DENY'`. Had to allow same-origin frames.

### The Fix

Three changes in `SecurityConfig.java`:

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .authorizeHttpRequests(auth -> auth
            // ... other rules ...
            // Fix 1: Allow H2 console access without authentication (dev only)
            .requestMatchers("/h2-console/**").permitAll()
            .anyRequest().permitAll()
        )
        .formLogin(/* ... */)
        .logout(/* ... */)
        // Fix 2: Disable CSRF for H2 console (it uses POST internally
        //         and doesn't send CSRF tokens)
        .csrf(csrf -> csrf
            .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
        )
        // Fix 3: Allow H2 console to render in frames
        //         (it uses iframes for its internal UI)
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.sameOrigin())
        )
        .build();
}
```

### Why Each Fix Is Needed

**Fix 1 -- URL Authorization:**
Without `.requestMatchers("/h2-console/**").permitAll()`, accessing `/h2-console` may require authentication depending on your default rule. The `/**` wildcard is important because the console serves sub-resources like `/h2-console/login.do` and `/h2-console/header.jsp`.

**Fix 2 -- CSRF Exception:**
The H2 console is a standalone web UI embedded inside your app. It submits forms (login, SQL execution) via POST but has no knowledge of Spring Security's CSRF tokens. Without this exception, every POST inside the console gets 403.

```java
// AntPathRequestMatcher is used because it supports ** wildcard matching
.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
```

**Fix 3 -- Frame Options:**
The H2 console renders its UI using HTML `<iframe>` elements. By default, Spring Security adds `X-Frame-Options: DENY` to all responses, which tells the browser to refuse rendering any page inside a frame. Changing to `sameOrigin()` allows frames only from the same domain.

| Frame Option | What It Does |
|-------------|-------------|
| `DENY` (default) | No page can be loaded in a frame -- blocks H2 console |
| `SAMEORIGIN` | Frames allowed only from the same origin -- H2 console works |
| `DISABLE` | No header sent -- not recommended for security |

### Security Warning

These H2 console security exceptions should **only be active in development**. In production:
- Set `spring.h2.console.enabled=false` in production properties
- Or use Spring profiles to load different security configs per environment
- Never expose the H2 console on a public-facing server

### Interview-Ready Answer

> "I got 403 Forbidden when accessing the H2 console, which turned out to be three separate Spring Security issues. First, the URL wasn't permitted -- I added `/h2-console/**` to `permitAll()`. Second, the console makes internal POST requests without CSRF tokens, so I had to exclude those paths from CSRF protection using `ignoringRequestMatchers()`. Third, the console uses iframes internally, but Spring Security's default `X-Frame-Options: DENY` header blocked them -- I changed it to `sameOrigin()`. This taught me that Spring Security's defense-in-depth means multiple layers can independently block a request, and you need to address each one."

---

## 18. Delete Not Working -- `@Transactional(readOnly = true)` on Write Operation

### The Problem

Deleting a course appeared to succeed (no error was thrown), but the course was still there after the operation. The delete was silently ignored.

### What Happened

The `deleteCourseById()` method was annotated with `@Transactional(readOnly = true)`:

```java
// WRONG: readOnly = true prevents any write operations
@Override
@Transactional(readOnly = true)
public void deleteCourseById(Long courseId) {
    if (!courseRepository.existsById(courseId)) {
        throw new CourseNotFoundException("Course with ID " + courseId + " not found");
    }
    courseRepository.deleteById(courseId);
}
```

### Why It Failed Silently

When `readOnly = true` is set, several things happen:

1. **Hibernate sets FlushMode to MANUAL** -- dirty checking and auto-flush are disabled, so pending changes (including deletes) are never flushed to the database
2. **JDBC connection is set to read-only** -- the database driver may optimize for reads and silently discard writes
3. **No exception is thrown** -- this is the tricky part. The `deleteById()` call executes in Java, but the actual SQL `DELETE` statement is never sent to the database

This makes it an especially hard bug to catch because there's no error, no stack trace -- it just doesn't work.

### The Fix

Use `@Transactional` (without `readOnly`) for any method that modifies data:

```java
// CORRECT: Default @Transactional allows read + write
@Override
@Transactional
public void deleteCourseById(Long courseId) {
    if (!courseRepository.existsById(courseId)) {
        throw new CourseNotFoundException("Course with ID " + courseId + " not found");
    }
    courseRepository.deleteById(courseId);
}
```

### When to Use Each

| Operation | Annotation | Why |
|-----------|-----------|-----|
| `findAll()`, `findById()`, `count()`, `existsBy...()` | `@Transactional(readOnly = true)` | Read-only hint optimizes performance -- Hibernate skips dirty checking, DB can use read replicas |
| `save()`, `update()`, `delete()` | `@Transactional` | Must allow writes -- Hibernate needs to flush changes to DB |

### How `readOnly = true` Optimizes Reads

```
readOnly = true
  └── Hibernate: FlushMode.MANUAL (skip dirty checking = faster)
  └── JDBC: connection.setReadOnly(true) (DB can route to read replica)
  └── Result: Better performance for read-heavy operations
```

### Interview-Ready Answer

> "My delete operation was silently failing because I accidentally used `@Transactional(readOnly = true)`. This tells Hibernate to set the flush mode to MANUAL, so the DELETE SQL was never sent to the database -- and no exception was thrown. The fix was simply removing `readOnly = true`. The lesson is: `readOnly = true` is a performance optimization for read operations only. Any method that inserts, updates, or deletes must use the default `@Transactional` to ensure changes are flushed and committed."

---

## Quick Reference: Common 403/404/500 Causes in Spring MVC

| Error | Common Cause | Fix |
|-------|-------------|-----|
| **403** | Missing CSRF token in POST form | Add `${_csrf.parameterName}` / `${_csrf.token}` |
| **403** | No `formLogin()` configured | Enable `.formLogin()` in security config |
| **403** | Wrong role prefix | `hasRole("ADMIN")` expects `ROLE_ADMIN` authority |
| **403** | H2 console blocked by Security | Permit URL + disable CSRF + allow sameOrigin frames |
| **404** | Double context path in URLs | Don't combine `<c:url>` with `${pageContext.request.contextPath}` |
| **404** | Path variable matching wrong handler | Use specific URL patterns, avoid ambiguity |
| **500** | Missing `BindingResult` after `@Valid` | Add `BindingResult` immediately after the validated parameter |
| **500** | `<form:form>` without model attribute | Use plain HTML form for Spring Security login |
| **500** | Message code not in `messages.properties` | Pass `null` as error code in `rejectValue()` |
