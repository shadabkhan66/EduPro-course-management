package com.eduproject.config;

import com.eduproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService that loads users from the database.
 *
 * Spring Security auto-discovers this bean (any @Service implementing
 * UserDetailsService) and uses it for authentication. No explicit wiring needed.
 *
 * This replaced the InMemoryUserDetailsManager from v0.1.0.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Loading user by username: {}", username);
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
}
