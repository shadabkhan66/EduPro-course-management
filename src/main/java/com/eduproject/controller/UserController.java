package com.eduproject.controller;

import com.eduproject.model.UserRequest;
import com.eduproject.model.UserResponse;
import com.eduproject.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/**
 * Handles user registration.
 *
 *  * URL Design (RESTful naming):
 *  *   GET  /users              → list all users
 *  *   GET  /users/{id}         → view single user
 *
 *  *   GET  /users/new          → show create form
 *  *   POST /users              → handle create
 *
 *  *   GET  /users/{id}/edit    → show edit form
 *  *   POST /users/{id}         → handle update
 *  *   POST /users/{id}/delete  → handle delete
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

//    =======================
//    fetch user
//    =============================================================

    /**
     * Redirects authenticated user to their own profile.
     * Avoids needing principal.id in templates (works with any UserDetails impl).
     */
    @GetMapping("/me")
    public String showCurrentUser(Principal principal) {
        if (principal == null) return "redirect:/login";
        Long userId = userService.getUserIdByUsername(principal.getName());
        if (userId == null) return "redirect:/login";
        return "redirect:/users/" + userId;
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable Long id, Model model) {
        log.info("Showing user with id {}", id);
        UserResponse userResponse =  this.userService.getUserById(id);

        model.addAttribute("userResponseDTO", userResponse);
        log.info("Displaying user profile {}", userResponse);
        return "user/showUserProfile";
    }

    @GetMapping//this page should only be see to admin

    public String showAllUsers(Model model) {
        List<UserResponse> users =  this.userService.getAllUsers();
        model.addAttribute("users", users);
        log.info("Displaying all users");
        return "user/allUsers";
    }


//    =======================
//    register new user
//    =============================================================
    @GetMapping("/new")
    public String showRegistrationForm(Model model) {
        log.info("Displaying registration form");
        model.addAttribute("registrationDTO", new UserRequest());
        return "user/register";
    }

    @PostMapping
    public String registerUser(
            @Valid @ModelAttribute("registrationDTO") UserRequest dto,
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


//    =======================
//    edit  user
//    =============================================================
    @GetMapping("/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        log.info("Displaying editing form for user: {}", id);
        UserResponse userResponse = userService.getUserById(id);

        model.addAttribute("userResponseDTO", userResponse);
        log.info("sending to  view editUser.html for editing with User: {}", userResponse);
        return "user/editUser";
    }

    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable Long id,
                           @Valid @ModelAttribute("userResponseDTO") UserResponse userRespDTO,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        log.info("Edit user attempt for user: {}", id);

        // Security: trust the URL path ID, not the form's hidden field
        userRespDTO.setId(id);

        if (!bindingResult.hasErrors()) {
            if (userService.existsByEmailExcludingCurrentUser(userRespDTO.getEmail(), id)) {
                bindingResult.rejectValue("email", "duplicate", "Email already exists");
            }
            if (userService.existsByUsernameExcludingCurrentUser(userRespDTO.getUsername(), id)) {
                bindingResult.rejectValue("username", "duplicate", "Username already exists");
            }
        }

        if (bindingResult.hasErrors()) {
            log.warn("Edit validation failed: {} errors", bindingResult.getErrorCount());
            model.addAttribute("userResponseDTO", userRespDTO);
            return "user/editUser";
        }

        userService.updateUser(userRespDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/users/" + id;
    }

//    =======================
//    delete  user
//    =============================================================
//    currently i am suing to delete my own account
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Delete user attempt for user: {}", id);

        this.userService.deleteUserById(id);

        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");

        return "forward:/logout";
    }
}

