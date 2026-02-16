package com.eduproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eduproject.exception.CourseNotFoundException;
import com.eduproject.model.CourseDTO;
import com.eduproject.service.CourseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

	private final CourseService courseService;

	// ==================== LIST ====================

	@GetMapping
	public String listCourses(Model model) {
		log.info("Listing all courses");
		model.addAttribute("courses", courseService.getAllCourses());
		model.addAttribute("courseCount", courseService.getCourseCount());
		return "course/list";
	}

	// ==================== VIEW ====================

	@GetMapping("/{id}")
	public String viewCourse(@PathVariable Long id, Model model) {
		log.info("Viewing course with ID: {}", id);
		model.addAttribute("course", courseService.getCourseById(id));
		return "course/view";
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
	public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
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
}
