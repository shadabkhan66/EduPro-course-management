package com.eduproject.modules.users.repository;

import java.util.Optional;

import com.eduproject.modules.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
	
	Optional<UserEntity> findByUsername(String username);


    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long currentUserId);

    boolean existsByUsernameAndIdNot(String username, Long currentUserId);
}
