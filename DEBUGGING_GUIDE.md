# EduPro Debugging Guide

A practical guide to debugging Spring Boot and Spring Security in the EduPro application. Use this to visualize beans, request flow, filters, and authentication.

---

## Table of Contents

1. [Run in Debug Mode](#1-run-in-debug-mode)
2. [View Spring Beans](#2-view-spring-beans)
3. [Request/Response Logging](#3-requestresponse-logging)
4. [Security Filter Chain](#4-security-filter-chain)
5. [Breakpoints for Authentication Flow](#5-breakpoints-for-authentication-flow)
6. [Debugger Techniques](#6-debugger-techniques)
7. [Step-by-Step Debugging Sessions](#7-step-by-step-debugging-sessions)
8. [Useful Logging Configuration](#8-useful-logging-configuration)
9. [Browser DevTools](#9-browser-devtools)
10. [Troubleshooting](#10-troubleshooting)

---

## 1. Run in Debug Mode

### IntelliJ / PyCharm

1. Open **Run → Edit Configurations**
2. Select your Spring Boot run configuration
3. Ensure it's set to **Debug** (bug icon), not Run
4. Start with **Shift+F9** (Debug) instead of Shift+F10 (Run)

### Maven (attach debugger later)

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005"
```

Then in IntelliJ: **Run → Attach to Process** → connect to port **5005**.

---

## 2. View Spring Beans

### Actuator Beans Endpoint

The project includes Spring Boot Actuator. When the app is running:

| URL | Purpose |
|-----|---------|
| `http://localhost:8080/actuator/beans` | All beans and their types |
| `http://localhost:8080/actuator/health` | Application health |
| `http://localhost:8080/actuator/info` | App info |

**Note:** If actuator endpoints return 404, ensure `management.endpoints.web.exposure.include=beans,info,health` is in `application.properties`.

**Security:** Add to `SecurityConfig` if actuator is blocked:

```java
.requestMatchers("/actuator/**").permitAll()
```

### Startup Bean Logging

Uncomment in `application.properties`:

```properties
debug=true
```

This prints a report of auto-configuration and conditions at startup. For bean creation details:

```properties
logging.level.org.springframework.beans.factory=DEBUG
```

---

## 3. Request/Response Logging

### RequestLoggingFilter

The project includes `RequestLoggingFilter` in `com.eduproject.config`. It logs:

- **>>> REQUEST:** Method, URI, query string, session ID, authenticated user
- **<<< RESPONSE:** Method, URI, status code, duration in ms

Example console output:

```
>>> REQUEST: GET /courses | Session: ABC123 | User: anonymous
<<< RESPONSE: GET /courses | Status: 200 | Duration: 45ms
```

To disable: remove `@Component` from `RequestLoggingFilter.java` or add `@Profile("debug")` and run with that profile.

---

## 4. Security Filter Chain

### Filter Order (typical Spring Security)

When a request hits your app, filters run in this order:

| Order | Filter | Purpose |
|-------|--------|---------|
| 1 | DisableEncodeUrlFilter | Prevents double URL encoding |
| 2 | WebAsyncManagerIntegrationFilter | Async support |
| 3 | SecurityContextHolderFilter | Restores SecurityContext from session |
| 4 | HeaderWriterFilter | Adds security headers |
| 5 | CsrfFilter | CSRF token validation |
| 6 | LogoutFilter | Handles `/logout` |
| 7 | UsernamePasswordAuthenticationFilter | Handles login form POST |
| 8 | RequestCacheAwareFilter | Caches requests for redirect after login |
| 9 | SecurityContextHolderAwareRequestFilter | Wraps request with security helpers |
| 10 | SessionManagementFilter | Session fixation protection |
| 11 | ExceptionTranslationFilter | Translates auth exceptions to redirects |
| 12 | FilterSecurityInterceptor | Checks URL access rules |

### Key Classes to Set Breakpoints

| Class | Method | When It Runs |
|-------|--------|--------------|
| `FilterChainProxy` | `doFilter` | Every request |
| `UsernamePasswordAuthenticationFilter` | `attemptAuthentication` | Login form POST |
| `UsernamePasswordAuthenticationFilter` | `successfulAuthentication` | After successful login |
| `LogoutFilter` | `doFilter` | When `/logout` is requested |
| `FilterSecurityInterceptor` | `invoke` | Access decision for protected URLs |

### Enable Security Debug Logging

Already configured in `application.properties`:

```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.web=DEBUG
logging.level.org.springframework.security.web.FilterChainProxy=DEBUG
logging.level.org.springframework.security.authentication=DEBUG
```

You'll see which filter is invoked for each request.

---

## 5. Breakpoints for Authentication Flow

### Login Flow (form-based)

1. **UsernamePasswordAuthenticationFilter.attemptAuthentication**
   - Trigger: POST to `/login` with username/password
   - Inspect: `request.getParameter("username")`, `request.getParameter("password")`

2. **UserDetailsService.loadUserByUsername** (e.g. CustomUserDetailsService)
   - Trigger: Called from DaoAuthenticationProvider
   - Inspect: Loaded user, authorities

3. **DaoAuthenticationProvider.authenticate**
   - Trigger: After UserDetailsService returns
   - Inspect: Password comparison, authentication result

4. **SecurityContextHolder.getContext().setAuthentication**
   - Trigger: After successful authentication
   - Inspect: The `Authentication` object stored in session

### Accessing a Protected Page

1. **SecurityContextHolderFilter.doFilter**
   - Restores `Authentication` from HTTP session
   - Inspect: `SecurityContextHolder.getContext().getAuthentication()`

2. **FilterSecurityInterceptor.invoke**
   - Checks if user has permission for the URL
   - Inspect: `ConfigAttribute`, access decision

### Logout Flow

1. **LogoutFilter.doFilter**
   - Trigger: Request to `/logout`
   - Inspect: Session invalidation, redirect

---

## 6. Debugger Techniques

### IntelliJ Shortcuts

| Action | Shortcut | Purpose |
|--------|----------|---------|
| Toggle breakpoint | Ctrl+F8 | Add/remove breakpoint |
| Step over | F8 | Execute current line, don't enter methods |
| Step into | F7 | Enter the method being called |
| Step out | Shift+F8 | Return to caller |
| Run to cursor | Alt+F9 | Run until the line at cursor |
| Evaluate expression | Alt+F8 | Inspect any variable or expression |
| Variables view | Alt+5 | See all local variables |
| Watches | Add expression | Monitor expressions across steps |

### Conditional Breakpoints

Right-click a breakpoint → **Condition**:

- `request.getRequestURI().contains("/login")` — only for login
- `request.getMethod().equals("POST")` — only for POST
- `request.getRequestURI().equals("/courses/1")` — only for specific course

### Evaluate Expression (Alt+F8)

Useful expressions while paused:

```java
request.getParameterNames()
request.getSession(false) != null
SecurityContextHolder.getContext().getAuthentication()
SecurityContextHolder.getContext().getAuthentication().getAuthorities()
request.getHeader("Cookie")
```

---

## 7. Step-by-Step Debugging Sessions

### Session 1: Trace a Simple GET Request

1. Set breakpoint in `FilterChainProxy.doFilter` (or `RequestLoggingFilter.doFilterInternal`)
2. Start app in debug mode
3. Open browser → `http://localhost:8080/`
4. When breakpoint hits, step through (F8) and watch the filter chain
5. Observe: Request → Filters → Controller → Response

### Session 2: Login Flow

1. Set breakpoints in:
   - `UsernamePasswordAuthenticationFilter.attemptAuthentication`
   - `UserDetailsService.loadUserByUsername` (your implementation)
   - `DaoAuthenticationProvider.authenticate`
2. Open `http://localhost:8080/login`
3. Enter credentials and submit
4. Step through each breakpoint and inspect:
   - Username/password from request
   - Loaded user from DB
   - Authentication result

### Session 3: Session Restoration

1. Log in successfully
2. Set breakpoint in `SecurityContextHolderFilter.doFilter`
3. Navigate to `http://localhost:8080/courses`
4. Inspect: `SecurityContextHolder.getContext().getAuthentication()` — should have your user

### Session 4: Logout

1. Set breakpoint in `LogoutFilter.doFilter`
2. Click logout or go to `http://localhost:8080/logout`
3. Step through and observe session invalidation and redirect

---

## 8. Useful Logging Configuration

### Minimal (default in this project)

```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### Full Security Trace

```properties
logging.level.org.springframework.security=TRACE
logging.level.org.springframework.security.web=TRACE
logging.level.org.springframework.security.web.FilterChainProxy=TRACE
logging.level.org.springframework.security.authentication=TRACE
```

### Web Layer

```properties
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.web.servlet.mvc.method.annotation=DEBUG
```

### Hibernate (SQL + parameters)

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

## 9. Browser DevTools

### Network Tab

- **Headers:** Request/response headers, status, cookies
- **Payload:** Form data for POST
- **Response:** HTML/JSON body

### Application Tab

- **Cookies:** `JSESSIONID` and other cookies
- **Session Storage / Local Storage:** If used

### Tips

- Preserve log: Keep network log across navigations
- Disable cache: Avoid cached responses while debugging

---

## 10. Troubleshooting

### Actuator endpoints return 404

- Ensure `spring-boot-starter-actuator` is in `pom.xml`
- Check `management.endpoints.web.exposure.include`
- Restart the application

### Breakpoints not hitting

- Confirm app is running in **Debug** mode (not Run)
- Rebuild project (Build → Rebuild Project)
- Verify breakpoint is in the correct class (e.g. `FilterChainProxy` from Spring Security, not a custom class)

### Too much log output

- Reduce logging levels to `INFO` or `WARN`
- Use conditional breakpoints instead of logging
- Disable `RequestLoggingFilter` by removing `@Component`

### SecurityContext is null after login

- Check that session is being created (`request.getSession()`)
- Ensure `SecurityContextRepository` stores in session (default)
- Verify redirect after login isn't losing the session

### Can't find FilterChainProxy

- Package: `org.springframework.security.web.FilterChainProxy`
- Use **Navigate → Class** (Ctrl+N) and search for `FilterChainProxy`

---

## Quick Reference: EduPro-Specific URLs

| URL | Purpose |
|-----|---------|
| `/` | Home |
| `/login` | Login form |
| `/logout` | Logout |
| `/courses` | Course list |
| `/courses/{id}` | Course detail |
| `/users/new` | Registration |
| `/actuator/beans` | All Spring beans |
| `/actuator/health` | Health check |
| `/h2-console` | H2 database console |

---

*Last updated for EduPro v0.2.0*
