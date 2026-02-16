package com.eduproject.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.eduproject.exception.CourseNotFoundException;
import com.eduproject.model.CourseEntity;
import com.eduproject.model.CourseVO;
import com.eduproject.repository.CourseRepository;
import com.eduproject.service.CourseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{

	private final CourseRepository courseRepository;


	@Override
	public List<CourseVO> getAllTheAvailableCourses() {
		return this.courseRepository.findAll().stream().map(this::convertEntityToVO).toList();
	}

	@Override
	@Transactional
	public String registerCourse(@Valid CourseVO courseVo) {
		return this.courseRepository.save(convertVOToEntity(courseVo)).getTitle();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByTitle(String title) {
		 return this.courseRepository.findByTitle(title).isPresent();

	}

	@Override
	public CourseVO getCourseById(Long courseId) {
		CourseVO courseVO = courseRepository.findById(courseId)
				.map(this::convertEntityToVO) // convert entity to VO if present
				.orElseThrow(() -> new CourseNotFoundException("Course with ID " + courseId + " not found"));
		return courseVO;
	}

	@Override
	@Transactional
	public void deleteCourseById(Long courseId) {
		if (!courseRepository.existsById(courseId)) {
			throw new CourseNotFoundException("Course with ID " + courseId + " not found");
		}
		courseRepository.deleteById(courseId);
	}

	@Override
	@Transactional
	public void updateCourseDetails(CourseVO courseVo) {

		//i am fetching the orignal first , overriding changed propery form courseVO into Orignal Entity , while keeping the same metadatas
		//doubt if  i am doing it correct or not
		CourseEntity courseEntity = this.courseRepository.findById(courseVo.getId()).orElseThrow(() -> new CourseNotFoundException("Course with ID " + courseVo.getId() + " not found"));
		BeanUtils.copyProperties(courseVo,courseEntity);
		this.courseRepository.save(courseEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public Long getNumberOfAvailableCourses() {
		return this.courseRepository.count();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByTitleExcludingCurrentCourseTitle(String title, Long id) {
		Optional<CourseEntity> courseWithTitle = courseRepository.findByTitle(title);

		boolean isCoursePresent = courseWithTitle.isPresent();

		return isCoursePresent && !courseWithTitle.get().getId().equals(id);
	}

	private CourseVO convertEntityToVO(CourseEntity course) {
		CourseVO courseVO = new CourseVO();
		BeanUtils.copyProperties(course, courseVO);
		return courseVO;
	}

	private CourseEntity convertVOToEntity(CourseVO courseVO) {
		CourseEntity courseEntity = new CourseEntity();
		BeanUtils.copyProperties(courseVO, courseEntity);
		return courseEntity;
	}
}
