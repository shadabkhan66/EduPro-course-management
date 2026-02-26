package com.eduproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration.
 *
 * Defines:
 * - URL authorization rules (who can access what)
 * - Form login configuration
 * - Logout handling with flash messages
 * - CSRF exception for H2 console (dev only)
 * - Frame options for H2 console iframe support
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		return http
				.authorizeHttpRequests(auth -> auth
						// Public pages
						.requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
						.requestMatchers("/courses").permitAll()
						.requestMatchers("/courses/{id}").permitAll()
						.requestMatchers("/users/new").permitAll()

						// Admin-only course management
						.requestMatchers("/courses/new").hasRole("ADMIN")
						.requestMatchers("/courses/*/edit").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/courses").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/courses/*/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/courses/enroll").authenticated()
						.requestMatchers(HttpMethod.POST, "/courses/*").hasRole("ADMIN")

						// Dev tools
						.requestMatchers("/h2-console/**").permitAll()

						// Everything else requires authentication
						.requestMatchers("/whoami").authenticated()
						.requestMatchers(("/users/{id}")).authenticated()
						.anyRequest().permitAll()
				)
				.formLogin(form -> form
						.loginPage("/login")
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessHandler((request, response, authentication) -> {
							request.getSession().setAttribute("logoutMessage",
									"You have been logged out successfully.");
							response.sendRedirect(request.getContextPath() + "/");
						})
						.permitAll()
				)
				// H2 console: disable CSRF (it makes internal POSTs without tokens)
				.csrf(csrf -> csrf
						.ignoringRequestMatchers("/h2-console/**")
				)
				// H2 console: allow iframes from same origin
				.headers(headers -> headers
						.frameOptions(frame -> frame.sameOrigin())
				)
				.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
