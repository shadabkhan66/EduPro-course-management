package com.eduproject.runner;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.eduproject.model.CourseEntity;
import com.eduproject.model.Role;
import com.eduproject.model.User;
import com.eduproject.repository.CourseRepository;
import com.eduproject.repository.UserRepository;

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
public class InsertDataInDB implements CommandLineRunner {

	private final CourseRepository courseRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		log.info("Seeding database with sample data...");

		List<CourseEntity> courses = courseRepository.saveAll(List.of(
				CourseEntity.builder().title("Java Programming").description("Learn Java from scratch").durationInHours(40).instructor("John Doe").fees(null).build(),
				CourseEntity.builder().title("Spring Boot").description("Master Spring Boot for REST APIs").durationInHours(30).instructor("Jane Smith").fees(BigDecimal.valueOf(5623.0)).build(),
				CourseEntity.builder().title("Hibernate ORM").description("Learn Hibernate for database access").durationInHours(25).instructor("Alice Johnson").fees(BigDecimal.valueOf(4500.0)).build(),
				CourseEntity.builder().title("Microservices with Spring Cloud").description("Build microservices using Spring Cloud").durationInHours(35).instructor("Bob Brown").fees(BigDecimal.valueOf(6000.0)).build()
		));

		List<User> users = userRepository.saveAll(List.of(
				User.builder().username("admin").password(passwordEncoder.encode("admin123")).firstName("Admin").lastName("User").email("admin@edupro.com").role(Role.ADMIN).build(),
				User.builder().username("student").password(passwordEncoder.encode("student123")).firstName("John").lastName("Doe").email("john@edupro.com").role(Role.STUDENT).build()
		));

		courses.forEach(c -> log.info("Seeded course: {}", c.getTitle()));
		users.forEach(u -> log.info("Seeded user: {} ({})", u.getUsername(), u.getRole()));
	}
}
