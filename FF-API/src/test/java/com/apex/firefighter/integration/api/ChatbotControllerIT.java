package com.apex.firefighter.integration.api;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ChatbotControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testQueryNoApiKey() {
        Map<String, String> payload = new HashMap<>();
        payload.put("query", "What is my status?");
        // Do NOT set "userId" to simulate missing userId

        webTestClient.post()
            .uri("/api/chatbot/query")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.message").value(msg -> Assertions.assertTrue(msg.toString().toLowerCase().contains("user id is required")));
    }

    @Test
    void testAdminQueryNonAdmin() {
        Map<String, String> payload = new HashMap<>();
        payload.put("query", "Show all tickets");
        payload.put("userId", "user1"); // not admin

        webTestClient.post()
            .uri("/api/chatbot/admin/query")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .jsonPath("$.message").value(msg -> Assertions.assertTrue(msg.toString().contains("Administrator privileges required")));
    }

/*
    @Test
    void testAdminQueryAdmin() {
        Map<String, String> payload = new HashMap<>();
        payload.put("query", "Show all tickets");
        payload.put("userId", "admin1"); // admin user

        webTestClient.post()
            .uri("/api/chatbot/admin/query")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").exists();
    }

    */

    @Test
    void testGetCapabilities() {
        webTestClient.get()
            .uri("/api/chatbot/capabilities/admin1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.available").exists()
            .jsonPath("$.accessLevel").exists();
    }

    @Test
    void testChatbotHealth() {
        webTestClient.get()
            .uri("/api/chatbot/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("healthy")
            .jsonPath("$.service").isEqualTo("AI Chatbot")
            .jsonPath("$.version").isEqualTo("1.0.0")
            .jsonPath("$.timestamp").exists();
    }

    @Test
    void testGetSuggestions() {
        webTestClient.get()
            .uri("/api/chatbot/suggestions/admin1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.available").exists()
            .jsonPath("$.userRole").exists()
            .jsonPath("$.suggestedQueries").isArray()
            .jsonPath("$.examples").isArray();
    }

    @Test
    void testEmptyQuery() {
        Map<String, String> payload = new HashMap<>();
        payload.put("query", "");
        payload.put("userId", "user1");

        webTestClient.post()
            .uri("/api/chatbot/query")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.message").value(msg -> Assertions.assertTrue(msg.toString().toLowerCase().contains("query cannot be empty")));
    }
}