package com.eduproject.service.impl;

import com.eduproject.exception.UserNotFoundException;
import com.eduproject.model.UserResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduproject.model.Role;
import com.eduproject.model.User;
import com.eduproject.model.UserRegistrationDTO;
import com.eduproject.repository.UserRepository;
import com.eduproject.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public String registerUser(UserRegistrationDTO dto) {
		log.info("Registering user: {}", dto.getUsername());

		User user = User.builder()
				.username(dto.getUsername())
				.password(passwordEncoder.encode(dto.getPassword()))
				.firstName(dto.getFirstName())
				.lastName(dto.getLastName())
				.email(dto.getEmail())
				.role(Role.STUDENT)  // Security: always default to STUDENT
				.build();

		User saved = userRepository.save(user);
		return saved.getFullName();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponseDTO getUserById(Long id) {
		log.info("Retrieving user by id: {}", id);
		User fetchedUser = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with Id : " + id));

		return UserResponseDTO.builder()
				.id(fetchedUser.getId())
				.username(fetchedUser.getUsername())
				.firstName(fetchedUser.getFirstName())
				.lastName(fetchedUser.getLastName())
				.email(fetchedUser.getEmail())
				.fullName(fetchedUser.getFullName())
				.createdAt(fetchedUser.getCreatedAt().toString())
				.lastUpdatedAt(fetchedUser.getUpdatedAt() == null ? "Never Updated" : fetchedUser.getUpdatedAt().toString())
				.role(fetchedUser.getRole().toString())
				.isActive(fetchedUser.isEnabled())
				.build();
	}
}
