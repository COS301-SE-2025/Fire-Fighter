package com.apex.firefighter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;

@Configuration
public class AIConfig {

    @Value("${GOOGLE_GEMINI_API_KEY}")
    private String geminiApiKey;

    @PostConstruct
    public void validateConfiguration() {
        if (geminiApiKey == null || geminiApiKey.trim().isEmpty() || geminiApiKey.equals("${GOOGLE_GEMINI_API_KEY}")) {
            System.err.println("WARNING: Google Gemini API key is not configured. AI chatbot will not function properly.");
            System.err.println("Please set GOOGLE_GEMINI_API_KEY in your .env file or environment variables.");
        } else {
            System.out.println("AI Configuration: Google Gemini API key is configured");
        }
    }

    @Bean
    public WebClient aiWebClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB buffer
                .build();
    }
}
