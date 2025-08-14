package com.apex.firefighter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class SwaggerAutoOpenConfig {

    @Value("${server.port:8080}")
    private String serverPort;



    @Value("${firefighter.swagger.auto-open:true}")
    private boolean autoOpenEnabled;

    @EventListener(ApplicationReadyEvent.class)
    public void openSwaggerUI() {
        if (!autoOpenEnabled) {
            System.out.println("Swagger UI auto-open is disabled");
            return;
        }

        try {
            String protocol = "http";
            String swaggerUrl = protocol + "://localhost:" + serverPort + "/swagger-ui.html";
            
            System.out.println("=".repeat(60));
            System.out.println("🚀 FireFighter Platform Started Successfully!");
            System.out.println("=".repeat(60));
            System.out.println("📚 Swagger UI: " + swaggerUrl);
            System.out.println("🤖 AI Chatbot: http://localhost:" + serverPort + "/api/chatbot/health");
            System.out.println("📧 Email Service: Configured and ready");
            System.out.println("=".repeat(60));

            // Attempt to open browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                System.out.println("🌐 Opening Swagger UI in your default browser...");
                Desktop.getDesktop().browse(new URI(swaggerUrl));
                System.out.println("✅ Browser opened successfully!");
            } else {
                System.out.println("⚠️  Desktop browsing not supported on this system");
                System.out.println("📋 Please manually open: " + swaggerUrl);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to auto-open Swagger UI: " + e.getMessage());
            String fallbackUrl = "http://localhost:" + serverPort + "/swagger-ui.html";
            System.out.println("📋 Please manually open: " + fallbackUrl);
        }
    }
}
