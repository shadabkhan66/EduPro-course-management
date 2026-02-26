package com.eduproject.service.impl;

import java.util.List;
import java.util.Optional;

import com.eduproject.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduproject.exception.CourseNotFoundException;
import com.eduproject.model.CourseDTO;
import com.eduproject.model.CourseEntity;
import com.eduproject.repository.CourseRepository;
import com.eduproject.service.CourseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

	private final CourseRepository courseRepository;
    private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public List<CourseDTO> getAllCourses() {
		return courseRepository.findAll()
				.stream()
				.map(this::toDTO)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CourseDTO getCourseById(Long courseId) {
		return courseRepository.findById(courseId)
				.map(this::toDTO)
				.orElseThrow(() -> new CourseNotFoundException("Course with ID " + courseId + " not found"));
	}

	@Override
	@Transactional
	public String createCourse(CourseDTO courseDto) {
		CourseEntity entity = toEntity(courseDto);
		return courseRepository.save(entity).getTitle();
	}

	@Override
	@Transactional
	public void updateCourse(CourseDTO courseDto) {
		// Load the managed entity first to preserve version, audit fields
		CourseEntity entity = courseRepository.findById(courseDto.getId())
				.orElseThrow(() -> new CourseNotFoundException("Course with ID " + courseDto.getId() + " not found"));

		// Copy only user-editable fields; exclude id, version, and audit columns
		BeanUtils.copyProperties(courseDto, entity, "id", "version", "createdBy", "createdDate");
		courseRepository.save(entity);
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
	@Transactional(readOnly = true)
	public long getCourseCount() {
		return courseRepository.count();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByTitle(String title) {
		return courseRepository.findByTitle(title).isPresent();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByTitleExcludingId(String title, Long id) {
		Optional<CourseEntity> existing = courseRepository.findByTitle(title);
		return existing.isPresent() && !existing.get().getId().equals(id);
	}

    @Override
    public boolean isCourseAlreadyEnrolled(Long courseId, String username) {
//        this.courseRepository.enro
        return false;
    }

    @Override
    public void enrollUser(Long courseId, String username) {

    }

    // --- Mapping methods ---

	private CourseDTO toDTO(CourseEntity entity) {
		CourseDTO dto = new CourseDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	private CourseEntity toEntity(CourseDTO dto) {
		CourseEntity entity = new CourseEntity();
		BeanUtils.copyProperties(dto, entity);
		return entity;
	}
}
