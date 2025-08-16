package com.apex.firefighter.integration.services;

import com.apex.firefighter.model.Notification;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.NotificationRepository;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.NotificationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NotificationServiceIT {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Notification notification1;
    private Notification notification2;

    @BeforeEach
    @Transactional
    void setup() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();

        user = new User("user1", "Normal User", "user1@example.com", "Medical Department");
        userRepository.save(user);

        notification1 = notificationService.createNotification(
            user.getUserId(),
            "ticket_created",
            "Ticket Created",
            "Your ticket has been created."
        );
        notification2 = notificationService.createNotification(
            user.getUserId(),
            "ticket_revoked",
            "Ticket Revoked",
            "Your ticket has been revoked."
        );
    }

    @Test
    void testGetNotificationsForUser() {
        List<Notification> notifications = notificationService.getNotificationsForUser(user.getUserId());
        Assertions.assertEquals(2, notifications.size());
    }

    @Test
    void testGetUnreadNotificationsForUser() {
        List<Notification> unread = notificationService.getUnreadNotificationsForUser(user.getUserId());
        Assertions.assertEquals(2, unread.size());
    }

    @Test
    void testMarkNotificationAsRead() {
        boolean marked = notificationService.markNotificationAsRead(notification1.getId(), user.getUserId());
        Assertions.assertTrue(marked);

        Notification updated = notificationRepository.findById(notification1.getId()).orElseThrow();
        Assertions.assertTrue(updated.isRead());
    }

    @Test
    void testMarkAllNotificationsAsRead() {
        int updatedCount = notificationService.markAllNotificationsAsRead(user.getUserId());
        Assertions.assertEquals(2, updatedCount);

        List<Notification> unread = notificationService.getUnreadNotificationsForUser(user.getUserId());
        Assertions.assertEquals(0, unread.size());
    }

    @Test
    void testDeleteReadNotifications() {
        // Mark both as read first
        notificationService.markAllNotificationsAsRead(user.getUserId());
        int deletedCount = notificationService.deleteReadNotifications(user.getUserId());
        Assertions.assertEquals(2, deletedCount);

        List<Notification> all = notificationService.getNotificationsForUser(user.getUserId());
        Assertions.assertEquals(0, all.size());
    }

    @Test
    void testDeleteNotification() {
        boolean deleted = notificationService.deleteNotification(notification1.getId(), user.getUserId());
        Assertions.assertTrue(deleted);

        Optional<Notification> found = notificationRepository.findById(notification1.getId());
        Assertions.assertFalse(found.isPresent());
    }

    @Test
    void testGetNotificationForUser() {
        Optional<Notification> found = notificationService.getNotificationForUser(notification2.getId(), user.getUserId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(notification2.getId(), found.get().getId());
    }
}