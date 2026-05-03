package com.eduproject.modules.course.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

	@NotBlank(message = "Course title is required")
	@Size(max = 100, message = "Title must not exceed 100 characters")
	private String title;

	@NotBlank(message = "Course description is required")
	@Size(max = 500, message = "Description must not exceed 500 characters")
	private String description;

	@NotNull(message = "Duration is required")
	@Min(value = 1, message = "Duration must be at least 1 hour")
	private Integer durationInHours;

	@NotNull(message = "Fees are required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Fees must be non-negative")
	private BigDecimal fees;

	@NotNull(message = "Capacity is required")
	@Min(value = 1, message = "Capacity must be at least 1")
	private Integer capacity;

	@NotNull(message = "Department is required")
	private Long departmentId;

	@NotNull(message = "Instructor is required")
	private Long instructorUserId;
}
