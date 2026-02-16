package com.eduproject.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.eduproject.model.CourseEntity;

/**
 * Repository integration tests using @DataJpaTest.
 *
 * TESTING NOTES:
 *
 * @DataJpaTest
 *   → Loads ONLY the JPA layer (entities, repositories, embedded database).
 *   → Uses H2 in-memory database (auto-configured).
 *   → Each test runs in a transaction that is ROLLED BACK after the test.
 *   → Much faster than @SpringBootTest (no web layer, no security, no controllers).
 *
 * WHY test repository?
 *   → findByTitle() is a derived query method. Spring generates the SQL.
 *   → We test that Spring generates the CORRECT query.
 *   → Also validates entity mapping (@Column, @Table) is correct.
 */
@DataJpaTest
@DisplayName("CourseRepository Integration Tests")
class CourseRepositoryTest {

	@Autowired
	private CourseRepository courseRepository;

	private CourseEntity savedCourse;

	@BeforeEach
	void setUp() {
		courseRepository.deleteAll();
		savedCourse = courseRepository.save(
				CourseEntity.builder()
						.title("Spring Boot Masterclass")
						.description("Complete Spring Boot course")
						.durationInHours(40)
						.instructor("John Doe")
						.fees(BigDecimal.valueOf(5000))
						.build()
		);
	}

	@Test
	@DisplayName("findByTitle should return course when title exists")
	void findByTitle_shouldReturnCourse() {
		Optional<CourseEntity> found = courseRepository.findByTitle("Spring Boot Masterclass");

		assertThat(found).isPresent();
		assertThat(found.get().getInstructor()).isEqualTo("John Doe");
		assertThat(found.get().getId()).isNotNull();
	}

	@Test
	@DisplayName("findByTitle should return empty when title does not exist")
	void findByTitle_shouldReturnEmpty() {
		Optional<CourseEntity> found = courseRepository.findByTitle("Nonexistent Course");
		assertThat(found).isEmpty();
	}

	@Test
	@DisplayName("findByTitle should be case-sensitive")
	void findByTitle_shouldBeCaseSensitive() {
		Optional<CourseEntity> found = courseRepository.findByTitle("spring boot masterclass");
		assertThat(found).isEmpty(); // lowercase should not match
	}

	@Test
	@DisplayName("save should auto-generate ID")
	void save_shouldAutoGenerateId() {
		assertThat(savedCourse.getId()).isNotNull();
		assertThat(savedCourse.getId()).isGreaterThan(0);
	}

	@Test
	@DisplayName("count should return correct number of courses")
	void count_shouldReturnCorrectCount() {
		assertThat(courseRepository.count()).isEqualTo(1);

		courseRepository.save(
				CourseEntity.builder()
						.title("Java Basics")
						.description("Intro to Java")
						.durationInHours(20)
						.instructor("Jane")
						.build()
		);

		assertThat(courseRepository.count()).isEqualTo(2);
	}

	@Test
	@DisplayName("delete should remove the course")
	void delete_shouldRemoveCourse() {
		courseRepository.deleteById(savedCourse.getId());
		assertThat(courseRepository.findById(savedCourse.getId())).isEmpty();
	}
}
