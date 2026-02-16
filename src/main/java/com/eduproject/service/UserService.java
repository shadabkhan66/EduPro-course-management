package com.eduproject.service;

import com.eduproject.model.User;

import jakarta.validation.Valid;

public interface UserService {

	String registerUser(@Valid User user);

	boolean doesUniqueEmailExists(String email);

	boolean doesUniqueUsernameExists(String username);
}
