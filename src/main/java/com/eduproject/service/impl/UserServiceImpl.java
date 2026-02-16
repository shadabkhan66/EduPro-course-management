package com.eduproject.service.impl;

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
}
