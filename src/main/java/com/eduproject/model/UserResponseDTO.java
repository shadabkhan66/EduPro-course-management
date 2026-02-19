package com.eduproject.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for user returning User to View.
 *
 * WHY not use the User entity directly as form-backing bean?
 * 1. Entity has fields the user shouldn't set (id, enabled, version, timestamps)
 * 2. Validation annotations clutter the entity
 * 3. Entity implements UserDetails (Spring Security concern) -- mixing with
 *    form binding violates Single Responsibility Principle
 * 4. Password field: DTO holds the raw password, entity stores encoded password
 *
 * SECURITY FIX: Role is hard form let users pick any role including ADMIN.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserResponseDTO {

	//i don't think we need validation

	private Long id;

//	@NotBlank(message = "Username is required")
//	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	private String username;

//	@NotBlank(message = "Password is required")
//	@Size(min = 6, message = "Password must be at least 6 characters")
//	private String password;

//	@NotBlank(message = "First name is required")
//	@Size(max = 50, message = "First name must be less than 50 characters")
	private String firstName;

//	@Size(max = 50, message = "Last name must be less than 50 characters")
	private String lastName;

//	@NotBlank(message = "Email is required")
//	@Size(max = 100, message = "Email must be less than 100 characters")
//	@Email(message = "Email should be valid")
	private String email;

	private String fullName;

	private String createdAt;

	private String lastUpdatedAt;

	private String role;

	private boolean isActive;
}
