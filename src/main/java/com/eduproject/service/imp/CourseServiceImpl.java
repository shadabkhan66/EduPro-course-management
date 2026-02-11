package com.eduproject.service.imp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduproject.exception.CourseNotFoundException;
import com.eduproject.model.CourseEntity;
import com.eduproject.model.CourseVO;
import com.eduproject.repository.CourseRepository;
import com.eduproject.service.CourseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{

	@Autowired
	private final CourseRepository courseRepository;


	@Override
	public List<CourseVO> getAllTheAvailableCourses() {
		return this.courseRepository.findAll().stream().map(this::convertEntityToVO).toList();
	}
	
	private CourseVO convertEntityToVO(CourseEntity course) {
		CourseVO courseVO = new CourseVO();
		BeanUtils.copyProperties(course, courseVO);
		return courseVO;
	}

	@Override
	public String registerNewCourse(@Valid CourseVO courseVo) {
		
		if(courseVo.getId() != null) {
			return this.courseRepository.save(convertVOToEntityForUpdate(courseVo)).getTitle();
			
		}
		String savedCourseName = this.courseRepository.save(convertVOToEntity(courseVo)).getTitle();
		return savedCourseName;
	}
	
	private CourseEntity convertVOToEntity(CourseVO courseVO) {
		CourseEntity courseEntity = new CourseEntity();
		BeanUtils.copyProperties(courseVO, courseEntity);
		return courseEntity;
	}
	
	private CourseEntity convertVOToEntityForUpdate(CourseVO courseVO) {
		CourseEntity courseEntity = this.courseRepository
				.findById(courseVO.getId())
				.orElseThrow(() -> new CourseNotFoundException("Course with ID " + courseVO.getId() + " not found"));
		BeanUtils.copyProperties(courseVO, courseEntity, "id", "createdBy", "createdDate", "version");
		return courseEntity;
	}

	@Override
	public boolean existsByTitle(String title) {
		boolean existsByTitle = this.courseRepository.findByTitle(title).isPresent();
		
		return existsByTitle;
	}

	@Override
	public CourseVO getCourseById(Long courseId) {
		CourseVO courseVO = courseRepository.findById(courseId)
			    .map(this::convertEntityToVO) // convert entity to VO if present
			    .orElseThrow(() -> new CourseNotFoundException("Course with ID " + courseId + " not found"));
		return courseVO;
	}

	@Override
	public void deleteCourseById(Long courseId) {
		if (!courseRepository.existsById(courseId)) {
			throw new CourseNotFoundException("Course with ID " + courseId + " not found");
		}
		courseRepository.deleteById(courseId);
	}

	@Override
	public void updateCourseDetails(CourseVO courseVo) {

		this.courseRepository.findById(courseVo.getId()).orElseThrow(() -> new CourseNotFoundException("Course with ID " + courseVo.getId() + " not found"));
		this.courseRepository.save(convertVOToEntityForUpdate(courseVo));
	}

	@Override
	public Long getNumberOfAvailabelCourses() {
		return this.courseRepository.count();
	}

	@Override
	public boolean existsByTitleExcludingCurrentCourseTitle(String title, Long id) {
	    Optional<CourseEntity> courseWithTitle = courseRepository.findByTitle(title);

	    boolean isCoursePresent = courseWithTitle.isPresent();

	    return isCoursePresent && !courseWithTitle.get().getId().equals(id);
	}

}
