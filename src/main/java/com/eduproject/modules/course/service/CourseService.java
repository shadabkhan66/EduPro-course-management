package com.eduproject.modules.course.service;

import java.util.List;

import com.eduproject.modules.course.dto.CourseResponse;

public interface CourseService {

	List<CourseResponse> getAllCourses();

//	CourseResponse getCourseById(Long courseId);
//
//	String createCourse(CourseDTO courseDto);
//
//	void updateCourse(CourseDTO courseDto);
//
//	void deleteCourseById(Long courseId);
//
//	long getCourseCount();
//
//	boolean existsByTitle(String title);
//
//	boolean existsByTitleExcludingId(String title, Long id);
//
//    boolean isCourseAlreadyEnrolled(Long courseId, String username);
//
//    void enrollUser(Long courseId, String username);
//
//    Long getUserId(String username);
}
