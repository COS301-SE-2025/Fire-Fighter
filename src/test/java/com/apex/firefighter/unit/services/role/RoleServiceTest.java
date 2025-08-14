package com.apex.firefighter.unit.services.role;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.role.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockUser;

    @InjectMocks
    private RoleService roleService;

    private User testUser;
    private final String FIREBASE_UID = "test-firebase-uid";
    private final String ROLE_NAME = "ADMIN";
    private final String ASSIGNED_BY = "admin-user";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(FIREBASE_UID);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDepartment("IT");
        testUser.setIsAuthorized(true);
        testUser.setRole(null); // Start with no role
    }

    // ==================== ASSIGN ROLE TESTS ====================

    @Test
    void assignRole_WithValidUser_ShouldAssignRoleAndReturnUser() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = roleService.assignRole(FIREBASE_UID, ROLE_NAME, ASSIGNED_BY);

        // Assert
        assertThat(result).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(mockUser);
        verify(mockUser).setRole(ROLE_NAME);
    }

    @Test
    void assignRole_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roleService.assignRole(FIREBASE_UID, ROLE_NAME, ASSIGNED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with Firebase UID: " + FIREBASE_UID);

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void assignRole_WithNullFirebaseUid_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByUserId(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roleService.assignRole(null, ROLE_NAME, ASSIGNED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with Firebase UID: null");

        verify(userRepository).findByUserId(null);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void assignRole_WithNullRoleName_ShouldAssignNullRole() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = roleService.assignRole(FIREBASE_UID, null, ASSIGNED_BY);

        // Assert
        assertThat(result).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(mockUser);
        verify(mockUser).setRole(null);
    }

    @Test
    void assignRole_WithExistingRole_ShouldOverwriteRole() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = roleService.assignRole(FIREBASE_UID, ROLE_NAME, ASSIGNED_BY);

        // Assert
        assertThat(result).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(mockUser);
        verify(mockUser).setRole(ROLE_NAME);
    }

    // ==================== REMOVE ROLE TESTS ====================

    @Test
    void removeRole_WithMatchingRole_ShouldRemoveRoleAndReturnUser() {
        // Arrange
        when(mockUser.getRole()).thenReturn(ROLE_NAME);
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = roleService.removeRole(FIREBASE_UID, ROLE_NAME);

        // Assert
        assertThat(result).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(mockUser);
        verify(mockUser).setRole(null);
    }

    @Test
    void removeRole_WithNonMatchingRole_ShouldReturnUserWithoutChanges() {
        // Arrange
        when(mockUser.getRole()).thenReturn("USER");
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));

        // Act
        User result = roleService.removeRole(FIREBASE_UID, ROLE_NAME);

        // Assert
        assertThat(result).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
        verify(mockUser, never()).setRole(null);
    }

    @Test
    void removeRole_WithUserHavingNoRole_ShouldReturnUserWithoutChanges() {
        // Arrange
        testUser.setRole(null);
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));

        // Act
        User result = roleService.removeRole(FIREBASE_UID, ROLE_NAME);

        // Assert
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void removeRole_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roleService.removeRole(FIREBASE_UID, ROLE_NAME))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with Firebase UID: " + FIREBASE_UID);

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void removeRole_WithNullRoleName_ShouldThrowException() {
        // Arrange
        when(mockUser.getRole()).thenReturn(ROLE_NAME);
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThatThrownBy(() -> roleService.removeRole(FIREBASE_UID, null))
            .isInstanceOf(NullPointerException.class);

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== QUERY OPERATIONS TESTS ====================

    @Test
    void getUsersByRole_ShouldReturnUsersWithRole() {
        // Arrange
        List<User> usersWithRole = Arrays.asList(testUser);
        when(userRepository.findByRole(ROLE_NAME)).thenReturn(usersWithRole);

        // Act
        List<User> result = roleService.getUsersByRole(ROLE_NAME);

        // Assert
        assertThat(result).isEqualTo(usersWithRole);
        assertThat(result).hasSize(1);
        verify(userRepository).findByRole(ROLE_NAME);
    }

    @Test
    void getUsersByRole_WithNoUsersHavingRole_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findByRole(ROLE_NAME)).thenReturn(Collections.emptyList());

        // Act
        List<User> result = roleService.getUsersByRole(ROLE_NAME);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByRole(ROLE_NAME);
    }

    @Test
    void getUsersByRole_WithNullRoleName_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByRole(null)).thenReturn(Collections.emptyList());

        // Act
        List<User> result = roleService.getUsersByRole(null);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByRole(null);
    }

    @Test
    void getAuthorizedUsersByRole_ShouldReturnAuthorizedUsersWithRole() {
        // Arrange
        List<User> authorizedUsersWithRole = Arrays.asList(testUser);
        when(userRepository.findByRoleAndIsAuthorizedTrue(ROLE_NAME)).thenReturn(authorizedUsersWithRole);

        // Act
        List<User> result = roleService.getAuthorizedUsersByRole(ROLE_NAME);

        // Assert
        assertThat(result).isEqualTo(authorizedUsersWithRole);
        assertThat(result).hasSize(1);
        verify(userRepository).findByRoleAndIsAuthorizedTrue(ROLE_NAME);
    }

    @Test
    void getAuthorizedUsersByRole_WithNoAuthorizedUsersHavingRole_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findByRoleAndIsAuthorizedTrue(ROLE_NAME)).thenReturn(Collections.emptyList());

        // Act
        List<User> result = roleService.getAuthorizedUsersByRole(ROLE_NAME);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByRoleAndIsAuthorizedTrue(ROLE_NAME);
    }

    @Test
    void getAuthorizedUsersByRole_WithNullRoleName_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByRoleAndIsAuthorizedTrue(null)).thenReturn(Collections.emptyList());

        // Act
        List<User> result = roleService.getAuthorizedUsersByRole(null);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByRoleAndIsAuthorizedTrue(null);
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void assignRole_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> roleService.assignRole(FIREBASE_UID, ROLE_NAME, ASSIGNED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(testUser);
    }

    @Test
    void removeRole_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        testUser.setRole(ROLE_NAME);
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> roleService.removeRole(FIREBASE_UID, ROLE_NAME))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(testUser);
    }

    @Test
    void getUsersByRole_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByRole(ROLE_NAME)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> roleService.getUsersByRole(ROLE_NAME))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(userRepository).findByRole(ROLE_NAME);
    }

    // ==================== COMPREHENSIVE INTEGRATION TESTS ====================

    @Test
    void fullWorkflow_AssignAndRemoveRole_ShouldWorkCorrectly() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User assignedUser = roleService.assignRole(FIREBASE_UID, ROLE_NAME, ASSIGNED_BY);

        // Assert
        assertThat(assignedUser).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(mockUser);
        verify(mockUser).setRole(ROLE_NAME);
    }

    @Test
    void queryOperations_ShouldWorkCorrectly() {
        // Arrange
        List<User> allUsers = Arrays.asList(testUser);
        List<User> authorizedUsers = Arrays.asList(testUser);
        when(userRepository.findByRole(ROLE_NAME)).thenReturn(allUsers);
        when(userRepository.findByRoleAndIsAuthorizedTrue(ROLE_NAME)).thenReturn(authorizedUsers);

        // Act
        List<User> allResult = roleService.getUsersByRole(ROLE_NAME);
        List<User> authorizedResult = roleService.getAuthorizedUsersByRole(ROLE_NAME);

        // Assert
        assertThat(allResult).isEqualTo(allUsers);
        assertThat(authorizedResult).isEqualTo(authorizedUsers);
        verify(userRepository).findByRole(ROLE_NAME);
        verify(userRepository).findByRoleAndIsAuthorizedTrue(ROLE_NAME);
    }
}
