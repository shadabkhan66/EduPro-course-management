package com.eduproject.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eduproject.model.CourseDTO;
import com.eduproject.service.CourseService;

/**
 * Controller integration tests using @WebMvcTest.
 *
 * TESTING NOTES:
 *
 * @WebMvcTest(CourseController.class)
 *   → Loads ONLY the web layer (controller + security + view resolution).
 *   → Does NOT load service beans, repositories, or database.
 *   → Much faster than @SpringBootTest.
 *
 * @MockitoBean
 *   → Creates a mock and registers it in the Spring context.
 *   → Different from @Mock: this is Spring-aware, @Mock is pure Mockito.
 *
 * @WithMockUser(roles = "ADMIN")
 *   → Simulates an authenticated user with ADMIN role.
 *   → Without this, secured endpoints return 302 (redirect to login).
 *
 * .with(csrf())
 *   → Adds CSRF token to POST requests (required by Spring Security).
 *   → Without this, POST returns 403 Forbidden.
 *
 * MockMvc
 *   → Simulates HTTP requests without starting a real server.
 *   → Tests the full request → controller → view pipeline.
 */
@WebMvcTest(CourseController.class)
@DisplayName("CourseController Web Tests")
class CourseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CourseService courseService;

	// ==================== GET /courses ====================

	@Nested
	@DisplayName("GET /courses")
	class ListCourses {

		@Test
		@DisplayName("should return course list view with model data")
		void shouldReturnListView() throws Exception {
			CourseDTO course = new CourseDTO(1L, "Java", "Learn Java", 40, null, "John");
			when(courseService.getAllCourses()).thenReturn(List.of(course));
			when(courseService.getCourseCount()).thenReturn(1L);

			mockMvc.perform(get("/courses"))
					.andExpect(status().isOk())
					.andExpect(view().name("course/list"))
					.andExpect(model().attributeExists("courses", "courseCount"))
					.andExpect(model().attribute("courseCount", 1L));
		}
	}

	// ==================== GET /courses/{id} ====================

	@Nested
	@DisplayName("GET /courses/{id}")
	class ViewCourse {

		@Test
		@DisplayName("should return course view with course data")
		void shouldReturnViewPage() throws Exception {
			CourseDTO course = new CourseDTO(1L, "Java", "Learn Java", 40, BigDecimal.valueOf(5000), "John");
			when(courseService.getCourseById(1L)).thenReturn(course);

			mockMvc.perform(get("/courses/1"))
					.andExpect(status().isOk())
					.andExpect(view().name("course/view"))
					.andExpect(model().attribute("course", course));
		}
	}

	// ==================== GET /courses/new (ADMIN) ====================

	@Nested
	@DisplayName("GET /courses/new")
	class ShowCreateForm {

		@Test
		@WithMockUser(roles = "ADMIN")
		@DisplayName("should show create form for ADMIN")
		void adminShouldSeeForm() throws Exception {
			mockMvc.perform(get("/courses/new"))
					.andExpect(status().isOk())
					.andExpect(view().name("course/form"))
					.andExpect(model().attributeExists("courseDTO"))
					.andExpect(model().attribute("editMode", false));
		}

		@Test
		@DisplayName("should redirect unauthenticated users to login")
		void unauthenticatedShouldRedirect() throws Exception {
			mockMvc.perform(get("/courses/new"))
					.andExpect(status().is3xxRedirection());
		}
	}

	// ==================== POST /courses (create) ====================

	@Nested
	@DisplayName("POST /courses")
	class CreateCourse {

		@Test
		@WithMockUser(roles = "ADMIN")
		@DisplayName("should create course and redirect on valid input")
		void shouldCreateAndRedirect() throws Exception {
			when(courseService.existsByTitle("New Course")).thenReturn(false);
			when(courseService.createCourse(org.mockito.ArgumentMatchers.any())).thenReturn("New Course");

			mockMvc.perform(post("/courses")
							.with(csrf())
							.param("title", "New Course")
							.param("description", "A new course description")
							.param("durationInHours", "30")
							.param("instructor", "Jane"))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/courses"));
		}

		@Test
		@WithMockUser(roles = "ADMIN")
		@DisplayName("should return form view when validation fails")
		void shouldReturnFormOnError() throws Exception {
			mockMvc.perform(post("/courses")
							.with(csrf())
							.param("title", "")     // blank = validation error
							.param("description", ""))
					.andExpect(status().isOk())
					.andExpect(view().name("course/form"))
					.andExpect(model().attributeHasFieldErrors("courseDTO", "title", "description"));
		}

		@Test
		@WithMockUser(roles = "ADMIN")
		@DisplayName("should reject duplicate title")
		void shouldRejectDuplicateTitle() throws Exception {
			when(courseService.existsByTitle("Spring Boot")).thenReturn(true);

			mockMvc.perform(post("/courses")
							.with(csrf())
							.param("title", "Spring Boot")
							.param("description", "A course")
							.param("durationInHours", "30"))
					.andExpect(status().isOk())
					.andExpect(view().name("course/form"))
					.andExpect(model().attributeHasFieldErrors("courseDTO", "title"));
		}
	}

	// ==================== POST /courses/{id}/delete (ADMIN) ====================

	@Nested
	@DisplayName("POST /courses/{id}/delete")
	class DeleteCourse {

		@Test
		@WithMockUser(roles = "ADMIN")
		@DisplayName("should delete and redirect")
		void shouldDeleteAndRedirect() throws Exception {
			CourseDTO course = new CourseDTO(1L, "Java", "Learn Java", 40, null, "John");
			when(courseService.getCourseById(1L)).thenReturn(course);

			mockMvc.perform(post("/courses/1/delete").with(csrf()))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/courses"));
		}
	}
}
