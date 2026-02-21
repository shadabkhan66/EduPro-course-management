package com.eduproject.service;

import com.eduproject.model.UserRegistrationDTO;
import com.eduproject.model.UserResponseDTO;

import java.util.List;

public interface UserService {

	String registerUser(UserRegistrationDTO registrationDto);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

    UserResponseDTO getUserById(Long id);

    List<UserResponseDTO> getAllUsers();

    String updateUser(UserResponseDTO userRespDTO);

    boolean existsByEmailExcludingCurrentUser(String email, Long id);

    boolean existsByUsernameExcludingCurrentUser(String username, Long id);
}
