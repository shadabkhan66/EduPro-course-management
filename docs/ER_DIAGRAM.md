# EduPro — conceptual ER diagram

This diagram matches the REST guide modules (users, roles, departments, courses, enrollments, grades, schedules, materials, notifications). **Student** and **Instructor** are modeled as users with roles; adjust if you prefer separate profile tables.

## Mermaid (render in GitHub, VS Code, or [mermaid.live](https://mermaid.live))

```mermaid
erDiagram
  USER ||--o{ USER_ROLE : "assigned"
  ROLE ||--o{ USER_ROLE : "assigned"
  DEPARTMENT ||--o{ COURSE : "offers"
  USER ||--o{ COURSE : "instructs"
  USER ||--o{ ENROLLMENT : "student"
  COURSE ||--o{ ENROLLMENT : "for"
  ENROLLMENT ||--o| GRADE : "receives"
  COURSE ||--o{ SCHEDULE : "meets"
  COURSE ||--o{ COURSE_MATERIAL : "has"
  USER ||--o{ NOTIFICATION : "gets"

  USER {
    uuid id PK
    string email UK
    string password_hash
    string first_name
    string last_name
    string status
    timestamp created_at
    timestamp updated_at
  }

  ROLE {
    uuid id PK
    string name UK
    string description
  }

  USER_ROLE {
    uuid user_id FK
    uuid role_id FK
  }

  DEPARTMENT {
    uuid id PK
    string name
    string code UK
    string description
  }

  COURSE {
    uuid id PK
    uuid department_id FK
    uuid instructor_user_id FK
    string code UK
    string title
    int capacity
    string status
    text description
  }

  ENROLLMENT {
    uuid id PK
    uuid student_user_id FK
    uuid course_id FK
    string status
    timestamp enrolled_at
  }

  GRADE {
    uuid id PK
    uuid enrollment_id FK
    string score_or_letter
    boolean published
    text feedback
    timestamp graded_at
  }

  SCHEDULE {
    uuid id PK
    uuid course_id FK
    string day_of_week
    time start_time
    time end_time
    string room
    string timezone
  }

  COURSE_MATERIAL {
    uuid id PK
    uuid course_id FK
    string title
    string file_url
    string content_type
    timestamp uploaded_at
    uuid uploaded_by_user_id FK
  }

  NOTIFICATION {
    uuid id PK
    uuid user_id FK
    string title
    text body
    boolean read
    string channel
    timestamp created_at
  }
```

## Design notes

- **Enrollment → Grade (1:0..1)** keeps grades tied to a specific enrollment row (student + course + term context). Alternative: grade keyed by `(student_id, course_id)` if you do not use an enrollment aggregate.
- **Instructor** is `USER` referenced by `COURSE.instructor_user_id`; your app enforces instructor role in the service layer (or via DB constraints if you add instructor profile tables later).
- **Flyway** scripts in `src/main/resources/db/migration/` should follow this model when you create tables.
