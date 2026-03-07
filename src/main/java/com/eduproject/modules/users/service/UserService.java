package com.eduproject.modules.users.service;

import com.eduproject.modules.users.dto.UserRegistrationDTO;
import com.eduproject.modules.users.dto.UserResponseDTO;

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

    void deleteUserById(Long id);
}
