package com.apex.firefighter.unit.services;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.user.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for admin-only Dolibarr UID management functionality
 */
@ExtendWith(MockitoExtension.class)
class AdminDolibarrManagementTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private User adminUser;
    private User regularUser;
    private User targetUser;

    private static final String ADMIN_UID = "admin-123";
    private static final String REGULAR_USER_UID = "user-456";
    private static final String TARGET_USER_UID = "target-789";
    private static final String DOLIBARR_ID = "DOL123456";

    @BeforeEach
    void setUp() {
        // Create admin user
        adminUser = new User();
        adminUser.setUserId(ADMIN_UID);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setIsAdmin(true);

        // Create regular user
        regularUser = new User();
        regularUser.setUserId(REGULAR_USER_UID);
        regularUser.setUsername("user");
        regularUser.setEmail("user@example.com");
        regularUser.setIsAdmin(false);

        // Create target user
        targetUser = new User();
        targetUser.setUserId(TARGET_USER_UID);
        targetUser.setUsername("target");
        targetUser.setEmail("target@example.com");
        targetUser.setIsAdmin(false);
    }

    @Test
    void updateUserDolibarrIdAsAdmin_WithValidAdmin_ShouldUpdateSuccessfully() {
        // Arrange
        when(userRepository.findByUserId(ADMIN_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUserId(TARGET_USER_UID)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any(User.class))).thenReturn(targetUser);

        // Act
        User result = userProfileService.updateUserDolibarrIdAsAdmin(ADMIN_UID, TARGET_USER_UID, DOLIBARR_ID);

        // Assert
        assertNotNull(result);
        assertEquals(DOLIBARR_ID, targetUser.getDolibarrId());
        verify(userRepository).findByUserId(ADMIN_UID);
        verify(userRepository).findByUserId(TARGET_USER_UID);
        verify(userRepository).save(targetUser);
    }

    @Test
    void updateUserDolibarrIdAsAdmin_WithNonAdminUser_ShouldThrowSecurityException() {
        // Arrange
        when(userRepository.findByUserId(REGULAR_USER_UID)).thenReturn(Optional.of(regularUser));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            userProfileService.updateUserDolibarrIdAsAdmin(REGULAR_USER_UID, TARGET_USER_UID, DOLIBARR_ID);
        });

        assertEquals("Administrator privileges required to manage Dolibarr UIDs", exception.getMessage());
        verify(userRepository).findByUserId(REGULAR_USER_UID);
        verify(userRepository, never()).findByUserId(TARGET_USER_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserDolibarrIdAsAdmin_WithNonExistentAdmin_ShouldThrowRuntimeException() {
        // Arrange
        when(userRepository.findByUserId(ADMIN_UID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userProfileService.updateUserDolibarrIdAsAdmin(ADMIN_UID, TARGET_USER_UID, DOLIBARR_ID);
        });

        assertEquals("Admin user not found with Firebase UID: " + ADMIN_UID, exception.getMessage());
        verify(userRepository).findByUserId(ADMIN_UID);
        verify(userRepository, never()).findByUserId(TARGET_USER_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserDolibarrIdAsAdmin_WithNonExistentTargetUser_ShouldThrowRuntimeException() {
        // Arrange
        when(userRepository.findByUserId(ADMIN_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUserId(TARGET_USER_UID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userProfileService.updateUserDolibarrIdAsAdmin(ADMIN_UID, TARGET_USER_UID, DOLIBARR_ID);
        });

        assertEquals("Target user not found with Firebase UID: " + TARGET_USER_UID, exception.getMessage());
        verify(userRepository).findByUserId(ADMIN_UID);
        verify(userRepository).findByUserId(TARGET_USER_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserDolibarrIdAsAdmin_WithValidAdmin_ShouldReturnDolibarrId() {
        // Arrange
        targetUser.setDolibarrId(DOLIBARR_ID);
        when(userRepository.findByUserId(ADMIN_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUserId(TARGET_USER_UID)).thenReturn(Optional.of(targetUser));

        // Act
        String result = userProfileService.getUserDolibarrIdAsAdmin(ADMIN_UID, TARGET_USER_UID);

        // Assert
        assertEquals(DOLIBARR_ID, result);
        verify(userRepository).findByUserId(ADMIN_UID);
        verify(userRepository).findByUserId(TARGET_USER_UID);
    }

    @Test
    void getUserDolibarrIdAsAdmin_WithNonAdminUser_ShouldThrowSecurityException() {
        // Arrange
        when(userRepository.findByUserId(REGULAR_USER_UID)).thenReturn(Optional.of(regularUser));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            userProfileService.getUserDolibarrIdAsAdmin(REGULAR_USER_UID, TARGET_USER_UID);
        });

        assertEquals("Administrator privileges required to access Dolibarr UIDs", exception.getMessage());
        verify(userRepository).findByUserId(REGULAR_USER_UID);
        verify(userRepository, never()).findByUserId(TARGET_USER_UID);
    }

    @Test
    void getUserDolibarrIdAsAdmin_WithNullDolibarrId_ShouldReturnNull() {
        // Arrange
        targetUser.setDolibarrId(null);
        when(userRepository.findByUserId(ADMIN_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUserId(TARGET_USER_UID)).thenReturn(Optional.of(targetUser));

        // Act
        String result = userProfileService.getUserDolibarrIdAsAdmin(ADMIN_UID, TARGET_USER_UID);

        // Assert
        assertNull(result);
        verify(userRepository).findByUserId(ADMIN_UID);
        verify(userRepository).findByUserId(TARGET_USER_UID);
    }

    @Test
    void getAllUsersAsAdmin_WithValidAdmin_ShouldReturnUsersAndStatistics() {
        // Arrange
        List<User> allUsers = Arrays.asList(adminUser, regularUser, targetUser);
        when(userRepository.findByUserId(ADMIN_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findAll()).thenReturn(allUsers);

        // Act
        Map<String, Object> result = userProfileService.getAllUsersAsAdmin(ADMIN_UID);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("users"));
        assertTrue(result.containsKey("statistics"));

        @SuppressWarnings("unchecked")
        List<User> returnedUsers = (List<User>) result.get("users");
        assertEquals(3, returnedUsers.size());

        @SuppressWarnings("unchecked")
        Map<String, Object> statistics = (Map<String, Object>) result.get("statistics");
        assertEquals(2L, statistics.get("normalUsers")); // regularUser and targetUser
        assertEquals(1L, statistics.get("adminUsers"));  // adminUser
        assertEquals(3, statistics.get("totalUsers"));

        verify(userRepository).findByUserId(ADMIN_UID);
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsersAsAdmin_WithNonAdminUser_ShouldThrowSecurityException() {
        // Arrange
        when(userRepository.findByUserId(REGULAR_USER_UID)).thenReturn(Optional.of(regularUser));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            userProfileService.getAllUsersAsAdmin(REGULAR_USER_UID);
        });

        assertEquals("Administrator privileges required to access all users", exception.getMessage());
        verify(userRepository).findByUserId(REGULAR_USER_UID);
        verify(userRepository, never()).findAll();
    }
}
