package com.apex.firefighter.service;

import com.apex.firefighter.model.UserPreferences;
import com.apex.firefighter.repository.UserPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;

    @Autowired
    public UserPreferencesService(UserPreferencesRepository userPreferencesRepository) {
        this.userPreferencesRepository = userPreferencesRepository;
    }

    /**
     * Get user preferences by user ID, create default if not exists
     */
    public UserPreferences getUserPreferences(String userId) {
        System.out.println("🔵 GET USER PREFERENCES: Fetching preferences for user - " + userId);
        
        Optional<UserPreferences> preferences = userPreferencesRepository.findByUserId(userId);
        if (preferences.isPresent()) {
            System.out.println("✅ PREFERENCES FOUND: " + preferences.get());
            return preferences.get();
        } else {
            // Create default preferences for new user
            UserPreferences defaultPreferences = new UserPreferences(userId);
            UserPreferences savedPreferences = userPreferencesRepository.save(defaultPreferences);
            System.out.println("✅ DEFAULT PREFERENCES CREATED: " + savedPreferences);
            return savedPreferences;
        }
    }

    /**
     * Update user preferences
     */
    public UserPreferences updateUserPreferences(String userId, Boolean emailNotificationsEnabled,
                                                Boolean emailTicketCreation, Boolean emailTicketCompletion,
                                                Boolean emailTicketRevocation, Boolean emailFiveMinuteWarning) {
        System.out.println("🔵 UPDATE USER PREFERENCES: Updating preferences for user - " + userId);
        
        UserPreferences preferences = getUserPreferences(userId); // This will create if not exists
        
        if (emailNotificationsEnabled != null) {
            preferences.setEmailNotificationsEnabled(emailNotificationsEnabled);
        }
        if (emailTicketCreation != null) {
            preferences.setEmailTicketCreation(emailTicketCreation);
        }
        if (emailTicketCompletion != null) {
            preferences.setEmailTicketCompletion(emailTicketCompletion);
        }
        if (emailTicketRevocation != null) {
            preferences.setEmailTicketRevocation(emailTicketRevocation);
        }
        if (emailFiveMinuteWarning != null) {
            preferences.setEmailFiveMinuteWarning(emailFiveMinuteWarning);
        }
        
        UserPreferences updatedPreferences = userPreferencesRepository.save(preferences);
        System.out.println("✅ PREFERENCES UPDATED: " + updatedPreferences);
        return updatedPreferences;
    }

    /**
     * Check if user has email notifications enabled
     */
    public boolean isEmailNotificationsEnabled(String userId) {
        Optional<UserPreferences> preferences = userPreferencesRepository.findByUserId(userId);
        return preferences.map(UserPreferences::isEmailNotificationsEnabled).orElse(false);
    }

    /**
     * Check if user has ticket creation email notifications enabled
     */
    public boolean isTicketCreationEmailEnabled(String userId) {
        return userPreferencesRepository.hasTicketCreationEmailEnabled(userId);
    }

    /**
     * Check if user has ticket completion email notifications enabled
     */
    public boolean isTicketCompletionEmailEnabled(String userId) {
        return userPreferencesRepository.hasTicketCompletionEmailEnabled(userId);
    }

    /**
     * Check if user has ticket revocation email notifications enabled
     */
    public boolean isTicketRevocationEmailEnabled(String userId) {
        return userPreferencesRepository.hasTicketRevocationEmailEnabled(userId);
    }

    /**
     * Check if user has five-minute warning email notifications enabled
     */
    public boolean isFiveMinuteWarningEmailEnabled(String userId) {
        return userPreferencesRepository.hasFiveMinuteWarningEmailEnabled(userId);
    }

    /**
     * Enable all email notifications for a user
     */
    public UserPreferences enableAllEmailNotifications(String userId) {
        System.out.println("🔵 ENABLE ALL EMAIL NOTIFICATIONS: Enabling all for user - " + userId);
        return updateUserPreferences(userId, true, true, true, true, true);
    }

    /**
     * Disable all email notifications for a user
     */
    public UserPreferences disableAllEmailNotifications(String userId) {
        System.out.println("🔵 DISABLE ALL EMAIL NOTIFICATIONS: Disabling all for user - " + userId);
        return updateUserPreferences(userId, false, false, false, false, false);
    }

    /**
     * Reset user preferences to default
     */
    public UserPreferences resetToDefault(String userId) {
        System.out.println("🔵 RESET PREFERENCES: Resetting to default for user - " + userId);
        
        Optional<UserPreferences> existingPreferences = userPreferencesRepository.findByUserId(userId);
        if (existingPreferences.isPresent()) {
            userPreferencesRepository.delete(existingPreferences.get());
        }
        
        UserPreferences defaultPreferences = new UserPreferences(userId);
        UserPreferences savedPreferences = userPreferencesRepository.save(defaultPreferences);
        System.out.println("✅ PREFERENCES RESET: " + savedPreferences);
        return savedPreferences;
    }
}
