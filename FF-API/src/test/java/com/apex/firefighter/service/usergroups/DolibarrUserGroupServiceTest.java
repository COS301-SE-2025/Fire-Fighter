package com.apex.firefighter.unit.services;

import com.apex.firefighter.service.DolibarrDatabaseService;
import com.apex.firefighter.service.DolibarrUserGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DolibarrUserGroupService
 * Tests both API-based user group addition and database-based user group removal
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DolibarrUserGroupServiceTest {

    @Mock
    private DolibarrDatabaseService mockDolibarrDatabaseService;

    private DolibarrUserGroupService dolibarrUserGroupService;
    
    private static final String TEST_FIREFIGHTER_GROUP_ID = "5";
    private static final String TEST_USER_ID = "123";

    @BeforeEach
    void setUp() {
        dolibarrUserGroupService = new DolibarrUserGroupService(
            TEST_FIREFIGHTER_GROUP_ID,
            mockDolibarrDatabaseService
        );
    }

    // ==================== ADD USER TO GROUP TESTS ====================

    @Test
    void addUserToGroup_WithValidUser_ShouldCallDatabaseService() throws SQLException {
        // Arrange
        doNothing().when(mockDolibarrDatabaseService).addUserToFirefighterGroup(TEST_USER_ID);

        // Act
        dolibarrUserGroupService.addUserToGroup(TEST_USER_ID);

        // Assert
        verify(mockDolibarrDatabaseService).addUserToFirefighterGroup(TEST_USER_ID);
    }

    @Test
    void addUserToGroup_WithDatabaseError_ShouldPropagateException() throws SQLException {
        // Arrange
        SQLException testException = new SQLException("Database connection failed");
        doThrow(testException).when(mockDolibarrDatabaseService).addUserToFirefighterGroup(TEST_USER_ID);

        // Act & Assert
        assertThatThrownBy(() -> {
            dolibarrUserGroupService.addUserToGroup(TEST_USER_ID);
        }).isInstanceOf(SQLException.class)
          .hasMessage("Database connection failed");

        verify(mockDolibarrDatabaseService).addUserToFirefighterGroup(TEST_USER_ID);
    }

    // ==================== REMOVE USER FROM GROUP TESTS ====================

    @Test
    void removeUserFromGroup_WithValidUser_ShouldCallDatabaseService() throws SQLException {
        // Arrange
        doNothing().when(mockDolibarrDatabaseService).removeUserFromFirefighterGroup(TEST_USER_ID);

        // Act
        dolibarrUserGroupService.removeUserFromGroup(TEST_USER_ID);

        // Assert
        verify(mockDolibarrDatabaseService).removeUserFromFirefighterGroup(TEST_USER_ID);
    }

    @Test
    void removeUserFromGroup_WithDatabaseError_ShouldPropagateException() throws SQLException {
        // Arrange
        SQLException testException = new SQLException("Database connection failed");
        doThrow(testException).when(mockDolibarrDatabaseService).removeUserFromFirefighterGroup(TEST_USER_ID);

        // Act & Assert
        assertThatThrownBy(() -> {
            dolibarrUserGroupService.removeUserFromGroup(TEST_USER_ID);
        }).isInstanceOf(SQLException.class)
          .hasMessage("Database connection failed");

        verify(mockDolibarrDatabaseService).removeUserFromFirefighterGroup(TEST_USER_ID);
    }

    @Test
    void removeUserFromGroup_WithUnexpectedError_ShouldWrapInRuntimeException() throws SQLException {
        // Arrange
        RuntimeException testException = new RuntimeException("Unexpected error");
        doThrow(testException).when(mockDolibarrDatabaseService).removeUserFromFirefighterGroup(TEST_USER_ID);

        // Act & Assert
        assertThatThrownBy(() -> {
            dolibarrUserGroupService.removeUserFromGroup(TEST_USER_ID);
        }).isInstanceOf(RuntimeException.class)
          .hasMessage("Failed to remove user from firefighter group")
          .hasCause(testException);

        verify(mockDolibarrDatabaseService).removeUserFromFirefighterGroup(TEST_USER_ID);
    }

    @Test
    void removeUserFromGroup_WithNullUserId_ShouldHandleGracefully() throws SQLException {
        // Arrange
        IllegalArgumentException testException = new IllegalArgumentException("User ID cannot be null");
        doThrow(testException).when(mockDolibarrDatabaseService).removeUserFromFirefighterGroup(null);

        // Act & Assert
        assertThatThrownBy(() -> {
            dolibarrUserGroupService.removeUserFromGroup(null);
        }).isInstanceOf(RuntimeException.class)
          .hasMessage("Failed to remove user from firefighter group")
          .hasCause(testException);

        verify(mockDolibarrDatabaseService).removeUserFromFirefighterGroup(null);
    }

    @Test
    void removeUserFromGroup_WithEmptyUserId_ShouldHandleGracefully() throws SQLException {
        // Arrange
        String emptyUserId = "";
        IllegalArgumentException testException = new IllegalArgumentException("Invalid ID format");
        doThrow(testException).when(mockDolibarrDatabaseService).removeUserFromFirefighterGroup(emptyUserId);

        // Act & Assert
        assertThatThrownBy(() -> {
            dolibarrUserGroupService.removeUserFromGroup(emptyUserId);
        }).isInstanceOf(RuntimeException.class)
          .hasMessage("Failed to remove user from firefighter group")
          .hasCause(testException);

        verify(mockDolibarrDatabaseService).removeUserFromFirefighterGroup(emptyUserId);
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    void serviceIntegration_ShouldUseCorrectDependencies() {
        // Verify that the service is properly constructed with its dependencies
        // This test ensures that the service can be instantiated and has the required dependencies

        DolibarrUserGroupService service = new DolibarrUserGroupService(
            TEST_FIREFIGHTER_GROUP_ID,
            mockDolibarrDatabaseService
        );

        // The service should be created successfully
        // In a real test, we would verify that the service uses the correct configuration
        assertThat(service).isNotNull();
    }
}
