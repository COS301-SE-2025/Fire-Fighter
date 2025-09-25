package com.apex.firefighter.controller;

import com.apex.firefighter.config.TestConfig;
import com.apex.firefighter.model.Notification;
import com.apex.firefighter.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
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
@Import(TestConfig.class)
@ActiveProfiles("test")
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

    // ==================== ADDITIONAL ERROR HANDLING TESTS ====================

    @Test
    @WithMockUser
    void getUnreadNotifications_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(notificationService.getUnreadNotificationsForUser(TEST_USER_ID))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/unread")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isInternalServerError());

        verify(notificationService).getUnreadNotificationsForUser(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getNotificationStats_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(notificationService.getNotificationStats(TEST_USER_ID))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/stats")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isInternalServerError());

        verify(notificationService).getNotificationStats(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void markNotificationAsRead_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(notificationService.markNotificationAsRead(1L, TEST_USER_ID))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/1/read")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(notificationService).markNotificationAsRead(1L, TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void markAllNotificationsAsRead_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(notificationService.markAllNotificationsAsRead(TEST_USER_ID))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/read-all")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(notificationService).markAllNotificationsAsRead(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void deleteReadNotifications_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(notificationService.deleteReadNotifications(TEST_USER_ID))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/read")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(notificationService).deleteReadNotifications(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void deleteNotification_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(notificationService.deleteNotification(1L, TEST_USER_ID))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/1")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(notificationService).deleteNotification(1L, TEST_USER_ID);
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    @WithMockUser
    void getNotifications_WithEmptyList_ShouldReturnEmptyArray() throws Exception {
        // Arrange
        when(notificationService.getNotificationsForUser(TEST_USER_ID)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL)
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(notificationService).getNotificationsForUser(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getUnreadNotifications_WithEmptyList_ShouldReturnEmptyArray() throws Exception {
        // Arrange
        when(notificationService.getUnreadNotificationsForUser(TEST_USER_ID)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/unread")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(notificationService).getUnreadNotificationsForUser(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getNotificationStats_WithZeroStats_ShouldReturnZeroValues() throws Exception {
        // Arrange
        NotificationService.NotificationStats stats = new NotificationService.NotificationStats(0, 0, 0);
        when(notificationService.getNotificationStats(TEST_USER_ID)).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/stats")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.unread").value(0))
                .andExpect(jsonPath("$.read").value(0));

        verify(notificationService).getNotificationStats(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void markAllNotificationsAsRead_WithZeroUpdates_ShouldReturnZeroCount() throws Exception {
        // Arrange
        when(notificationService.markAllNotificationsAsRead(TEST_USER_ID)).thenReturn(0);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/read-all")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All notifications marked as read"))
                .andExpect(jsonPath("$.updatedCount").value(0));

        verify(notificationService).markAllNotificationsAsRead(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void deleteReadNotifications_WithZeroDeletes_ShouldReturnZeroCount() throws Exception {
        // Arrange
        when(notificationService.deleteReadNotifications(TEST_USER_ID)).thenReturn(0);

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/read")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Read notifications deleted successfully"))
                .andExpect(jsonPath("$.deletedCount").value(0));

        verify(notificationService).deleteReadNotifications(TEST_USER_ID);
    }

    // ==================== MULTIPLE NOTIFICATIONS TESTS ====================

    @Test
    @WithMockUser
    void getNotifications_WithMultipleNotifications_ShouldReturnAllNotifications() throws Exception {
        // Arrange
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setUserId(TEST_USER_ID);
        notification1.setType("ticket_created");
        notification1.setTitle("Ticket Created");
        notification1.setMessage("Your ticket has been created");
        notification1.setRead(false);

        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setUserId(TEST_USER_ID);
        notification2.setType("ticket_completed");
        notification2.setTitle("Ticket Completed");
        notification2.setMessage("Your ticket has been completed");
        notification2.setRead(true);

        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationService.getNotificationsForUser(TEST_USER_ID)).thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get(BASE_URL)
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("ticket_created"))
                .andExpect(jsonPath("$[0].read").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].type").value("ticket_completed"))
                .andExpect(jsonPath("$[1].read").value(true));

        verify(notificationService).getNotificationsForUser(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getUnreadNotifications_WithMixedReadStatus_ShouldReturnOnlyUnread() throws Exception {
        // Arrange
        Notification unreadNotification = new Notification();
        unreadNotification.setId(1L);
        unreadNotification.setUserId(TEST_USER_ID);
        unreadNotification.setType("ticket_created");
        unreadNotification.setTitle("Ticket Created");
        unreadNotification.setMessage("Your ticket has been created");
        unreadNotification.setRead(false);

        List<Notification> unreadNotifications = Arrays.asList(unreadNotification);
        when(notificationService.getUnreadNotificationsForUser(TEST_USER_ID)).thenReturn(unreadNotifications);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/unread")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].read").value(false));

        verify(notificationService).getUnreadNotificationsForUser(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getNotificationStats_WithMixedStats_ShouldReturnCorrectCounts() throws Exception {
        // Arrange
        NotificationService.NotificationStats stats = new NotificationService.NotificationStats(15, 7, 8);
        when(notificationService.getNotificationStats(TEST_USER_ID)).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/stats")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(15))
                .andExpect(jsonPath("$.unread").value(7))
                .andExpect(jsonPath("$.read").value(8));

        verify(notificationService).getNotificationStats(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void markAllNotificationsAsRead_WithMultipleNotifications_ShouldReturnCorrectCount() throws Exception {
        // Arrange
        when(notificationService.markAllNotificationsAsRead(TEST_USER_ID)).thenReturn(5);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/read-all")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All notifications marked as read"))
                .andExpect(jsonPath("$.updatedCount").value(5));

        verify(notificationService).markAllNotificationsAsRead(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void deleteReadNotifications_WithMultipleReadNotifications_ShouldReturnCorrectCount() throws Exception {
        // Arrange
        when(notificationService.deleteReadNotifications(TEST_USER_ID)).thenReturn(8);

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/read")
                .param("userId", TEST_USER_ID)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Read notifications deleted successfully"))
                .andExpect(jsonPath("$.deletedCount").value(8));

        verify(notificationService).deleteReadNotifications(TEST_USER_ID);
    }

    // ==================== DIFFERENT NOTIFICATION TYPES TESTS ====================

    @Test
    @WithMockUser
    void getNotification_WithTicketCompletedType_ShouldReturnNotification() throws Exception {
        // Arrange
        Notification completedNotification = new Notification();
        completedNotification.setId(1L);
        completedNotification.setUserId(TEST_USER_ID);
        completedNotification.setType("ticket_completed");
        completedNotification.setTitle("Ticket Completed");
        completedNotification.setMessage("Your emergency ticket has been completed");
        completedNotification.setTicketId("TICKET-001");
        completedNotification.setRead(false);
        completedNotification.setTimestamp(LocalDateTime.now());

        when(notificationService.getNotificationForUser(1L, TEST_USER_ID))
                .thenReturn(Optional.of(completedNotification));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/1")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.type").value("ticket_completed"))
                .andExpect(jsonPath("$.title").value("Ticket Completed"))
                .andExpect(jsonPath("$.message").value("Your emergency ticket has been completed"))
                .andExpect(jsonPath("$.ticketId").value("TICKET-001"))
                .andExpect(jsonPath("$.read").value(false));

        verify(notificationService).getNotificationForUser(1L, TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getNotification_WithTicketRevokedType_ShouldReturnNotification() throws Exception {
        // Arrange
        Notification revokedNotification = new Notification();
        revokedNotification.setId(2L);
        revokedNotification.setUserId(TEST_USER_ID);
        revokedNotification.setType("ticket_revoked");
        revokedNotification.setTitle("Ticket Revoked");
        revokedNotification.setMessage("Your emergency ticket has been revoked");
        revokedNotification.setTicketId("TICKET-002");
        revokedNotification.setRead(true);
        revokedNotification.setTimestamp(LocalDateTime.now());

        when(notificationService.getNotificationForUser(2L, TEST_USER_ID))
                .thenReturn(Optional.of(revokedNotification));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/2")
                .param("userId", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.type").value("ticket_revoked"))
                .andExpect(jsonPath("$.title").value("Ticket Revoked"))
                .andExpect(jsonPath("$.message").value("Your emergency ticket has been revoked"))
                .andExpect(jsonPath("$.ticketId").value("TICKET-002"))
                .andExpect(jsonPath("$.read").value(true));

        verify(notificationService).getNotificationForUser(2L, TEST_USER_ID);
    }
}
