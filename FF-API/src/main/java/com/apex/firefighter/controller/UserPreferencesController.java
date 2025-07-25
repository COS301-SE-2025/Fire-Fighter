package com.apex.firefighter.controller;

import com.apex.firefighter.model.UserPreferences;
import com.apex.firefighter.service.UserPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user-preferences")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200", "http://localhost:8100", "http://127.0.0.1:8100", "ionic://localhost", "capacitor://localhost"})
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;

    @Autowired
    public UserPreferencesController(UserPreferencesService userPreferencesService) {
        this.userPreferencesService = userPreferencesService;
    }

    /**
     * Get user preferences by user ID
     * GET /api/user-preferences/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserPreferences(@PathVariable String userId) {
        try {
            System.out.println("🔵 GET USER PREFERENCES: Request for user - " + userId);
            
            UserPreferences preferences = userPreferencesService.getUserPreferences(userId);
            
            System.out.println("✅ USER PREFERENCES RETRIEVED: " + preferences);
            return ResponseEntity.ok(preferences);
            
        } catch (Exception e) {
            System.err.println("❌ GET USER PREFERENCES FAILED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to retrieve user preferences: " + e.getMessage());
        }
    }

    /**
     * Update user preferences
     * PUT /api/user-preferences/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserPreferences(
            @PathVariable String userId,
            @RequestBody Map<String, Object> preferencesData) {
        try {
            System.out.println("🔵 UPDATE USER PREFERENCES: Request for user - " + userId);
            System.out.println("🔵 PREFERENCES DATA: " + preferencesData);
            
            // Extract preferences from request body
            Boolean emailNotificationsEnabled = (Boolean) preferencesData.get("emailNotificationsEnabled");
            Boolean emailTicketCreation = (Boolean) preferencesData.get("emailTicketCreation");
            Boolean emailTicketCompletion = (Boolean) preferencesData.get("emailTicketCompletion");
            Boolean emailTicketRevocation = (Boolean) preferencesData.get("emailTicketRevocation");
            Boolean emailFiveMinuteWarning = (Boolean) preferencesData.get("emailFiveMinuteWarning");
            
            UserPreferences updatedPreferences = userPreferencesService.updateUserPreferences(
                userId, 
                emailNotificationsEnabled,
                emailTicketCreation,
                emailTicketCompletion,
                emailTicketRevocation,
                emailFiveMinuteWarning
            );
            
            System.out.println("✅ USER PREFERENCES UPDATED: " + updatedPreferences);
            return ResponseEntity.ok(updatedPreferences);
            
        } catch (Exception e) {
            System.err.println("❌ UPDATE USER PREFERENCES FAILED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to update user preferences: " + e.getMessage());
        }
    }

    /**
     * Update specific preference setting
     * PATCH /api/user-preferences/{userId}/{setting}
     */
    @PatchMapping("/{userId}/{setting}")
    public ResponseEntity<?> updateSpecificPreference(
            @PathVariable String userId,
            @PathVariable String setting,
            @RequestBody Map<String, Boolean> requestBody) {
        try {
            System.out.println("🔵 UPDATE SPECIFIC PREFERENCE: " + setting + " for user - " + userId);
            
            Boolean value = requestBody.get("enabled");
            if (value == null) {
                return ResponseEntity.badRequest().body("Missing 'enabled' field in request body");
            }
            
            UserPreferences updatedPreferences;
            
            switch (setting.toLowerCase()) {
                case "email-notifications":
                    updatedPreferences = userPreferencesService.updateUserPreferences(userId, value, null, null, null, null);
                    break;
                case "ticket-creation":
                    updatedPreferences = userPreferencesService.updateUserPreferences(userId, null, value, null, null, null);
                    break;
                case "ticket-completion":
                    updatedPreferences = userPreferencesService.updateUserPreferences(userId, null, null, value, null, null);
                    break;
                case "ticket-revocation":
                    updatedPreferences = userPreferencesService.updateUserPreferences(userId, null, null, null, value, null);
                    break;
                case "five-minute-warning":
                    updatedPreferences = userPreferencesService.updateUserPreferences(userId, null, null, null, null, value);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid setting: " + setting);
            }
            
            System.out.println("✅ SPECIFIC PREFERENCE UPDATED: " + updatedPreferences);
            return ResponseEntity.ok(updatedPreferences);
            
        } catch (Exception e) {
            System.err.println("❌ UPDATE SPECIFIC PREFERENCE FAILED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to update preference: " + e.getMessage());
        }
    }

    /**
     * Enable all email notifications for a user
     * POST /api/user-preferences/{userId}/enable-all
     */
    @PostMapping("/{userId}/enable-all")
    public ResponseEntity<?> enableAllEmailNotifications(@PathVariable String userId) {
        try {
            System.out.println("🔵 ENABLE ALL EMAIL NOTIFICATIONS: Request for user - " + userId);
            
            UserPreferences updatedPreferences = userPreferencesService.enableAllEmailNotifications(userId);
            
            System.out.println("✅ ALL EMAIL NOTIFICATIONS ENABLED: " + updatedPreferences);
            return ResponseEntity.ok(updatedPreferences);
            
        } catch (Exception e) {
            System.err.println("❌ ENABLE ALL EMAIL NOTIFICATIONS FAILED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to enable all email notifications: " + e.getMessage());
        }
    }

    /**
     * Disable all email notifications for a user
     * POST /api/user-preferences/{userId}/disable-all
     */
    @PostMapping("/{userId}/disable-all")
    public ResponseEntity<?> disableAllEmailNotifications(@PathVariable String userId) {
        try {
            System.out.println("🔵 DISABLE ALL EMAIL NOTIFICATIONS: Request for user - " + userId);
            
            UserPreferences updatedPreferences = userPreferencesService.disableAllEmailNotifications(userId);
            
            System.out.println("✅ ALL EMAIL NOTIFICATIONS DISABLED: " + updatedPreferences);
            return ResponseEntity.ok(updatedPreferences);
            
        } catch (Exception e) {
            System.err.println("❌ DISABLE ALL EMAIL NOTIFICATIONS FAILED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to disable all email notifications: " + e.getMessage());
        }
    }

    /**
     * Reset user preferences to default
     * POST /api/user-preferences/{userId}/reset
     */
    @PostMapping("/{userId}/reset")
    public ResponseEntity<?> resetUserPreferences(@PathVariable String userId) {
        try {
            System.out.println("🔵 RESET USER PREFERENCES: Request for user - " + userId);
            
            UserPreferences resetPreferences = userPreferencesService.resetToDefault(userId);
            
            System.out.println("✅ USER PREFERENCES RESET: " + resetPreferences);
            return ResponseEntity.ok(resetPreferences);
            
        } catch (Exception e) {
            System.err.println("❌ RESET USER PREFERENCES FAILED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to reset user preferences: " + e.getMessage());
        }
    }

    /**
     * Check if user has specific email notification enabled
     * GET /api/user-preferences/{userId}/check/{setting}
     */
    @GetMapping("/{userId}/check/{setting}")
    public ResponseEntity<?> checkSpecificPreference(
            @PathVariable String userId,
            @PathVariable String setting) {
        try {
            System.out.println("🔵 CHECK SPECIFIC PREFERENCE: " + setting + " for user - " + userId);
            
            boolean enabled;
            
            switch (setting.toLowerCase()) {
                case "email-notifications":
                    enabled = userPreferencesService.isEmailNotificationsEnabled(userId);
                    break;
                case "ticket-creation":
                    enabled = userPreferencesService.isTicketCreationEmailEnabled(userId);
                    break;
                case "ticket-completion":
                    enabled = userPreferencesService.isTicketCompletionEmailEnabled(userId);
                    break;
                case "ticket-revocation":
                    enabled = userPreferencesService.isTicketRevocationEmailEnabled(userId);
                    break;
                case "five-minute-warning":
                    enabled = userPreferencesService.isFiveMinuteWarningEmailEnabled(userId);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid setting: " + setting);
            }
            
            System.out.println("✅ PREFERENCE CHECK RESULT: " + setting + " = " + enabled);
            return ResponseEntity.ok(Map.of("setting", setting, "enabled", enabled));
            
        } catch (Exception e) {
            System.err.println("❌ CHECK SPECIFIC PREFERENCE FAILED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to check preference: " + e.getMessage());
        }
    }
}
