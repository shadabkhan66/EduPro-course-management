package com.eduproject.service.impl;

import com.eduproject.exception.UserNotFoundException;
import com.eduproject.model.UserResponseDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduproject.model.Role;
import com.eduproject.model.User;
import com.eduproject.model.UserRegistrationDTO;
import com.eduproject.repository.UserRepository;
import com.eduproject.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {


	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public String registerUser(UserRegistrationDTO dto) {
		log.info("Registering user: {}", dto.getUsername());

		User user = User.builder()
				.username(dto.getUsername())
				.password(passwordEncoder.encode(dto.getPassword()))
				.firstName(dto.getFirstName())
				.lastName(dto.getLastName())
				.email(dto.getEmail())
				.role(Role.STUDENT)  // Security: always default to STUDENT
				.build();

		User saved = userRepository.save(user);
		return saved.getFullName();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponseDTO getUserById(Long id) {
		log.info("Retrieving user by id: {}", id);
		return this.userRepository.findById(id)
                .map(this::toUserResponseDTO)
                .orElseThrow(() -> new UserNotFoundException("User not found with Id : " + id));


	}

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return this.userRepository.findAll().stream().map(this::toUserResponseDTO).toList();
    }


    @Override
    public String updateUser(UserResponseDTO userRespDTO) {

        User user = this.userRepository.findById(userRespDTO.getId()).orElseThrow( () -> new UserNotFoundException("User not found with Id : "+ userRespDTO.getId()));
        BeanUtils.copyProperties(userRespDTO, user, "role", "lastUpdatedAt","createdAt", "isActive");
        this.userRepository.save(user);


        return "User with id "+ userRespDTO.getId()+" updated successfully ";
    }

    @Override
    public boolean existsByEmailExcludingCurrentUser(String email, Long currentUserId) {
        return userRepository.existsByEmailAndIdNot(email, currentUserId);
    }

    @Override
    public boolean existsByUsernameExcludingCurrentUser(String username, Long currentUserId) {
        return userRepository.existsByUsernameAndIdNot(username, currentUserId);
    }

    private UserResponseDTO toUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .createdAt(user.getCreatedAt().toString())
                .lastUpdatedAt(user.getUpdatedAt() == null ? "Never Updated" : user.getUpdatedAt().toString())
                .role(user.getRole().toString())
                .isActive(user.isEnabled())
                .build();
    }
}

