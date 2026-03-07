package com.eduproject.common.exception;

public class CourseNotFoundException extends RuntimeException {

	public CourseNotFoundException(String message) {
		super(message);
	}
}
