package com.eduproject.service;

import java.util.List;

import com.eduproject.model.CourseDTO;

public interface CourseService {

	List<CourseDTO> getAllCourses();

	CourseDTO getCourseById(Long courseId);

	String createCourse(CourseDTO courseDto);

	void updateCourse(CourseDTO courseDto);

	void deleteCourseById(Long courseId);

	long getCourseCount();

	boolean existsByTitle(String title);

	boolean existsByTitleExcludingId(String title, Long id);

    boolean isCourseAlreadyEnrolled(Long courseId, String username);

    void enrollUser(Long courseId, String username);

    Long getUserId(String username);
}
