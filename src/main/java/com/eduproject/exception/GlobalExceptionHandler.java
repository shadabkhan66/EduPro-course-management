package com.eduproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CourseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCourseNotFound(CourseNotFoundException ex, Model model){
        log.error(ex.getMessage());
        log.info("in globalExceptionHandler");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";  // resolves to 404.jsp
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, Model model) {
        log.error(ex.getMessage());
        log.info("in globalExceptionHandler");
        model.addAttribute("errorMessage", "Something went wrong.");

        return "error/500";
    }
}
