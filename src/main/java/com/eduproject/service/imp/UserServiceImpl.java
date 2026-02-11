package com.eduproject.service.imp;

import com.eduproject.exception.EmailAlreadyExistsException;
import com.eduproject.exception.UserNameAlreadyExists;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eduproject.model.User;
import com.eduproject.repository.UserRepository;
import com.eduproject.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private  final PasswordEncoder  encoder;
	private final UserRepository userRepository; 
	
	@Override
	public String registerUser(@Valid User user) {
		log.info("Registering user: {}", user);
		user.setPassword(this.encoder.encode(user.getPassword()));
		return this.userRepository.save(user).getFullName();
	}

	@Override
	public boolean doesUniqueEmailExists(String email) {
		return this.userRepository.existsByEmail(email);
	}

	@Override
	public boolean doesUniqueUsernameExists(String username) {
		return userRepository.existsByUsername(username);
	}

}
