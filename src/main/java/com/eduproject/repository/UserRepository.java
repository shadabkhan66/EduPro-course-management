package com.eduproject.repository;

import java.util.Optional;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eduproject.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByUsername(String username);


    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
