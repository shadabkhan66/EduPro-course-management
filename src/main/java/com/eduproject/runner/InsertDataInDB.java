package com.eduproject.runner;

import java.math.BigDecimal;
import java.util.List;

import com.eduproject.model.Role;
import com.eduproject.model.User;
import com.eduproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.eduproject.model.CourseEntity;
import com.eduproject.repository.CourseRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InsertDataInDB implements CommandLineRunner {

	@Autowired
	private final CourseRepository courseRepository;

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		log.info("Inserting data into Oracle DB...");
		
		List<CourseEntity> saveAllCourse = this.courseRepository.saveAll(
				List.of(
						CourseEntity.builder().title("Java Programming").description("Learn Java from scratch").durationInHours(40).instructor("John Doe").fees(null).build(),
						CourseEntity.builder().title("Spring Boot").description("Master Spring Boot for REST APIs").durationInHours(30).instructor("Jane Smith").fees(BigDecimal.valueOf(5623.0)).build(),
						CourseEntity.builder().title("Hibernate ORM").description("Learn Hibernate for database access").durationInHours(25).instructor("Alice Johnson").fees(BigDecimal.valueOf(4500.0)).build(),
						CourseEntity.builder().title("Microservices with Spring Cloud").description("Build microservices using Spring Cloud").durationInHours(35).instructor("Bob Brown").fees(BigDecimal.valueOf(6000.0)).build()
				)
		);

		List<User> saveAllUsers = this.userRepository.saveAll(
				List.of(User.builder().username("king").password(passwordEncoder.encode("king123")).firstName("King").lastName(null).email("King@gmail.com").role(Role.STUDENT).build(),
						User.builder().username("user").password(passwordEncoder.encode("user123")).firstName("user").lastName("king").email("user@gmail.com").role(Role.ADMIN).build()
		));

		saveAllCourse.forEach(course -> log.info("Saved course with courese name: {} ", course.getTitle()));
		saveAllUsers.forEach(u -> log.info("Saved user with courese name: {} ", u.getUsername()));
	}

}
