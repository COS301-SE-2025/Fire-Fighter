package com.apex.firefighter.unit.services;

import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import com.apex.firefighter.service.auth.AuthenticationService;
import com.apex.firefighter.service.auth.AuthorizationService;
import com.apex.firefighter.service.role.RoleService;
import com.apex.firefighter.service.user.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private RoleService roleService;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("test-firebase-uid");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDepartment("IT");
        testUser.setIsAuthorized(true);
        testUser.setIsAdmin(false);

        adminUser = new User();
        adminUser.setUserId("admin-firebase-uid");
        adminUser.setUsername("adminuser");
        adminUser.setEmail("admin@example.com");
        adminUser.setDepartment("Management");
        adminUser.setIsAuthorized(true);
        adminUser.setIsAdmin(true);
    }

    // ==================== FIREBASE USER VERIFICATION TESTS ====================

    @Test
    void verifyOrCreateUser_ShouldDelegateToAuthenticationService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        String username = "testuser";
        String email = "test@example.com";
        String department = "IT";
        
        when(authenticationService.verifyOrCreateUser(firebaseUid, username, email, department))
            .thenReturn(testUser);

        // Act
        User result = userService.verifyOrCreateUser(firebaseUid, username, email, department);

        // Assert
        assertThat(result).isEqualTo(testUser);
        verify(authenticationService).verifyOrCreateUser(firebaseUid, username, email, department);
    }

    // ==================== USER AUTHORIZATION TESTS ====================

    @Test
    void isUserAuthorized_ShouldDelegateToAuthorizationService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        when(authorizationService.isUserAuthorized(firebaseUid)).thenReturn(true);

        // Act
        boolean result = userService.isUserAuthorized(firebaseUid);

        // Assert
        assertThat(result).isTrue();
        verify(authorizationService).isUserAuthorized(firebaseUid);
    }

    @Test
    void hasRole_ShouldDelegateToAuthorizationService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        String roleName = "ADMIN";
        when(authorizationService.hasRole(firebaseUid, roleName)).thenReturn(false);

        // Act
        boolean result = userService.hasRole(firebaseUid, roleName);

        // Assert
        assertThat(result).isFalse();
        verify(authorizationService).hasRole(firebaseUid, roleName);
    }

    // ==================== ADMIN OPERATIONS TESTS ====================

    @Test
    void authorizeUser_ShouldDelegateToAuthorizationService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        String authorizedBy = "admin-user";
        when(authorizationService.authorizeUser(firebaseUid, authorizedBy)).thenReturn(testUser);

        // Act
        User result = userService.authorizeUser(firebaseUid, authorizedBy);

        // Assert
        assertThat(result).isEqualTo(testUser);
        verify(authorizationService).authorizeUser(firebaseUid, authorizedBy);
    }

    @Test
    void revokeUserAuthorization_ShouldDelegateToAuthorizationService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        String revokedBy = "admin-user";
        when(authorizationService.revokeUserAuthorization(firebaseUid, revokedBy)).thenReturn(testUser);

        // Act
        User result = userService.revokeUserAuthorization(firebaseUid, revokedBy);

        // Assert
        assertThat(result).isEqualTo(testUser);
        verify(authorizationService).revokeUserAuthorization(firebaseUid, revokedBy);
    }

    // ==================== ROLE MANAGEMENT TESTS ====================

    @Test
    void assignRole_ShouldDelegateToRoleService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        String roleName = "MANAGER";
        String assignedBy = "admin-user";
        when(roleService.assignRole(firebaseUid, roleName, assignedBy)).thenReturn(testUser);

        // Act
        User result = userService.assignRole(firebaseUid, roleName, assignedBy);

        // Assert
        assertThat(result).isEqualTo(testUser);
        verify(roleService).assignRole(firebaseUid, roleName, assignedBy);
    }

    @Test
    void removeRole_ShouldDelegateToRoleService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        String roleName = "MANAGER";
        when(roleService.removeRole(firebaseUid, roleName)).thenReturn(testUser);

        // Act
        User result = userService.removeRole(firebaseUid, roleName);

        // Assert
        assertThat(result).isEqualTo(testUser);
        verify(roleService).removeRole(firebaseUid, roleName);
    }

    @Test
    void getUsersByRole_ShouldDelegateToRoleService() {
        // Arrange
        String roleName = "ADMIN";
        List<User> users = Arrays.asList(adminUser);
        when(roleService.getUsersByRole(roleName)).thenReturn(users);

        // Act
        List<User> result = userService.getUsersByRole(roleName);

        // Assert
        assertThat(result).isEqualTo(users);
        verify(roleService).getUsersByRole(roleName);
    }

    @Test
    void getAuthorizedUsersByRole_ShouldDelegateToRoleService() {
        // Arrange
        String roleName = "ADMIN";
        List<User> users = Arrays.asList(adminUser);
        when(roleService.getAuthorizedUsersByRole(roleName)).thenReturn(users);

        // Act
        List<User> result = userService.getAuthorizedUsersByRole(roleName);

        // Assert
        assertThat(result).isEqualTo(users);
        verify(roleService).getAuthorizedUsersByRole(roleName);
    }

    // ==================== QUERY OPERATIONS TESTS ====================

    @Test
    void getUserByFirebaseUid_ShouldDelegateToAuthenticationService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        when(authenticationService.getUserByFirebaseUid(firebaseUid)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserByFirebaseUid(firebaseUid);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(authenticationService).getUserByFirebaseUid(firebaseUid);
    }

    @Test
    void getUserByEmail_ShouldDelegateToUserProfileService() {
        // Arrange
        String email = "test@example.com";
        when(userProfileService.getUserByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserByEmail(email);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userProfileService).getUserByEmail(email);
    }

    @Test
    void getAuthorizedUsers_ShouldDelegateToUserProfileService() {
        // Arrange
        List<User> users = Arrays.asList(testUser, adminUser);
        when(userProfileService.getAuthorizedUsers()).thenReturn(users);

        // Act
        List<User> result = userService.getAuthorizedUsers();

        // Assert
        assertThat(result).isEqualTo(users);
        verify(userProfileService).getAuthorizedUsers();
    }

    @Test
    void getUsersByDepartment_ShouldDelegateToUserProfileService() {
        // Arrange
        String department = "IT";
        List<User> users = Arrays.asList(testUser);
        when(userProfileService.getUsersByDepartment(department)).thenReturn(users);

        // Act
        List<User> result = userService.getUsersByDepartment(department);

        // Assert
        assertThat(result).isEqualTo(users);
        verify(userProfileService).getUsersByDepartment(department);
    }

    @Test
    void getUserWithRoles_ShouldDelegateToUserProfileService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        when(userProfileService.getUserWithRoles(firebaseUid)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserWithRoles(firebaseUid);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userProfileService).getUserWithRoles(firebaseUid);
    }

    // ==================== PROFILE UPDATE TESTS ====================

    @Test
    void updateUserProfile_ShouldDelegateToUserProfileService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        String username = "newusername";
        String email = "newemail@example.com";
        String department = "HR";
        when(userProfileService.updateUserProfile(firebaseUid, username, email, department)).thenReturn(testUser);

        // Act
        User result = userService.updateUserProfile(firebaseUid, username, email, department);

        // Assert
        assertThat(result).isEqualTo(testUser);
        verify(userProfileService).updateUserProfile(firebaseUid, username, email, department);
    }

    @Test
    void updateContactNumber_ShouldDelegateToUserProfileService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        String contactNumber = "123-456-7890";
        when(userProfileService.updateContactNumber(firebaseUid, contactNumber)).thenReturn(testUser);

        // Act
        User result = userService.updateContactNumber(firebaseUid, contactNumber);

        // Assert
        assertThat(result).isEqualTo(testUser);
        verify(userProfileService).updateContactNumber(firebaseUid, contactNumber);
    }

    // ==================== UTILITY METHODS TESTS ====================

    @Test
    void getAllUsers_ShouldDelegateToUserProfileService() {
        // Arrange
        List<User> users = Arrays.asList(testUser, adminUser);
        when(userProfileService.getAllUsers()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertThat(result).isEqualTo(users);
        verify(userProfileService).getAllUsers();
    }

    @Test
    void userExists_ShouldDelegateToAuthenticationService() {
        // Arrange
        String firebaseUid = "test-firebase-uid";
        when(authenticationService.userExists(firebaseUid)).thenReturn(true);

        // Act
        boolean result = userService.userExists(firebaseUid);

        // Assert
        assertThat(result).isTrue();
        verify(authenticationService).userExists(firebaseUid);
    }

    @Test
    void getUserCount_ShouldDelegateToUserProfileService() {
        // Arrange
        when(userProfileService.getUserCount()).thenReturn(10L);

        // Act
        long result = userService.getUserCount();

        // Assert
        assertThat(result).isEqualTo(10L);
        verify(userProfileService).getUserCount();
    }

    @Test
    void getAuthorizedUserCount_ShouldDelegateToUserProfileService() {
        // Arrange
        when(userProfileService.getAuthorizedUserCount()).thenReturn(8L);

        // Act
        long result = userService.getAuthorizedUserCount();

        // Assert
        assertThat(result).isEqualTo(8L);
        verify(userProfileService).getAuthorizedUserCount();
    }

    // ==================== EDGE CASES AND NULL HANDLING ====================

    @Test
    void getUserByFirebaseUid_WithNonExistentUser_ShouldReturnEmpty() {
        // Arrange
        String firebaseUid = "non-existent-uid";
        when(authenticationService.getUserByFirebaseUid(firebaseUid)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserByFirebaseUid(firebaseUid);

        // Assert
        assertThat(result).isEmpty();
        verify(authenticationService).getUserByFirebaseUid(firebaseUid);
    }

    @Test
    void getUserByEmail_WithNonExistentEmail_ShouldReturnEmpty() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userProfileService.getUserByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserByEmail(email);

        // Assert
        assertThat(result).isEmpty();
        verify(userProfileService).getUserByEmail(email);
    }

    @Test
    void isUserAuthorized_WithUnauthorizedUser_ShouldReturnFalse() {
        // Arrange
        String firebaseUid = "unauthorized-uid";
        when(authorizationService.isUserAuthorized(firebaseUid)).thenReturn(false);

        // Act
        boolean result = userService.isUserAuthorized(firebaseUid);

        // Assert
        assertThat(result).isFalse();
        verify(authorizationService).isUserAuthorized(firebaseUid);
    }

    @Test
    void userExists_WithNonExistentUser_ShouldReturnFalse() {
        // Arrange
        String firebaseUid = "non-existent-uid";
        when(authenticationService.userExists(firebaseUid)).thenReturn(false);

        // Act
        boolean result = userService.userExists(firebaseUid);

        // Assert
        assertThat(result).isFalse();
        verify(authenticationService).userExists(firebaseUid);
    }
}
