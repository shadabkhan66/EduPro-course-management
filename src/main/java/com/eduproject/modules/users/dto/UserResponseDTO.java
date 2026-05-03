package com.eduproject.modules.users.dto;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Read model for API / views. No password. Roles are role names only (e.g. for JWT/UI).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserResponseDTO {

	private Long id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String fullName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Builder.Default
	private Set<String> roleNames = new LinkedHashSet<>();

	private boolean active;
}
