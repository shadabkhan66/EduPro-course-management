package com.eduproject.service;

import java.util.List;

import com.eduproject.model.CourseResponse;
import com.eduproject.model.CreateCourseRequest;

public interface CourseService {

	List<CourseResponse> getAllCourses();

	CourseResponse getCourseById(Long courseId);

	String createCourse(CreateCourseRequest createCourseRequest);

	void updateCourse(CreateCourseRequest createCourseRequest);

	void deleteCourseById(Long courseId);

	long getCourseCount();

	boolean existsByTitle(String title);

	boolean existsByTitleExcludingId(String title, Long id);

    boolean isCourseAlreadyEnrolled(Long courseId, String username);

    void enrollUser(Long courseId, String username);
}
