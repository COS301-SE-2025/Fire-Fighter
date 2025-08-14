package com.apex.firefighter.unit.services.auth;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.auth.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    private User authorizedUser;
    private User unauthorizedUser;
    private User adminUser;
    private final String FIREBASE_UID = "firebase-uid-123";
    private final String ADMIN_UID = "admin-uid-456";
    private final String AUTHORIZED_BY = "admin-user";
    private final String REVOKED_BY = "admin-user";

    @BeforeEach
    void setUp() {
        authorizedUser = new User();
        authorizedUser.setUserId(FIREBASE_UID);
        authorizedUser.setUsername("authorizeduser");
        authorizedUser.setEmail("authorized@example.com");
        authorizedUser.setDepartment("IT");
        authorizedUser.setIsAuthorized(true);
        authorizedUser.setIsAdmin(false);
        authorizedUser.setRole("USER");

        unauthorizedUser = new User();
        unauthorizedUser.setUserId(FIREBASE_UID);
        unauthorizedUser.setUsername("unauthorizeduser");
        unauthorizedUser.setEmail("unauthorized@example.com");
        unauthorizedUser.setDepartment("IT");
        unauthorizedUser.setIsAuthorized(false);
        unauthorizedUser.setIsAdmin(false);
        unauthorizedUser.setRole("USER");

        adminUser = new User();
        adminUser.setUserId(ADMIN_UID);
        adminUser.setUsername("adminuser");
        adminUser.setEmail("admin@example.com");
        adminUser.setDepartment("Management");
        adminUser.setIsAuthorized(true);
        adminUser.setIsAdmin(true);
        adminUser.setRole("ADMIN");
    }

    // ==================== USER AUTHORIZATION CHECK TESTS ====================

    @Test
    void isUserAuthorized_WithAuthorizedUser_ShouldReturnTrue() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(authorizedUser));

        // Act
        boolean result = authorizationService.isUserAuthorized(FIREBASE_UID);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void isUserAuthorized_WithUnauthorizedUser_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(unauthorizedUser));

        // Act
        boolean result = authorizationService.isUserAuthorized(FIREBASE_UID);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void isUserAuthorized_WithNonExistentUser_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act
        boolean result = authorizationService.isUserAuthorized(FIREBASE_UID);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void isUserAuthorized_WithNullFirebaseUid_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findByUserId(null)).thenReturn(Optional.empty());

        // Act
        boolean result = authorizationService.isUserAuthorized(null);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).findByUserId(null);
    }

    @Test
    void isUserAuthorized_WithEmptyFirebaseUid_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findByUserId("")).thenReturn(Optional.empty());

        // Act
        boolean result = authorizationService.isUserAuthorized("");

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).findByUserId("");
    }

    // ==================== ROLE VERIFICATION TESTS ====================

    @Test
    void hasRole_WithUserHavingRole_ShouldReturnTrue() {
        // Arrange
        String roleName = "USER";
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(authorizedUser));

        // Act
        boolean result = authorizationService.hasRole(FIREBASE_UID, roleName);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void hasRole_WithUserNotHavingRole_ShouldReturnFalse() {
        // Arrange
        String roleName = "ADMIN";
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(authorizedUser));

        // Act
        boolean result = authorizationService.hasRole(FIREBASE_UID, roleName);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void hasRole_WithAdminUser_ShouldReturnTrueForAdminRole() {
        // Arrange
        String roleName = "ADMIN";
        when(userRepository.findByUserId(ADMIN_UID)).thenReturn(Optional.of(adminUser));

        // Act
        boolean result = authorizationService.hasRole(ADMIN_UID, roleName);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).findByUserId(ADMIN_UID);
    }

    @Test
    void hasRole_WithNonExistentUser_ShouldReturnFalse() {
        // Arrange
        String roleName = "USER";
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act
        boolean result = authorizationService.hasRole(FIREBASE_UID, roleName);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void hasRole_WithNullRole_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(authorizedUser));

        // Act
        boolean result = authorizationService.hasRole(FIREBASE_UID, null);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void hasRole_WithEmptyRole_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(authorizedUser));

        // Act
        boolean result = authorizationService.hasRole(FIREBASE_UID, "");

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void hasRole_WithNullFirebaseUid_ShouldReturnFalse() {
        // Arrange
        String roleName = "USER";
        when(userRepository.findByUserId(null)).thenReturn(Optional.empty());

        // Act
        boolean result = authorizationService.hasRole(null, roleName);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).findByUserId(null);
    }

    // ==================== ADMIN OPERATIONS TESTS ====================

    @Test
    void authorizeUser_WithExistingUser_ShouldAuthorizeAndReturnUser() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(unauthorizedUser));
        when(userRepository.save(any(User.class))).thenReturn(unauthorizedUser);

        // Act
        User result = authorizationService.authorizeUser(FIREBASE_UID, AUTHORIZED_BY);

        // Assert
        assertThat(result).isEqualTo(unauthorizedUser);
        assertThat(result.isAuthorized()).isTrue();
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(unauthorizedUser);
    }

    @Test
    void authorizeUser_WithAlreadyAuthorizedUser_ShouldStillAuthorizeAndReturnUser() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(authorizedUser));
        when(userRepository.save(any(User.class))).thenReturn(authorizedUser);

        // Act
        User result = authorizationService.authorizeUser(FIREBASE_UID, AUTHORIZED_BY);

        // Assert
        assertThat(result).isEqualTo(authorizedUser);
        assertThat(result.isAuthorized()).isTrue();
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(authorizedUser);
    }

    @Test
    void authorizeUser_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authorizationService.authorizeUser(FIREBASE_UID, AUTHORIZED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with Firebase UID: " + FIREBASE_UID);

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void revokeUserAuthorization_WithExistingUser_ShouldRevokeAndReturnUser() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(authorizedUser));
        when(userRepository.save(any(User.class))).thenReturn(authorizedUser);

        // Act
        User result = authorizationService.revokeUserAuthorization(FIREBASE_UID, REVOKED_BY);

        // Assert
        assertThat(result).isEqualTo(authorizedUser);
        assertThat(result.isAuthorized()).isFalse();
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(authorizedUser);
    }

    @Test
    void revokeUserAuthorization_WithAlreadyUnauthorizedUser_ShouldStillRevokeAndReturnUser() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(unauthorizedUser));
        when(userRepository.save(any(User.class))).thenReturn(unauthorizedUser);

        // Act
        User result = authorizationService.revokeUserAuthorization(FIREBASE_UID, REVOKED_BY);

        // Assert
        assertThat(result).isEqualTo(unauthorizedUser);
        assertThat(result.isAuthorized()).isFalse();
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(unauthorizedUser);
    }

    @Test
    void revokeUserAuthorization_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authorizationService.revokeUserAuthorization(FIREBASE_UID, REVOKED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with Firebase UID: " + FIREBASE_UID);

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void authorizeUser_WithNullAuthorizedBy_ShouldStillWork() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(unauthorizedUser));
        when(userRepository.save(any(User.class))).thenReturn(unauthorizedUser);

        // Act
        User result = authorizationService.authorizeUser(FIREBASE_UID, null);

        // Assert
        assertThat(result).isEqualTo(unauthorizedUser);
        assertThat(result.isAuthorized()).isTrue();
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(unauthorizedUser);
    }

    @Test
    void revokeUserAuthorization_WithNullRevokedBy_ShouldStillWork() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(authorizedUser));
        when(userRepository.save(any(User.class))).thenReturn(authorizedUser);

        // Act
        User result = authorizationService.revokeUserAuthorization(FIREBASE_UID, null);

        // Assert
        assertThat(result).isEqualTo(authorizedUser);
        assertThat(result.isAuthorized()).isFalse();
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(authorizedUser);
    }

    @Test
    void authorizeUser_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> authorizationService.authorizeUser(FIREBASE_UID, AUTHORIZED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void revokeUserAuthorization_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> authorizationService.revokeUserAuthorization(FIREBASE_UID, REVOKED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }
}
