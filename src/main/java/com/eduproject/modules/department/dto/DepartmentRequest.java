package com.eduproject.modules.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequest {

	@NotBlank
	@Size(max = 60)
	private String name;

	@NotBlank
	@Size(max = 32)
	private String code;

	@Size(max = 500)
	private String description;
}
