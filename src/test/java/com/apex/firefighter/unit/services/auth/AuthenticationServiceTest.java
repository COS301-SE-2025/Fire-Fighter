package com.apex.firefighter.unit.services.auth;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User existingUser;
    private final String FIREBASE_UID = "firebase-uid-123";
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final String DEPARTMENT = "IT";

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setUserId(FIREBASE_UID);
        existingUser.setUsername(USERNAME);
        existingUser.setEmail(EMAIL);
        existingUser.setDepartment(DEPARTMENT);
        existingUser.setIsAuthorized(true);
        existingUser.setIsAdmin(false);
        existingUser.setLastLogin(ZonedDateTime.now().minusDays(1));
    }

    // ==================== VERIFY OR CREATE USER TESTS ====================

    @Test
    void verifyOrCreateUser_WithExistingUser_ShouldUpdateLastLoginAndReturnUser() {
        // Arrange
        ZonedDateTime originalLastLogin = existingUser.getLastLogin();
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User result = authenticationService.verifyOrCreateUser(FIREBASE_UID, USERNAME, EMAIL, DEPARTMENT);

        // Assert
        assertThat(result).isEqualTo(existingUser);
        assertThat(result.getLastLogin()).isAfter(originalLastLogin);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(existingUser);
    }

    @Test
    void verifyOrCreateUser_WithNewUser_ShouldCreateAndReturnNewUser() {
        // Arrange
        User newUser = new User(FIREBASE_UID, USERNAME, EMAIL, DEPARTMENT);
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User result = authenticationService.verifyOrCreateUser(FIREBASE_UID, USERNAME, EMAIL, DEPARTMENT);

        // Assert
        assertThat(result).isEqualTo(newUser);
        assertThat(result.getUserId()).isEqualTo(FIREBASE_UID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getDepartment()).isEqualTo(DEPARTMENT);
        // Note: lastLogin is set in the User constructor for new users
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void verifyOrCreateUser_WithExistingUserNullLastLogin_ShouldSetLastLogin() {
        // Arrange
        existingUser.setLastLogin(null);
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User result = authenticationService.verifyOrCreateUser(FIREBASE_UID, USERNAME, EMAIL, DEPARTMENT);

        // Assert
        assertThat(result).isEqualTo(existingUser);
        assertThat(result.getLastLogin()).isNotNull();
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(existingUser);
    }

    @Test
    void verifyOrCreateUser_WithDifferentUsernameForExistingUser_ShouldUpdateLastLoginOnly() {
        // Arrange
        String differentUsername = "differentuser";
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User result = authenticationService.verifyOrCreateUser(FIREBASE_UID, differentUsername, EMAIL, DEPARTMENT);

        // Assert
        assertThat(result).isEqualTo(existingUser);
        assertThat(result.getUsername()).isEqualTo(USERNAME); // Should remain original username
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(existingUser);
    }

    @Test
    void verifyOrCreateUser_WithDifferentEmailForExistingUser_ShouldUpdateLastLoginOnly() {
        // Arrange
        String differentEmail = "different@example.com";
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User result = authenticationService.verifyOrCreateUser(FIREBASE_UID, USERNAME, differentEmail, DEPARTMENT);

        // Assert
        assertThat(result).isEqualTo(existingUser);
        assertThat(result.getEmail()).isEqualTo(EMAIL); // Should remain original email
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(existingUser);
    }

    @Test
    void verifyOrCreateUser_WithDifferentDepartmentForExistingUser_ShouldUpdateLastLoginOnly() {
        // Arrange
        String differentDepartment = "HR";
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User result = authenticationService.verifyOrCreateUser(FIREBASE_UID, USERNAME, EMAIL, differentDepartment);

        // Assert
        assertThat(result).isEqualTo(existingUser);
        assertThat(result.getDepartment()).isEqualTo(DEPARTMENT); // Should remain original department
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(existingUser);
    }

    // ==================== GET USER BY FIREBASE UID TESTS ====================

    @Test
    void getUserByFirebaseUid_WithExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(existingUser));

        // Act
        Optional<User> result = authenticationService.getUserByFirebaseUid(FIREBASE_UID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(existingUser);
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getUserByFirebaseUid_WithNonExistentUser_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = authenticationService.getUserByFirebaseUid(FIREBASE_UID);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getUserByFirebaseUid_WithNullFirebaseUid_ShouldDelegateToRepository() {
        // Arrange
        when(userRepository.findByUserId(null)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = authenticationService.getUserByFirebaseUid(null);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByUserId(null);
    }

    @Test
    void getUserByFirebaseUid_WithEmptyFirebaseUid_ShouldDelegateToRepository() {
        // Arrange
        when(userRepository.findByUserId("")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = authenticationService.getUserByFirebaseUid("");

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByUserId("");
    }

    // ==================== USER EXISTS TESTS ====================

    @Test
    void userExists_WithExistingUser_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByUserId(FIREBASE_UID)).thenReturn(true);

        // Act
        boolean result = authenticationService.userExists(FIREBASE_UID);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).existsByUserId(FIREBASE_UID);
    }

    @Test
    void userExists_WithNonExistentUser_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByUserId(FIREBASE_UID)).thenReturn(false);

        // Act
        boolean result = authenticationService.userExists(FIREBASE_UID);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByUserId(FIREBASE_UID);
    }

    @Test
    void userExists_WithNullFirebaseUid_ShouldDelegateToRepository() {
        // Arrange
        when(userRepository.existsByUserId(null)).thenReturn(false);

        // Act
        boolean result = authenticationService.userExists(null);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByUserId(null);
    }

    @Test
    void userExists_WithEmptyFirebaseUid_ShouldDelegateToRepository() {
        // Arrange
        when(userRepository.existsByUserId("")).thenReturn(false);

        // Act
        boolean result = authenticationService.userExists("");

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByUserId("");
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void verifyOrCreateUser_WithNullUsername_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = authenticationService.verifyOrCreateUser(FIREBASE_UID, null, EMAIL, DEPARTMENT);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(FIREBASE_UID);
        assertThat(result.getUsername()).isNull();
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void verifyOrCreateUser_WithNullEmail_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = authenticationService.verifyOrCreateUser(FIREBASE_UID, USERNAME, null, DEPARTMENT);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(FIREBASE_UID);
        assertThat(result.getEmail()).isNull();
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void verifyOrCreateUser_WithNullDepartment_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = authenticationService.verifyOrCreateUser(FIREBASE_UID, USERNAME, EMAIL, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(FIREBASE_UID);
        assertThat(result.getDepartment()).isNull();
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void verifyOrCreateUser_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        try {
            authenticationService.verifyOrCreateUser(FIREBASE_UID, USERNAME, EMAIL, DEPARTMENT);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Database error");
        }

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(userRepository, never()).save(any(User.class));
    }
}
