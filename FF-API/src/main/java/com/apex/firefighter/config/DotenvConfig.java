package com.apex.firefighter.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    // Static block to load environment variables before Spring starts processing @Value annotations
    static {
        try {
            System.out.println("🔧 Loading environment variables from .env file...");

            Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

            // Set system properties so Spring can access them
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
                if (entry.getKey().contains("PASSWORD")) {
                    System.out.println("Loaded env var: " + entry.getKey() + " = ***HIDDEN*** (length: " + entry.getValue().length() + ")");
                } else {
                    System.out.println("Loaded env var: " + entry.getKey() + " = " + entry.getValue());
                }
            });

            // Specifically check database password
            String dbPassword = System.getProperty("DB_PASSWORD");
            if (dbPassword != null) {
                System.out.println("✅ DB_PASSWORD loaded successfully (length: " + dbPassword.length() + ")");
            } else {
                System.err.println("❌ DB_PASSWORD not found in environment variables!");
            }

        } catch (Exception e) {
            System.err.println("Failed to load .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}