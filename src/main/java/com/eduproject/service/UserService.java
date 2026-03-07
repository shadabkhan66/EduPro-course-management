package com.eduproject.service;

import com.eduproject.model.UserRegistrationDTO;
import com.eduproject.model.UserResponseDTO;

import java.util.List;

public interface UserService {

	String registerUser(UserRegistrationDTO registrationDto);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

    UserResponseDTO getUserById(Long id);

    /**
     * Returns the user ID for the given username, or null if not found.
     * Used for "My Profile" redirect without requiring principal.id in templates.
     */
    Long getUserIdByUsername(String username);

    List<UserResponseDTO> getAllUsers();

    String updateUser(UserResponseDTO userRespDTO);

    boolean existsByEmailExcludingCurrentUser(String email, Long id);

    boolean existsByUsernameExcludingCurrentUser(String username, Long id);

    void deleteUserById(Long id);
}
