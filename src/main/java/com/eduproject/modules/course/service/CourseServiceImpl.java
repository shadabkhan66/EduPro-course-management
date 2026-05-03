package com.eduproject.modules.course.service;

import java.util.List;
import java.util.Map;

import com.eduproject.common.exception.DuplicateResourceException;
import com.eduproject.common.exception.ResourceNotFoundException;
import com.eduproject.modules.course.dto.CourseRequest;
import com.eduproject.modules.course.dto.CourseResponse;
import com.eduproject.modules.course.mapper.CourseMapper;
import com.eduproject.modules.department.repository.DepartmentRepository;
import com.eduproject.modules.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduproject.common.exception.CourseNotFoundException;
import com.eduproject.modules.course.entity.CourseEntity;
import com.eduproject.modules.course.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

	private final CourseRepository courseRepository;
	private final UserRepository userRepository;
	private final DepartmentRepository departmentRepository;

	@Transactional(readOnly = true)
	public List<CourseResponse> getAllCourses() {
		return courseRepository.findAll()
				.stream()
				.map(
//                        c ->{ CourseResponse cr = new CourseResponse();
//                                        cr.setCourseName(c.getTitle());
//                                        cr.setCourseFee(c.getFees() == null ? 0.0 : c.getFees().doubleValue());
//                                        return cr;
//                                    }
                        CourseMapper::toResponse  //so should i return some filds like fee as null or convert it into 0.0
                )
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CourseResponse getCourseById(Long id) {
		return courseRepository.findById(id)
				.map(CourseMapper::toResponse)
				.orElseThrow(() -> new CourseNotFoundException("Course with ID " + id + " not found"));
	}

	@Override
	@Transactional
	public CourseResponse createCourse(CourseRequest courseRequest){

        //checking if course already present or not
        if(courseRepository.existsByTitle(courseRequest.getTitle())){
            throw new DuplicateResourceException( "Course with title " + courseRequest.getTitle() + " already exists!");
        }
        CourseEntity course = CourseMapper.toNewEntity(courseRequest);
		course.setDepartment(departmentRepository.findById(courseRequest.getDepartmentId())
				.orElseThrow(() -> new ResourceNotFoundException("Department not found: " + courseRequest.getDepartmentId())));
		course.setInstructor(userRepository.findById(courseRequest.getInstructorUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Instructor user not found: " + courseRequest.getInstructorUserId())));
        course = this.courseRepository.save(course);
        return CourseMapper.toResponse(course);
	}


	@Override
	@Transactional
	public CourseResponse updateCourse(Long id ,CourseRequest request) {
        // 1. Fetch existing entity
		CourseEntity course = courseRepository.findById(id)
				.orElseThrow(() -> new CourseNotFoundException("Course with ID " + id + " not found"));

        // doubt not the best method to handle update, I guess there are a better way to handle validation problem

        // 2. Update entity from request (does NOT create new object)
        CourseMapper.applyScalars(request, course);
		course.setDepartment(departmentRepository.findById(request.getDepartmentId())
				.orElseThrow(() -> new ResourceNotFoundException("Department not found: " + request.getDepartmentId())));
		course.setInstructor(userRepository.findById(request.getInstructorUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Instructor user not found: " + request.getInstructorUserId())));

		return CourseMapper.toResponse(courseRepository.save(course));
	}

    @Override
    public CourseResponse patchCourse(Long id, Map<String, Object> updates) {
        return null;
    }


    @Override
	@Transactional
	public void deleteCourseById(Long courseId) {
		if (!courseRepository.existsById(courseId)) {
			throw new CourseNotFoundException("Course with ID " + courseId + " not found");
		}
		courseRepository.deleteById(courseId);
	}
/*

    @Override
    @Transactional(readOnly = true)
    public boolean isCourseAlreadyEnrolled(Long courseId, String username) {
        CourseEntity courseEntity = this.courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course with ID " + courseId + " not found"));
        return courseEntity.getEnrolledUsers().contains(userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User with username  " + username + " not found")));
    }

    @Override
    @Transactional
    public void enrollUser(Long courseId, String username) {
        this.courseRepository.findById(courseId)
                .get()
                .setEnrolledUsers(List.of(userRepository.findByUsername(username).get()));


    }

    */
}
