package com.eduproject.modules.course.mapper;

import com.eduproject.modules.course.dto.CourseRequest;
import com.eduproject.modules.course.dto.CourseResponse;
import com.eduproject.modules.course.entity.CourseEntity;
import com.eduproject.modules.department.dto.DepartmentResponse;
import com.eduproject.modules.users.dto.UserSummaryDTO;

public final class CourseMapper {

	private CourseMapper() {
	}

	public static CourseResponse toResponse(CourseEntity entity) {
		if (entity == null) {
			return null;
		}
		DepartmentResponse dept = null;
		if (entity.getDepartment() != null) {
			dept = DepartmentResponse.builder()
					.id(entity.getDepartment().getId())
					.name(entity.getDepartment().getName())
					.code(entity.getDepartment().getCode())
					.description(entity.getDepartment().getDescription())
					.build();
		}
		UserSummaryDTO instructor = null;
		if (entity.getInstructor() != null) {
			var u = entity.getInstructor();
			instructor = UserSummaryDTO.builder()
					.id(u.getId())
					.username(u.getUsername())
					.firstName(u.getFirstName())
					.lastName(u.getLastName())
					.email(u.getEmail())
					.build();
		}
		return CourseResponse.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.description(entity.getDescription())
				.durationInHours(entity.getDurationInHours())
				.fees(entity.getFees())
				.capacity(entity.getCapacity())
				.enrolledCount(entity.getEnrolledCount())
				.isActive(entity.getIsActive())
				.department(dept)
				.instructor(instructor)
				.build();
	}

	/**
	 * Maps scalar fields only. Caller must set {@link CourseEntity#getDepartment()} and
	 * {@link CourseEntity#getInstructor()} from {@link CourseRequest#getDepartmentId()} /
	 * {@link CourseRequest#getInstructorUserId()}.
	 */
	public static CourseEntity toNewEntity(CourseRequest request) {
		if (request == null) {
			return null;
		}
		return CourseEntity.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.durationInHours(request.getDurationInHours())
				.fees(request.getFees())
				.capacity(request.getCapacity())
				.build();
	}

	public static void applyScalars(CourseRequest request, CourseEntity entity) {
		if (request == null || entity == null) {
			return;
		}
		entity.setTitle(request.getTitle());
		entity.setDescription(request.getDescription());
		entity.setDurationInHours(request.getDurationInHours());
		entity.setFees(request.getFees());
		entity.setCapacity(request.getCapacity());
	}
}
