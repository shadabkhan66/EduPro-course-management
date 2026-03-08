package com.eduproject.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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
						.requestMatchers("/users/new").permitAll()

						// Admin-only course management (order matters: more specific first)
						.requestMatchers("/courses/new").hasRole("ADMIN")
						.requestMatchers("/courses/*/edit").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/courses").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/courses/*/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/courses/enroll").authenticated()
						.requestMatchers(HttpMethod.POST, "/courses/*").hasRole("ADMIN")

						// Public: course list and course view (permitAll)
						.requestMatchers("/courses").permitAll()
						.requestMatchers("/courses/*").permitAll()

						// Dev tools
						.requestMatchers("/h2-console/**").permitAll()
						.requestMatchers("/actuator/**").permitAll()

						// Everything else requires authentication
						.requestMatchers("/whoami").authenticated()
						.requestMatchers("/users/me").authenticated()
						.requestMatchers("/users/{id}").authenticated()
						.anyRequest().permitAll()
				)
				.formLogin(form -> form
						.loginPage("/login")
						.successHandler(loginSuccessHandler())
						.defaultSuccessUrl("/courses", false)
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

	/**
	 * After login: redirect to the "redirect" param if present (e.g. from "Login to enroll").
	 * Otherwise use defaultSuccessUrl from formLogin config.
	 */
	@Bean
	AuthenticationSuccessHandler loginSuccessHandler() {
		return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
			String redirectUrl = request.getParameter("redirect");
			// Only allow internal paths (prevent open redirect)
			if (redirectUrl != null && !redirectUrl.isBlank()
					&& redirectUrl.startsWith("/") && !redirectUrl.startsWith("//")) {
				response.sendRedirect(request.getContextPath() + redirectUrl);
			} else {
				response.sendRedirect(request.getContextPath() + "/courses");
			}
		};
	}
}
