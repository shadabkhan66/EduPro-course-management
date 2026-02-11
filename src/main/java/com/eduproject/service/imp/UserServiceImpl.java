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
		// Check if email already exists
		if(userRepository.existsByEmail(user.getEmail())) {
			log.warn("Registration failed: Email {} already exists", user.getEmail());
			//custome exception can be created for better handling
			throw new EmailAlreadyExistsException("Email already exists");
		}
		//check if username already exists
		if(userRepository.existsByUsername(user.getUsername())) {
			log.warn("Registration failed: Username {} already exists", user.getUsername());
			//custome exception can be created for better handling
			throw new UserNameAlreadyExists("Username already exists");
		}

		user.setPassword(this.encoder.encode(user.getPassword()));
		return this.userRepository.save(user).getId() + "";
	}
	
}
