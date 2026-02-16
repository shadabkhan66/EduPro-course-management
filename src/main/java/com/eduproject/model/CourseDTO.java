package com.eduproject.model;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for Course data.
 *
 * WHY a separate DTO instead of using the Entity directly?
 * 1. Validation annotations belong on the form-backing object, not the entity
 * 2. Entity may have fields the user shouldn't see/edit (version, audit fields)
 * 3. Decouples the API contract from the database schema
 * 4. Prevents accidental lazy-loading or serialization issues
 *
 * NAMING: "DTO" (Data Transfer Object) is the industry-standard name.
 *         Previously this was called "CourseVO" (Value Object) -- renamed
 *         because VO has a different meaning in Domain-Driven Design.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseDTO {

	private Long id;

	@NotBlank(message = "Course title is required")
	@Size(max = 100, message = "Title must not exceed 100 characters")
	private String title;

	@NotBlank(message = "Course description is required")
	@Size(max = 500, message = "Description must not exceed 500 characters")
	private String description;

	@Min(value = 1, message = "Duration must be at least 1 hour")
	private Integer durationInHours;

	@DecimalMin(value = "0.0", message = "Fees must be non-negative")
	private BigDecimal fees;

	private String instructor;
}
