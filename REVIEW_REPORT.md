# EduPro Project Review Report

**Review Date:** February 16, 2025  
**Scope:** JSP views, CSS, JS, resources, config files, pom.xml

---

## 1. BROKEN LINKS / REFERENCES

### 1.1 `src/main/webapp/WEB-INF/views/error.jsp` — Line 9
**Problem:** References non-existent CSS file.
```html
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
```
**Fix:** Change to `css/style.css` (the project's actual stylesheet), or remove this file if unused (see §6.1).

---

### 1.2 `src/main/webapp/WEB-INF/views/user/user-form.jsp` — Lines 23–26
**Problem:** Edit mode references non-existent endpoint `/users/edit`.
```jsp
<c:when test="${isEdit}">
    <c:url value="/users/edit" var="formAction" />
</c:when>
```
**Fix:** `UserController` has no `/users/edit` GET or POST. Either:
- Implement the edit endpoint and related logic, or
- Remove edit-mode logic and always use `/users/register` for registration.

---

## 2. HARDCODED PATHS / CONTEXT PATH

### 2.1 `src/main/java/com/eduproject/config/SecurityConfig.java` — Line 50
**Problem:** Hardcoded redirect path in logout success handler.
```java
response.sendRedirect("/");
```
**Fix:** Use context path for portability:
```java
response.sendRedirect(request.getContextPath() + "/");
```

---

## 3. MISSING CSRF TOKENS

**Status:** All POST forms include CSRF tokens:
- `header.jsp` (logout form) — ✓
- `login.jsp` — ✓
- `course-form.jsp` — ✓
- `course-list.jsp` (delete form) — ✓
- `user-form.jsp` — ✓

No CSRF issues found.

---

## 4. INCONSISTENT STYLING / CLASS USAGE

### 4.1 `src/main/webapp/WEB-INF/views/home/home.jsp` — Lines 5–9
**Problem:** Inline styles instead of utility classes.
```html
<h1 class="page-title" style="font-size: 1.8rem; margin-bottom: 0.5rem;">
<p class="page-subtitle" style="font-size: 1rem;">
```
**Fix:** Use existing utility classes (e.g. `.text-center`, `.mb-1`) or add new ones in `style.css` instead of inline styles.

---

### 4.2 `src/main/webapp/WEB-INF/views/error/500.jsp` — Line 6
**Problem:** Inline style overrides `.error-code` color.
```html
<div class="error-code" style="color: #e67e22;">500</div>
```
**Fix:** Add a modifier class in CSS (e.g. `.error-code.error-500`) and use it instead of inline style.

---

### 4.3 `src/main/webapp/WEB-INF/views/course/course-list.jsp` — Lines 57–58
**Problem:** Inline styles on course title link.
```html
<a href="..." style="font-weight: 500; color: #2980b9;">
```
**Fix:** Add a class such as `.course-title-link` in `style.css` and use it here.

---

## 5. MISSING SECURITY TAGS / SECURITY ISSUES

### 5.1 `src/main/webapp/WEB-INF/views/user/user-form.jsp` — Lines 68–77
**Problem:** Role dropdown is visible and editable during registration. Users can select `ROLE_ADMIN`.
```jsp
<div class="form-group">
    <frm:label path="role">Role</frm:label>
    <frm:select path="role">
        ...
    </frm:select>
</div>
```
**Fix:** For self-registration, either:
- Remove the role field and set `ROLE_USER` in the controller, or
- Show the role field only when `sec:authorize access="hasRole('ADMIN')"` (admin editing users).

---

### 5.2 `src/main/webapp/WEB-INF/views/course/course-view.jsp` — Line 43
**Problem:** Edit button is correctly wrapped in `<sec:authorize access="hasRole('ADMIN')">`. No change needed.

---

## 6. JSP COMPILATION / TAGLIB ISSUES

### 6.1 `src/main/webapp/WEB-INF/views/error.jsp` — Line 2
**Problem:** Uses non-standard taglib URI.
```jsp
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
```
**Fix:** Use the standard JSTL URI (as in other JSPs):
```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
```
**Note:** This file is not referenced by `GlobalExceptionHandler` (which uses `error/404.jsp` and `error/500.jsp`). Consider deleting `error.jsp` or refactoring error handling to use it consistently.

---

### 6.2 `src/main/webapp/WEB-INF/views/error.jsp` — Structure
**Problem:** Standalone page with its own `<html>`, `<head>`, `<body>`; does not use shared `header.jsp` and `footer.jsp`. Inconsistent with the rest of the app.

---

## 7. CSS / JS ISSUES

### 7.1 `src/main/webapp/js/app.js` — Line 35
**Problem:** `confirmDelete()` receives unescaped user input from JSP.
```javascript
function confirmDelete(itemName) {
    return confirm('Are you sure you want to delete "' + itemName + '"?\n\nThis action cannot be undone.');
}
```
**JSP (course-list.jsp line 89):**
```jsp
onsubmit="return confirmDelete('${course.title}')"
```
**Fix:** Escape for JavaScript to avoid XSS and broken dialogs (e.g. quotes in title). Options:
- Use `<c:out value="${course.title}" escapeXml="true"/>` and pass through a data attribute, or
- Add a small JSP function/tag to escape for JS, or
- Use a data attribute and read it in JS: `data-delete-title="<c:out value='${course.title}'/>"` and sanitize in JS.

---

### 7.2 `src/main/webapp/css/style.css`
**Status:** No issues found. Structure and naming are consistent.

---

## 8. PROPERTIES FILE ISSUES

### 8.1 `src/main/resources/application.properties`
**Status:** No issues found. H2 config, JPA, JSP view resolver, and logging are set correctly.

---

### 8.2 `application-oracle.properties.example`
**Status:** Correctly documented and gitignored in `.gitignore`. No issues.

---

## 9. POM.XML DEPENDENCY ISSUES

### 9.1 `pom.xml` — Lines 76–81
**Problem:** Uses Jetty `apache-jstl` instead of standard JSTL.
```xml
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>apache-jstl</artifactId>
    <version>11.0.0</version>
    <scope>compile</scope>
</dependency>
```
**Fix:** Prefer standard Jakarta JSTL for Spring Boot 3:
```xml
<dependency>
    <groupId>jakarta.servlet.jsp.jstl</groupId>
    <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
</dependency>
<dependency>
    <groupId>org.glassfish.web</groupId>
    <artifactId>jakarta.servlet.jsp.jstl</artifactId>
</dependency>
```

---

### 9.2 `pom.xml` — Packaging
**Problem:** Default packaging is `jar`. Spring Boot + JSP typically requires WAR for production deployment.
**Fix:** Add `<packaging>war</packaging>` and extend `SpringBootServletInitializer` in the main application class if deploying to an external servlet container.

---

## 10. ROOT DIRECTORY FILES

### 10.1 `.gitignore`
**Status:** Appropriate exclusions for Maven, IDEs, Oracle config, and build artifacts.

---

### 10.2 `README.md`
**Status:** Clear and accurate. Package path in structure (`com/eduproject/`) matches the codebase.

---

## SUMMARY TABLE

| Category              | Count |
|-----------------------|-------|
| Broken links          | 2     |
| Hardcoded paths       | 1     |
| CSRF issues           | 0     |
| Styling inconsistencies | 3   |
| Security issues       | 1     |
| JSP/taglib issues     | 2     |
| CSS/JS issues         | 1     |
| Properties issues     | 0     |
| POM issues            | 2     |

---

## PRIORITY FIXES

1. **High:** Remove or restrict role selection on registration (security).
2. **High:** Fix or remove `error.jsp` (broken CSS, wrong taglib, orphaned).
3. **Medium:** Escape `course.title` in `confirmDelete` to prevent XSS.
4. **Medium:** Remove or implement `/users/edit` logic in `user-form.jsp`.
5. **Low:** Replace inline styles with CSS classes.
6. **Low:** Use context path in logout redirect.
7. **Low:** Consider standard JSTL and WAR packaging in `pom.xml`.
