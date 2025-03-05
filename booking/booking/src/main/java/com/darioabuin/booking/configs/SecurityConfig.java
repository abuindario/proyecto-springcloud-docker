package com.darioabuin.booking.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	@Bean
	InMemoryUserDetailsManager userDetails() throws Exception {
		return new InMemoryUserDetailsManager(
				List.of(User.withUsername("administrator").password("{noop}admin1234").roles("ADMIN").build()));
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(cr -> cr.disable())
			.authorizeHttpRequests( aut -> 
				aut.requestMatchers(HttpMethod.GET).hasRole("ADMIN")
				.anyRequest().permitAll())
			.httpBasic(Customizer.withDefaults());
		return http.build();
	}
}
