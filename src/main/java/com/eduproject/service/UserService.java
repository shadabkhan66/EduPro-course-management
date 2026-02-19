package com.eduproject.service;

import com.eduproject.model.UserRegistrationDTO;
import com.eduproject.model.UserResponseDTO;

public interface UserService {

	String registerUser(UserRegistrationDTO registrationDto);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

    UserResponseDTO getUserById(Long id);
}
