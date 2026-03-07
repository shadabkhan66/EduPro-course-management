package com.eduproject.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HomeController {

	@GetMapping("/")
	public String home(HttpServletRequest request, Model model) {
		// Transfer logout message from session to model (one-time display)
		Object logoutMsg = request.getSession().getAttribute("logoutMessage");
		if (logoutMsg != null) {
			model.addAttribute("logoutMessage", logoutMsg);
			request.getSession().removeAttribute("logoutMessage");
			log.info("Showing logout message on home page");
		}
		return "home";
	}

	@GetMapping("/login")
	public String login() {
		log.info("Accessing login page");
		return "auth/login";
	}

	@GetMapping("/whoami")
	@ResponseBody
	public Object whoAmI(Authentication authentication) {
		return authentication.getAuthorities();
	}
}
