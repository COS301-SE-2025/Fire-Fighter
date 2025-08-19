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
public class UserControllerIT {

    @Autowired
    private UserService userService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    @Transactional
    void setup() {
        userService.verifyOrCreateUser("admin1", "Admin User", "admin1@example.com", "Fire Department");
        userService.authorizeUser("admin1", "system");
        userService.verifyOrCreateUser("user1", "Normal User", "user1@example.com", "Medical Department");
    }

    @Test
    void testVerifyUser_HappyPath() {
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/api/users/verify")
                .queryParam("firebaseUid", "newuser")
                .queryParam("username", "New User")
                .queryParam("email", "newuser@example.com")
                .queryParam("department", "Fire Department")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.userId").isEqualTo("newuser");
    }

    @Test
    void testVerifyUser_InvalidData() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("firebaseUid", "baduser");
        // Missing required fields

        webTestClient.post().uri("/api/users/verify")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void testGetUserAuthorized() {
        webTestClient.get()
            .uri("/api/users/admin1/authorized")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(response -> {
                // The response is a boolean, not a JSON object
                String body = new String(response.getResponseBody());
                Assertions.assertTrue(body.contains("true"));
            });
    }

    @Test
    void testGetUserRoles() {
        webTestClient.get()
            .uri("/api/users/admin1/roles/ADMIN")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(response -> {
                // The response is a boolean, not a JSON object
                String body = new String(response.getResponseBody());
                Assertions.assertTrue(body.contains("true") || body.contains("false"));
            });
    }

    @Test
    void testGetUserById() {
        webTestClient.get().uri("/api/users/admin1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.userId").isEqualTo("admin1");
    }

    @Test
    void testGetUserByEmail() {
        webTestClient.get().uri("/api/users/email/admin1@example.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.email").isEqualTo("admin1@example.com");
    }

    @Test
    void testAuthorizeUser() {
        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/api/users/user1/authorize")
                .queryParam("authorizedBy", "admin1")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.authorized").isEqualTo(true);
    }

    @Test
    void testRevokeUserAuthorization() {
        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/api/users/user1/revoke")
                .queryParam("revokedBy", "admin1")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.authorized").isEqualTo(false);
    }

    @Test
    void testAssignRole() {
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/api/users/user1/roles")
                .queryParam("roleName", "FIREFIGHTER")
                .queryParam("assignedBy", "admin1")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.role").isEqualTo("FIREFIGHTER");
    }
 
    @Test
    void testUpdateContact_Valid() {
        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/api/users/user1/contact")
                .queryParam("contactNumber", "0123456789")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.contactNumber").isEqualTo("0123456789");
    }

    @Test
    void testUpdateContact_Invalid() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("contactNumber", ""); // Invalid

        webTestClient.put().uri("/api/users/user1/contact")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void testGetAuthorizedUsers() {
        webTestClient.get().uri("/api/users/authorized")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    @Test
    void testGetUsersByDepartment() {
        webTestClient.get().uri("/api/users/department/Fire Department")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    @Test
    void testGetUsersByRole() {
        webTestClient.get().uri("/api/users/role/ADMIN")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    @Test
    void testGetAuthorizedUsersByRole() {
        webTestClient.get().uri("/api/users/authorized/role/ADMIN")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }
}