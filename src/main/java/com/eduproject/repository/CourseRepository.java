package com.eduproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduproject.model.CourseEntity;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

	Optional<CourseEntity> findByTitle(String title);
//    List<CourseEntity> findAll

}
