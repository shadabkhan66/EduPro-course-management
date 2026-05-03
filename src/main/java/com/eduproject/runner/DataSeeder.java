package com.eduproject.runner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.eduproject.modules.department.entity.DepartmentEntity;
import com.eduproject.modules.department.repository.DepartmentRepository;
import com.eduproject.modules.roles.entity.RoleEntity;
import com.eduproject.modules.roles.repository.RoleRepository;
import com.eduproject.modules.users.entity.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.eduproject.modules.course.entity.CourseEntity;
import com.eduproject.modules.course.repository.CourseRepository;
import com.eduproject.modules.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

	private final CourseRepository courseRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final DepartmentRepository departmentRepository;
	private final RoleRepository roleRepository;

	@Override
	public void run(String... args) {
		log.info("Seeding database with sample data...");
		if (roleRepository.count() > 0) {
			log.info("Data already seeded (roles present); skipping.");
			return;
		}

		RoleEntity adminRole = roleRepository.save(RoleEntity.builder()
				.name("ADMIN")
				.description("System administrator")
				.build());
		RoleEntity instructorRole = roleRepository.save(RoleEntity.builder()
				.name("INSTRUCTOR")
				.description("Teaches courses")
				.build());
		RoleEntity studentRole = roleRepository.save(RoleEntity.builder()
				.name("STUDENT")
				.description("Enrolled learner")
				.build());

		var dept = new DepartmentEntity();
		dept.setCode("CSE");
		dept.setName("Computer Science");
		dept.setDescription("Computer Science department.");

		var dept2 = new DepartmentEntity();
		dept2.setCode("IT");
		dept2.setName("Information Technology");
		dept2.setDescription("Information Technology department.");

		List<DepartmentEntity> departments = departmentRepository.saveAll(List.of(dept, dept2));
		DepartmentEntity cse = departments.get(0);
		DepartmentEntity it = departments.get(1);

		List<UserEntity> users = userRepository.saveAll(List.of(
				UserEntity.builder()
						.username("admin")
						.password(passwordEncoder.encode("admin123"))
						.firstName("Admin")
						.lastName("User")
						.email("admin@edupro.com")
						.roles(Set.of(adminRole))
						.build(),
				UserEntity.builder()
						.username("student")
						.password(passwordEncoder.encode("student123"))
						.firstName("Alice")
						.lastName("Smith")
						.email("alice.smith@edupro.com")
						.roles(Set.of(studentRole))
						.build(),
				UserEntity.builder()
						.username("instructor")
						.password(passwordEncoder.encode("instructor123"))
						.firstName("John")
						.lastName("Doe")
						.email("john.doe@edupro.com")
						.roles(Set.of(instructorRole))
						.build(),
				UserEntity.builder()
						.username("student1")
						.password(passwordEncoder.encode("student123"))
						.firstName("Bob")
						.lastName("Johnson")
						.email("bob.johnson@edupro.com")
						.roles(Set.of(studentRole))
						.build()
		));

		List<CourseEntity> courses = courseRepository.saveAll(List.of(
				CourseEntity.builder()
						.title("Java Programming")
						.description("Learn Java from scratch")
						.durationInHours(40)
						.fees(BigDecimal.valueOf(999.99))
						.capacity(100)
						.enrolledCount(0)
						.department(cse)
						.instructor(users.get(2))
						.build(),
				CourseEntity.builder()
						.title("Spring Boot")
						.description("Master Spring Boot for REST APIs")
						.durationInHours(30)
						.fees(BigDecimal.valueOf(1500.00))
						.capacity(80)
						.enrolledCount(0)
						.department(cse)
						.instructor(users.get(2))
						.build(),
				CourseEntity.builder()
						.title("Hibernate ORM")
						.description("Learn Hibernate for database access")
						.durationInHours(25)
						.fees(BigDecimal.valueOf(1200.00))
						.capacity(60)
						.enrolledCount(0)
						.department(it)
						.instructor(users.get(2))
						.build(),
				CourseEntity.builder()
						.title("Microservices with Spring Cloud")
						.description("Build microservices using Spring Cloud")
						.durationInHours(35)
						.fees(BigDecimal.valueOf(2000.00))
						.capacity(50)
						.enrolledCount(0)
						.department(it)
						.instructor(users.get(2))
						.build(),
				CourseEntity.builder()
						.title("Python for AI")
						.description("Learn Python for Artificial Intelligence")
						.durationInHours(45)
						.fees(BigDecimal.valueOf(1800.00))
						.capacity(120)
						.enrolledCount(0)
						.department(cse)
						.instructor(users.get(2))
						.build(),
				CourseEntity.builder()
						.title("Data Structures & Algorithms")
						.description("Master DSA for coding interviews")
						.durationInHours(50)
						.fees(BigDecimal.valueOf(2200.00))
						.capacity(70)
						.enrolledCount(0)
						.department(cse)
						.instructor(users.get(2))
						.build()
		));

		courses.forEach(c -> log.info("Seeded course: {}", c.getTitle()));
		users.forEach(u -> log.info("Seeded user: {} roles={}", u.getUsername(), u.getRoles()));
	}
}
