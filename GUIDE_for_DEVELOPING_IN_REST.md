src/
└── main/
├── java/
│   └── com/yourcompany/courseregistration/
│       │
│       ├── CourceRegistrationApplication.java   # Entry point
│       │
│       ├── config/                  # All configuration classes
│       │   ├── SecurityConfig.java
│       │   ├── JwtConfig.java
│       │   └── SwaggerConfig.java
│       │
│       ├── common/                  # Shared utilities across modules
│       │   ├── exception/
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   ├── ResourceNotFoundException.java
│       │   │   └── BadRequestException.java
│       │   ├── response/
│       │   │   └── ApiResponse.java        # Standard response wrapper
│       │   ├── pagination/
│       │   │   └── PageResponse.java
│       │   └── constants/
│       │       └── AppConstants.java
│       │
│       ├── modules/                 # Feature modules (core of scalability)
│       │   │
│       │   ├── auth/
│       │   │   ├── controller/
│       │   │   │   └── AuthController.java
│       │   │   ├── service/
│       │   │   │   ├── AuthService.java
│       │   │   │   └── AuthServiceImpl.java
│       │   │   ├── dto/
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── LoginResponse.java
│       │   │   │   └── RegisterRequest.java
│       │   │   └── util/
│       │   │       └── JwtUtil.java
│       │   │
│       │   ├── user/
│       │   │   ├── controller/
│       │   │   │   └── UserController.java
│       │   │   ├── service/
│       │   │   │   ├── UserService.java
│       │   │   │   └── UserServiceImpl.java
│       │   │   ├── repository/
│       │   │   │   └── UserRepository.java
│       │   │   ├── entity/
│       │   │   │   └── User.java
│       │   │   ├── dto/
│       │   │   │   ├── UserRequest.java
│       │   │   │   └── UserResponse.java
│       │   │   └── mapper/
│       │   │       └── UserMapper.java
│       │   │
│       │   ├── course/
│       │   │   ├── controller/
│       │   │   ├── service/
│       │   │   ├── repository/
│       │   │   ├── entity/
│       │   │   ├── dto/
│       │   │   └── mapper/
│       │   │
│       │   ├── enrollment/
│       │   │   └── ...same structure...
│       │   │
│       │   ├── grade/
│       │   │   └── ...same structure...
│       │   │
│       │   ├── department/
│       │   │   └── ...same structure...
│       │   │
│       │   └── notification/
│       │       └── ...same structure...
│       │
│       └── security/                # JWT filters, guards
│           ├── JwtAuthFilter.java
│           ├── UserDetailsServiceImpl.java
│           └── SecurityUtils.java
│
└── resources/
├── application.yml              # Main config
├── application-dev.yml          # Dev environment
├── application-prod.yml         # Prod environment
└── db/migration/                # Flyway migrations
├── V1__create_users.sql
├── V2__create_courses.sql
└── V3__create_enrollments.sql



Here are the method names for all endpoints following Spring Boot conventions:

---

## 🔐 Auth

| Method Name | HTTP | Endpoint |
|---|---|---|
| `register` | POST | `/v1/auth/register` |
| `login` | POST | `/v1/auth/login` |
| `logout` | POST | `/v1/auth/logout` |
| `refreshToken` | POST | `/v1/auth/refresh-token` |
| `forgotPassword` | POST | `/v1/auth/forgot-password` |
| `resetPassword` | POST | `/v1/auth/reset-password` |
| `verifyEmail` | GET | `/v1/auth/verify-email` |

---

## 👤 Users

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getAllUsers` | GET | `/v1/users` |
| `getUserById` | GET | `/v1/users/{id}` |
| `updateUser` | PUT | `/v1/users/{id}` |
| `deleteUser` | DELETE | `/v1/users/{id}` |
| `updateUserStatus` | PATCH | `/v1/users/{id}/status` |
| `getUserEnrollments` | GET | `/v1/users/{id}/enrollments` |
| `assignRoleToUser` | POST | `/v1/users/{id}/roles` |
| `removeRoleFromUser` | DELETE | `/v1/users/{id}/roles/{roleId}` |
| `getUserRoles` | GET | `/v1/users/{id}/roles` |

---

## 🧑‍🎓 Students

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getAllStudents` | GET | `/v1/students` |
| `getStudentById` | GET | `/v1/students/{id}` |
| `getStudentCourses` | GET | `/v1/students/{id}/courses` |
| `getStudentGrades` | GET | `/v1/students/{id}/grades` |
| `getStudentSchedule` | GET | `/v1/students/{id}/schedule` |

---

## 🧑‍🏫 Instructors

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getAllInstructors` | GET | `/v1/instructors` |
| `getInstructorById` | GET | `/v1/instructors/{id}` |
| `createInstructor` | POST | `/v1/instructors` |
| `updateInstructor` | PUT | `/v1/instructors/{id}` |
| `deleteInstructor` | DELETE | `/v1/instructors/{id}` |
| `getInstructorCourses` | GET | `/v1/instructors/{id}/courses` |
| `getInstructorSchedule` | GET | `/v1/instructors/{id}/schedule` |

---

## 📚 Courses

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getAllCourses` | GET | `/v1/courses` |
| `getCourseById` | GET | `/v1/courses/{id}` |
| `createCourse` | POST | `/v1/courses` |
| `updateCourse` | PUT | `/v1/courses/{id}` |
| `patchCourse` | PATCH | `/v1/courses/{id}` |
| `deleteCourse` | DELETE | `/v1/courses/{id}` |
| `getCourseStudents` | GET | `/v1/courses/{id}/students` |
| `getCourseSchedule` | GET | `/v1/courses/{id}/schedule` |
| `getCourseMaterials` | GET | `/v1/courses/{id}/materials` |
| `uploadCourseMaterial` | POST | `/v1/courses/{id}/materials` |
| `deleteCourseMaterial` | DELETE | `/v1/courses/{id}/materials/{materialId}` |

---

## 📝 Enrollments

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getAllEnrollments` | GET | `/v1/enrollments` |
| `getEnrollmentById` | GET | `/v1/enrollments/{id}` |
| `createEnrollment` | POST | `/v1/enrollments` |
| `cancelEnrollment` | DELETE | `/v1/enrollments/{id}` |
| `updateEnrollmentStatus` | PATCH | `/v1/enrollments/{id}/status` |
| `getCourseEnrollments` | GET | `/v1/courses/{id}/enrollments` |
| `enrollStudentInCourse` | POST | `/v1/courses/{id}/enrollments` |
| `removeStudentFromCourse` | DELETE | `/v1/courses/{id}/enrollments/{studentId}` |

---

## 🗓️ Schedules

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getAllSchedules` | GET | `/v1/schedules` |
| `getScheduleById` | GET | `/v1/schedules/{id}` |
| `createSchedule` | POST | `/v1/schedules` |
| `updateSchedule` | PUT | `/v1/schedules/{id}` |
| `deleteSchedule` | DELETE | `/v1/schedules/{id}` |

---

## 🏫 Departments

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getAllDepartments` | GET | `/v1/departments` |
| `getDepartmentById` | GET | `/v1/departments/{id}` |
| `createDepartment` | POST | `/v1/departments` |
| `updateDepartment` | PUT | `/v1/departments/{id}` |
| `deleteDepartment` | DELETE | `/v1/departments/{id}` |
| `getDepartmentCourses` | GET | `/v1/departments/{id}/courses` |

---

## 📊 Grades

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getAllGrades` | GET | `/v1/grades` |
| `getGradeById` | GET | `/v1/grades/{id}` |
| `createGrade` | POST | `/v1/grades` |
| `updateGrade` | PUT | `/v1/grades/{id}` |
| `deleteGrade` | DELETE | `/v1/grades/{id}` |
| `publishGrade` | PATCH | `/v1/grades/{id}/publish` |
| `getCourseGrades` | GET | `/v1/courses/{id}/grades` |

---

## 🔔 Notifications

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getUserNotifications` | GET | `/v1/notifications` |
| `markNotificationAsRead` | PATCH | `/v1/notifications/{id}/read` |
| `markAllNotificationsAsRead` | PATCH | `/v1/notifications/read-all` |
| `deleteNotification` | DELETE | `/v1/notifications/{id}` |

---

## 📋 Reports

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getEnrollmentReport` | GET | `/v1/reports/enrollments` |
| `getCourseReport` | GET | `/v1/reports/courses` |
| `getStudentReport` | GET | `/v1/reports/students` |
| `getGradeReport` | GET | `/v1/reports/grades` |

---

## 🔑 Roles

| Method Name | HTTP | Endpoint |
|---|---|---|
| `getAllRoles` | GET | `/v1/roles` |
| `createRole` | POST | `/v1/roles` |
| `updateRole` | PUT | `/v1/roles/{id}` |
| `deleteRole` | DELETE | `/v1/roles/{id}` |

---

## Naming Convention Rules Followed

- `getAll` → fetching a list of resources
- `getXxxById` → fetching single resource by ID
- `getXxxYyy` → fetching nested/related resource
- `create` → POST, making a new resource
- `update` → PUT, full update
- `patch` → PATCH, partial update
- `delete` → DELETE, removing resource
- `action verb` for special actions → `publishGrade`, `markAsRead`, `assignRole`

Want me to now generate the actual controller classes with all these methods stubbed out?