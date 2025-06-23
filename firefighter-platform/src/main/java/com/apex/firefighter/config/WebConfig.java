package com.apex.firefighter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration moved to SecurityConfig to avoid conflicts
    // This class can be used for other web configuration if needed
} 