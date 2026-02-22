package com.eduproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler using @ControllerAdvice.
 *
 * Catches exceptions thrown from any controller and returns
 * appropriate error views with HTTP status codes.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(CourseNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleCourseNotFound(CourseNotFoundException ex, Model model) {
		log.error("Course not found: {}", ex.getMessage());
		model.addAttribute("errorMessage", ex.getMessage());
		return "error/404";
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNotFound(NoResourceFoundException ex, Model model) {
		log.error("Resource not found: {}", ex.getMessage());
		model.addAttribute("errorMessage", "Page not found.");
		return "error/404";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleGeneralException(Exception ex, Model model) {
		log.error("Unexpected error occurred", ex);  // Log full stack trace
		model.addAttribute("errorMessage", "Something went wrong. Please try again later.");
		return "error/500";
	}
}
