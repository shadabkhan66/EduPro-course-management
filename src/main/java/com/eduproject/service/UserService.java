package com.eduproject.service;

import com.eduproject.model.UserRegistrationDTO;

public interface UserService {

	String registerUser(UserRegistrationDTO registrationDto);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);
}
