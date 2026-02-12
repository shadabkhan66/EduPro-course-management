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
import com.eduproject.model.CourseVO;
import com.eduproject.service.CourseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

	private final CourseService courseService;

	@GetMapping
	public String listCourses(Model model) {

		log.info("Listing all courses");

		
		//adding data to the model to be used in the view
		model.addAttribute("pageTitle", "Course List");
		model.addAttribute("courses", courseService.getAllTheAvailableCourses());
		model.addAttribute("numberOfCourses", this.courseService.getNumberOfAvailabelCourses());
		return "course/course-list"; // returns the logical view name
	}
	
	@GetMapping("/add")
	public String showAddCourseForm(Model model) {
		log.info("Showing add course form");
		model.addAttribute("course", new CourseVO()); // Add an empty CourseVO for the form
		model.addAttribute("pageHeading", "Create New Course"); 
		model.addAttribute("submitButtonLabel", "Register Course"); 
		model.addAttribute("isEditMode", false); // Flag to indicate this is an add form, not edit
		return "course/course-form"; // returns the logical view name for the add course form
	}
	
	@PostMapping
	public String registerCourse(
	        @Valid @ModelAttribute("course") CourseVO course,
	        BindingResult bindingResult,
	        Model model,
	        RedirectAttributes redirectAttributes) {

		log.info("Received course registration request: {}", course);
		
	    // Check DB uniqueness
	    if (courseService.existsByTitle(course.getTitle())) {
	        bindingResult.rejectValue("title", "error.course", "Course title already exists");
	    }
	    
		
	    if (bindingResult.hasErrors()) {
	        log.warn("Validation errors while registering course: {}", bindingResult.getAllErrors());

	        //return to the form with error messages and previously entered valide data
	        // Why we don't need to add the course object back to the model here? Because @ModelAttribute already adds it to the model, and it will contain the submitted data along with validation errors. So we can directly use it in the view to display error messages and pre-populate the form fields with the submitted data.
	        // The course object with validation errors is already in the model due to @ModelAttribute, BindingResult will also be available in the model for the view to display error messages
	        model.addAttribute("pageHeading", course.getId() != null ? "Edit Course" : "Create New Course");
	        model.addAttribute("submitButtonLabel", course.getId() != null ? "Update Course" : "Register Course");
	        model.addAttribute("isEditMode", course.getId() != null); // Set edit mode based on whether the course has an ID
	        return "course/course-form"; // return to the form view if there are validation errors
	    }
		
	    log.info("Registering course: {}", course);
	    String savedCourseName = courseService.registerNewCourse(course);
	    log.info("Course registered successfully: {}", savedCourseName);
	    
	    redirectAttributes.addFlashAttribute("successMessage",
	            "Course Name :"+ savedCourseName + (course.getId() != null ? " updated successfully!" : " registered successfully!"));

	    return "redirect:/courses";
	}
	
	@GetMapping("/edit/{courseId}")
	public String showEditCourseForm(@PathVariable Long courseId, Model model) {

		// Implementation for showing edit course form
		log.info("Showing edit course form for courseId: {}", courseId);
		CourseVO course;
		try {
			
			 course =  this.courseService.getCourseById(courseId);
		}
		catch (CourseNotFoundException e) {
			log.error("Error fetching course with ID {}: {}", courseId, e.getMessage());
			// Handle the error, e.g., redirect to an error page or show a message
			return "redirect:/courses?error=CourseNotFoundForEditing";
		}
		model.addAttribute("course", course); // Add the course to the model for the form
		model.addAttribute("pageHeading", "Edit Course");
		model.addAttribute("submitButtonLabel", "Update Course");
		model.addAttribute("isEditMode", true); // Flag to indicate this is an edit form
		log.info("Course fetched successfully for editing: {}", course);
		return "course/course-form"; // returns the logical view name for the edit course form
	}
	
	
	@PostMapping("/{courseId}")
	public String editCourseForm(
			Model model,
	        @Valid @ModelAttribute("course") CourseVO courseVo,
	        BindingResult bindingResult,
	        RedirectAttributes redirectAttributes) {

	    log.info("Received request to edit course with ID: {}", courseVo.getId());
	    
	    // Check DB uniqueness by editing course title, my mistakely placing the course title which is already present
	    if(this.courseService.existsByTitleExcludingCurrentCourseTitle(courseVo.getTitle(), courseVo.getId())) {
	    	bindingResult.rejectValue("title", "error.course", "Course title already exists");
	    }
	    
	    if (bindingResult.hasErrors()) {
	        log.warn("Validation errors while registering course: {}", bindingResult.getAllErrors());

	        //return to the form with error messages and previously entered valide data
	        // Why we don't need to add the course object back to the model here? Because @ModelAttribute already adds it to the model, and it will contain the submitted data along with validation errors. So we can directly use it in the view to display error messages and pre-populate the form fields with the submitted data.
	        // The course object with validation errors is already in the model due to @ModelAttribute, BindingResult will also be available in the model for the view to display error messages
			model.addAttribute("pageHeading", "Edit Course");
			model.addAttribute("submitButtonLabel", "Update Course");
			model.addAttribute("isEditMode", true); // Flag to indicate this is an edit form
	        return "course/course-form"; // return to the form view if there are validation errors
	    }
	    
	    try {
	        this.courseService.updateCourseDetails(courseVo);
	        log.info("Course updated successfully: {}", courseVo);

	        redirectAttributes.addFlashAttribute(
	            "successMessage",
	            "Course Name : " + courseVo.getTitle() + " updated successfully!"
	        );

	    } catch (CourseNotFoundException e) {
	        log.error("Error updating course: {}", e.getMessage());

	        redirectAttributes.addFlashAttribute(
	            "errorMessage",
	            "Course not found. Update failed."
	        );
	    }

	    return "redirect:/courses";
	}
	

	@PostMapping("/delete/{courseId}")
	public String deleteCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
		// Implementation for deleting a course
		log.info("Received request to delete course with ID: {}", courseId);
		try {
			CourseVO course = this.courseService.getCourseById(courseId);
			this.courseService.deleteCourseById(courseId);
			log.info("Course deleted successfully with ID: {}", courseId);
			redirectAttributes.addFlashAttribute("successMessage",
		            "Course Name :"+ course.getTitle()+" deleted successfully!");
		} catch (CourseNotFoundException e) {
			log.error("Error deleting course with ID {}: {}", courseId, e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", "Course not found for deletion");
		}
		
		return "redirect:/courses";
	}
	
}
