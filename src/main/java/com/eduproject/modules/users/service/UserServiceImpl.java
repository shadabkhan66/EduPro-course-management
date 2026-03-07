package com.eduproject.modules.users.service;

import com.eduproject.common.exception.UserNotFoundException;
import com.eduproject.modules.users.dto.UserResponseDTO;
import com.eduproject.modules.users.entity.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduproject.model.Role;
import com.eduproject.modules.users.dto.UserRegistrationDTO;
import com.eduproject.modules.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

		UserEntity user = UserEntity.builder()
				.username(dto.getUsername())
				.password(passwordEncoder.encode(dto.getPassword()))
				.firstName(dto.getFirstName())
				.lastName(dto.getLastName())
				.email(dto.getEmail())
				.role(Role.STUDENT)  // Security: always default to STUDENT
				.build();

		UserEntity saved = userRepository.save(user);
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

    @Transactional
    @Override
    public String updateUser(UserResponseDTO userRespDTO) {

        if (userRespDTO.getId() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        UserEntity user = userRepository.findById(userRespDTO.getId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with Id : " + userRespDTO.getId())
                );

        // Manual mapping (safer)
        if (userRespDTO.getFirstName() != null) {
            user.setFirstName(userRespDTO.getFirstName());
        }

        if (userRespDTO.getLastName() != null) {
            user.setLastName(userRespDTO.getLastName());
        }

        if (userRespDTO.getEmail() != null) {
            user.setEmail(userRespDTO.getEmail());
        }

        return "User with id " + userRespDTO.getId() + " updated successfully";
    }
    @Override
    public boolean existsByEmailExcludingCurrentUser(String email, Long currentUserId) {
        return userRepository.existsByEmailAndIdNot(email, currentUserId);
    }

    @Override
    public boolean existsByUsernameExcludingCurrentUser(String username, Long currentUserId) {
        return userRepository.existsByUsernameAndIdNot(username, currentUserId);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {

        if (!this.userRepository.existsById(id))
        {
            throw new UserNotFoundException("User not found with Id : " + id);
        }
        this.userRepository.deleteById(id);
        log.info("User with id {} has been deleted successfully", id);
    }


    private UserResponseDTO toUserResponseDTO(UserEntity user) {
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

