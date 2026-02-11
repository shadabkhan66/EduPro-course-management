package com.eduproject.controller;

import com.eduproject.exception.EmailAlreadyExistsException;
import com.eduproject.exception.UserNameAlreadyExists;
import com.eduproject.model.Role;
import com.eduproject.model.User;
import com.eduproject.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	// Add common attributes to model for both GET and POST
	@ModelAttribute
	public void addCommonAttributes(Model model) {
		model.addAttribute("roles", Role.values());
		model.addAttribute("pageTitle", "User Registration");
		model.addAttribute("isEdit", false);
		model.addAttribute("submitButtonLabel", "Register");
	}

	// Show registration form
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		log.info("Displaying user registration form");
		model.addAttribute("user", new User()); // for form binding
		return "user/user_form";
	}

	// Handle registration submission
	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user,
							   BindingResult result,
							   RedirectAttributes redirectAttributes,
							   Model model) {
		log.info("Trying to register user: {}", user);


		if(this.userService.doesUniqueEmailExists(user.getEmail())) {
			result.rejectValue("email", null, "Email already exists");
		}

		if(this.userService.doesUniqueUsernameExists(user.getUsername())) {
			result.rejectValue("username", null, "Username already exists");
		}
		if(result.hasErrors()) {
			log.error("Validation failed: {}", result.getAllErrors());
			return "user/user_form";
		}


		String savedUserName = userService.registerUser(user);
		redirectAttributes.addFlashAttribute(
				"message",
				"User has been registered successfully with username: " + savedUserName
		);
		return "redirect:/login";


	}
}
