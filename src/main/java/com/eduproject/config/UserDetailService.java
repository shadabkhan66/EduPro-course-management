package com.eduproject.config;

import com.eduproject.modules.users.entity.UserEntity;
import com.eduproject.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Loads {@link UserEntity} from the database for Spring Security.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Loading user by username: {}", username);

		UserEntity userEntity = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		return new UserDetails() {

			@Override
			public String getPassword() {
				return userEntity.getPassword();
			}

			@Override
			public String getUsername() {
				return userEntity.getUsername();
			}

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				if (userEntity.getRoles() == null || userEntity.getRoles().isEmpty()) {
					return Collections.emptyList();
				}
				return userEntity.getRoles().stream()
						.map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
						.toList();
			}

			@Override
			public boolean isAccountNonExpired() {
				return true;
			}

			@Override
			public boolean isAccountNonLocked() {
				return true;
			}

			@Override
			public boolean isCredentialsNonExpired() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return userEntity.isEnabled();
			}
		};
	}
}
