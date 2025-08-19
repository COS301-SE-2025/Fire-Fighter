package com.apex.firefighter.integration.api;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HealthControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testHealth() {
        webTestClient.get()
            .uri("/api/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP")
            .jsonPath("$.service").isEqualTo("FireFighter Backend")
            .jsonPath("$.version").isEqualTo("1.0.0")
            .jsonPath("$.timestamp").exists();
    }

    @Test
    void testDetailedHealth() {
        webTestClient.get()
            .uri("/api/health/detailed")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP")
            .jsonPath("$.components.database").isEqualTo("UP")
            .jsonPath("$.components.authentication").isEqualTo("UP")
            .jsonPath("$.components.api").isEqualTo("UP")
            .jsonPath("$.system['java.version']").exists()
            .jsonPath("$.system['spring.profiles.active']").exists();
    }
}