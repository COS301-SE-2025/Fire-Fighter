package com.apex.firefighter.integration.services;

import com.apex.firefighter.model.User;
import com.apex.firefighter.model.UserPreferences;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.repository.UserPreferencesRepository;
import com.apex.firefighter.service.UserPreferencesService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserPreferencesServiceIT {

    @Autowired
    private UserPreferencesService userPreferencesService;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    @Transactional
    void setup() {
        userPreferencesRepository.deleteAll();
        userRepository.deleteAll();
        user = new User("user1", "Normal User", "user1@example.com", "Medical Department");
        userRepository.save(user);
    }

    @Test
    void testGetUserPreferences() {
        UserPreferences prefs = userPreferencesService.getUserPreferences(user.getUserId());
        Assertions.assertNotNull(prefs);
        Assertions.assertEquals(user.getUserId(), prefs.getUserId());
    }

    @Test
    void testUpdateUserPreferences() {
        UserPreferences updated = userPreferencesService.updateUserPreferences(
            user.getUserId(), true, true, false, true, false
        );
        Assertions.assertTrue(updated.getEmailNotificationsEnabled());
        Assertions.assertTrue(updated.getEmailTicketCreation());
        Assertions.assertFalse(updated.getEmailTicketCompletion());
        Assertions.assertTrue(updated.getEmailTicketRevocation());
        Assertions.assertFalse(updated.getEmailFiveMinuteWarning());
    }

    @Test
    void testEnableAllEmailNotifications() {
        UserPreferences enabled = userPreferencesService.enableAllEmailNotifications(user.getUserId());
        Assertions.assertTrue(enabled.getEmailNotificationsEnabled());
        Assertions.assertTrue(enabled.getEmailTicketCreation());
        Assertions.assertTrue(enabled.getEmailTicketCompletion());
        Assertions.assertTrue(enabled.getEmailTicketRevocation());
        Assertions.assertTrue(enabled.getEmailFiveMinuteWarning());
    }

    @Test
    void testDisableAllEmailNotifications() {
        UserPreferences disabled = userPreferencesService.disableAllEmailNotifications(user.getUserId());
        Assertions.assertFalse(disabled.getEmailNotificationsEnabled());
        Assertions.assertFalse(disabled.getEmailTicketCreation());
        Assertions.assertFalse(disabled.getEmailTicketCompletion());
        Assertions.assertFalse(disabled.getEmailTicketRevocation());
        Assertions.assertFalse(disabled.getEmailFiveMinuteWarning());
    }

    @Test
    void testResetToDefault() {
        userPreferencesService.enableAllEmailNotifications(user.getUserId());
        UserPreferences reset = userPreferencesService.resetToDefault(user.getUserId());
        Assertions.assertFalse(reset.getEmailNotificationsEnabled());
        Assertions.assertFalse(reset.getEmailTicketCreation());
        Assertions.assertFalse(reset.getEmailTicketCompletion());
        Assertions.assertFalse(reset.getEmailTicketRevocation());
        Assertions.assertFalse(reset.getEmailFiveMinuteWarning());
    }

    @Test
    void testIndividualFlagsGetters() {
        userPreferencesService.updateUserPreferences(user.getUserId(), true, true, false, true, false);
        Assertions.assertTrue(userPreferencesService.isEmailNotificationsEnabled(user.getUserId()));
        Assertions.assertTrue(userPreferencesService.isTicketCreationEmailEnabled(user.getUserId()));
        Assertions.assertFalse(userPreferencesService.isTicketCompletionEmailEnabled(user.getUserId()));
        Assertions.assertTrue(userPreferencesService.isTicketRevocationEmailEnabled(user.getUserId()));
        Assertions.assertFalse(userPreferencesService.isFiveMinuteWarningEmailEnabled(user.getUserId()));
    }
}