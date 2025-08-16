package com.apex.firefighter.integration.api;

import com.apex.firefighter.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserPreferencesControllerIT {

    @Autowired
    private UserService userService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    @Transactional
    void setup() {
        userService.verifyOrCreateUser("user1", "Normal User", "user1@example.com", "Medical Department");
    }

    @Test
    void testGetUserPreferences() {
        webTestClient.get()
            .uri("/api/user-preferences/user1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.userId").isEqualTo("user1");
    }

    @Test
    void testUpdateUserPreferences_AllFields() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("emailNotificationsEnabled", true);
        payload.put("emailTicketCreation", true);
        payload.put("emailTicketCompletion", false);
        payload.put("emailTicketRevocation", true);
        payload.put("emailFiveMinuteWarning", false);

        webTestClient.put()
            .uri("/api/user-preferences/user1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.emailNotificationsEnabled").isEqualTo(true)
            .jsonPath("$.emailTicketCreation").isEqualTo(true)
            .jsonPath("$.emailTicketCompletion").isEqualTo(false)
            .jsonPath("$.emailTicketRevocation").isEqualTo(true)
            .jsonPath("$.emailFiveMinuteWarning").isEqualTo(false);
    }

    @Test
    void testPatchUserPreferenceSetting() {
        Map<String, Boolean> patchBody = new HashMap<>();
        patchBody.put("enabled", true);

        webTestClient.patch()
            .uri("/api/user-preferences/user1/email-notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(patchBody)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.emailNotificationsEnabled").isEqualTo(true);
    }

    @Test
    void testEnableAllPreferences() {
        webTestClient.post()
            .uri("/api/user-preferences/user1/enable-all")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.emailNotificationsEnabled").isEqualTo(true)
            .jsonPath("$.emailTicketCreation").isEqualTo(true)
            .jsonPath("$.emailTicketCompletion").isEqualTo(true)
            .jsonPath("$.emailTicketRevocation").isEqualTo(true)
            .jsonPath("$.emailFiveMinuteWarning").isEqualTo(true);
    }

    @Test
    void testDisableAllPreferences() {
        webTestClient.post()
            .uri("/api/user-preferences/user1/disable-all")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.emailNotificationsEnabled").isEqualTo(false)
            .jsonPath("$.emailTicketCreation").isEqualTo(false)
            .jsonPath("$.emailTicketCompletion").isEqualTo(false)
            .jsonPath("$.emailTicketRevocation").isEqualTo(false)
            .jsonPath("$.emailFiveMinuteWarning").isEqualTo(false);
    }

    @Test
    void testResetPreferences() {
        webTestClient.post()
            .uri("/api/user-preferences/user1/reset")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.emailNotificationsEnabled").exists();
    }

    @Test
    void testCheckPreferenceSetting() {
        webTestClient.get()
            .uri("/api/user-preferences/user1/check/email-notifications")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.setting").isEqualTo("email-notifications")
            .jsonPath("$.enabled").exists();
    }
}