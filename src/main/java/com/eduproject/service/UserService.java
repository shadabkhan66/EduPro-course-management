package com.eduproject.service;

import com.eduproject.model.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public interface UserService {

	String registerUser(@Valid User user);

	boolean doesUniqueEmailExists(String email);

	boolean doesUniqueUsernameExists(String username);
}
