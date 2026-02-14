package com.eduproject.service;

import java.util.List;

import com.eduproject.model.CourseVO;

import jakarta.validation.Valid;

public interface CourseService {
	
	List<CourseVO> getAllTheAvailableCourses();

	String registerCourse(@Valid CourseVO courseVo);

	boolean existsByTitle(String title);

	CourseVO getCourseById(Long courseId);

	void deleteCourseById(Long courseId);

	void updateCourseDetails(CourseVO courseVo);

	Long getNumberOfAvailableCourses();

	boolean existsByTitleExcludingCurrentCourseTitle(String title, Long id);

}
