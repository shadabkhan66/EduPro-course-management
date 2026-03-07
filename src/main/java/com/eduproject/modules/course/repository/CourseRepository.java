package com.eduproject.modules.course.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduproject.modules.course.entity.CourseEntity;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

	Optional<CourseEntity> findByTitle(String title);
//    List<CourseEntity> findAll

}
