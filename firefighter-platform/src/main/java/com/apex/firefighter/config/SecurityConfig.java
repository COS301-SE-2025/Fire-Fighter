package com.apex.firefighter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
            .authorizeHttpRequests(authz -> authz
                // Database test endpoints - allow public access
                .requestMatchers("/api/database/**").permitAll()
                
                // Admin endpoints - require authentication (more specific patterns first)
                .requestMatchers("/api/users/*/authorize").hasRole("ADMIN")
                .requestMatchers("/api/users/*/revoke").hasRole("ADMIN")
                .requestMatchers("/api/users/authorized").hasRole("ADMIN")
                .requestMatchers("/api/users/department/*").hasRole("ADMIN")
                .requestMatchers("/api/users/role/*").hasRole("ADMIN")
                .requestMatchers("/api/users/authorized/role/*").hasRole("ADMIN")
                
                // Role assignment endpoint (POST to /api/users/{id}/roles)
                .requestMatchers("/api/users/*/roles").hasRole("ADMIN")
                
                // Public user endpoints - no authentication required
                .requestMatchers("/api/users/verify").permitAll()
                .requestMatchers("/api/users/*/authorized").permitAll()
                .requestMatchers("/api/users/*/roles/*").permitAll() // GET role check
                .requestMatchers("/api/users/email/*").permitAll()
                .requestMatchers("/api/users/*").permitAll() // GET user info by ID
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );
            
        return http.build();
    }
} 