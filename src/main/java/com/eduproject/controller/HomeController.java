package com.eduproject.controller;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
//@RequestMapping("/")
@Slf4j
public class HomeController {

	@GetMapping("/")
	public String home(HttpServletRequest request, Model model) {
		if(request.getSession().getAttribute("logoutMessage") != null) {
			model.addAttribute("logoutMessage", request.getSession().getAttribute("logoutMessage"));
			request.getSession().removeAttribute("logoutMessage");
			log.info("Accessing home page after logout, showing logout message");
		}
		log.info("Accessing home page without logout parameter");
		return "home/home";
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
