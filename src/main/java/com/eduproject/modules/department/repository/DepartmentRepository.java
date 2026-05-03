package com.eduproject.modules.department.repository;

import com.eduproject.modules.department.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long  > {
}
