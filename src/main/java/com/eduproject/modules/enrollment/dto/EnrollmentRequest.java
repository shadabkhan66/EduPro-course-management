package com.eduproject.modules.enrollment.dto;

import com.eduproject.common.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {

	@NotNull
	private Long studentUserId;

	@NotNull
	private Long courseId;

	/** Optional; service can default (e.g. WAITING). */
	private EnrollmentStatus status;
}
