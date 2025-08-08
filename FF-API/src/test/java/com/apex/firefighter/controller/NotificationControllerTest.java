package com.apex.firefighter.controller;

import com.apex.firefighter.model.Notification;
import com.apex.firefighter.service.NotificationService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification testNotification;
    private final String TEST_USER_ID = "test-user-123";
    private final String BASE_URL = "/api/notifications";

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUserId(TEST_USER_ID);
        testNotification.setType("ticket_created");
        testNotification.setTitle("Test Notification");
        testNotification.setMessage("Test message");
        testNotification.setTimestamp(LocalDateTime.now());
        testNotification.setRead(false);
        testNotification.setTicketId("TICKET-001");
    }

    @Test
    @WithMockUser
    void getNotifications_ShouldReturnNotificationsList() throws Exception {
        // Arrange
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationService.getNotificationsForUser(TEST_USER_ID)).thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get(BASE_URL)
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$[0].type").value("ticket_created"))
                .andExpect(jsonPath("$[0].title").value("Test Notification"))
                .andExpect(jsonPath("$[0].message").value("Test message"))
                .andExpect(jsonPath("$[0].read").value(false));

        verify(notificationService).getNotificationsForUser(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getNotifications_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(notificationService.getNotificationsForUser(TEST_USER_ID))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL)
                .param("userId", TEST_USER_ID))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void getUnreadNotifications_ShouldReturnUnreadNotificationsList() throws Exception {
        // Arrange
        List<Notification> unreadNotifications = Arrays.asList(testNotification);
        when(notificationService.getUnreadNotificationsForUser(TEST_USER_ID)).thenReturn(unreadNotifications);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/unread")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].read").value(false));

        verify(notificationService).getUnreadNotificationsForUser(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getNotificationStats_ShouldReturnStatistics() throws Exception {
        // Arrange
        NotificationService.NotificationStats stats = new NotificationService.NotificationStats(10, 5, 5);
        when(notificationService.getNotificationStats(TEST_USER_ID)).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/stats")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.unread").value(5))
                .andExpect(jsonPath("$.read").value(5));

        verify(notificationService).getNotificationStats(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void markNotificationAsRead_WhenSuccessful_ShouldReturnOk() throws Exception {
        // Arrange
        when(notificationService.markNotificationAsRead(1L, TEST_USER_ID)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/1/read")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification marked as read"));

        verify(notificationService).markNotificationAsRead(1L, TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void markNotificationAsRead_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(notificationService.markNotificationAsRead(1L, TEST_USER_ID)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/1/read")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(notificationService).markNotificationAsRead(1L, TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void markAllNotificationsAsRead_ShouldReturnUpdatedCount() throws Exception {
        // Arrange
        when(notificationService.markAllNotificationsAsRead(TEST_USER_ID)).thenReturn(3);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/read-all")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All notifications marked as read"))
                .andExpect(jsonPath("$.updatedCount").value(3));

        verify(notificationService).markAllNotificationsAsRead(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void deleteReadNotifications_ShouldReturnDeletedCount() throws Exception {
        // Arrange
        when(notificationService.deleteReadNotifications(TEST_USER_ID)).thenReturn(2);

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/read")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Read notifications deleted successfully"))
                .andExpect(jsonPath("$.deletedCount").value(2));

        verify(notificationService).deleteReadNotifications(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void deleteNotification_WhenSuccessful_ShouldReturnOk() throws Exception {
        // Arrange
        when(notificationService.deleteNotification(1L, TEST_USER_ID)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/1")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification deleted successfully"));

        verify(notificationService).deleteNotification(1L, TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void deleteNotification_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(notificationService.deleteNotification(1L, TEST_USER_ID)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/1")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(notificationService).deleteNotification(1L, TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getNotification_WhenExists_ShouldReturnNotification() throws Exception {
        // Arrange
        when(notificationService.getNotificationForUser(1L, TEST_USER_ID))
                .thenReturn(Optional.of(testNotification));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/1")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID));

        verify(notificationService).getNotificationForUser(1L, TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getNotification_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(notificationService.getNotificationForUser(1L, TEST_USER_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/1")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isNotFound());

        verify(notificationService).getNotificationForUser(1L, TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getNotification_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(notificationService.getNotificationForUser(1L, TEST_USER_ID))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/1")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isInternalServerError());
    }
}
