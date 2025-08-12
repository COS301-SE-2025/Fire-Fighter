package com.apex.firefighter.unit.services;

import com.apex.firefighter.model.UserPreferences;
import com.apex.firefighter.repository.UserPreferencesRepository;
import com.apex.firefighter.service.UserPreferencesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferencesServiceTest {

    @Mock
    private UserPreferencesRepository userPreferencesRepository;

    @InjectMocks
    private UserPreferencesService userPreferencesService;

    private UserPreferences testPreferences;
    private final String TEST_USER_ID = "test-user-123";

    @BeforeEach
    void setUp() {
        testPreferences = new UserPreferences();
        testPreferences.setUserId(TEST_USER_ID);
        testPreferences.setEmailNotificationsEnabled(true);
        testPreferences.setEmailTicketCreation(true);
        testPreferences.setEmailTicketCompletion(true);
        testPreferences.setEmailTicketRevocation(true);
        testPreferences.setEmailFiveMinuteWarning(true);
    }

    // ==================== GET USER PREFERENCES TESTS ====================

    @Test
    void getUserPreferences_WithExistingPreferences_ShouldReturnExistingPreferences() {
        // Arrange
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testPreferences));

        // Act
        UserPreferences result = userPreferencesService.getUserPreferences(TEST_USER_ID);

        // Assert
        assertThat(result).isEqualTo(testPreferences);
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
        verify(userPreferencesRepository, never()).save(any(UserPreferences.class));
    }

    @Test
    void getUserPreferences_WithNoExistingPreferences_ShouldCreateDefaultPreferences() {
        // Arrange
        UserPreferences defaultPreferences = new UserPreferences(TEST_USER_ID);
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(defaultPreferences);

        // Act
        UserPreferences result = userPreferencesService.getUserPreferences(TEST_USER_ID);

        // Assert
        assertThat(result).isEqualTo(defaultPreferences);
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
        verify(userPreferencesRepository).save(any(UserPreferences.class));
    }

    // ==================== UPDATE USER PREFERENCES TESTS ====================

    @Test
    void updateUserPreferences_WithAllParameters_ShouldUpdateAllFields() {
        // Arrange
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testPreferences));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(testPreferences);

        // Act
        UserPreferences result = userPreferencesService.updateUserPreferences(
            TEST_USER_ID, false, false, false, false, false);

        // Assert
        assertThat(result).isEqualTo(testPreferences);
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
        verify(userPreferencesRepository).save(testPreferences);
    }

    @Test
    void updateUserPreferences_WithPartialParameters_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testPreferences));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(testPreferences);

        // Act
        UserPreferences result = userPreferencesService.updateUserPreferences(
            TEST_USER_ID, false, null, true, null, false);

        // Assert
        assertThat(result).isEqualTo(testPreferences);
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
        verify(userPreferencesRepository).save(testPreferences);
    }

    @Test
    void updateUserPreferences_WithNullParameters_ShouldNotUpdateAnyFields() {
        // Arrange
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testPreferences));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(testPreferences);

        // Act
        UserPreferences result = userPreferencesService.updateUserPreferences(
            TEST_USER_ID, null, null, null, null, null);

        // Assert
        assertThat(result).isEqualTo(testPreferences);
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
        verify(userPreferencesRepository).save(testPreferences);
    }

    @Test
    void updateUserPreferences_WithNonExistentUser_ShouldCreateDefaultAndUpdate() {
        // Arrange
        UserPreferences defaultPreferences = new UserPreferences(TEST_USER_ID);
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(defaultPreferences);

        // Act
        UserPreferences result = userPreferencesService.updateUserPreferences(
            TEST_USER_ID, true, true, true, true, true);

        // Assert
        assertThat(result).isEqualTo(defaultPreferences);
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
        verify(userPreferencesRepository, times(2)).save(any(UserPreferences.class));
    }

    // ==================== EMAIL NOTIFICATION CHECKS TESTS ====================

    @Test
    void isEmailNotificationsEnabled_WithEnabledPreferences_ShouldReturnTrue() {
        // Arrange
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testPreferences));

        // Act
        boolean result = userPreferencesService.isEmailNotificationsEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
    }

    @Test
    void isEmailNotificationsEnabled_WithDisabledPreferences_ShouldReturnFalse() {
        // Arrange
        testPreferences.setEmailNotificationsEnabled(false);
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testPreferences));

        // Act
        boolean result = userPreferencesService.isEmailNotificationsEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isFalse();
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
    }

    @Test
    void isEmailNotificationsEnabled_WithNoPreferences_ShouldReturnFalse() {
        // Arrange
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        // Act
        boolean result = userPreferencesService.isEmailNotificationsEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isFalse();
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
    }

    @Test
    void isTicketCreationEmailEnabled_ShouldDelegateToRepository() {
        // Arrange
        when(userPreferencesRepository.hasTicketCreationEmailEnabled(TEST_USER_ID)).thenReturn(true);

        // Act
        boolean result = userPreferencesService.isTicketCreationEmailEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(userPreferencesRepository).hasTicketCreationEmailEnabled(TEST_USER_ID);
    }

    @Test
    void isTicketCompletionEmailEnabled_ShouldDelegateToRepository() {
        // Arrange
        when(userPreferencesRepository.hasTicketCompletionEmailEnabled(TEST_USER_ID)).thenReturn(true);

        // Act
        boolean result = userPreferencesService.isTicketCompletionEmailEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(userPreferencesRepository).hasTicketCompletionEmailEnabled(TEST_USER_ID);
    }

    @Test
    void isTicketRevocationEmailEnabled_ShouldDelegateToRepository() {
        // Arrange
        when(userPreferencesRepository.hasTicketRevocationEmailEnabled(TEST_USER_ID)).thenReturn(true);

        // Act
        boolean result = userPreferencesService.isTicketRevocationEmailEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(userPreferencesRepository).hasTicketRevocationEmailEnabled(TEST_USER_ID);
    }

    @Test
    void isFiveMinuteWarningEmailEnabled_ShouldDelegateToRepository() {
        // Arrange
        when(userPreferencesRepository.hasFiveMinuteWarningEmailEnabled(TEST_USER_ID)).thenReturn(true);

        // Act
        boolean result = userPreferencesService.isFiveMinuteWarningEmailEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(userPreferencesRepository).hasFiveMinuteWarningEmailEnabled(TEST_USER_ID);
    }

    // ==================== BULK OPERATIONS TESTS ====================

    @Test
    void enableAllEmailNotifications_ShouldEnableAllNotifications() {
        // Arrange
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testPreferences));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(testPreferences);

        // Act
        UserPreferences result = userPreferencesService.enableAllEmailNotifications(TEST_USER_ID);

        // Assert
        assertThat(result).isEqualTo(testPreferences);
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
        verify(userPreferencesRepository).save(testPreferences);
    }

    @Test
    void disableAllEmailNotifications_ShouldDisableAllNotifications() {
        // Arrange
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testPreferences));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(testPreferences);

        // Act
        UserPreferences result = userPreferencesService.disableAllEmailNotifications(TEST_USER_ID);

        // Assert
        assertThat(result).isEqualTo(testPreferences);
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
        verify(userPreferencesRepository).save(testPreferences);
    }

    // ==================== RESET PREFERENCES TESTS ====================

    @Test
    void resetToDefault_WithExistingPreferences_ShouldDeleteAndCreateNew() {
        // Arrange
        UserPreferences defaultPreferences = new UserPreferences(TEST_USER_ID);
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testPreferences));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(defaultPreferences);
        doNothing().when(userPreferencesRepository).delete(testPreferences);

        // Act
        UserPreferences result = userPreferencesService.resetToDefault(TEST_USER_ID);

        // Assert
        assertThat(result).isEqualTo(defaultPreferences);
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
        verify(userPreferencesRepository).delete(testPreferences);
        verify(userPreferencesRepository).save(any(UserPreferences.class));
    }

    @Test
    void resetToDefault_WithNoExistingPreferences_ShouldCreateNew() {
        // Arrange
        UserPreferences defaultPreferences = new UserPreferences(TEST_USER_ID);
        when(userPreferencesRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(defaultPreferences);

        // Act
        UserPreferences result = userPreferencesService.resetToDefault(TEST_USER_ID);

        // Assert
        assertThat(result).isEqualTo(defaultPreferences);
        verify(userPreferencesRepository).findByUserId(TEST_USER_ID);
        verify(userPreferencesRepository, never()).delete(any(UserPreferences.class));
        verify(userPreferencesRepository).save(any(UserPreferences.class));
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void isTicketCreationEmailEnabled_WithRepositoryReturnsFalse_ShouldReturnFalse() {
        // Arrange
        when(userPreferencesRepository.hasTicketCreationEmailEnabled(TEST_USER_ID)).thenReturn(false);

        // Act
        boolean result = userPreferencesService.isTicketCreationEmailEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isFalse();
        verify(userPreferencesRepository).hasTicketCreationEmailEnabled(TEST_USER_ID);
    }

    @Test
    void isTicketCompletionEmailEnabled_WithRepositoryReturnsFalse_ShouldReturnFalse() {
        // Arrange
        when(userPreferencesRepository.hasTicketCompletionEmailEnabled(TEST_USER_ID)).thenReturn(false);

        // Act
        boolean result = userPreferencesService.isTicketCompletionEmailEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isFalse();
        verify(userPreferencesRepository).hasTicketCompletionEmailEnabled(TEST_USER_ID);
    }

    @Test
    void isTicketRevocationEmailEnabled_WithRepositoryReturnsFalse_ShouldReturnFalse() {
        // Arrange
        when(userPreferencesRepository.hasTicketRevocationEmailEnabled(TEST_USER_ID)).thenReturn(false);

        // Act
        boolean result = userPreferencesService.isTicketRevocationEmailEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isFalse();
        verify(userPreferencesRepository).hasTicketRevocationEmailEnabled(TEST_USER_ID);
    }

    @Test
    void isFiveMinuteWarningEmailEnabled_WithRepositoryReturnsFalse_ShouldReturnFalse() {
        // Arrange
        when(userPreferencesRepository.hasFiveMinuteWarningEmailEnabled(TEST_USER_ID)).thenReturn(false);

        // Act
        boolean result = userPreferencesService.isFiveMinuteWarningEmailEnabled(TEST_USER_ID);

        // Assert
        assertThat(result).isFalse();
        verify(userPreferencesRepository).hasFiveMinuteWarningEmailEnabled(TEST_USER_ID);
    }
}
