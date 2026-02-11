package com.eduproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eduproject.model.Role;
import com.eduproject.model.User;
import com.eduproject.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		log.info("Show registration form");
		// for role dropdown
		model.addAttribute("roles", Role.values());		
		model.addAttribute("user", new User()); // for form binding
		model.addAttribute("pageTitle", "User Registration"); // later it may become Student , Teacher etc.
//		model.addAttribute("formAction", "/users/register"); // for form action URL
		model.addAttribute("isEdit", false); // to differentiate between create and edit form
		model.addAttribute("submitButtonLabel", "Register"); // for submit button label
		return "user/user_form";
	}
	
	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user, 
								BindingResult result,
								RedirectAttributes redirectAttributes,
								Model model) {
		log.info("Trying to register user with user details {} ", user);
		if(result.hasErrors()) {
			model.addAttribute("pageTitle", "User Registration");
			log.error("Validation error {} ", result.getAllErrors());
			return "user/user_form";
		}
		String savedUserName;
		try {
			savedUserName = userService.registerUser(user);
			
		}
		catch(Exception e) {
			result.rejectValue("email", "error.user", e.getMessage());
			model.addAttribute("pageTitle", "User Registration");
			return "user/user_form";
		}
		redirectAttributes.addFlashAttribute("message", "User has been registered successfully with user Name :" +savedUserName);
		return "redirect:/login";
	}
}
