package com.eduproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduproject.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByUsername(String username);


    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long currentUserId);

    boolean existsByUsernameAndIdNot(String username, Long currentUserId);
}
