# Improvements Applied to EduPro

> Summary of code improvements made to the project. These focus on fixing bugs, following conventions, and improving maintainability — without adding major new features.

---

## Table of Contents

1. [Critical Bug Fixes](#1-critical-bug-fixes)
2. [Conventions & Naming](#2-conventions--naming)
3. [Code Quality](#3-code-quality)
4. [Security & Validation](#4-security--validation)
5. [Data Seeding](#5-data-seeding)
6. [Enroll Flow & Security](#6-enroll-flow--security)
7. [Cleanup](#7-cleanup)

---

## 1. Critical Bug Fixes

### 1.1 CourseServiceImpl — `enrollUser` (CRITICAL)

**Problem:** Two bugs in enrollment logic:
- Used `.get()` on `Optional` without checking — could throw `NoSuchElementException`
- Replaced entire `enrolledUsers` list with a single user instead of **adding** to it

**Before:**
```java
this.courseRepository.findById(courseId).get()
    .setEnrolledUsers(List.of(userRepository.findByUsername(username).get()));
```

**After:**
```java
CourseEntity course = courseRepository.findById(courseId)
    .orElseThrow(() -> new CourseNotFoundException(...));
User user = userRepository.findByUsername(username)
    .orElseThrow(() -> new UserNotFoundException(...));
List<User> enrolled = course.getEnrolledUsers() != null
    ? new ArrayList<>(course.getEnrolledUsers())
    : new ArrayList<>();
enrolled.add(user);
course.setEnrolledUsers(enrolled);
courseRepository.save(course);
```

### 1.2 CourseServiceImpl — `isCourseAlreadyEnrolled`

**Problem:** `course.getEnrolledUsers()` could be `null` → `NullPointerException`. Also used `contains()` which relies on object identity; switched to ID comparison.

**Fix:** Null check + `stream().anyMatch(u -> u.getId().equals(user.getId()))`.

### 1.3 UserServiceImpl — `updateUser` (CRITICAL)

**Problem:** User entity was modified but **never saved**. Changes were lost.

**Fix:** Added `userRepository.save(user)` before returning.

### 1.4 UserController — `editUser`

**Problem:** On validation error:
- Redirected to `redirect:/users/allUsers` (wrong URL — would 404)
- Lost form data and validation errors

**Fix:** Return view name `"user/editUser"` directly with model, so form is redisplayed with errors.

---

## 2. Conventions & Naming

### 2.1 `InsertDataInDB` → `DataSeeder`

- **Why:** Name describes purpose, not implementation. Works with any database (H2, Oracle).
- **File:** `runner/DataSeeder.java` (replaced `InsertDataInDB.java`).

### 2.2 `UserDetailService` → `CustomUserDetailsService`

- **Why:** Matches Spring's `UserDetailsService` (plural "Details"); "Custom" differentiates from built-in implementations.
- **File:** `config/CustomUserDetailsService.java` (replaced `UserDetailService.java`).

### 2.3 CourseController — Use Interface, Not Implementation

**Before:** `private final CourseServiceImpl courseService;`  
**After:** `private final CourseService courseService;`

- **Why:** Dependency inversion — depend on abstraction, not concrete class. Easier to test and swap implementations.

---

## 3. Code Quality

### 3.1 UserServiceImpl — Missing `@Transactional`

Added `@Transactional(readOnly = true)` to:
- `getAllUsers()`
- `existsByEmailExcludingCurrentUser()`
- `existsByUsernameExcludingCurrentUser()`

### 3.2 UserController — ID Mismatch Security Fix

**Before:** `if (!id.equals(userRespDTO.getId())) { //do something }` — did nothing.  
**After:** `userRespDTO.setId(id)` — trust URL path, not form (prevents tampering).

### 3.3 GlobalExceptionHandler — UserNotFoundException

Added handler for `UserNotFoundException` → returns 404 page (same as `CourseNotFoundException`).

### 3.4 HomeController — Removed Dead Code

Removed `@PostMapping("/logout")` — Spring Security already handles `/logout`. The method was never reached.

---

## 4. Security & Validation

### 4.1 UserResponseDTO — Validation for Edit Form

Enabled validation annotations for edit form:
- `@NotBlank` on username, firstName, email
- `@Size` constraints
- `@Email` on email

### 4.2 editUser.html — Error Display & Security Namespace

- Added `th:errors` for firstName, lastName, email
- Fixed `sec:authorize` namespace: `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"`
- Fixed role check: `hasRole('ADMIN')` (Spring adds `ROLE_` prefix; `hasRole('ROLE_ADMIN')` was wrong)

---

## 5. Data Seeding

### 5.1 Idempotency

**Problem:** Seeder ran on every startup → duplicate key errors on second run.

**Fix:** Skip if data exists:
```java
if (userRepository.count() > 0) {
    log.info("Database already seeded, skipping.");
    return;
}
```

### 5.2 Correct Entity Relationships

**Problem:** Python course created a transient `User` inline in `CourseEntity.builder()` — could cause persistence issues.

**Fix:** Create all users first, save them, then use persisted `User` for enrollment.

### 5.3 CourseEntity — Remove Cascade on enrolledUsers (CRITICAL)

**Problem:** `detached entity passed to persist: com.eduproject.model.User` when DataSeeder saved a course with enrolled users. `@OneToMany(cascade = CascadeType.ALL)` caused Hibernate to cascade PERSIST to already-persisted users.

**Fix:** Removed cascade from `CourseEntity.enrolledUsers`:

```java
// Before: cascade = CascadeType.ALL (caused "detached entity passed to persist")
@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
private List<User> enrolledUsers;

// After: no cascade — enrolled users are already persisted
@OneToMany(fetch = FetchType.LAZY)
private List<User> enrolledUsers;
```

---

## 6. Enroll Flow & Security

### 6.1 Course View — Conditional Enroll UI (IMPORTANT)

**Problem:** The Register/Enroll button showed for everyone. Anonymous users who clicked it were redirected to login by Spring Security, but after login they landed on `/courses` (default) — not the course page. They had to navigate back and click again. Poor UX.

**Fix:** Three distinct UI states based on auth + enrollment:

| User State | UI | Reasoning |
|------------|-----|-----------|
| **Anonymous** | "Login to Enroll" link → `/login?redirect=/courses/{id}` | Preserves intent; after login, user returns to course page |
| **Authenticated, not enrolled** | "Enroll" button (POST with CSRF) | Only show when they can actually enroll |
| **Authenticated, enrolled** | "Enrolled" badge | Prevents duplicate enrollment attempts; clear feedback |

**Why this matters:** Without the redirect param, users lose context. The "Login to Enroll" link makes the flow clear and reduces friction.

### 6.2 Controller — Pass `isEnrolled` to View

**Change:** `viewCourse()` now takes `Principal` and calls `courseService.isCourseAlreadyEnrolled(id, principal.getName())` to set `isEnrolled` in the model.

**Why:** The view needs to know whether the current user is already enrolled so it can show the correct UI (Enroll button vs Enrolled badge).

### 6.3 Login — Preserve Redirect Param

**Problem:** When user lands on `/login?redirect=/courses/5`, the redirect param is in the URL. After submitting the form (POST), the param is lost — POST body only has username/password.

**Fix:** Hidden field in login form:
```html
<input type="hidden" name="redirect" th:value="${param.redirect != null ? param.redirect[0] : ''}" />
```

**Why:** The redirect value must be sent back with the login POST so the success handler knows where to send the user.

### 6.4 SecurityConfig — Custom Login Success Handler

**Change:** Added `AuthenticationSuccessHandler` that reads the `redirect` param and redirects there after successful login.

**Security (IMPORTANT):** Open redirect protection — only allow internal paths:
```java
if (redirectUrl != null && !redirectUrl.isBlank()
        && redirectUrl.startsWith("/") && !redirectUrl.startsWith("//")) {
```
- `startsWith("/")` — only relative paths (our app)
- `!startsWith("//")` — blocks `//evil.com` (protocol-relative URL)

**Why:** Without this check, an attacker could craft `/login?redirect=//evil.com` and redirect users to a malicious site after login.

### 6.5 Security Rule Order

**Why it matters:** `POST /courses/enroll` (authenticated) must be declared **before** `POST /courses/*` (ADMIN). Both match `/courses/enroll` — Ant's `*` matches one segment. The first matching rule wins. If `POST /courses/*` came first, enroll would require ADMIN instead of any authenticated user.

### 6.6 CSS — badge-success

Added `.badge-success` for the "Enrolled" badge (green styling).

---

## 7. Cleanup

| Item | Change |
|------|--------|
| CourseController | Removed commented code, unused `CourseRepository` import |
| CourseController | Fixed typo: "Please login! First Before Enrolling!" → "Please log in first before enrolling." |
| UserResponseDTO | Removed unused `LocalDateTime` import |
| UserResponseDTO | Fixed Javadoc typo: "hard form" → "hardcoded" |
| editUser.html | Fixed comment typo: "crating" → "Hidden fields for id and username" |
| CustomUserDetailsService | Changed `log.info` to `log.debug` for loadUserByUsername (reduces log noise) |

---

## Files Changed

| Action | File |
|--------|------|
| Modified | `CourseController.java` |
| Modified | `CourseServiceImpl.java` |
| Modified | `UserController.java` |
| Modified | `UserServiceImpl.java` |
| Modified | `UserResponseDTO.java` |
| Modified | `GlobalExceptionHandler.java` |
| Modified | `CourseEntity.java` |
| Modified | `HomeController.java` |
| Modified | `editUser.html` |
| Modified | `course/view.html` |
| Modified | `auth/login.html` |
| Modified | `SecurityConfig.java` |
| Modified | `style.css` |
| Created | `DataSeeder.java` |
| Deleted | `InsertDataInDB.java` |
| Created | `CustomUserDetailsService.java` |
| Deleted | `UserDetailService.java` |

---

## Testing

After these changes, run:

```bash
mvn clean test
```

All existing tests should pass. Manual verification:
- User edit form: validation errors display, profile updates persist
- Course enrollment: adds user to list correctly, no duplicate enrollments
- App restart: no duplicate key errors from seeder
