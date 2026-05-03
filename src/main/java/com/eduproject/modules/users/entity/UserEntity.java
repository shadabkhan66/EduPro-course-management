package com.eduproject.modules.users.entity;

import java.time.LocalDateTime;
import java.util.Set;

import com.eduproject.modules.roles.entity.RoleEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity representing a user in the system.
 *
 * Implements UserDetails so Spring Security can use this entity directly
 * for authentication without needing a separate adapter class.
 *
 * DESIGN NOTE: Validation annotations are on UserRegistrationDTO (the form DTO),
 * not here. The entity only has JPA column constraints (@Column).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@ToString(exclude = "password")
public class UserEntity
//        implements UserDetails
{

	/**
	 * Surrogate key: {@link GenerationType#IDENTITY} for consistency across H2 (dev) and Oracle 12c+ (identity columns).
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false, length = 50)
	private String username;

	@JsonIgnore
	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 50)
	private String firstName;

	@Column(length = 50)
	private String lastName;

	@Column(unique = true, nullable = false, length = 100)
	private String email;

//	@Enumerated(EnumType.STRING)
//	@Column(nullable = false, length = 10)
//	private Role role;

	@ManyToMany
	@JoinTable(
			name = "USER_ROLE",
			joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "id"))
	private Set<RoleEntity> roles;

	/**
	 * Student/instructor-specific data lives in {@link com.eduproject.modules.students.entity.StudentProfile} /
	 * {@link com.eduproject.modules.instructors.entity.InstructorProfile} with {@code @JoinColumn(user_id)} —
	 * not every user has a profile (e.g. admins). Navigation is profile → user, to keep this entity generic.
	 */
	@Builder.Default
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

    /*
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

     */
}
