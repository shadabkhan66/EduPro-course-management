package com.eduproject.modules.course.service;

import java.util.List;
import java.util.Map;

import com.eduproject.modules.course.dto.CourseRequest;
import com.eduproject.modules.course.dto.CourseResponse;

public interface CourseService {

	List<CourseResponse> getAllCourses();

	CourseResponse getCourseById(Long courseId);

	CourseResponse createCourse(CourseRequest courseRequest);

    CourseResponse updateCourse(Long id, CourseRequest courseRequest);

    CourseResponse patchCourse(Long id, Map<String, Object> updates);

	void deleteCourseById(Long courseId);
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
