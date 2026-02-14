package com.eduproject.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/*
	 * Security Filter Chain is a sequence of Spring Security filters that intercepts HTTP requests before controller execution to enforce authentication and authorization rules. Requests failing these security checks are rejected, while valid requests are allowed to proceed. 
	*/
	@Bean
	SecurityFilterChain filterChain( HttpSecurity httpSecurity) throws Exception {
		
		return httpSecurity
				.authorizeHttpRequests( auth ->auth
					.requestMatchers("/courses").permitAll()
					.requestMatchers("/courses/edit", "/courses/edit/**").hasRole("ADMIN")
					.requestMatchers("/courses/add").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/courses").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/courses/*").hasRole("ADMIN")
					.requestMatchers("/courses/delete", "/courses/delete/**").hasRole("ADMIN")
					.requestMatchers("/whoami").authenticated()
					.anyRequest().permitAll()
					)
				.formLogin(  formLoginConfig -> formLoginConfig
						.loginPage("/login")
						.permitAll()
						)
				.logout( logoutConfig -> logoutConfig
						.logoutUrl("/logout")
						  .logoutSuccessHandler((request, response, authentication) -> {
					            // Set flash attribute for logout message
					            request.getSession().setAttribute("logoutMessage", "You have been logged out successfully.");
					            response.sendRedirect("/");
					        })
						.permitAll()
					)	
				.build();
	}
	
/*	@Bean
	UserDetailsService userStore(PasswordEncoder encoder) {
		
		var user1 = User.withUsername("user")
				.password(encoder.encode("user123"))
				.roles("USER")
				.build();
		
		var user2 = User.withUsername("admin")
				.password(encoder.encode("admin123"))
				.roles("ADMIN")
				.build();
		
		return new InMemoryUserDetailsManager(Arrays.asList(user1,user2));
	}*/

    @Bean
    PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
}
