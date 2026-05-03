package com.eduproject.modules.course.dto;

import java.math.BigDecimal;

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
public class CourseResponse {

	private Long id;
	private String title;
	private String description;
	private Integer durationInHours;
	private BigDecimal fees;
	private Integer capacity;
	private Integer enrolledCount;
	private Boolean isActive;
	private DepartmentResponse department;
	private UserSummaryDTO instructor;
}
