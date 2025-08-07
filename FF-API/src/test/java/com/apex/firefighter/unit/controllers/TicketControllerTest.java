package com.apex.firefighter.controller;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.service.ticket.TicketService;
import com.apex.firefighter.service.GmailEmailService;
import com.apex.firefighter.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.hamcrest.Matchers.containsString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private GmailEmailService gmailEmailService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Ticket testTicket;
    private User testUser;
    private final String TEST_USER_ID = "test-user-123";
    private final String BASE_URL = "/api/tickets";

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        // Note: Ticket.id is auto-generated, no setter available
        testTicket.setTicketId("TICKET-001");
        testTicket.setDescription("Test emergency");
        testTicket.setUserId(TEST_USER_ID);
        testTicket.setEmergencyType("FIRE");
        testTicket.setEmergencyContact("123-456-7890");
        testTicket.setDuration(60);
        testTicket.setStatus("ACTIVE");
        testTicket.setDateCreated(LocalDateTime.now());
        testTicket.setRequestDate(LocalDate.now());

        testUser = new User();
        testUser.setUserId(TEST_USER_ID);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setIsAdmin(true);
    }

    @Test
    @WithMockUser
    void createTicket_ShouldReturnCreatedTicket() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "TICKET-001");
        payload.put("description", "Test emergency");
        payload.put("userId", TEST_USER_ID);
        payload.put("emergencyType", "FIRE");
        payload.put("emergencyContact", "123-456-7890");
        payload.put("duration", 60);

        when(ticketService.createTicket(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(testTicket);

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketId").value("TICKET-001"))
                .andExpect(jsonPath("$.description").value("Test emergency"))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.emergencyType").value("FIRE"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(ticketService).createTicket("TICKET-001", "Test emergency", TEST_USER_ID, "FIRE", "123-456-7890", 60);
    }

    @Test
    @WithMockUser
    void createTicket_WithDefaultDuration_ShouldUse60Minutes() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "TICKET-001");
        payload.put("description", "Test emergency");
        payload.put("userId", TEST_USER_ID);
        payload.put("emergencyType", "FIRE");
        payload.put("emergencyContact", "123-456-7890");
        // No duration specified

        when(ticketService.createTicket(anyString(), anyString(), anyString(), anyString(), anyString(), eq(60)))
                .thenReturn(testTicket);

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(ticketService).createTicket("TICKET-001", "Test emergency", TEST_USER_ID, "FIRE", "123-456-7890", 60);
    }

    @Test
    @WithMockUser
    void getAllTickets_ShouldReturnTicketsList() throws Exception {
        // Arrange
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketService.getAllTickets()).thenReturn(tickets);

        // Act & Assert
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ticketId").value("TICKET-001"));

        verify(ticketService).getAllTickets();
    }

    @Test
    @WithMockUser
    void getTicketById_WhenExists_ShouldReturnTicket() throws Exception {
        // Arrange
        when(ticketService.getTicketById(1L)).thenReturn(Optional.of(testTicket));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Note: ID is null in mock response, so we don't check it
                .andExpect(jsonPath("$.ticketId").value("TICKET-001"));

        verify(ticketService).getTicketById(1L);
    }

    @Test
    @WithMockUser
    void getTicketById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(ticketService.getTicketById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isNotFound());

        verify(ticketService).getTicketById(1L);
    }

    @Test
    @WithMockUser
    void updateTicket_WhenSuccessful_ShouldReturnUpdatedTicket() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("description", "Updated description");
        payload.put("status", "COMPLETED");
        payload.put("emergencyType", "MEDICAL");
        payload.put("emergencyContact", "987-654-3210");
        payload.put("duration", 90);

        Ticket updatedTicket = new Ticket();
        // Note: Ticket.id is auto-generated, no setter available
        updatedTicket.setDescription("Updated description");
        updatedTicket.setStatus("COMPLETED");

        when(ticketService.updateTicket(eq(1L), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(updatedTicket);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(ticketService).updateTicket(1L, "Updated description", "COMPLETED", "MEDICAL", "987-654-3210", 90);
    }

    @Test
    @WithMockUser
    void updateTicket_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("description", "Updated description");

        when(ticketService.updateTicket(eq(1L), anyString(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Ticket not found"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(ticketService).updateTicket(eq(1L), anyString(), any(), any(), any(), any());
    }

    @Test
    @WithMockUser
    void deleteTicket_WhenSuccessful_ShouldReturnOk() throws Exception {
        // Arrange
        when(ticketService.deleteTicket(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(ticketService).deleteTicket(1L);
    }

    @Test
    @WithMockUser
    void deleteTicket_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(ticketService.deleteTicket(1L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/1")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(ticketService).deleteTicket(1L);
    }

    @Test
    @WithMockUser
    void getActiveTickets_ShouldReturnActiveTicketsList() throws Exception {
        // Arrange
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketService.getActiveTickets()).thenReturn(activeTickets);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/admin/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(ticketService).getActiveTickets();
    }

    @Test
    @WithMockUser
    void getActiveTickets_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(ticketService.getActiveTickets()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/admin/active"))
                .andExpect(status().isInternalServerError());

        verify(ticketService).getActiveTickets();
    }

    @Test
    @WithMockUser
    void getTicketHistory_ShouldReturnTicketHistoryList() throws Exception {
        // Arrange
        List<Ticket> ticketHistory = Arrays.asList(testTicket);
        when(ticketService.getTicketHistory()).thenReturn(ticketHistory);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/admin/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(ticketService).getTicketHistory();
    }

    @Test
    @WithMockUser
    void exportTickets_WhenUserIsAdmin_ShouldReturnSuccess() throws Exception {
        // Arrange
        String targetEmail = "test@example.com"; // This will be the user's email from the controller
        List<Ticket> tickets = Arrays.asList(testTicket);
        String csvContent = "id,ticketId,description\n1,TICKET-001,Test emergency";

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", TEST_USER_ID);

        when(userService.getUserWithRoles(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(ticketService.getAllTickets()).thenReturn(tickets);
        when(gmailEmailService.exportTicketsToCsv(tickets)).thenReturn(csvContent);
        doNothing().when(gmailEmailService).sendTicketsCsv(targetEmail, csvContent, testUser);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/admin/export")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tickets exported and emailed successfully to " + targetEmail)));

        verify(userService).getUserWithRoles(TEST_USER_ID);
        verify(ticketService).getAllTickets();
        verify(gmailEmailService).exportTicketsToCsv(tickets);
        verify(gmailEmailService).sendTicketsCsv(targetEmail, csvContent, testUser);
    }

    @Test
    @WithMockUser
    void exportTickets_WhenUserIsNotAdmin_ShouldReturnForbidden() throws Exception {
        // Arrange
        testUser.setIsAdmin(false);
        when(userService.getUserWithRoles(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", TEST_USER_ID);
        payload.put("email", "admin@example.com");

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/admin/export")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Not authorized: Only admins can export tickets")));

        verify(userService).getUserWithRoles(TEST_USER_ID);
        verifyNoInteractions(ticketService);
        verifyNoInteractions(gmailEmailService);
    }

    @Test
    @WithMockUser
    void exportTickets_WhenUserNotFound_ShouldReturnForbidden() throws Exception {
        // Arrange
        when(userService.getUserWithRoles(TEST_USER_ID)).thenReturn(Optional.empty());

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", TEST_USER_ID);
        payload.put("email", "admin@example.com");

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/admin/export")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService).getUserWithRoles(TEST_USER_ID);
        verifyNoInteractions(ticketService);
        verifyNoInteractions(gmailEmailService);
    }
}
