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
import java.util.List;

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


        return new UserDetails(){

            UserEntity userEntity = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));



            @Override
            public String getPassword() {
                return "";
            }

            @Override
            public boolean isAccountNonExpired() {
                return UserDetails.super.isAccountNonExpired();  //default is true
            }

            @Override
            public boolean isAccountNonLocked() {
                return UserDetails.super.isAccountNonLocked(); //default is true
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return UserDetails.super.isCredentialsNonExpired(); //default is true
            }

            @Override
            public boolean isEnabled() {
                return userEntity.isEnabled(); //default value given in entity is ture
            }

            @Override
            public String getUsername() {
                return userEntity.getUsername();
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole()));
            }

//            @Override
//            public boolean isAccountNonExpired() {
//                return true;
//            }
//
//            @Override
//            public boolean isAccountNonLocked() {
//                return true;
//            }
//
//            @Override
//            public boolean isCredentialsNonExpired() {
//                return true;
//            }
//
//            @Override
//            public boolean isEnabled() {
//                return this.enabled;
//            }
        };

	}
}
