package com.apex.firefighter.unit.models;

import com.apex.firefighter.model.UserPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserPreferencesTest {

    private UserPreferences userPreferences;

    @BeforeEach
    void setUp() {
        userPreferences = new UserPreferences();
    }

    @Test
    void defaultConstructor_ShouldInitializeWithDefaults() {
        // Assert
        assertThat(userPreferences.getUserId()).isNull();
        assertThat(userPreferences.getEmailNotificationsEnabled()).isFalse();
        assertThat(userPreferences.getEmailTicketCreation()).isFalse();
        assertThat(userPreferences.getEmailTicketCompletion()).isFalse();
        assertThat(userPreferences.getEmailTicketRevocation()).isFalse();
        assertThat(userPreferences.getEmailFiveMinuteWarning()).isFalse();
        assertThat(userPreferences.getCreatedAt()).isNotNull();
        assertThat(userPreferences.getUpdatedAt()).isNotNull();
    }

    @Test
    void parameterizedConstructor_WithUserId_ShouldInitializeUserId() {
        // Arrange
        String userId = "test-user-123";

        // Act
        UserPreferences paramUserPreferences = new UserPreferences(userId);

        // Assert
        assertThat(paramUserPreferences.getUserId()).isEqualTo(userId);
        assertThat(paramUserPreferences.getEmailNotificationsEnabled()).isFalse();
        assertThat(paramUserPreferences.getEmailTicketCreation()).isFalse();
        assertThat(paramUserPreferences.getEmailTicketCompletion()).isFalse();
        assertThat(paramUserPreferences.getEmailTicketRevocation()).isFalse();
        assertThat(paramUserPreferences.getEmailFiveMinuteWarning()).isFalse();
        assertThat(paramUserPreferences.getCreatedAt()).isNotNull();
        assertThat(paramUserPreferences.getUpdatedAt()).isNotNull();
    }

    @Test
    void parameterizedConstructor_WithAllPreferences_ShouldInitializeAllFields() {
        // Arrange
        String userId = "test-user-123";
        Boolean emailNotificationsEnabled = true;
        Boolean emailTicketCreation = true;
        Boolean emailTicketCompletion = false;
        Boolean emailTicketRevocation = true;
        Boolean emailFiveMinuteWarning = false;

        // Act
        UserPreferences paramUserPreferences = new UserPreferences(
            userId, emailNotificationsEnabled, emailTicketCreation, 
            emailTicketCompletion, emailTicketRevocation, emailFiveMinuteWarning
        );

        // Assert
        assertThat(paramUserPreferences.getUserId()).isEqualTo(userId);
        assertThat(paramUserPreferences.getEmailNotificationsEnabled()).isEqualTo(emailNotificationsEnabled);
        assertThat(paramUserPreferences.getEmailTicketCreation()).isEqualTo(emailTicketCreation);
        assertThat(paramUserPreferences.getEmailTicketCompletion()).isEqualTo(emailTicketCompletion);
        assertThat(paramUserPreferences.getEmailTicketRevocation()).isEqualTo(emailTicketRevocation);
        assertThat(paramUserPreferences.getEmailFiveMinuteWarning()).isEqualTo(emailFiveMinuteWarning);
        assertThat(paramUserPreferences.getCreatedAt()).isNotNull();
        assertThat(paramUserPreferences.getUpdatedAt()).isNotNull();
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        String userId = "test-user-123";
        Boolean emailNotificationsEnabled = true;
        Boolean emailTicketCreation = true;
        Boolean emailTicketCompletion = true;
        Boolean emailTicketRevocation = false;
        Boolean emailFiveMinuteWarning = true;
        ZonedDateTime createdAt = ZonedDateTime.now().minusDays(1);
        ZonedDateTime updatedAt = ZonedDateTime.now();

        // Act
        userPreferences.setUserId(userId);
        userPreferences.setEmailNotificationsEnabled(emailNotificationsEnabled);
        userPreferences.setEmailTicketCreation(emailTicketCreation);
        userPreferences.setEmailTicketCompletion(emailTicketCompletion);
        userPreferences.setEmailTicketRevocation(emailTicketRevocation);
        userPreferences.setEmailFiveMinuteWarning(emailFiveMinuteWarning);
        userPreferences.setCreatedAt(createdAt);
        userPreferences.setUpdatedAt(updatedAt);

        // Assert
        assertThat(userPreferences.getUserId()).isEqualTo(userId);
        assertThat(userPreferences.getEmailNotificationsEnabled()).isEqualTo(emailNotificationsEnabled);
        assertThat(userPreferences.getEmailTicketCreation()).isEqualTo(emailTicketCreation);
        assertThat(userPreferences.getEmailTicketCompletion()).isEqualTo(emailTicketCompletion);
        assertThat(userPreferences.getEmailTicketRevocation()).isEqualTo(emailTicketRevocation);
        assertThat(userPreferences.getEmailFiveMinuteWarning()).isEqualTo(emailFiveMinuteWarning);
        assertThat(userPreferences.getCreatedAt()).isEqualTo(createdAt);
        assertThat(userPreferences.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void allFields_ShouldAcceptNullValues() {
        // Act
        userPreferences.setUserId(null);
        userPreferences.setEmailNotificationsEnabled(null);
        userPreferences.setEmailTicketCreation(null);
        userPreferences.setEmailTicketCompletion(null);
        userPreferences.setEmailTicketRevocation(null);
        userPreferences.setEmailFiveMinuteWarning(null);
        userPreferences.setCreatedAt(null);
        userPreferences.setUpdatedAt(null);

        // Assert
        assertThat(userPreferences.getUserId()).isNull();
        assertThat(userPreferences.getEmailNotificationsEnabled()).isNull();
        assertThat(userPreferences.getEmailTicketCreation()).isNull();
        assertThat(userPreferences.getEmailTicketCompletion()).isNull();
        assertThat(userPreferences.getEmailTicketRevocation()).isNull();
        assertThat(userPreferences.getEmailFiveMinuteWarning()).isNull();
        assertThat(userPreferences.getCreatedAt()).isNull();
        assertThat(userPreferences.getUpdatedAt()).isNull();
    }

    @Test
    void createdAt_ShouldBeSetOnConstruction() {
        // Arrange
        ZonedDateTime beforeCreation = ZonedDateTime.now().minusSeconds(1);
        
        // Act
        UserPreferences newUserPreferences = new UserPreferences();
        ZonedDateTime afterCreation = ZonedDateTime.now().plusSeconds(1);

        // Assert
        assertThat(newUserPreferences.getCreatedAt()).isAfter(beforeCreation);
        assertThat(newUserPreferences.getCreatedAt()).isBefore(afterCreation);
    }

    @Test
    void updatedAt_ShouldBeSetOnConstruction() {
        // Arrange
        ZonedDateTime beforeCreation = ZonedDateTime.now().minusSeconds(1);
        
        // Act
        UserPreferences newUserPreferences = new UserPreferences();
        ZonedDateTime afterCreation = ZonedDateTime.now().plusSeconds(1);

        // Assert
        assertThat(newUserPreferences.getUpdatedAt()).isAfter(beforeCreation);
        assertThat(newUserPreferences.getUpdatedAt()).isBefore(afterCreation);
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        // Act
        UserPreferences nullUserPreferences = new UserPreferences(null, null, null, null, null, null);

        // Assert
        assertThat(nullUserPreferences.getUserId()).isNull();
        assertThat(nullUserPreferences.getEmailNotificationsEnabled()).isNull();
        assertThat(nullUserPreferences.getEmailTicketCreation()).isNull();
        assertThat(nullUserPreferences.getEmailTicketCompletion()).isNull();
        assertThat(nullUserPreferences.getEmailTicketRevocation()).isNull();
        assertThat(nullUserPreferences.getEmailFiveMinuteWarning()).isNull();
        assertThat(nullUserPreferences.getCreatedAt()).isNotNull();
        assertThat(nullUserPreferences.getUpdatedAt()).isNotNull();
    }

    @Test
    void booleanFields_ShouldAcceptTrueAndFalse() {
        // Act & Assert
        userPreferences.setEmailNotificationsEnabled(true);
        assertThat(userPreferences.getEmailNotificationsEnabled()).isTrue();

        userPreferences.setEmailNotificationsEnabled(false);
        assertThat(userPreferences.getEmailNotificationsEnabled()).isFalse();

        userPreferences.setEmailTicketCreation(true);
        assertThat(userPreferences.getEmailTicketCreation()).isTrue();

        userPreferences.setEmailTicketCreation(false);
        assertThat(userPreferences.getEmailTicketCreation()).isFalse();
    }

    @Test
    void userId_ShouldBeSettable() {
        // Arrange
        String userId = "new-user-456";

        // Act
        userPreferences.setUserId(userId);

        // Assert
        assertThat(userPreferences.getUserId()).isEqualTo(userId);
    }

    @Test
    void createdAt_ShouldBeSettable() {
        // Arrange
        ZonedDateTime createdAt = ZonedDateTime.now().minusDays(5);

        // Act
        userPreferences.setCreatedAt(createdAt);

        // Assert
        assertThat(userPreferences.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void updatedAt_ShouldBeSettable() {
        // Arrange
        ZonedDateTime updatedAt = ZonedDateTime.now().minusHours(2);

        // Act
        userPreferences.setUpdatedAt(updatedAt);

        // Assert
        assertThat(userPreferences.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void emailPreferences_ShouldBeIndividuallySettable() {
        // Act & Assert
        userPreferences.setEmailTicketCreation(true);
        assertThat(userPreferences.getEmailTicketCreation()).isTrue();

        userPreferences.setEmailTicketCompletion(true);
        assertThat(userPreferences.getEmailTicketCompletion()).isTrue();

        userPreferences.setEmailTicketRevocation(true);
        assertThat(userPreferences.getEmailTicketRevocation()).isTrue();

        userPreferences.setEmailFiveMinuteWarning(true);
        assertThat(userPreferences.getEmailFiveMinuteWarning()).isTrue();
    }

    @Test
    void parameterizedConstructor_WithPartialNullValues_ShouldHandleGracefully() {
        // Act
        UserPreferences partialNullUserPreferences = new UserPreferences(
            "test-user", true, null, false, null, true
        );

        // Assert
        assertThat(partialNullUserPreferences.getUserId()).isEqualTo("test-user");
        assertThat(partialNullUserPreferences.getEmailNotificationsEnabled()).isTrue();
        assertThat(partialNullUserPreferences.getEmailTicketCreation()).isNull();
        assertThat(partialNullUserPreferences.getEmailTicketCompletion()).isFalse();
        assertThat(partialNullUserPreferences.getEmailTicketRevocation()).isNull();
        assertThat(partialNullUserPreferences.getEmailFiveMinuteWarning()).isTrue();
    }
}
