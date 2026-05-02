package com.eduproject.common.exception;

import com.eduproject.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ApiResponse<CourseNotFoundException>> courseNotFoundHandler(CourseNotFoundException e) {
        log.error("Exception occurred while trying to fetch course " , e);
        String message = (e.getMessage() != null && !e.getMessage().isBlank()) //for protection from NullPointerException don't really now if it is required or not.
                ? e.getMessage()
                : "Course not found";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Exception>> exceptionHandler(Exception e) {
        log.error("Exception occurred while trying to fetch course ", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
    }
}
