package com.eduproject.service;

import com.eduproject.model.UserRequest;
import com.eduproject.model.UserResponse;

import java.util.List;

public interface UserService {

	String registerUser(UserRequest registrationDto);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

    UserResponse getUserById(Long id);

    /**
     * Returns the user ID for the given username, or null if not found.
     * Used for "My Profile" redirect without requiring principal.id in templates.
     */
    Long getUserIdByUsername(String username);

    List<UserResponse> getAllUsers();

    String updateUser(UserResponse userRespDTO);

    boolean existsByEmailExcludingCurrentUser(String email, Long id);

    boolean existsByUsernameExcludingCurrentUser(String username, Long id);

    void deleteUserById(Long id);
}
