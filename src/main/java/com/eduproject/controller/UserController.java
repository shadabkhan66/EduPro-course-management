package com.eduproject.controller;

import com.eduproject.model.UserRegistrationDTO;
import com.eduproject.model.UserResponseDTO;
import com.eduproject.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles user registration.
 *
 * DESIGN CHANGE (v0.2.0):
 * - Uses UserRegistrationDTO instead of User entity as form-backing bean
 * - Role is hardcoded to STUDENT (security fix: users can't self-assign ADMIN)
 * - Uniqueness checks run only after @Valid passes (optimization)
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		log.info("Displaying registration form");
		model.addAttribute("registrationDTO", new UserRegistrationDTO());
		return "user/register";
	}

	@PostMapping("/register")
	public String registerUser(
			@Valid @ModelAttribute("registrationDTO") UserRegistrationDTO dto,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		log.info("Registration attempt for: {}", dto.getUsername());

		// Only check uniqueness if basic validation passed (avoid unnecessary DB calls)
		if (!bindingResult.hasErrors()) {
			if (userService.existsByEmail(dto.getEmail())) {
				bindingResult.rejectValue("email", "duplicate", "Email already exists");
			}
			if (userService.existsByUsername(dto.getUsername())) {
				bindingResult.rejectValue("username", "duplicate", "Username already exists");
			}
		}

		if (bindingResult.hasErrors()) {
			log.warn("Registration validation failed: {}", bindingResult.getErrorCount());
			return "user/register";
		}

		String fullName = userService.registerUser(dto);
		redirectAttributes.addFlashAttribute("successMessage",
				"Welcome, " + fullName + "! Your account has been created. Please login.");
		return "redirect:/login";
	}

	@GetMapping("/{id}")
	public String showUser(@PathVariable Long id, Model model) {
		UserResponseDTO userResponseDTO =  this.userService.getUserById(id);

		//exception handling

		model.addAttribute("userResponseDTO", userResponseDTO);
		return "user/showUserProfile";
	}

	@GetMapping("/{id}/edit")
	public String showEditUserForm(@PathVariable Long id, Model model) {
		log.info("Displaying editing form for user: {}", id);
		UserResponseDTO userResponseDTO = userService.getUserById(id);

		model.addAttribute("userResponseDTO", userResponseDTO);

		return "user/editUser";
	}

}
