# v0.2.0 Migration -- JSP → Thymeleaf, Testing, Model Redesign

> Everything that changed in v0.2.0, why it changed, challenges faced during migration,
> and interview-ready explanations for each decision.

---

## Table of Contents

1. [What Changed (Summary)](#1-what-changed-summary)
2. [JSP → Thymeleaf Migration](#2-jsp--thymeleaf-migration)
3. [Model Redesign: VO → DTO + Registration DTO](#3-model-redesign-vo--dto--registration-dto)
4. [Controller Rewrite](#4-controller-rewrite)
5. [Security Fixes](#5-security-fixes)
6. [Testing (New)](#6-testing-new)
7. [Code Cleanup](#7-code-cleanup)
8. [Migration Challenges](#8-migration-challenges)
9. [Thymeleaf vs JSP -- Quick Reference](#9-thymeleaf-vs-jsp----quick-reference)
10. [Testing FAQ](#10-testing-faq)
11. [Interview Questions for v0.2.0](#11-interview-questions-for-v020)

---

## 1. What Changed (Summary)

| Area | v0.1.0 (Before) | v0.2.0 (After) |
|------|-----------------|-----------------|
| View Engine | JSP + JSTL + Spring Form Tags | **Thymeleaf** |
| Form DTO | `CourseVO` | **`CourseDTO`** (industry naming) |
| User Registration | Entity (`User`) as form bean | **`UserRegistrationDTO`** (separation of concerns) |
| Role Assignment | User picks from dropdown (including ADMIN!) | **Hardcoded to STUDENT** (security fix) |
| CSRF Handling | Manual `<input type="hidden" name="${_csrf...">` | **Automatic** (Thymeleaf handles it) |
| XSS Protection | Unescaped `${variable}` in JSP (vulnerable) | **Auto-escaped** by `th:text` |
| Testing | 1 test (`contextLoads()`) | **20+ tests** (unit, controller, repository) |
| Dead Code | Unused exceptions, commented blocks, stale imports | **Removed** |
| URL Design | `/courses/add`, `/courses/edit/{id}` | **`/courses/new`**, **`/courses/{id}/edit`** (RESTful) |
| `@Transactional` | Missing on some methods | **Consistent** across all service methods |
| Static Resources | `webapp/css/`, `webapp/js/` | **`resources/static/css/`**, **`resources/static/js/`** |
| Dependencies | JSP (Jasper, JSTL, Security Taglibs) | **Thymeleaf + thymeleaf-extras-springsecurity6** |

---

## 2. JSP → Thymeleaf Migration

### Why Thymeleaf Over JSP?

| Aspect | JSP | Thymeleaf |
|--------|-----|-----------|
| File type | `.jsp` (not valid HTML) | `.html` (valid HTML -- "Natural Templates") |
| IDE Support | Limited (needs server to preview) | Full HTML support, can preview in browser |
| CSRF tokens | Manual hidden field in every form | **Automatic** with `th:action` |
| XSS protection | `${var}` is NOT escaped by default | `th:text` auto-escapes HTML entities |
| Spring Boot support | Needs extra deps (Jasper, JSTL) | First-class starter (`spring-boot-starter-thymeleaf`) |
| Template location | `webapp/WEB-INF/views/` | `resources/templates/` (classpath) |
| Static resources | `webapp/css/`, `webapp/js/` | `resources/static/` |
| Packaging | Works best as WAR | Works as JAR (no servlet container needed) |
| Security integration | `spring-security-taglibs` | `thymeleaf-extras-springsecurity6` |

### Key Syntax Differences

```
JSP:                                    Thymeleaf:
─────────────────────────────────────   ─────────────────────────────────
<%@ include file="header.jsp" %>        th:replace="~{fragments/header :: header}"
${variable}                             th:text="${variable}"
<c:if test="${condition}">              th:if="${condition}"
<c:forEach var="x" items="${list}">     th:each="x : ${list}"
<c:url value='/path'/>                  @{/path}
<frm:form modelAttribute="obj">        th:object="${obj}"
<frm:input path="field"/>              th:field="*{field}"
<frm:errors path="field"/>             th:errors="*{field}"
<sec:authorize access="hasRole()">     sec:authorize="hasRole()"
${_csrf.parameterName}                  (automatic with th:action)
${pageContext.request.contextPath}      (automatic with @{})
```

### What We Removed from pom.xml

```xml
<!-- REMOVED: JSP dependencies -->
<dependency>org.eclipse.jetty:apache-jstl</dependency>
<dependency>org.springframework.security:spring-security-taglibs</dependency>
<dependency>org.apache.tomcat.embed:tomcat-embed-jasper</dependency>

<!-- ADDED: Thymeleaf dependencies -->
<dependency>org.springframework.boot:spring-boot-starter-thymeleaf</dependency>
<dependency>org.thymeleaf.extras:thymeleaf-extras-springsecurity6</dependency>
```

### What Changed in application.properties

```properties
# REMOVED:
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp

# NOT NEEDED (Spring Boot auto-configures Thymeleaf):
# Templates are resolved from classpath:/templates/ with .html suffix
```

---

## 3. Model Redesign: VO → DTO + Registration DTO

### CourseVO → CourseDTO

**Why rename?**
- "VO" (Value Object) has a specific meaning in Domain-Driven Design (immutable, equality by value)
- Our object is mutable and used for data transfer → **DTO** (Data Transfer Object) is the correct term
- Industry standard: most Spring projects use `*DTO`, `*Request`, `*Response`

### User Entity → UserRegistrationDTO

**Before (v0.1.0):** The `User` entity was used directly as the form-backing bean:
```java
// PROBLEM: Entity used for form binding
@PostMapping("/register")
public String register(@Valid @ModelAttribute("user") User user, ...)
```

**Problems:**
1. Validation annotations cluttered the entity
2. User could set `role` via the form dropdown (ADMIN access!)
3. Entity has fields users shouldn't control (id, enabled, version, timestamps)
4. Password was stored as-is, then encoded -- mixing concerns

**After (v0.2.0):** Separate DTO for the form:
```java
// UserRegistrationDTO has only user-fillable fields (no role, no id)
@PostMapping("/register")
public String register(@Valid @ModelAttribute("registrationDTO") UserRegistrationDTO dto, ...)
```

The service layer maps DTO → Entity and hardcodes `Role.STUDENT`.

---

## 4. Controller Rewrite

### URL Design Changes

| Action | v0.1.0 | v0.2.0 | Why |
|--------|--------|--------|-----|
| Create form | `/courses/add` | `/courses/new` | More RESTful |
| Edit form | `/courses/edit/{id}` | `/courses/{id}/edit` | Resource-centric URL |
| Delete | `/courses/delete/{id}` | `/courses/{id}/delete` | Consistent pattern |

### Security Fix: Trust URL, Not Form

```java
// v0.1.0: Used form's hidden ID (tamper-able)
courseVo.getId()

// v0.2.0: Trust the URL path variable
courseDTO.setId(id); // Override form ID with URL ID
```

---

## 5. Security Fixes

| Fix | Before | After |
|-----|--------|-------|
| Role escalation | Users could register as ADMIN via dropdown | Role hardcoded to `STUDENT` in service |
| XSS in course title | `${course.title}` unescaped in JSP | `th:text` auto-escapes in Thymeleaf |
| CSRF manual tokens | Required manual hidden field in every form | Thymeleaf `th:action` auto-inserts token |
| Form ID tampering | No validation between URL and form ID | `courseDTO.setId(id)` trusts URL |
| Password logging | User entity with password in toString() | `@ToString(exclude="password")` on DTO too |
| Redirect hardcoded | `response.sendRedirect("/")` | `response.sendRedirect(request.getContextPath() + "/")` |

---

## 6. Testing (New)

### Test Architecture

```
src/test/java/com/eduproject/
├── service/
│   ├── CourseServiceImplTest.java     ← Unit tests (Mockito)
│   └── UserServiceImplTest.java       ← Unit tests (Mockito)
├── controller/
│   └── CourseControllerTest.java      ← Web layer tests (@WebMvcTest)
└── repository/
    └── CourseRepositoryTest.java      ← JPA tests (@DataJpaTest)
```

### Three Types of Tests

| Type | Annotation | What It Tests | Speed |
|------|-----------|---------------|-------|
| **Unit** | `@ExtendWith(MockitoExtension.class)` | Service logic in isolation | ~100ms |
| **Web/Controller** | `@WebMvcTest` | HTTP request → controller → view | ~2s |
| **Repository** | `@DataJpaTest` | JPA queries against real H2 database | ~3s |

### Key Testing Patterns

**Mockito (Unit Tests):**
```java
@Mock                          // Fake dependency
CourseRepository courseRepository;

@InjectMocks                   // Real class, injected with fakes
CourseServiceImpl courseService;

when(repo.findById(1L))        // Arrange: define mock behavior
    .thenReturn(Optional.of(entity));

courseService.getCourseById(1L) // Act: call method under test

verify(repo).findById(1L);     // Assert: verify interaction
```

**MockMvc (Controller Tests):**
```java
mockMvc.perform(post("/courses")
        .with(csrf())              // Required for POST (Spring Security)
        .param("title", "Java"))   // Simulate form fields
    .andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrl("/courses"));
```

**@WithMockUser (Security Tests):**
```java
@WithMockUser(roles = "ADMIN")  // Simulate logged-in admin
void adminShouldSeeForm() { ... }

// Without @WithMockUser → request gets 302 redirect to /login
```

---

## 7. Code Cleanup

| Removed | Why |
|---------|-----|
| `EmailAlreadyExistsException.java` | Never thrown; `rejectValue()` used instead |
| `UserNameAlreadyExists.java` | Same as above |
| `CourseVO.java` | Replaced by `CourseDTO.java` |
| `InMemoryUserDetailsManager` block in SecurityConfig | Replaced by DB-backed auth in v0.1.0 |
| Unused imports (9 across files) | Clean code |
| `@Autowired` on final fields | Redundant with `@RequiredArgsConstructor` |
| Typos in log messages | `"courese"` → fixed |
| Informal comments / TODOs | Replaced with Javadoc |

---

## 8. Migration Challenges

### Challenge 1: Thymeleaf Fragment Includes vs JSP Includes

**JSP approach:** `<%@ include file="header.jsp" %>` literally copy-pastes the file content at compile time. The header opens `<html><head><body>` and the footer closes them.

**Thymeleaf approach:** Each page is a **complete, valid HTML file**. Fragments replace specific elements:
```html
<!-- Each page has its own <html>, <head>, <body> -->
<header th:replace="~{fragments/header :: header}"></header>
```

**Challenge:** Had to restructure from "header opens tags, footer closes them" to "each page is self-contained with fragment replacements."

### Challenge 2: Spring Form Tags → Thymeleaf Object Binding

**JSP:**
```jsp
<frm:form modelAttribute="course">
    <frm:input path="title"/>
    <frm:errors path="title"/>
</frm:form>
```

**Thymeleaf:**
```html
<form th:object="${courseDTO}">
    <input th:field="*{title}"/>
    <span th:errors="*{title}"></span>
</form>
```

**Challenge:** The model attribute name changed from `"course"` to `"courseDTO"`. Every controller method and template reference needed updating. Missed references cause runtime errors (no compile-time checking).

### Challenge 3: CSRF Token Handling

In JSP, forgetting the CSRF hidden field caused 403 errors. In Thymeleaf, `th:action` automatically adds it. But for plain `action=` (without `th:`), you still need to add it manually.

### Challenge 4: Security Namespace in Thymeleaf

JSP: `<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>`

Thymeleaf: `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"`

Different namespace URI. Also needs `thymeleaf-extras-springsecurity6` dependency (not `spring-security-taglibs`).

### Challenge 5: Static Resource Location Change

JSP served static files from `webapp/`. Thymeleaf serves from `resources/static/`.

URL references changed:
```
JSP:       ${pageContext.request.contextPath}/css/style.css
Thymeleaf: th:href="@{/css/style.css}"
```

---

## 9. Thymeleaf vs JSP -- Quick Reference

| Feature | JSP Syntax | Thymeleaf Syntax |
|---------|-----------|-----------------|
| Output text | `${name}` | `th:text="${name}"` |
| Conditional | `<c:if test="${x}">` | `th:if="${x}"` |
| Else | `<c:choose>/<c:otherwise>` | `th:unless="${x}"` |
| Loop | `<c:forEach var="i" items="${list}">` | `th:each="i : ${list}"` |
| URL | `<c:url value='/path'/>` | `@{/path}` |
| URL with var | `${pageContext.request.contextPath}/courses/${id}` | `@{/courses/{id}(id=${course.id})}` |
| Form binding | `<frm:form modelAttribute="obj">` | `th:object="${obj}"` |
| Input binding | `<frm:input path="name"/>` | `th:field="*{name}"` |
| Error display | `<frm:errors path="name"/>` | `th:errors="*{name}"` |
| Fragments | `<%@ include file="x.jsp" %>` | `th:replace="~{fragments/x :: y}"` |
| Security check | `<sec:authorize access="hasRole('X')">` | `sec:authorize="hasRole('X')"` |
| CSRF token | Manual hidden input | **Automatic** with `th:action` |
| Context path | `${pageContext.request.contextPath}` | **Automatic** with `@{}` |
| Null-safe | `${obj.field}` (NPE risk) | `${obj?.field}` (safe navigation) |

---

## 10. Testing FAQ

### Q: Why use @Mock instead of @Autowired for service tests?

**A:** `@Mock` creates a pure fake with no Spring context. `@Autowired` requires `@SpringBootTest` which loads the entire application (database, security, all beans). Unit tests should be fast and isolated -- `@Mock` + `@InjectMocks` tests ONLY the service logic.

### Q: What's the difference between @Mock and @MockitoBean?

| Annotation | Context | Used In |
|-----------|---------|---------|
| `@Mock` | Pure Mockito (no Spring) | Unit tests (`@ExtendWith(MockitoExtension.class)`) |
| `@MockitoBean` | Spring context | Integration tests (`@WebMvcTest`, `@SpringBootTest`) |

`@MockitoBean` registers the mock in the Spring application context, replacing the real bean.

### Q: Why .with(csrf()) in controller tests?

Spring Security enables CSRF by default. POST/PUT/DELETE requests without a valid token get 403 Forbidden. `.with(csrf())` adds a test CSRF token to the request.

### Q: Why @WithMockUser?

Without it, all requests to secured endpoints return 302 (redirect to login). `@WithMockUser(roles = "ADMIN")` simulates an authenticated user so the controller code actually executes.

### Q: What does @DataJpaTest give us?

- Auto-configures an embedded H2 database
- Scans for `@Entity` classes and JPA repositories
- Each test runs in a transaction that rolls back automatically
- Does NOT load controllers, services, or security

### Q: Why test findByTitle() if Spring generates it?

Spring Data generates the query from the method name. If we rename a field in the entity and forget to update the method name, the app fails at runtime. The test catches this at build time.

---

## 11. Interview Questions for v0.2.0

### Q: "Why did you migrate from JSP to Thymeleaf?"

> "JSP files aren't valid HTML, can't be previewed without a server, and require manual CSRF token handling. Thymeleaf produces natural HTML templates that work in any browser, auto-escapes output to prevent XSS, and auto-handles CSRF tokens. It's also the recommended view technology for Spring Boot -- JSP requires extra dependencies and doesn't work with JAR packaging."

### Q: "How do you handle form validation?"

> "I use Bean Validation annotations on DTOs (`@NotBlank`, `@Size`, `@Email`), combined with `@Valid` and `BindingResult` in the controller. For business rules like uniqueness checks, I use `BindingResult.rejectValue()` to show all errors at once. Thymeleaf displays field errors with `th:errors='*{field}'`."

### Q: "Why separate DTO from Entity?"

> "The entity has JPA concerns (audit fields, version, relationships) that the form shouldn't expose. The DTO has validation concerns that don't belong on the entity. Using the entity as a form bean also lets users set fields they shouldn't -- like in my case where users could register as ADMIN. The DTO gives us a clean boundary."

### Q: "Explain your testing strategy."

> "I use three layers: unit tests with Mockito for service logic (fast, isolated), `@WebMvcTest` for controller behavior (HTTP handling, validation, redirects), and `@DataJpaTest` for repository queries (validates entity mapping and custom queries). Each layer tests one concern. I chose this over `@SpringBootTest` because it's faster and failures point to the exact layer."

### Q: "How did you handle the security issue with user roles?"

> "In v0.1.0, the registration form had a role dropdown where users could select ADMIN. I fixed this by creating a `UserRegistrationDTO` that doesn't include a role field. The service layer hardcodes `Role.STUDENT`. I also wrote a specific unit test to verify this: `shouldAlwaysAssignStudentRole()`. This is defense in depth -- even if someone crafts a malicious POST with `role=ADMIN`, the service ignores it."

---

## File Changes Summary

### New Files
- `src/main/java/.../model/CourseDTO.java`
- `src/main/java/.../model/UserRegistrationDTO.java`
- `src/main/resources/templates/**/*.html` (9 template files)
- `src/main/resources/static/css/style.css`
- `src/main/resources/static/js/app.js`
- `src/test/java/.../service/CourseServiceImplTest.java`
- `src/test/java/.../service/UserServiceImplTest.java`
- `src/test/java/.../controller/CourseControllerTest.java`
- `src/test/java/.../repository/CourseRepositoryTest.java`
- `V2_MIGRATION.md` (this file)

### Modified Files
- `pom.xml` (version 0.2.0, swapped JSP → Thymeleaf deps)
- `application.properties` (removed JSP config)
- All controllers (rewritten)
- All services (CourseDTO, UserRegistrationDTO, added @Transactional)
- `SecurityConfig.java` (cleaned, fixed redirect)
- `InsertDataInDB.java` (removed @Autowired, fixed logs)
- `GlobalExceptionHandler.java` (proper logging)
- `CourseEntity.java` (cleaned comments)
- `User.java` (moved validation to DTO)

### Deleted Files
- `CourseVO.java` (replaced by CourseDTO)
- `EmailAlreadyExistsException.java` (unused)
- `UserNameAlreadyExists.java` (unused)
