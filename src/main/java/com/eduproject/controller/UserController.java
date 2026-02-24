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

    @GetMapping("/{id}")
    public String showUser(@PathVariable Long id, Model model) {
        log.info("Showing user with id {}", id);
        UserResponseDTO userResponseDTO =  this.userService.getUserById(id);

        model.addAttribute("userResponseDTO", userResponseDTO);
        log.info("Displaying user profile {}", userResponseDTO);
        return "user/showUserProfile";
    }

    @GetMapping//this page should only be see to admin

    public String showAllUsers(Model model) {
        List<UserResponseDTO> users =  this.userService.getAllUsers();
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
        model.addAttribute("registrationDTO", new UserRegistrationDTO());
        return "user/register";
    }

    @PostMapping
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


//    =======================
//    edit  user
//    =============================================================
    @GetMapping("/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        log.info("Displaying editing form for user: {}", id);
        UserResponseDTO userResponseDTO = userService.getUserById(id);

        model.addAttribute("userResponseDTO", userResponseDTO);
        log.info("sending to  view editUser.html for editing with User: {}", userResponseDTO);
        return "user/editUser";
    }

    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable Long id,
                           @ModelAttribute("userResponseDTO") UserResponseDTO userRespDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes
    ){
        log.info("Edit user attempt for user: {}", id);

        if( !id.equals(userRespDTO.getId()) ) {
            //do something
        }

        if (!bindingResult.hasErrors()) {

            if (userService.existsByEmailExcludingCurrentUser(userRespDTO.getEmail(), id)) {
                bindingResult.rejectValue("email", "duplicate", "Email already exists");
            }

            if (userService.existsByUsernameExcludingCurrentUser(userRespDTO.getUsername(), id)) {
                bindingResult.rejectValue("username", "duplicate", "Username already exists");
            }
        }

        if(bindingResult.hasErrors()){
            log.info("Updating validation fail {} " ,bindingResult.getErrorCount());
            //is this line even necessery especially with this huge name
            // isent it send automatically
//        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRespDTO", bindingResult);
//        redirectAttributes.addFlashAttribute("userResponseDTO", userRespDTO);

            return "redirect:/users/allUsers";
        }

        String msg = this.userService.updateUser(userRespDTO);



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

