package com.apex.firefighter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Allow Ionic development server and other common ports
                .allowedOriginPatterns(
                    "http://localhost:8100",     // Ionic dev server
                    "http://127.0.0.1:8100",     // Alternative localhost
                    "https://localhost:8100",    // HTTPS version
                    "https://127.0.0.1:8100",    // HTTPS alternative
                    "ionic://localhost",         // Capacitor iOS
                    "http://localhost",          // General localhost (any port)
                    "capacitor://localhost"      // Capacitor Android
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight response for 1 hour
    }
} 