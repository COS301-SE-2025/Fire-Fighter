package com.apex.firefighter.unit.repositories;

import com.apex.firefighter.model.Notification;
import com.apex.firefighter.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification notification1;
    private Notification notification2;
    private Notification notification3;
    private final String TEST_USER_ID = "test-user-123";
    private final String OTHER_USER_ID = "other-user-456";

    @BeforeEach
    void setUp() {
        // Create test notifications
        notification1 = new Notification();
        notification1.setUserId(TEST_USER_ID);
        notification1.setType("ticket_created");
        notification1.setTitle("Ticket Created");
        notification1.setMessage("Your ticket has been created");
        notification1.setTimestamp(LocalDateTime.now().minusHours(2));
        notification1.setRead(false);
        notification1.setTicketId("TICKET-001");

        notification2 = new Notification();
        notification2.setUserId(TEST_USER_ID);
        notification2.setType("request_completed");
        notification2.setTitle("Request Completed");
        notification2.setMessage("Your request has been completed");
        notification2.setTimestamp(LocalDateTime.now().minusHours(1));
        notification2.setRead(true);
        notification2.setTicketId("TICKET-001");

        notification3 = new Notification();
        notification3.setUserId(OTHER_USER_ID);
        notification3.setType("ticket_created");
        notification3.setTitle("Other User Ticket");
        notification3.setMessage("Other user's ticket");
        notification3.setTimestamp(LocalDateTime.now());
        notification3.setRead(false);
        notification3.setTicketId("TICKET-002");

        // Persist test data
        entityManager.persistAndFlush(notification1);
        entityManager.persistAndFlush(notification2);
        entityManager.persistAndFlush(notification3);
    }

    @Test
    void findByUserIdOrderByTimestampDesc_ShouldReturnUserNotificationsOrderedByTimestamp() {
        // Act
        List<Notification> result = notificationRepository.findByUserIdOrderByTimestampDesc(TEST_USER_ID);

        // Assert
        assertEquals(2, result.size());
        assertEquals(notification2.getId(), result.get(0).getId()); // Most recent first
        assertEquals(notification1.getId(), result.get(1).getId());
    }

    @Test
    void findByUserIdAndReadFalseOrderByTimestampDesc_ShouldReturnUnreadNotifications() {
        // Act
        List<Notification> result = notificationRepository.findByUserIdAndReadFalseOrderByTimestampDesc(TEST_USER_ID);

        // Assert
        assertEquals(1, result.size());
        assertEquals(notification1.getId(), result.get(0).getId());
        assertFalse(result.get(0).getRead());
    }

    @Test
    void findByUserIdAndReadTrueOrderByTimestampDesc_ShouldReturnReadNotifications() {
        // Act
        List<Notification> result = notificationRepository.findByUserIdAndReadTrueOrderByTimestampDesc(TEST_USER_ID);

        // Assert
        assertEquals(1, result.size());
        assertEquals(notification2.getId(), result.get(0).getId());
        assertTrue(result.get(0).getRead());
    }

    @Test
    void findByUserIdAndTypeOrderByTimestampDesc_ShouldReturnNotificationsByType() {
        // Act
        List<Notification> result = notificationRepository.findByUserIdAndTypeOrderByTimestampDesc(TEST_USER_ID, "ticket_created");

        // Assert
        assertEquals(1, result.size());
        assertEquals(notification1.getId(), result.get(0).getId());
        assertEquals("ticket_created", result.get(0).getType());
    }

    @Test
    void findByTicketIdOrderByTimestampDesc_ShouldReturnNotificationsForTicket() {
        // Act
        List<Notification> result = notificationRepository.findByTicketIdOrderByTimestampDesc("TICKET-001");

        // Assert
        assertEquals(2, result.size());
        assertEquals(notification2.getId(), result.get(0).getId()); // Most recent first
        assertEquals(notification1.getId(), result.get(1).getId());
    }

    @Test
    void findByUserIdAndTicketIdOrderByTimestampDesc_ShouldReturnUserTicketNotifications() {
        // Act
        List<Notification> result = notificationRepository.findByUserIdAndTicketIdOrderByTimestampDesc(TEST_USER_ID, "TICKET-001");

        // Assert
        assertEquals(2, result.size());
        assertEquals(notification2.getId(), result.get(0).getId());
        assertEquals(notification1.getId(), result.get(1).getId());
    }

    @Test
    void countByUserId_ShouldReturnTotalNotificationCount() {
        // Act
        long count = notificationRepository.countByUserId(TEST_USER_ID);

        // Assert
        assertEquals(2, count);
    }

    @Test
    void countByUserIdAndReadFalse_ShouldReturnUnreadCount() {
        // Act
        long count = notificationRepository.countByUserIdAndReadFalse(TEST_USER_ID);

        // Assert
        assertEquals(1, count);
    }

    @Test
    void countByUserIdAndReadTrue_ShouldReturnReadCount() {
        // Act
        long count = notificationRepository.countByUserIdAndReadTrue(TEST_USER_ID);

        // Assert
        assertEquals(1, count);
    }

    @Test
    void markAsRead_ShouldUpdateNotificationReadStatus() {
        // Act
        int updatedRows = notificationRepository.markAsRead(notification1.getId(), TEST_USER_ID);

        // Assert
        assertEquals(1, updatedRows);
        
        // Verify the notification is marked as read
        entityManager.clear(); // Clear persistence context to force reload
        Optional<Notification> updated = notificationRepository.findById(notification1.getId());
        assertTrue(updated.isPresent());
        assertTrue(updated.get().getRead());
    }

    @Test
    void markAsRead_WithWrongUser_ShouldNotUpdate() {
        // Act
        int updatedRows = notificationRepository.markAsRead(notification1.getId(), OTHER_USER_ID);

        // Assert
        assertEquals(0, updatedRows);
        
        // Verify the notification is still unread
        entityManager.clear();
        Optional<Notification> unchanged = notificationRepository.findById(notification1.getId());
        assertTrue(unchanged.isPresent());
        assertFalse(unchanged.get().getRead());
    }

    @Test
    void markAllAsReadForUser_ShouldUpdateAllUnreadNotifications() {
        // Act
        int updatedRows = notificationRepository.markAllAsReadForUser(TEST_USER_ID);

        // Assert
        assertEquals(1, updatedRows); // Only notification1 was unread
        
        // Verify all notifications are now read
        entityManager.clear();
        List<Notification> userNotifications = notificationRepository.findByUserIdOrderByTimestampDesc(TEST_USER_ID);
        assertTrue(userNotifications.stream().allMatch(Notification::getRead));
    }

    @Test
    void deleteReadNotificationsForUser_ShouldDeleteOnlyReadNotifications() {
        // Act
        int deletedRows = notificationRepository.deleteReadNotificationsForUser(TEST_USER_ID);

        // Assert
        assertEquals(1, deletedRows); // Only notification2 was read
        
        // Verify only unread notifications remain
        entityManager.clear();
        List<Notification> remaining = notificationRepository.findByUserIdOrderByTimestampDesc(TEST_USER_ID);
        assertEquals(1, remaining.size());
        assertEquals(notification1.getId(), remaining.get(0).getId());
        assertFalse(remaining.get(0).getRead());
    }

    @Test
    void deleteNotificationForUser_ShouldDeleteSpecificNotification() {
        // Act
        int deletedRows = notificationRepository.deleteNotificationForUser(notification1.getId(), TEST_USER_ID);

        // Assert
        assertEquals(1, deletedRows);
        
        // Verify notification is deleted
        entityManager.clear();
        Optional<Notification> deleted = notificationRepository.findById(notification1.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void deleteNotificationForUser_WithWrongUser_ShouldNotDelete() {
        // Act
        int deletedRows = notificationRepository.deleteNotificationForUser(notification1.getId(), OTHER_USER_ID);

        // Assert
        assertEquals(0, deletedRows);
        
        // Verify notification still exists
        entityManager.clear();
        Optional<Notification> stillExists = notificationRepository.findById(notification1.getId());
        assertTrue(stillExists.isPresent());
    }

    @Test
    void existsByIdAndUserId_WhenExists_ShouldReturnTrue() {
        // Act
        boolean exists = notificationRepository.existsByIdAndUserId(notification1.getId(), TEST_USER_ID);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByIdAndUserId_WhenNotExists_ShouldReturnFalse() {
        // Act
        boolean exists = notificationRepository.existsByIdAndUserId(notification1.getId(), OTHER_USER_ID);

        // Assert
        assertFalse(exists);
    }

    @Test
    void findByIdAndUserId_WhenExists_ShouldReturnNotification() {
        // Act
        Optional<Notification> result = notificationRepository.findByIdAndUserId(notification1.getId(), TEST_USER_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(notification1.getId(), result.get().getId());
    }

    @Test
    void findByIdAndUserId_WhenNotExists_ShouldReturnEmpty() {
        // Act
        Optional<Notification> result = notificationRepository.findByIdAndUserId(notification1.getId(), OTHER_USER_ID);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void deleteOldReadNotifications_ShouldDeleteOldReadNotifications() {
        // Create a new old read notification
        Notification oldNotification = new Notification();
        oldNotification.setUserId(TEST_USER_ID);
        oldNotification.setType("old_notification");
        oldNotification.setTitle("Old Notification");
        oldNotification.setMessage("This is an old notification");
        oldNotification.setTimestamp(LocalDateTime.now().minusDays(31));
        oldNotification.setRead(true);
        oldNotification.setTicketId("OLD-TICKET");

        entityManager.persistAndFlush(oldNotification);

        // Act
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        int deletedRows = notificationRepository.deleteOldReadNotifications(cutoffDate);

        // Assert
        assertEquals(1, deletedRows);

        // Verify old read notification is deleted
        entityManager.clear();
        Optional<Notification> deleted = notificationRepository.findById(oldNotification.getId());
        assertFalse(deleted.isPresent());
    }
}
