package com.eduproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eduproject.model.Role;
import com.eduproject.model.User;
import com.eduproject.model.UserRegistrationDTO;
import com.eduproject.repository.UserRepository;
import com.eduproject.service.impl.UserServiceImpl;

/**
 * Unit tests for UserServiceImpl.
 *
 * KEY TEST: Verifies that role is always STUDENT regardless of input.
 * This is a security test -- users must not be able to register as ADMIN.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	private UserRegistrationDTO sampleDTO() {
		UserRegistrationDTO dto = new UserRegistrationDTO();
		dto.setUsername("testuser");
		dto.setPassword("password123");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setEmail("john@test.com");
		return dto;
	}

	@Test
	@DisplayName("should encode password before saving")
	void shouldEncodePassword() {
		when(passwordEncoder.encode("password123")).thenReturn("$2a$encoded");
		when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

		userService.registerUser(sampleDTO());

		// Capture the User object that was saved
		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());

		assertThat(captor.getValue().getPassword()).isEqualTo("$2a$encoded");
	}

	@Test
	@DisplayName("should always assign STUDENT role (security)")
	void shouldAlwaysAssignStudentRole() {
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");
		when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

		userService.registerUser(sampleDTO());

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());

		// Security: role must be STUDENT, not ADMIN
		assertThat(captor.getValue().getRole()).isEqualTo(Role.STUDENT);
	}

	@Test
	@DisplayName("should return full name after registration")
	void shouldReturnFullName() {
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");
		when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

		String result = userService.registerUser(sampleDTO());

		assertThat(result).isEqualTo("John Doe");
	}

	@Test
	@DisplayName("existsByEmail should delegate to repository")
	void shouldCheckEmail() {
		when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
		assertThat(userService.existsByEmail("test@test.com")).isTrue();
	}

	@Test
	@DisplayName("existsByUsername should delegate to repository")
	void shouldCheckUsername() {
		when(userRepository.existsByUsername("testuser")).thenReturn(false);
		assertThat(userService.existsByUsername("testuser")).isFalse();
	}
}
