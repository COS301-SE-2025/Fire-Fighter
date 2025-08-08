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
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "TICKET-001");
        payload.put("description", "Test emergency");
        payload.put("userId", TEST_USER_ID);
        payload.put("emergencyType", "FIRE");
        payload.put("emergencyContact", "123-456-7890");
        payload.put("duration", 60);

        when(ticketService.createTicket(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(testTicket);

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
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketId", "TICKET-001");
        payload.put("description", "Test emergency");
        payload.put("userId", TEST_USER_ID);
        payload.put("emergencyType", "FIRE");
        payload.put("emergencyContact", "123-456-7890");

        when(ticketService.createTicket(anyString(), anyString(), anyString(), anyString(), anyString(), eq(60)))
                .thenReturn(testTicket);

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
       
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketService.getAllTickets()).thenReturn(tickets);

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
        
        when(ticketService.getTicketById(1L)).thenReturn(Optional.of(testTicket));

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketId").value("TICKET-001"));

        verify(ticketService).getTicketById(1L);
    }

    @Test
    @WithMockUser
    void getTicketById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        
        when(ticketService.getTicketById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isNotFound());

        verify(ticketService).getTicketById(1L);
    }

    @Test
    @WithMockUser
    void updateTicket_WhenSuccessful_ShouldReturnUpdatedTicket() throws Exception {
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("description", "Updated description");
        payload.put("status", "COMPLETED");
        payload.put("emergencyType", "MEDICAL");
        payload.put("emergencyContact", "987-654-3210");
        payload.put("duration", 90);

        Ticket updatedTicket = new Ticket();
        updatedTicket.setDescription("Updated description");
        updatedTicket.setStatus("COMPLETED");

        when(ticketService.updateTicket(eq(1L), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(updatedTicket);

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
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("description", "Updated description");

        when(ticketService.updateTicket(eq(1L), anyString(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Ticket not found"));

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
        
        when(ticketService.deleteTicket(1L)).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(ticketService).deleteTicket(1L);
    }

    @Test
    @WithMockUser
    void deleteTicket_WhenNotFound_ShouldReturnNotFound() throws Exception {
        
        when(ticketService.deleteTicket(1L)).thenReturn(false);

        mockMvc.perform(delete(BASE_URL + "/1")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(ticketService).deleteTicket(1L);
    }

    @Test
    @WithMockUser
    void getActiveTickets_ShouldReturnActiveTicketsList() throws Exception {
        
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketService.getActiveTickets()).thenReturn(activeTickets);

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
        
        when(ticketService.getActiveTickets()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get(BASE_URL + "/admin/active"))
                .andExpect(status().isInternalServerError());

        verify(ticketService).getActiveTickets();
    }

    @Test
    @WithMockUser
    void getTicketHistory_ShouldReturnTicketHistoryList() throws Exception {
        
        List<Ticket> ticketHistory = Arrays.asList(testTicket);
        when(ticketService.getTicketHistory()).thenReturn(ticketHistory);

        mockMvc.perform(get(BASE_URL + "/admin/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(ticketService).getTicketHistory();
    }

    @Test
    @WithMockUser
    void exportTickets_WhenUserIsAdmin_ShouldReturnSuccess() throws Exception {
        
        String targetEmail = "test@example.com"; 
        List<Ticket> tickets = Arrays.asList(testTicket);
        String csvContent = "id,ticketId,description\n1,TICKET-001,Test emergency";

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", TEST_USER_ID);

        when(userService.getUserWithRoles(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(ticketService.getAllTickets()).thenReturn(tickets);
        when(gmailEmailService.exportTicketsToCsv(tickets)).thenReturn(csvContent);
        doNothing().when(gmailEmailService).sendTicketsCsv(targetEmail, csvContent, testUser);

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
        
        testUser.setIsAdmin(false);
        when(userService.getUserWithRoles(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", TEST_USER_ID);
        payload.put("email", "admin@example.com");

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
        
        when(userService.getUserWithRoles(TEST_USER_ID)).thenReturn(Optional.empty());

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", TEST_USER_ID);
        payload.put("email", "admin@example.com");

        mockMvc.perform(post(BASE_URL + "/admin/export")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService).getUserWithRoles(TEST_USER_ID);
        verifyNoInteractions(ticketService);
        verifyNoInteractions(gmailEmailService);
    }

    @Test
    @WithMockUser
    void getTicketByTicketId_WhenExists_ShouldReturnTicket() throws Exception {
        
        String ticketId = "TICKET-001";
        when(ticketService.getTicketByTicketId(ticketId)).thenReturn(Optional.of(testTicket));

        mockMvc.perform(get(BASE_URL + "/ticket-id/" + ticketId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketId").value("TICKET-001"))
                .andExpect(jsonPath("$.description").value("Test emergency"));

        verify(ticketService).getTicketByTicketId(ticketId);
    }

    @Test
    @WithMockUser
    void getTicketByTicketId_WhenNotExists_ShouldReturnNotFound() throws Exception {
        
        String ticketId = "NONEXISTENT-001";
        when(ticketService.getTicketByTicketId(ticketId)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/ticket-id/" + ticketId))
                .andExpect(status().isNotFound());

        verify(ticketService).getTicketByTicketId(ticketId);
    }

    @Test
    @WithMockUser
    void deleteTicketByTicketId_WhenSuccessful_ShouldReturnOk() throws Exception {
        
        String ticketId = "TICKET-001";
        when(ticketService.deleteTicketByTicketId(ticketId)).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/ticket-id/" + ticketId)
                .with(csrf()))
                .andExpect(status().isOk());

        verify(ticketService).deleteTicketByTicketId(ticketId);
    }

    @Test
    @WithMockUser
    void deleteTicketByTicketId_WhenNotFound_ShouldReturnNotFound() throws Exception {
        
        String ticketId = "NONEXISTENT-001";
        when(ticketService.deleteTicketByTicketId(ticketId)).thenReturn(false);

        mockMvc.perform(delete(BASE_URL + "/ticket-id/" + ticketId)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(ticketService).deleteTicketByTicketId(ticketId);
    }

    @Test
    @WithMockUser
    void getTicketsByStatus_ShouldReturnTicketsWithStatus() throws Exception {
        
        String status = "ACTIVE";
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketService.getTicketsByStatus(status)).thenReturn(activeTickets);

        mockMvc.perform(get(BASE_URL + "/admin/status/" + status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(ticketService).getTicketsByStatus(status);
    }

    @Test
    @WithMockUser
    void getTicketsByStatus_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        
        String status = "ACTIVE";
        when(ticketService.getTicketsByStatus(status)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get(BASE_URL + "/admin/status/" + status))
                .andExpect(status().isInternalServerError());

        verify(ticketService).getTicketsByStatus(status);
    }

    @Test
    @WithMockUser
    void revokeTicket_WhenSuccessful_ShouldReturnSuccessResponse() throws Exception {
        
        Long ticketId = 1L;
        String adminUserId = "admin-user";
        String rejectReason = "Security violation";

        Map<String, String> payload = new HashMap<>();
        payload.put("adminUserId", adminUserId);
        payload.put("rejectReason", rejectReason);

        // Create a revoked ticket to return from service
        Ticket revokedTicket = new Ticket();
        revokedTicket.setTicketId("TICKET-001");
        revokedTicket.setStatus("Rejected");
        revokedTicket.setRejectReason(rejectReason);
        revokedTicket.setRevokedBy(adminUserId);

        when(ticketService.revokeTicket(ticketId, adminUserId, rejectReason)).thenReturn(revokedTicket);

        mockMvc.perform(put(BASE_URL + "/admin/revoke/" + ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Ticket revoked successfully"))
                .andExpect(jsonPath("$.ticket").exists())
                .andExpect(jsonPath("$.ticket.ticketId").value("TICKET-001"))
                .andExpect(jsonPath("$.ticket.status").value("Rejected"));

        verify(ticketService).revokeTicket(ticketId, adminUserId, rejectReason);
    }

    @Test
    @WithMockUser
    void revokeTicket_WhenNotFound_ShouldReturnBadRequest() throws Exception {

        Long ticketId = 999L;
        String adminUserId = "admin-user";
        String rejectReason = "Security violation";

        Map<String, String> payload = new HashMap<>();
        payload.put("adminUserId", adminUserId);
        payload.put("rejectReason", rejectReason);

        when(ticketService.revokeTicket(ticketId, adminUserId, rejectReason))
                .thenThrow(new RuntimeException("Ticket not found"));

        mockMvc.perform(put(BASE_URL + "/admin/revoke/" + ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ticket not found"));

        verify(ticketService).revokeTicket(ticketId, adminUserId, rejectReason);
    }

    @Test
    @WithMockUser
    void revokeTicketByTicketId_WhenSuccessful_ShouldReturnSuccessResponse() throws Exception {
        
        String ticketId = "TICKET-001";
        String adminUserId = "admin-user";
        String rejectReason = "Policy violation";

        Map<String, String> payload = new HashMap<>();
        payload.put("adminUserId", adminUserId);
        payload.put("rejectReason", rejectReason);

        // Create a revoked ticket to return from service
        Ticket revokedTicket = new Ticket();
        revokedTicket.setTicketId(ticketId);
        revokedTicket.setStatus("Rejected");
        revokedTicket.setRejectReason(rejectReason);
        revokedTicket.setRevokedBy(adminUserId);

        when(ticketService.revokeTicketByTicketId(ticketId, adminUserId, rejectReason)).thenReturn(revokedTicket);

        mockMvc.perform(put(BASE_URL + "/admin/revoke/ticket-id/" + ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Ticket revoked successfully"))
                .andExpect(jsonPath("$.ticket").exists())
                .andExpect(jsonPath("$.ticket.ticketId").value(ticketId))
                .andExpect(jsonPath("$.ticket.status").value("Rejected"));

        verify(ticketService).revokeTicketByTicketId(ticketId, adminUserId, rejectReason);
    }

    @Test
    @WithMockUser
    void revokeTicketByTicketId_WhenNotFound_ShouldReturnBadRequest() throws Exception {

        String ticketId = "NONEXISTENT-001";
        String adminUserId = "admin-user";
        String rejectReason = "Policy violation";

        Map<String, String> payload = new HashMap<>();
        payload.put("adminUserId", adminUserId);
        payload.put("rejectReason", rejectReason);

        when(ticketService.revokeTicketByTicketId(ticketId, adminUserId, rejectReason))
                .thenThrow(new RuntimeException("Ticket not found"));

        mockMvc.perform(put(BASE_URL + "/admin/revoke/ticket-id/" + ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ticket not found"));

        verify(ticketService).revokeTicketByTicketId(ticketId, adminUserId, rejectReason);
    }

    @Test
    @WithMockUser
    void checkAdminStatus_WhenUserIsAdmin_ShouldReturnTrue() throws Exception {
        
        String userId = "admin-user-123";
        when(ticketService.isUserAdmin(userId)).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/admin/check/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isAdmin").value(true));

        verify(ticketService).isUserAdmin(userId);
    }

    @Test
    @WithMockUser
    void checkAdminStatus_WhenUserIsNotAdmin_ShouldReturnFalse() throws Exception {
        
        String userId = "regular-user-123";
        when(ticketService.isUserAdmin(userId)).thenReturn(false);

        mockMvc.perform(get(BASE_URL + "/admin/check/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isAdmin").value(false));

        verify(ticketService).isUserAdmin(userId);
    }
}
