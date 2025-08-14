package com.apex.firefighter.integration.api;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import com.apex.firefighter.service.ticket.TicketService;
import com.apex.firefighter.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.mock.mockito.MockBean;

import jakarta.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TicketControllerIT {

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;
    
    @MockBean
    private com.apex.firefighter.service.GmailEmailService gmailEmailService;
    
    private User adminUser;
    private User normalUser;
    private Ticket ticket;

    @BeforeEach
    @Transactional
    void setup() {
        // Create admin and normal user
        adminUser = new User("admin1", "Admin User", "admin1@example.com", "Fire Department");
        adminUser.setIsAdmin(true);
        userRepository.save(adminUser);

        normalUser = new User("user1", "Normal User", "user1@example.com", "Medical Department");
        normalUser.setIsAdmin(false);
        userRepository.save(normalUser);
    }

    @BeforeEach
    void setupMocks() throws MessagingException {
        doNothing().when(gmailEmailService).sendTicketsCsv(anyString(), anyString(), any(User.class));
        when(gmailEmailService.exportTicketsToCsv(anyList())).thenReturn("csv,data");
    }

    @Test
    void testCreateTicket() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "T125");
        payload.put("description", "Fire emergency");
        payload.put("userId", "admin1");
        payload.put("emergencyType", "Fire");
        payload.put("emergencyContact", "0123456789");
        payload.put("duration", 60);

        webTestClient.post().uri("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.ticketId").isEqualTo("T125");
    }

    @Test
    void testListTickets() {
        webTestClient.get().uri("/api/tickets")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].ticketId").exists();
    }

    @Test
    void testGetTicketById() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "T126");
        payload.put("description", "Medical emergency");
        payload.put("userId", "user1");
        payload.put("emergencyType", "Medical");
        payload.put("emergencyContact", "0987654321");
        payload.put("duration", 30);

        final Long[] idHolder = new Long[1];
        webTestClient.post().uri("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").value(id -> idHolder[0] = Long.valueOf(id.toString()));

        webTestClient.get().uri("/api/tickets/" + idHolder[0])
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.ticketId").isEqualTo("T126");
    }

    @Test
    void testGetTicketByTicketId() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "T123");
        payload.put("description", "Fire emergency");
        payload.put("userId", "admin1");
        payload.put("emergencyType", "Fire");
        payload.put("emergencyContact", "0123456789");
        payload.put("duration", 60);

        webTestClient.post().uri("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk();

        webTestClient.get().uri("/api/tickets/ticket-id/T123")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.ticketId").isEqualTo("T123");
    }

    @Test
    void testUpdateTicketById() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "T200");
        payload.put("description", "Initial");
        payload.put("userId", "admin1");
        payload.put("emergencyType", "Fire");
        payload.put("emergencyContact", "0123456789");
        payload.put("duration", 60);

        final Long[] idHolder = new Long[1];
        webTestClient.post().uri("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").value(id -> idHolder[0] = Long.valueOf(id.toString()));

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("description", "Updated description");

        webTestClient.put().uri("/api/tickets/" + idHolder[0])
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatePayload)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.description").isEqualTo("Updated description");
    }

    @Test
    void testDeleteTicketById() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "T201");
        payload.put("description", "Delete me");
        payload.put("userId", "admin1");
        payload.put("emergencyType", "Fire");
        payload.put("emergencyContact", "0123456789");
        payload.put("duration", 60);

        final Long[] idHolder = new Long[1];
        webTestClient.post().uri("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").value(id -> idHolder[0] = Long.valueOf(id.toString()));

        webTestClient.delete().uri("/api/tickets/" + idHolder[0])
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testDeleteTicketByTicketId() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "T202");
        payload.put("description", "Delete by ticketId");
        payload.put("userId", "admin1");
        payload.put("emergencyType", "Fire");
        payload.put("emergencyContact", "0123456789");
        payload.put("duration", 60);

        webTestClient.post().uri("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk();

        webTestClient.delete().uri("/api/tickets/ticket-id/T202")
            .exchange()
            .expectStatus().isOk();
    }


    @Test
    void testAdminActiveTickets() {
        webTestClient.get().uri("/api/tickets/admin/active")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    @Test
    void testAdminHistory() {
        webTestClient.get().uri("/api/tickets/admin/history")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    @Test
    void testAdminStatus() {
        webTestClient.get().uri("/api/tickets/admin/status/OPEN")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    /*

    @Test
    void testAdminRevokeById() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "T203");
        payload.put("description", "Revoke by id");
        payload.put("userId", "admin1");
        payload.put("emergencyType", "Fire");
        payload.put("emergencyContact", "0123456789");
        payload.put("duration", 60);

        final Long[] idHolder = new Long[1];
        webTestClient.post().uri("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").value(id -> idHolder[0] = Long.valueOf(id.toString()));

        Map<String, Object> revokePayload = new HashMap<>();
        revokePayload.put("rejectReason", "Test reason");
        webTestClient.put().uri("/api/tickets/admin/revoke/" + idHolder[0])
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(revokePayload)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testAdminRevokeByTicketId() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "T204");
        payload.put("description", "Revoke by ticketId");
        payload.put("userId", "admin1");
        payload.put("emergencyType", "Fire");
        payload.put("emergencyContact", "0123456789");
        payload.put("duration", 60);

        webTestClient.post().uri("/api/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk();

        webTestClient.put().uri("/api/tickets/admin/revoke/ticket-id/T204")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testAdminCheck() {
        webTestClient.get().uri("/api/tickets/admin/check/admin1")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testAdminExportNonAdmin() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("startDate", "2024-01-01");
        payload.put("endDate", "2024-12-31");
        payload.put("userId", "user1"); // non-admin

        webTestClient.post().uri("/api/tickets/admin/export")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void testAdminExportAdmin() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("startDate", "2024-01-01");
        payload.put("endDate", "2024-12-31");
        payload.put("userId", "admin1"); // admin

        webTestClient.post().uri(uriBuilder -> uriBuilder
    .path("/api/tickets/admin/export")
    .queryParam("startDate", "2024-01-01")
    .queryParam("endDate", "2024-12-31")
    .queryParam("userId", "admin1")
    .build())
    .exchange()
    .expectStatus().isOk();
        // Optionally, check for email sending logic if stubbed
    }

    @Test
    void testAdminExportWithDateRange() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("startDate", "2024-06-01");
        payload.put("endDate", "2024-06-30");
        payload.put("userId", "admin1");

        webTestClient.post().uri("/api/tickets/admin/export")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk();
    }

    */

}
