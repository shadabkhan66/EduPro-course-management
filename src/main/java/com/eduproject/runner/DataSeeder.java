package com.eduproject.runner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.eduproject.modules.department.entity.DepartmentEntity;
import com.eduproject.modules.department.repository.DepartmentRepository;
import com.eduproject.modules.roles.entity.RoleEntity;
import com.eduproject.modules.users.entity.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.eduproject.modules.course.entity.CourseEntity;
import com.eduproject.model.Role;
import com.eduproject.modules.course.repository.CourseRepository;
import com.eduproject.modules.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds the database with sample data on application startup.
 *
 * Runs automatically because it implements CommandLineRunner and is
 * registered as a Spring @Component.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;

    @Override

    public void run(String... args) {
        log.info("Seeding database with sample data...");
        var dept = new DepartmentEntity();
        dept.setId(5_00_000L);
        dept.setCode("CSE");
        dept.setName("Computer Science");
        dept.setDescription("Computer Science is ........");

        var dept2 = new DepartmentEntity();
        dept2.setId(5_00_001L);
        dept2.setCode("IT");
        dept2.setName("Information Technology");
        dept2.setDescription("Information Technology is ........");

        List<DepartmentEntity> department = departmentRepository.saveAll(List.of(
            dept,dept2
        ));

        List<UserEntity> users = userRepository.saveAll(List.of(
                UserEntity.builder().username("admin").password(passwordEncoder.encode("admin123")).firstName("Admin").lastName("User").email("admin@edupro.com").role(Set.of(RoleEntity.builder().id(100L).name("ADMIN").description("he is boss").build())).build(),
                UserEntity.builder()
                        .username("student")
                        .password(passwordEncoder.encode("student123"))
                        .firstName("Alice")
                        .lastName("Smith")
                        .email("alice.smith@edupro.com")
                        .role(Set.of(
                                RoleEntity.builder()
                                        .id(102L)
                                        .name("STUDENT")
                                        .description("enrolled learner")
                                        .build()
                        ))
                        .build(),
                UserEntity.builder()
                        .username("instructor")
                        .password(passwordEncoder.encode("instructor123"))
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@edupro.com")
                        .role(Set.of(
                                RoleEntity.builder()
                                        .id(101L)
                                        .name("INSTRUCTOR")
                                        .description("teaches courses")
                                        .build()
                        ))
                        .build(),
                UserEntity.builder()
                        .username("student1")
                        .password(passwordEncoder.encode("student123"))
                        .firstName("Bob")
                        .lastName("Johnson")
                        .email("bob.johnson@edupro.com")
                        .role(Set.of(
                                RoleEntity.builder()
                                        .id(102L)
                                        .name("STUDENT")
                                        .description("enrolled learner")
                                        .build()
                        ))
                        .build()
        ));

//        List<CourseEntity> courses = courseRepository.saveAll(List.of(
//                CourseEntity.builder().title("Java Programming").description("Learn Java from scratch").durationInHours(40).fees(BigDecimal.valueOf(999.99)).capacity(100).department(departmentRepository.findById(5_00_001L).get()).instructor().build(),
//                CourseEntity.builder().title("Spring Boot").description("Master Spring Boot for REST APIs").durationInHours(30).instructor("Jane Smith").fees(BigDecimal.valueOf(5623.0)).build(),
//                CourseEntity.builder().title("Hibernate ORM").description("Learn Hibernate for database access").durationInHours(25).instructor("Alice Johnson").fees(BigDecimal.valueOf(4500.0)).build(),
//                CourseEntity.builder().title("Microservices with Spring Cloud").description("Build microservices using Spring Cloud").durationInHours(35).instructor("Bob Brown").fees(BigDecimal.valueOf(6000.0)).build(),
//                CourseEntity.builder().title("Python").description("Learn Python for AI").enrolledUsers(Arrays.asList(UserEntity.builder().username("king").password(passwordEncoder.encode("king123")).firstName("King").lastName("Khan").email("king@edupro.com").role(Role.STUDENT).build())).build()
//        ));

        List<CourseEntity> courses = courseRepository.saveAll(List.of(

                CourseEntity.builder()
                        .title("Java Programming")
                        .description("Learn Java from scratch")
                        .durationInHours(40)
                        .fees(BigDecimal.valueOf(999.99))
                        .capacity(100)
                        .enrolledCount(0)
                        .department(dept)
                        .instructor(users.get(2)) // instructor user
                        .build(),

                CourseEntity.builder()
                        .title("Spring Boot")
                        .description("Master Spring Boot for REST APIs")
                        .durationInHours(30)
                        .fees(BigDecimal.valueOf(1500.00))
                        .capacity(80)
                        .enrolledCount(0)
                        .department(dept)
                        .instructor(users.get(2))
                        .build(),

                CourseEntity.builder()
                        .title("Hibernate ORM")
                        .description("Learn Hibernate for database access")
                        .durationInHours(25)
                        .fees(BigDecimal.valueOf(1200.00))
                        .capacity(60)
                        .enrolledCount(0)
                        .department(dept2)
                        .instructor(users.get(2))
                        .build(),

                CourseEntity.builder()
                        .title("Microservices with Spring Cloud")
                        .description("Build microservices using Spring Cloud")
                        .durationInHours(35)
                        .fees(BigDecimal.valueOf(2000.00))
                        .capacity(50)
                        .enrolledCount(0)
                        .department(dept2)
                        .instructor(users.get(2))
                        .build(),

                CourseEntity.builder()
                        .title("Python for AI")
                        .description("Learn Python for Artificial Intelligence")
                        .durationInHours(45)
                        .fees(BigDecimal.valueOf(1800.00))
                        .capacity(120)
                        .enrolledCount(0)
                        .department(dept)
                        .instructor(users.get(2))
                        .build(),

                CourseEntity.builder()
                        .title("Data Structures & Algorithms")
                        .description("Master DSA for coding interviews")
                        .durationInHours(50)
                        .fees(BigDecimal.valueOf(2200.00))
                        .capacity(70)
                        .enrolledCount(0)
                        .department(dept)
                        .instructor(users.get(2))
                        .build()
        ));



        courses.forEach(c -> log.info("Seeded course: {}", c.getTitle()));
        users.forEach(u -> log.info("Seeded user: {} ({})", u.getUsername(), u.getRole()));
    }
}
