package com.apex.firefighter.unit.services.user;

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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockUser;

    @InjectMocks
    private UserProfileService userProfileService;

    private User testUser;
    private User adminUser;
    private final String FIREBASE_UID = "test-firebase-uid";
    private final String ADMIN_FIREBASE_UID = "admin-firebase-uid";
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final String DEPARTMENT = "IT";
    private final String CONTACT_NUMBER = "123-456-7890";
    private final String DOLIBARR_ID = "dolibarr-123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(FIREBASE_UID);
        testUser.setUsername(USERNAME);
        testUser.setEmail(EMAIL);
        testUser.setDepartment(DEPARTMENT);
        testUser.setContactNumber(CONTACT_NUMBER);
        testUser.setIsAuthorized(true);
        testUser.setDolibarrId(DOLIBARR_ID);

        adminUser = new User();
        adminUser.setUserId(ADMIN_FIREBASE_UID);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setDepartment("Admin");
        adminUser.setIsAdmin(true);
        adminUser.setIsAuthorized(true);
    }

    // ==================== GET USER WITH ROLES TESTS ====================

    @Test
    void getUserWithRoles_WithExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userProfileService.getUserWithRoles(FIREBASE_UID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getUserWithRoles_WithNonExistentUser_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userProfileService.getUserWithRoles(FIREBASE_UID);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getUserWithRoles_WithNullFirebaseUid_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByUserId(null)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userProfileService.getUserWithRoles(null);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByUserId(null);
    }

    // ==================== GET USER BY EMAIL TESTS ====================

    @Test
    void getUserByEmail_WithExistingEmail_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userProfileService.getUserByEmail(EMAIL);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userRepository).findByEmail(EMAIL);
    }

    @Test
    void getUserByEmail_WithNonExistentEmail_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userProfileService.getUserByEmail(EMAIL);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByEmail(EMAIL);
    }

    @Test
    void getUserByEmail_WithNullEmail_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userProfileService.getUserByEmail(null);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByEmail(null);
    }

    // ==================== GET USER BY USERNAME TESTS ====================

    @Test
    void getUserByUsername_WithExistingUsername_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userProfileService.getUserByUsername(USERNAME);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void getUserByUsername_WithNonExistentUsername_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userProfileService.getUserByUsername(USERNAME);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByUsername(USERNAME);
    }

    // ==================== UPDATE USER PROFILE TESTS ====================

    @Test
    void updateUserProfile_WithValidUser_ShouldUpdateAndReturnUser() {
        // Arrange
        String newUsername = "newuser";
        String newEmail = "new@example.com";
        String newDepartment = "HR";
        
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = userProfileService.updateUserProfile(FIREBASE_UID, newUsername, newEmail, newDepartment);

        // Assert
        assertThat(result).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(mockUser);
        verify(mockUser).setUsername(newUsername);
        verify(mockUser).setEmail(newEmail);
        verify(mockUser).setDepartment(newDepartment);
    }

    @Test
    void updateUserProfile_WithPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        String newUsername = "newuser";
        
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = userProfileService.updateUserProfile(FIREBASE_UID, newUsername, null, null);

        // Assert
        assertThat(result).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(mockUser);
        verify(mockUser).setUsername(newUsername);
        verify(mockUser, never()).setEmail(any());
        verify(mockUser, never()).setDepartment(any());
    }

    @Test
    void updateUserProfile_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.updateUserProfile(FIREBASE_UID, USERNAME, EMAIL, DEPARTMENT))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with Firebase UID: " + FIREBASE_UID);

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserProfile_WithAllNullFields_ShouldNotUpdateAnyField() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = userProfileService.updateUserProfile(FIREBASE_UID, null, null, null);

        // Assert
        assertThat(result).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(mockUser);
        verify(mockUser, never()).setUsername(any());
        verify(mockUser, never()).setEmail(any());
        verify(mockUser, never()).setDepartment(any());
    }

    // ==================== UPDATE CONTACT NUMBER TESTS ====================

    @Test
    void updateContactNumber_WithValidUser_ShouldUpdateAndReturnUser() {
        // Arrange
        String newContactNumber = "987-654-3210";
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = userProfileService.updateContactNumber(FIREBASE_UID, newContactNumber);

        // Assert
        assertThat(result).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(mockUser);
        verify(mockUser).setContactNumber(newContactNumber);
    }

    @Test
    void updateContactNumber_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.updateContactNumber(FIREBASE_UID, CONTACT_NUMBER))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with Firebase UID: " + FIREBASE_UID);

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateContactNumber_WithNullContactNumber_ShouldSetNullContactNumber() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = userProfileService.updateContactNumber(FIREBASE_UID, null);

        // Assert
        assertThat(result).isEqualTo(mockUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(mockUser);
        verify(mockUser).setContactNumber(null);
    }

    // ==================== QUERY OPERATIONS TESTS ====================

    @Test
    void getAuthorizedUsers_ShouldReturnAuthorizedUsers() {
        // Arrange
        List<User> authorizedUsers = Arrays.asList(testUser);
        when(userRepository.findByIsAuthorizedTrue()).thenReturn(authorizedUsers);

        // Act
        List<User> result = userProfileService.getAuthorizedUsers();

        // Assert
        assertThat(result).isEqualTo(authorizedUsers);
        assertThat(result).hasSize(1);
        verify(userRepository).findByIsAuthorizedTrue();
    }

    @Test
    void getAuthorizedUsers_WithNoAuthorizedUsers_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findByIsAuthorizedTrue()).thenReturn(Collections.emptyList());

        // Act
        List<User> result = userProfileService.getAuthorizedUsers();

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByIsAuthorizedTrue();
    }

    @Test
    void getUsersByDepartment_ShouldReturnUsersInDepartment() {
        // Arrange
        List<User> departmentUsers = Arrays.asList(testUser);
        when(userRepository.findByDepartment(DEPARTMENT)).thenReturn(departmentUsers);

        // Act
        List<User> result = userProfileService.getUsersByDepartment(DEPARTMENT);

        // Assert
        assertThat(result).isEqualTo(departmentUsers);
        assertThat(result).hasSize(1);
        verify(userRepository).findByDepartment(DEPARTMENT);
    }

    @Test
    void getUsersByDepartment_WithNoDepartmentUsers_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findByDepartment(DEPARTMENT)).thenReturn(Collections.emptyList());

        // Act
        List<User> result = userProfileService.getUsersByDepartment(DEPARTMENT);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByDepartment(DEPARTMENT);
    }

    @Test
    void getUsersByAuthorizationStatus_WithAuthorizedTrue_ShouldReturnAuthorizedUsers() {
        // Arrange
        List<User> authorizedUsers = Arrays.asList(testUser);
        when(userRepository.findByIsAuthorized(true)).thenReturn(authorizedUsers);

        // Act
        List<User> result = userProfileService.getUsersByAuthorizationStatus(true);

        // Assert
        assertThat(result).isEqualTo(authorizedUsers);
        assertThat(result).hasSize(1);
        verify(userRepository).findByIsAuthorized(true);
    }

    @Test
    void getUsersByAuthorizationStatus_WithAuthorizedFalse_ShouldReturnUnauthorizedUsers() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setIsAuthorized(false);
        List<User> unauthorizedUsers = Arrays.asList(unauthorizedUser);
        when(userRepository.findByIsAuthorized(false)).thenReturn(unauthorizedUsers);

        // Act
        List<User> result = userProfileService.getUsersByAuthorizationStatus(false);

        // Assert
        assertThat(result).isEqualTo(unauthorizedUsers);
        assertThat(result).hasSize(1);
        verify(userRepository).findByIsAuthorized(false);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> allUsers = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(allUsers);

        // Act
        List<User> result = userProfileService.getAllUsers();

        // Assert
        assertThat(result).isEqualTo(allUsers);
        assertThat(result).hasSize(1);
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_WithNoUsers_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<User> result = userProfileService.getAllUsers();

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findAll();
    }

    // ==================== EXISTENCE CHECK TESTS ====================

    @Test
    void userExistsByEmail_WithExistingEmail_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // Act
        boolean result = userProfileService.userExistsByEmail(EMAIL);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail(EMAIL);
    }

    @Test
    void userExistsByEmail_WithNonExistentEmail_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);

        // Act
        boolean result = userProfileService.userExistsByEmail(EMAIL);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByEmail(EMAIL);
    }

    @Test
    void userExistsByEmail_WithNullEmail_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.existsByEmail(null)).thenReturn(false);

        // Act
        boolean result = userProfileService.userExistsByEmail(null);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByEmail(null);
    }

    @Test
    void userExistsByUsername_WithExistingUsername_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        // Act
        boolean result = userProfileService.userExistsByUsername(USERNAME);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).existsByUsername(USERNAME);
    }

    @Test
    void userExistsByUsername_WithNonExistentUsername_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);

        // Act
        boolean result = userProfileService.userExistsByUsername(USERNAME);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByUsername(USERNAME);
    }

    @Test
    void userExistsByUsername_WithNullUsername_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.existsByUsername(null)).thenReturn(false);

        // Act
        boolean result = userProfileService.userExistsByUsername(null);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByUsername(null);
    }

    // ==================== COUNT OPERATIONS TESTS ====================

    @Test
    void getUserCount_ShouldReturnCorrectCount() {
        // Arrange
        when(userRepository.count()).thenReturn(5L);

        // Act
        long result = userProfileService.getUserCount();

        // Assert
        assertThat(result).isEqualTo(5L);
        verify(userRepository).count();
    }

    @Test
    void getUserCount_WithNoUsers_ShouldReturnZero() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);

        // Act
        long result = userProfileService.getUserCount();

        // Assert
        assertThat(result).isEqualTo(0L);
        verify(userRepository).count();
    }

    @Test
    void getAuthorizedUserCount_ShouldReturnCorrectCount() {
        // Arrange
        List<User> authorizedUsers = Arrays.asList(testUser, new User(), new User());
        when(userRepository.findByIsAuthorizedTrue()).thenReturn(authorizedUsers);

        // Act
        long result = userProfileService.getAuthorizedUserCount();

        // Assert
        assertThat(result).isEqualTo(3L);
        verify(userRepository).findByIsAuthorizedTrue();
    }

    @Test
    void getAuthorizedUserCount_WithNoAuthorizedUsers_ShouldReturnZero() {
        // Arrange
        when(userRepository.findByIsAuthorizedTrue()).thenReturn(Collections.emptyList());

        // Act
        long result = userProfileService.getAuthorizedUserCount();

        // Assert
        assertThat(result).isEqualTo(0L);
        verify(userRepository).findByIsAuthorizedTrue();
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    void updateUserProfile_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.updateUserProfile(FIREBASE_UID, USERNAME, EMAIL, DEPARTMENT))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateContactNumber_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.updateContactNumber(FIREBASE_UID, CONTACT_NUMBER))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(testUser);
    }

    @Test
    void getAllUsers_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.getAllUsers())
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(userRepository).findAll();
    }

    // ==================== COMPREHENSIVE INTEGRATION TESTS ====================

    @Test
    void fullWorkflow_GetUpdateAndQuery_ShouldWorkCorrectly() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(userRepository.findByDepartment(DEPARTMENT)).thenReturn(Arrays.asList(testUser));

        // Act
        Optional<User> foundUser = userProfileService.getUserWithRoles(FIREBASE_UID);
        User updatedUser = userProfileService.updateUserProfile(FIREBASE_UID, "newuser", null, null);
        Optional<User> userByEmail = userProfileService.getUserByEmail(EMAIL);
        List<User> departmentUsers = userProfileService.getUsersByDepartment(DEPARTMENT);

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(updatedUser).isEqualTo(testUser);
        assertThat(userByEmail).isPresent();
        assertThat(departmentUsers).hasSize(1);

        verify(userRepository, times(2)).findByUserId(FIREBASE_UID);
        verify(userRepository).save(testUser);
        verify(userRepository).findByEmail(EMAIL);
        verify(userRepository).findByDepartment(DEPARTMENT);
    }

    @Test
    void queryOperations_AllMethods_ShouldWorkCorrectly() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.findByIsAuthorizedTrue()).thenReturn(users);
        when(userRepository.findByDepartment(DEPARTMENT)).thenReturn(users);
        when(userRepository.findByIsAuthorized(true)).thenReturn(users);
        when(userRepository.count()).thenReturn(1L);

        // Act & Assert - All query methods should work
        List<User> all = userProfileService.getAllUsers();
        List<User> authorized = userProfileService.getAuthorizedUsers();
        List<User> byDepartment = userProfileService.getUsersByDepartment(DEPARTMENT);
        List<User> byAuthStatus = userProfileService.getUsersByAuthorizationStatus(true);
        long totalCount = userProfileService.getUserCount();
        long authorizedCount = userProfileService.getAuthorizedUserCount();

        assertThat(all).hasSize(1);
        assertThat(authorized).hasSize(1);
        assertThat(byDepartment).hasSize(1);
        assertThat(byAuthStatus).hasSize(1);
        assertThat(totalCount).isEqualTo(1L);
        assertThat(authorizedCount).isEqualTo(1L);

        verify(userRepository).findAll();
        verify(userRepository, times(2)).findByIsAuthorizedTrue(); // Called by both getAuthorizedUsers and getAuthorizedUserCount
        verify(userRepository).findByDepartment(DEPARTMENT);
        verify(userRepository).findByIsAuthorized(true);
        verify(userRepository).count();
    }

    @Test
    void existenceChecks_ShouldWorkCorrectly() {
        // Arrange
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        // Act
        boolean emailExists = userProfileService.userExistsByEmail(EMAIL);
        boolean usernameExists = userProfileService.userExistsByUsername(USERNAME);

        // Assert
        assertThat(emailExists).isTrue();
        assertThat(usernameExists).isTrue();

        verify(userRepository).existsByEmail(EMAIL);
        verify(userRepository).existsByUsername(USERNAME);
    }

    // ==================== ADMIN DOLIBARR UID MANAGEMENT TESTS ====================

    @Test
    void updateUserDolibarrIdAsAdmin_WithValidAdmin_ShouldUpdateDolibarrId() {
        // Arrange
        String newDolibarrId = "new-dolibarr-456";
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userProfileService.updateUserDolibarrIdAsAdmin(ADMIN_FIREBASE_UID, FIREBASE_UID, newDolibarrId);

        // Assert
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserDolibarrIdAsAdmin_WithNonExistentAdmin_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.updateUserDolibarrIdAsAdmin(ADMIN_FIREBASE_UID, FIREBASE_UID, DOLIBARR_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Admin user not found with Firebase UID: " + ADMIN_FIREBASE_UID);

        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository, never()).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserDolibarrIdAsAdmin_WithNonAdminUser_ShouldThrowSecurityException() {
        // Arrange
        User nonAdminUser = new User();
        nonAdminUser.setUserId(ADMIN_FIREBASE_UID);
        nonAdminUser.setIsAdmin(false);
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.of(nonAdminUser));

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.updateUserDolibarrIdAsAdmin(ADMIN_FIREBASE_UID, FIREBASE_UID, DOLIBARR_ID))
            .isInstanceOf(SecurityException.class)
            .hasMessage("Administrator privileges required to manage Dolibarr UIDs");

        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository, never()).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserDolibarrIdAsAdmin_WithNonExistentTargetUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.updateUserDolibarrIdAsAdmin(ADMIN_FIREBASE_UID, FIREBASE_UID, DOLIBARR_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Target user not found with Firebase UID: " + FIREBASE_UID);

        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserDolibarrIdAsAdmin_WithValidAdmin_ShouldReturnDolibarrId() {
        // Arrange
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));

        // Act
        String result = userProfileService.getUserDolibarrIdAsAdmin(ADMIN_FIREBASE_UID, FIREBASE_UID);

        // Assert
        assertThat(result).isEqualTo(DOLIBARR_ID);
        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getUserDolibarrIdAsAdmin_WithNonExistentAdmin_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.getUserDolibarrIdAsAdmin(ADMIN_FIREBASE_UID, FIREBASE_UID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Admin user not found with Firebase UID: " + ADMIN_FIREBASE_UID);

        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository, never()).findByUserId(FIREBASE_UID);
    }

    @Test
    void getUserDolibarrIdAsAdmin_WithNonAdminUser_ShouldThrowSecurityException() {
        // Arrange
        User nonAdminUser = new User();
        nonAdminUser.setUserId(ADMIN_FIREBASE_UID);
        nonAdminUser.setIsAdmin(false);
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.of(nonAdminUser));

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.getUserDolibarrIdAsAdmin(ADMIN_FIREBASE_UID, FIREBASE_UID))
            .isInstanceOf(SecurityException.class)
            .hasMessage("Administrator privileges required to access Dolibarr UIDs");

        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository, never()).findByUserId(FIREBASE_UID);
    }

    @Test
    void getUserDolibarrIdAsAdmin_WithNonExistentTargetUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.getUserDolibarrIdAsAdmin(ADMIN_FIREBASE_UID, FIREBASE_UID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Target user not found with Firebase UID: " + FIREBASE_UID);

        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getAllUsersAsAdmin_WithValidAdmin_ShouldReturnUsersAndStatistics() {
        // Arrange
        User normalUser = new User();
        normalUser.setIsAdmin(false);
        normalUser.setIsAuthorized(true); // Only authorized users should be returned
        
        User unauthorizedUser = new User();
        unauthorizedUser.setIsAdmin(false);
        unauthorizedUser.setIsAuthorized(false); // This user should be filtered out
        
        List<User> allUsers = Arrays.asList(adminUser, normalUser, unauthorizedUser);
        
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findAll()).thenReturn(allUsers);

        // Act
        java.util.Map<String, Object> result = userProfileService.getAllUsersAsAdmin(ADMIN_FIREBASE_UID);

        // Assert
        assertThat(result).containsKey("users");
        assertThat(result).containsKey("statistics");
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) result.get("users");
        assertThat(users).hasSize(2); // Should only return authorized users (admin and normalUser)
        assertThat(users).contains(adminUser, normalUser);
        assertThat(users).doesNotContain(unauthorizedUser); // Unauthorized user should not be in the list
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> statistics = (java.util.Map<String, Object>) result.get("statistics");
        assertThat(statistics.get("totalUsers")).isEqualTo(2); // Only authorized users counted
        assertThat(statistics.get("adminUsers")).isEqualTo(1L);
        assertThat(statistics.get("normalUsers")).isEqualTo(1L);
        
        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsersAsAdmin_WithNonExistentAdmin_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.getAllUsersAsAdmin(ADMIN_FIREBASE_UID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Admin user not found with Firebase UID: " + ADMIN_FIREBASE_UID);

        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository, never()).findAll();
    }

    @Test
    void getAllUsersAsAdmin_WithNonAdminUser_ShouldThrowSecurityException() {
        // Arrange
        User nonAdminUser = new User();
        nonAdminUser.setUserId(ADMIN_FIREBASE_UID);
        nonAdminUser.setIsAdmin(false);
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.of(nonAdminUser));

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.getAllUsersAsAdmin(ADMIN_FIREBASE_UID))
            .isInstanceOf(SecurityException.class)
            .hasMessage("Administrator privileges required to access all users");

        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository, never()).findAll();
    }

    @Test
    void getAllUsersAsAdmin_WithEmptyUserList_ShouldReturnEmptyStatistics() {
        // Arrange
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        java.util.Map<String, Object> result = userProfileService.getAllUsersAsAdmin(ADMIN_FIREBASE_UID);

        // Assert
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) result.get("users");
        assertThat(users).isEmpty();
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> statistics = (java.util.Map<String, Object>) result.get("statistics");
        assertThat(statistics.get("totalUsers")).isEqualTo(0);
        assertThat(statistics.get("adminUsers")).isEqualTo(0L);
        assertThat(statistics.get("normalUsers")).isEqualTo(0L);
        
        verify(userRepository).findByUserId(ADMIN_FIREBASE_UID);
        verify(userRepository).findAll();
    }

    // ==================== ADMIN OPERATIONS INTEGRATION TESTS ====================

    @Test
    void adminOperations_FullWorkflow_ShouldWorkCorrectly() {
        // Arrange
        String newDolibarrId = "new-dolibarr-789";
        when(userRepository.findByUserId(ADMIN_FIREBASE_UID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userRepository.findAll()).thenReturn(Arrays.asList(adminUser, testUser));

        // Act
        String originalDolibarrId = userProfileService.getUserDolibarrIdAsAdmin(ADMIN_FIREBASE_UID, FIREBASE_UID);
        User updatedUser = userProfileService.updateUserDolibarrIdAsAdmin(ADMIN_FIREBASE_UID, FIREBASE_UID, newDolibarrId);
        java.util.Map<String, Object> allUsersData = userProfileService.getAllUsersAsAdmin(ADMIN_FIREBASE_UID);

        // Assert
        assertThat(originalDolibarrId).isEqualTo(DOLIBARR_ID);
        assertThat(updatedUser).isEqualTo(testUser);
        assertThat(allUsersData).containsKey("users");
        assertThat(allUsersData).containsKey("statistics");

        verify(userRepository, times(3)).findByUserId(ADMIN_FIREBASE_UID); // Called 3 times
        verify(userRepository, times(2)).findByUserId(FIREBASE_UID); // Called 2 times
        verify(userRepository).save(testUser);
        verify(userRepository).findAll();
    }
}
