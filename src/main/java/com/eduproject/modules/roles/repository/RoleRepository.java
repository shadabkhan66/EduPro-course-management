package com.eduproject.modules.roles.repository;

import java.util.Optional;

import com.eduproject.modules.roles.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

	Optional<RoleEntity> findByNameIgnoreCase(String name);
}
