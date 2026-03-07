# Shadab Khan

**Location:** India | **Email:** xyz@gmail.com | **Phone:** 1234567890 | **LinkedIn:** xyz | **GitHub:** xyz

---

## Summary

**Java Backend Developer (Fresher)** with hands-on experience building RESTful applications using **Spring Boot, JPA/Hibernate, and Oracle DB**. Proficient in OOP, multithreading, database design, and enterprise patterns (MVC, DAO). Built end-to-end backend systems with authentication, layered architecture, and production-style workflows. Seeking an entry-level backend role to contribute and grow in a collaborative engineering environment.

---

## Technical Skills

| Category | Technologies |
|----------|---------------|
| **Languages** | Java, SQL |
| **Backend** | Spring Boot, Spring MVC, Spring Security, Spring Data JPA, JWT |
| **Architecture** | RESTful APIs, Microservices, MVC, DAO, Layered Architecture |
| **Databases** | Oracle DB, MySQL, MongoDB, H2 |
| **Tools** | Git, Maven, Docker, Postman, JUnit 5, Mockito |

---

## Projects

### EduPro — Course Management System *(Primary Project — Interview Ready)*
**Tech:** Spring Boot 3.5, Spring Security 6, Spring Data JPA, Thymeleaf, H2/Oracle, Bean Validation

- **Layered architecture:** Controller → Service → Repository with Entity–DTO separation for clean boundaries
- **Spring Security:** Form-based auth, BCrypt passwords, role-based access (ADMIN/STUDENT/TEACHER), CSRF protection, custom `UserDetailsService` for DB-backed authentication
- **Optimistic locking:** `@Version` on entities to prevent concurrent update conflicts; solved "Detached entity with null version" bug
- **Design patterns:** PRG (Post-Redirect-Get) for flash messages, `@ControllerAdvice` for global exception handling, service interfaces for testability
- **Audit & validation:** `@CreationTimestamp`/`@UpdateTimestamp`, Bean Validation with `BindingResult.rejectValue()` for multi-field error display
- **Testing:** Unit tests (Mockito), controller tests (`@WebMvcTest`), repository tests (`@DataJpaTest`)

*[GitHub](https://github.com/xyz) | [INTERVIEW_QNA.md](INTERVIEW_QNA.md) — 25 STAR-format answers for this project*

---

### Microservices E-Commerce Platform
**Tech:** Spring Boot, Spring Cloud Gateway, JWT, Docker, Docker Compose

- Built microservices system with API Gateway, Auth, User, Product, Order services
- JWT-based authentication enforced at Gateway; service discovery for inter-service communication
- Containerized with Docker; orchestrated via Docker Compose

---

### Product Management System (Full Stack)
**Tech:** Spring Boot, JPA/Hibernate, MySQL, Bootstrap

- CRUD platform with RESTful APIs, layered architecture (Controller–Service–DAO), DTOs, global exception handling
- Stock management and sales workflow with validation

---

### Task Management System
**Tech:** Spring Boot, MongoDB, React

- JWT authentication and role-based authorization with Spring Security
- REST APIs with Service–Repository pattern, pagination, filtering; React frontend integration

---

### EduTrack — Student Information System
**Tech:** Java EE (Servlets, JSP), Oracle Database

- Layered MVC + Front Controller pattern; user registration, login, student-course CRUD with DAO layer

---

## Education

**B.Tech in Chemical Engineering** — Institute of Engineering and Technology, Lucknow *(Sept 2017 – May 2021)*  
CGPA: 6.91/10.0

---

## Training & Certification

**Full Stack Java Development Program** *(Nov 2024 – Ongoing)* — Core Java, JDBC/Servlets/JSP, Spring Boot, Spring Security, JWT, Microservices, JUnit 5, Agile exposure

---

*Last updated: March 2025*
