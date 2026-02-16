package com.eduproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eduproject.exception.CourseNotFoundException;
import com.eduproject.model.CourseDTO;
import com.eduproject.model.CourseEntity;
import com.eduproject.repository.CourseRepository;
import com.eduproject.service.impl.CourseServiceImpl;

/**
 * Unit tests for CourseServiceImpl.
 *
 * TESTING NOTES:
 *
 * @ExtendWith(MockitoExtension.class)
 *   → Initializes @Mock and @InjectMocks without starting Spring context.
 *   → Faster than @SpringBootTest (no application context = milliseconds vs seconds).
 *
 * @Mock
 *   → Creates a fake implementation of the interface.
 *   → By default, methods return null/0/false/empty collections.
 *   → Use when(...).thenReturn(...) to define behavior.
 *
 * @InjectMocks
 *   → Creates a REAL instance of the class under test.
 *   → Injects all @Mock objects into its constructor.
 *
 * PATTERN: Arrange → Act → Assert (AAA)
 *   1. Arrange: Set up mock behavior with when().thenReturn()
 *   2. Act: Call the method under test
 *   3. Assert: Verify the result and mock interactions
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CourseServiceImpl Unit Tests")
class CourseServiceImplTest {

	@Mock
	private CourseRepository courseRepository;

	@InjectMocks
	private CourseServiceImpl courseService;

	// ==================== Test Data Helpers ====================

	private CourseEntity sampleEntity() {
		return CourseEntity.builder()
				.id(1L)
				.title("Spring Boot")
				.description("Learn Spring Boot")
				.durationInHours(30)
				.instructor("John Doe")
				.fees(BigDecimal.valueOf(5000))
				.build();
	}

	private CourseDTO sampleDTO() {
		return new CourseDTO(null, "Spring Boot", "Learn Spring Boot", 30, BigDecimal.valueOf(5000), "John Doe");
	}

	// ==================== getAllCourses ====================

	@Nested
	@DisplayName("getAllCourses()")
	class GetAllCourses {

		@Test
		@DisplayName("should return list of CourseDTO when courses exist")
		void shouldReturnCourseDTOList() {
			// Arrange
			when(courseRepository.findAll()).thenReturn(List.of(sampleEntity()));

			// Act
			List<CourseDTO> result = courseService.getAllCourses();

			// Assert
			assertThat(result).hasSize(1);
			assertThat(result.get(0).getTitle()).isEqualTo("Spring Boot");
			verify(courseRepository).findAll();
		}

		@Test
		@DisplayName("should return empty list when no courses exist")
		void shouldReturnEmptyList() {
			when(courseRepository.findAll()).thenReturn(List.of());

			List<CourseDTO> result = courseService.getAllCourses();

			assertThat(result).isEmpty();
		}
	}

	// ==================== getCourseById ====================

	@Nested
	@DisplayName("getCourseById()")
	class GetCourseById {

		@Test
		@DisplayName("should return CourseDTO when course exists")
		void shouldReturnCourse() {
			when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleEntity()));

			CourseDTO result = courseService.getCourseById(1L);

			assertThat(result.getTitle()).isEqualTo("Spring Boot");
			assertThat(result.getInstructor()).isEqualTo("John Doe");
		}

		@Test
		@DisplayName("should throw CourseNotFoundException when course does not exist")
		void shouldThrowWhenNotFound() {
			when(courseRepository.findById(99L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> courseService.getCourseById(99L))
					.isInstanceOf(CourseNotFoundException.class)
					.hasMessageContaining("99");
		}
	}

	// ==================== createCourse ====================

	@Nested
	@DisplayName("createCourse()")
	class CreateCourse {

		@Test
		@DisplayName("should save entity and return title")
		void shouldSaveAndReturnTitle() {
			CourseEntity savedEntity = sampleEntity();
			when(courseRepository.save(any(CourseEntity.class))).thenReturn(savedEntity);

			String title = courseService.createCourse(sampleDTO());

			assertThat(title).isEqualTo("Spring Boot");
			verify(courseRepository).save(any(CourseEntity.class));
		}
	}

	// ==================== updateCourse ====================

	@Nested
	@DisplayName("updateCourse()")
	class UpdateCourse {

		@Test
		@DisplayName("should load existing entity, copy fields, and save")
		void shouldUpdateExistingCourse() {
			CourseEntity existing = sampleEntity();
			when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
			when(courseRepository.save(any())).thenReturn(existing);

			CourseDTO dto = new CourseDTO(1L, "Updated Title", "Updated Desc", 40, BigDecimal.valueOf(6000), "Jane");
			courseService.updateCourse(dto);

			// Verify the entity was loaded first (not a new one created)
			verify(courseRepository).findById(1L);
			verify(courseRepository).save(existing);
			assertThat(existing.getTitle()).isEqualTo("Updated Title");
		}

		@Test
		@DisplayName("should throw when course to update does not exist")
		void shouldThrowOnUpdateNotFound() {
			when(courseRepository.findById(99L)).thenReturn(Optional.empty());

			CourseDTO dto = new CourseDTO(99L, "Title", "Desc", 10, null, "X");

			assertThatThrownBy(() -> courseService.updateCourse(dto))
					.isInstanceOf(CourseNotFoundException.class);
		}
	}

	// ==================== deleteCourseById ====================

	@Nested
	@DisplayName("deleteCourseById()")
	class DeleteCourse {

		@Test
		@DisplayName("should delete when course exists")
		void shouldDeleteCourse() {
			when(courseRepository.existsById(1L)).thenReturn(true);

			courseService.deleteCourseById(1L);

			verify(courseRepository).deleteById(1L);
		}

		@Test
		@DisplayName("should throw when course to delete does not exist")
		void shouldThrowOnDeleteNotFound() {
			when(courseRepository.existsById(99L)).thenReturn(false);

			assertThatThrownBy(() -> courseService.deleteCourseById(99L))
					.isInstanceOf(CourseNotFoundException.class);

			verify(courseRepository, never()).deleteById(any());
		}
	}

	// ==================== existsByTitle ====================

	@Nested
	@DisplayName("existsByTitle()")
	class ExistsByTitle {

		@Test
		@DisplayName("should return true when title exists")
		void shouldReturnTrue() {
			when(courseRepository.findByTitle("Spring Boot")).thenReturn(Optional.of(sampleEntity()));
			assertThat(courseService.existsByTitle("Spring Boot")).isTrue();
		}

		@Test
		@DisplayName("should return false when title does not exist")
		void shouldReturnFalse() {
			when(courseRepository.findByTitle("Unknown")).thenReturn(Optional.empty());
			assertThat(courseService.existsByTitle("Unknown")).isFalse();
		}
	}

	// ==================== existsByTitleExcludingId ====================

	@Nested
	@DisplayName("existsByTitleExcludingId()")
	class ExistsByTitleExcludingId {

		@Test
		@DisplayName("should return false when title belongs to same course (editing itself)")
		void shouldAllowSameCourseTitle() {
			CourseEntity entity = sampleEntity(); // id = 1
			when(courseRepository.findByTitle("Spring Boot")).thenReturn(Optional.of(entity));

			// Checking title "Spring Boot" excluding course ID 1 (the same course)
			assertThat(courseService.existsByTitleExcludingId("Spring Boot", 1L)).isFalse();
		}

		@Test
		@DisplayName("should return true when title belongs to a different course")
		void shouldRejectDuplicateTitle() {
			CourseEntity entity = sampleEntity(); // id = 1
			when(courseRepository.findByTitle("Spring Boot")).thenReturn(Optional.of(entity));

			// Checking from course ID 2 -- title belongs to course 1 = duplicate
			assertThat(courseService.existsByTitleExcludingId("Spring Boot", 2L)).isTrue();
		}
	}
}
