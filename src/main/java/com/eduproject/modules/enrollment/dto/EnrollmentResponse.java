package com.eduproject.modules.enrollment.dto;

import com.eduproject.common.enums.EnrollmentStatus;
import com.eduproject.modules.department.dto.DepartmentResponse;
import com.eduproject.modules.users.dto.UserSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {

	private Long id;
	private EnrollmentStatus status;
	private UserSummaryDTO student;
	private Long courseId;
	private String courseTitle;
	private DepartmentResponse department;
}
