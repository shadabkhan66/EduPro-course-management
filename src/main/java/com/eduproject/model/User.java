package com.eduproject.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@ToString(exclude = "password")
@AllArgsConstructor

public class User implements UserDetails{



	@Builder
	public User(String username, String password, String firstName, String lastName, String email, Role role) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.role = role;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@NotBlank(message = "Username is required")
	@Column(unique = true, nullable = false, length = 50)
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	private String username;

	@JsonIgnore // Don't serialize password in API responses
	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;

	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name must be less than 50 characters")
	@Column(nullable = false, length = 50)
	private String firstName;

	@Column(length = 50)
	private String lastName;

	@NotBlank(message = "Email is required")
	@Size(max = 100, message = "Email must be less than 100 characters")
	@Column(unique = true, nullable = false, length = 100)
	@Email(message = "Email should be valid")
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	@NotNull(message = "Role is required")
	private Role role;



	private boolean enabled = true;

	@Version
	private Long updateCounter;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;

	public String getFullName() {
		return lastName != null ? firstName + " " + lastName : firstName;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
}
