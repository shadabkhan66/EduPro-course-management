package com.eduproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the minimal static UI under {@code /}, {@code /courses}, and Spring Security's {@code /login}.
 */
@Controller
public class WebPageController {

	@GetMapping("/")
	public String home() {
		return "forward:/index.html";
	}

	/** Matches {@link com.eduproject.config.SecurityConfig} default success URL after form login. */
	@GetMapping("/courses")
	public String courses() {
		return "forward:/index.html";
	}

	@GetMapping("/login")
	public String login() {
		return "forward:/login.html";
	}
}
