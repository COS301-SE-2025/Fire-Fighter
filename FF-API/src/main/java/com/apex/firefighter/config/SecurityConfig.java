package com.apex.firefighter.config;

import com.apex.firefighter.repository.ApiKeyRepository;
import com.apex.firefighter.security.ApiKeyAuthFilter;
import com.apex.firefighter.security.JwtAuthenticationFilter;
import com.apex.firefighter.service.auth.JwtService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private JwtService jwtService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Auth endpoints should be public
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users/verify").permitAll()
                // Chatbot endpoints require authentication (except health check)
                .requestMatchers("/api/chatbot/health").permitAll()
                .requestMatchers("/api/chatbot/**").authenticated()
                // Notifications require authentication
                .requestMatchers("/api/notifications/**").authenticated()
                // Tickets require authentication
                .requestMatchers("/api/tickets/**").authenticated()
                // Require API key for extra secured endpoints
                .requestMatchers("/api/endpoints/**").authenticated()
                // Require API key for protected endpoints  
                .requestMatchers("/api/protected/**").authenticated()
                // Allow other API endpoints for development
                .requestMatchers("/api/**").permitAll()
                .anyRequest().permitAll()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new ApiKeyAuthFilter(apiKeyRepository), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins for both HTTP and HTTPS in development
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost",
            "https://localhost",
            "http://localhost:*",
            "https://localhost:*", 
            "http://127.0.0.1:*",
            "https://127.0.0.1:*",
            "ionic://localhost",
            "capacitor://localhost",
            "http://100.123.32.43:*",
            "https://100.123.32.43:*",
            "*"  // Allow all origins for development (remove in production)
        ));
        
        // Allow all common HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials (important for authentication)
        configuration.setAllowCredentials(true);
        
        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply CORS to all endpoints, not just /api/**
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 
