package com.apex.firefighter.integration.api;

import com.apex.firefighter.service.NotificationService;
import com.apex.firefighter.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NotificationControllerIT {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WebTestClient webTestClient;

    private Long notificationId;

    @BeforeEach
    @Transactional
    void setup() {
        userService.verifyOrCreateUser("user1", "Normal User", "user1@example.com", "Medical Department");
        // Create a notification for user1 for testing
        notificationId = notificationService.createNotification("user1", "Test notification", "info");
    }

    @Test
    void testGetNotifications() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/notifications")
                .queryParam("userId", "user1")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    @Test
    void testGetUnreadNotifications() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/notifications/unread")
                .queryParam("userId", "user1")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    @Test
    void testGetNotificationStats() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/notifications/stats")
                .queryParam("userId", "user1")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.total").exists();
    }

    @Test
    void testMarkNotificationAsRead() {
        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/api/notifications/" + notificationId + "/read")
                .queryParam("userId", "user1")
                .build())
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testMarkAllNotificationsAsRead() {
        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/api/notifications/read-all")
                .queryParam("userId", "user1")
                .build())
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testDeleteReadNotifications() {
        webTestClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/api/notifications/read")
                .queryParam("userId", "user1")
                .build())
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testDeleteNotificationById() {
        webTestClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/api/notifications/" + notificationId)
                .queryParam("userId", "user1")
                .build())
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testGetNotificationById() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/notifications/" + notificationId)
                .queryParam("userId", "user1")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(notificationId.intValue());
    }
}