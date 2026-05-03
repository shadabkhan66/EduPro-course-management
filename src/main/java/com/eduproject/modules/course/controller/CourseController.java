package com.eduproject.modules.course.controller;

import com.eduproject.common.response.ApiResponse;
import com.eduproject.modules.course.dto.CourseRequest;
import com.eduproject.modules.course.dto.CourseResponse;
import com.eduproject.modules.course.service.CourseServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eduproject.common.exception.CourseNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Handles all course-related web requests.
 *
 * URL Design (RESTful naming):
 *   GET  /courses              → list all courses
 *   GET  /courses/{id}         → view single course
 *   GET  /courses/new          → show create form
 *   POST /courses              → handle create
 *   GET  /courses/{id}/edit    → show edit form
 *   POST /courses/{id}         → handle update
 *   POST /courses/{id}/delete  → handle delete
 */

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/v1/courses", produces = "application/json")
public class CourseController {

    private final CourseServiceImpl  courseService;

	// ==================== LIST ALL COURSES ====================

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {

        log.info("Fetching all courses");

        List<CourseResponse> courses = courseService.getAllCourses();

        if (courses.isEmpty()) { // doubt if this conditional statment is good
            return ResponseEntity.noContent().build(); // Returns 204 or should we return any other status code
        }

        return ResponseEntity.ok(ApiResponse.success(courses,"All Courses fetched")); // Returns 200 + Data
    }

	// ==================== GET COURSE BY ID ====================

    @GetMapping("/{id}") //	@GetMapping("/{id:\\d+}") maybe this may help
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) { // problem what if we encounter String how to handle it
        log.info("Request received to get course by id {}", id);
        CourseResponse course = this.courseService.getCourseById(id);

//        if(course == null) {  // i don't really course will ever come null because if course not found it will throw exception , do don't know if this line was necessary
//            return ResponseEntity.noContent().build();
//        }

        return ResponseEntity.ok(
                ApiResponse.success(course,"Course found")
        );
    }

    // ==================== CREATE COURSE ====================

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CourseRequest courseRequest) {

        log.info("Creating course: {}", courseRequest.getTitle());

        CourseResponse course = courseService.createCourse(courseRequest);

        String msg = String.format(
                "Course '%s' created successfully with id %d",
                course.getTitle(),
                course.getId()
        );

        URI location = URI.create("/v1/courses/" + course.getId());

        return ResponseEntity
                .created(location)
                .body(ApiResponse.success(course, msg));
    }

    // ==================== UPDATE COURSE (FULL) ====================

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {

        log.info("Updating course id: {}", id);

        CourseResponse updated = courseService.updateCourse(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(updated, "Course updated successfully")
        );
    }

    // ==================== PATCH COURSE (PARTIAL UPDATE) ====================

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> patchCourse(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        log.info("Patching course id: {}", id);

        CourseResponse updated = courseService.patchCourse(id, updates);

        return ResponseEntity.ok(
                ApiResponse.success(updated, "Course partially updated successfully")
        );
    }
    // ==================== DELETE COURSE ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {

        log.info("Deleting course id: {}", id);

        courseService.deleteCourseById(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Course deleted successfully")
        );
    }

    // ================== Enroll ====================
/*
    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<ApiResponse<Void>> enroll(
            @PathVariable Long courseId,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Please login first"));
        }

        String username = principal.getName();

        log.info("User {} enrolling in course {}", username, courseId);

        if (courseService.isCourseAlreadyEnrolled(courseId, username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Already enrolled in this course"));
        }

        courseService.enrollUser(courseId, username);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Enrolled successfully")
        );
    }

 */
}
