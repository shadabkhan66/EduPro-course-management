package com.eduproject.model;

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
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseVO {

	private Long id;
	@NotNull(message = "Course title must not be null")
	@NotBlank(message = "Course title is required")
	@Size(max = 100, message = "Title must not exceed 100 characters")
	
	private String title;
	@NotNull(message = "Course description must not be null")
	 @NotBlank(message = "Course description is required")
	@Size(max = 500, message = "Description must not exceed 500 characters")
	
	private String description;
	
	@Min(value = 1, message = "Duration must be at least 1 hour")
	private Integer durationInHours;
	
	@DecimalMin(value = "0.0", message = "Fees must be non-negative")
	private BigDecimal fees;// don't know if taking BigDecimal is correct or not

	private String instructor;
}
