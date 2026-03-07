package com.eduproject.modules.course.controller;

import com.eduproject.common.response.ApiResponse;
import com.eduproject.modules.course.dto.CourseResponse;
import com.eduproject.modules.course.service.CourseServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eduproject.common.exception.CourseNotFoundException;
import com.eduproject.modules.course.dto.CourseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.List;

/**
 * Handles all course-related web requests.
 *
 * URL Design (RESTful naming):
 *   GET  /courses              → list all courses
 *   GET  /courses/{id}         → view single course
 *   GET  /courses/new          → show create form
 *   POST /courses              → handle create
 *   GET  /courses/{id}/edit    → show edit form
 *   POST /courses/{id}         → handle update
 *   POST /courses/{id}/delete  → handle delete
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/courses")
public class CourseController {

    private final CourseServiceImpl  courseService;

	// ==================== LIST ====================

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> listAllCourses() {
        log.info("Request received to list all courses");
        List<CourseResponse> courses = this.courseService.getAllCourses();

        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns 204
        }

        return ResponseEntity.ok(ApiResponse.success(courses,"All Courses fetched")); // Returns 200 + Data
    }

	// ==================== VIEW ====================

	@GetMapping("/{id:\\d+}")
	public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
		log.info("Viewing course with ID: {}", id);
        CourseResponse course = this.courseService.getCourseById(id);

		return ResponseEntity.ok(course);
	}

	// ==================== CREATE ====================

	@GetMapping("/new")
	public String showCreateForm(Model model) {
		log.info("Showing create course form");
		model.addAttribute("courseDTO", new CourseDTO());
		model.addAttribute("pageHeading", "Create New Course");
		model.addAttribute("submitLabel", "Create Course");
		model.addAttribute("editMode", false);
		return "course/form";
	}

	@PostMapping
	public String createCourse(
			@Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		log.info("Creating course: {}", courseDTO.getTitle());

		if (courseService.existsByTitle(courseDTO.getTitle())) {
			bindingResult.rejectValue("title", "duplicate", "Course title already exists");
		}

		if (bindingResult.hasErrors()) {
			log.warn("Validation errors: {}", bindingResult.getAllErrors());
			model.addAttribute("pageHeading", "Create New Course");
			model.addAttribute("submitLabel", "Create Course");
			model.addAttribute("editMode", false);
			return "course/form";
		}

		String savedTitle = courseService.createCourse(courseDTO);
		redirectAttributes.addFlashAttribute("successMessage", "Course '" + savedTitle + "' created successfully!");
		return "redirect:/courses";
	}

	// ==================== EDIT ====================

	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model) {
		log.info("Showing edit form for course ID: {}", id);
		model.addAttribute("courseDTO", courseService.getCourseById(id));
		model.addAttribute("pageHeading", "Edit Course");
		model.addAttribute("submitLabel", "Update Course");
		model.addAttribute("editMode", true);
		return "course/form";
	}

	@PostMapping("/{id}")
	public String updateCourse(
			@PathVariable Long id,
			@Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		log.info("Updating course ID: {}", id);

		// Security: trust the URL path ID, not the form's hidden field
		courseDTO.setId(id);

		if (courseService.existsByTitleExcludingId(courseDTO.getTitle(), id)) {
			bindingResult.rejectValue("title", "duplicate", "Course title already exists");
		}

		if (bindingResult.hasErrors()) {
			log.warn("Validation errors: {}", bindingResult.getAllErrors());
			model.addAttribute("pageHeading", "Edit Course");
			model.addAttribute("submitLabel", "Update Course");
			model.addAttribute("editMode", true);
			return "course/form";
		}

		try {
			courseService.updateCourse(courseDTO);
			redirectAttributes.addFlashAttribute("successMessage",
					"Course '" + courseDTO.getTitle() + "' updated successfully!");
		} catch (CourseNotFoundException e) {
			log.error("Course not found during update: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", "Course not found. Update failed.");
		}

		return "redirect:/courses";
	}

	// ==================== DELETE ====================

	@PostMapping("/{id}/delete")
	public String deleteCourse(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
		log.info("Deleting course ID: {}", id);
		try {
			CourseDTO course = courseService.getCourseById(id);
			courseService.deleteCourseById(id);
			redirectAttributes.addFlashAttribute("successMessage",
					"Course '" + course.getTitle() + "' deleted successfully!");
		} catch (CourseNotFoundException e) {
			log.error("Course not found for deletion: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", "Course not found for deletion.");
		}
		return "redirect:/courses";
	}

    // ================== Enroll ====================

    @PostMapping("/enroll")
    public String enroll(@RequestParam("courseId") Long courseId,
                         Principal principal,
                         RedirectAttributes redirectAttributes
    ) {

        //checking if user is logId in or not if not redirect to log in page and moment after log in redirect to seme page
        //but i think my savedreqeust is not working so i get redirected back to same page
        if(principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login! First Before Enrolling!");
            return "redirect:/login?enroll";
        }
        String username = principal.getName();
        log.info("Enrolling course ID: {} for user {}", courseId, username);

        if (courseService.isCourseAlreadyEnrolled(courseId, username)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You are already enrolled in this course.");
            return "redirect:/courses/" + courseId;
        }

        courseService.enrollUser(courseId, username);
        Long userId = this.courseService.getUserId(username);
        redirectAttributes.addFlashAttribute("successMessage",
                "Successfully enrolled in the course.");

        return "redirect:/users/" + userId;
    }
}
