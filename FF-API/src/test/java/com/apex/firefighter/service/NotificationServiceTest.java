package com.apex.firefighter.service;

import com.apex.firefighter.model.Notification;
import com.apex.firefighter.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;
    private final String TEST_USER_ID = "test-user-123";
    private final String TEST_TICKET_ID = "TICKET-001";

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
        testNotification.setTicketId(TEST_TICKET_ID);
    }

    @Test
    void createNotification_ShouldCreateAndReturnNotification() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        Notification result = notificationService.createNotification(
            TEST_USER_ID, "ticket_created", "Test Notification", "Test message"
        );

        // Assert
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());
        assertEquals("ticket_created", result.getType());
        assertEquals("Test Notification", result.getTitle());
        assertEquals("Test message", result.getMessage());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotificationWithTicketId_ShouldCreateAndReturnNotification() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        Notification result = notificationService.createNotification(
            TEST_USER_ID, "ticket_created", "Test Notification", "Test message", TEST_TICKET_ID
        );

        // Assert
        assertNotNull(result);
        assertEquals(TEST_TICKET_ID, result.getTicketId());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getNotificationsForUser_ShouldReturnUserNotifications() {
        // Arrange
        List<Notification> expectedNotifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdOrderByTimestampDesc(TEST_USER_ID))
            .thenReturn(expectedNotifications);

        // Act
        List<Notification> result = notificationService.getNotificationsForUser(TEST_USER_ID);

        // Assert
        assertEquals(1, result.size());
        assertEquals(testNotification, result.get(0));
        verify(notificationRepository).findByUserIdOrderByTimestampDesc(TEST_USER_ID);
    }

    @Test
    void getUnreadNotificationsForUser_ShouldReturnUnreadNotifications() {
        // Arrange
        List<Notification> expectedNotifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdAndReadFalseOrderByTimestampDesc(TEST_USER_ID))
            .thenReturn(expectedNotifications);

        // Act
        List<Notification> result = notificationService.getUnreadNotificationsForUser(TEST_USER_ID);

        // Assert
        assertEquals(1, result.size());
        assertFalse(result.get(0).getRead());
        verify(notificationRepository).findByUserIdAndReadFalseOrderByTimestampDesc(TEST_USER_ID);
    }

    @Test
    void markNotificationAsRead_WhenNotificationExists_ShouldReturnTrue() {
        // Arrange
        when(notificationRepository.markAsRead(1L, TEST_USER_ID)).thenReturn(1);

        // Act
        boolean result = notificationService.markNotificationAsRead(1L, TEST_USER_ID);

        // Assert
        assertTrue(result);
        verify(notificationRepository).markAsRead(1L, TEST_USER_ID);
    }

    @Test
    void markNotificationAsRead_WhenNotificationNotFound_ShouldReturnFalse() {
        // Arrange
        when(notificationRepository.markAsRead(1L, TEST_USER_ID)).thenReturn(0);

        // Act
        boolean result = notificationService.markNotificationAsRead(1L, TEST_USER_ID);

        // Assert
        assertFalse(result);
        verify(notificationRepository).markAsRead(1L, TEST_USER_ID);
    }

    @Test
    void markAllNotificationsAsRead_ShouldReturnUpdatedCount() {
        // Arrange
        when(notificationRepository.markAllAsReadForUser(TEST_USER_ID)).thenReturn(3);

        // Act
        int result = notificationService.markAllNotificationsAsRead(TEST_USER_ID);

        // Assert
        assertEquals(3, result);
        verify(notificationRepository).markAllAsReadForUser(TEST_USER_ID);
    }

    @Test
    void deleteReadNotifications_ShouldReturnDeletedCount() {
        // Arrange
        when(notificationRepository.deleteReadNotificationsForUser(TEST_USER_ID)).thenReturn(2);

        // Act
        int result = notificationService.deleteReadNotifications(TEST_USER_ID);

        // Assert
        assertEquals(2, result);
        verify(notificationRepository).deleteReadNotificationsForUser(TEST_USER_ID);
    }

    @Test
    void deleteNotification_WhenNotificationExists_ShouldReturnTrue() {
        // Arrange
        when(notificationRepository.deleteNotificationForUser(1L, TEST_USER_ID)).thenReturn(1);

        // Act
        boolean result = notificationService.deleteNotification(1L, TEST_USER_ID);

        // Assert
        assertTrue(result);
        verify(notificationRepository).deleteNotificationForUser(1L, TEST_USER_ID);
    }

    @Test
    void deleteNotification_WhenNotificationNotFound_ShouldReturnFalse() {
        // Arrange
        when(notificationRepository.deleteNotificationForUser(1L, TEST_USER_ID)).thenReturn(0);

        // Act
        boolean result = notificationService.deleteNotification(1L, TEST_USER_ID);

        // Assert
        assertFalse(result);
        verify(notificationRepository).deleteNotificationForUser(1L, TEST_USER_ID);
    }

    @Test
    void getNotificationStats_ShouldReturnCorrectStats() {
        // Arrange
        when(notificationRepository.countByUserId(TEST_USER_ID)).thenReturn(5L);
        when(notificationRepository.countByUserIdAndReadFalse(TEST_USER_ID)).thenReturn(3L);
        when(notificationRepository.countByUserIdAndReadTrue(TEST_USER_ID)).thenReturn(2L);

        // Act
        NotificationService.NotificationStats result = notificationService.getNotificationStats(TEST_USER_ID);

        // Assert
        assertEquals(5L, result.getTotal());
        assertEquals(3L, result.getUnread());
        assertEquals(2L, result.getRead());
    }

    @Test
    void getNotificationForUser_WhenExists_ShouldReturnNotification() {
        // Arrange
        when(notificationRepository.findByIdAndUserId(1L, TEST_USER_ID))
            .thenReturn(Optional.of(testNotification));

        // Act
        Optional<Notification> result = notificationService.getNotificationForUser(1L, TEST_USER_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testNotification, result.get());
    }

    @Test
    void getNotificationForUser_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(notificationRepository.findByIdAndUserId(1L, TEST_USER_ID))
            .thenReturn(Optional.empty());

        // Act
        Optional<Notification> result = notificationService.getNotificationForUser(1L, TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void notificationExistsForUser_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(notificationRepository.existsByIdAndUserId(1L, TEST_USER_ID)).thenReturn(true);

        // Act
        boolean result = notificationService.notificationExistsForUser(1L, TEST_USER_ID);

        // Assert
        assertTrue(result);
    }

    @Test
    void getNotificationsByType_ShouldReturnFilteredNotifications() {
        // Arrange
        List<Notification> expectedNotifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdAndTypeOrderByTimestampDesc(TEST_USER_ID, "ticket_created"))
            .thenReturn(expectedNotifications);

        // Act
        List<Notification> result = notificationService.getNotificationsByType(TEST_USER_ID, "ticket_created");

        // Assert
        assertEquals(1, result.size());
        assertEquals("ticket_created", result.get(0).getType());
    }

    @Test
    void cleanupOldReadNotifications_ShouldReturnDeletedCount() {
        // Arrange
        when(notificationRepository.deleteOldReadNotifications(any(LocalDateTime.class))).thenReturn(5);

        // Act
        int result = notificationService.cleanupOldReadNotifications(30);

        // Assert
        assertEquals(5, result);
        verify(notificationRepository).deleteOldReadNotifications(any(LocalDateTime.class));
    }
}
