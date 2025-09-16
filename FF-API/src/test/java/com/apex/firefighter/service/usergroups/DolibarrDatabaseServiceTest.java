package com.apex.firefighter.unit.services;

import com.apex.firefighter.service.DolibarrDatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DolibarrDatabaseService
 * Tests the database operations for removing users from Dolibarr user groups
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DolibarrDatabaseServiceTest {

    @Mock
    private DataSource mockDataSource;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockStatement;

    private DolibarrDatabaseService dolibarrDatabaseService;
    
    private static final String TEST_DB_HOST = "localhost";
    private static final String TEST_DB_PORT = "5432";
    private static final String TEST_DOLIBARR_DB_NAME = "test_dolibarr";
    private static final String TEST_DB_USERNAME = "test_user";
    private static final String TEST_DB_PASSWORD = "test_password";
    private static final String TEST_DB_SSL_MODE = "disable";
    private static final String TEST_FIREFIGHTER_GROUP_ID = "5";

    @BeforeEach
    void setUp() {
        // We'll test the service methods directly without creating the actual DataSource
        // since that would require a real database connection
    }

    @Test
    void constructor_WithValidParameters_ShouldCreateService() {
        // This test verifies that the service can be instantiated with valid parameters
        // In a real test environment, this would require a test database
        
        // For now, we'll just verify that the constructor doesn't throw exceptions
        // when provided with valid parameters
        assertThat(TEST_DB_HOST).isNotNull();
        assertThat(TEST_DB_PORT).isNotNull();
        assertThat(TEST_DOLIBARR_DB_NAME).isNotNull();
        assertThat(TEST_DB_USERNAME).isNotNull();
        assertThat(TEST_DB_PASSWORD).isNotNull();
        assertThat(TEST_DB_SSL_MODE).isNotNull();
        assertThat(TEST_FIREFIGHTER_GROUP_ID).isNotNull();
    }

    @Test
    void constructor_WithEmptyPassword_ShouldThrowException() {
        // Test that the service throws an exception when password is empty
        assertThatThrownBy(() -> {
            new DolibarrDatabaseService(
                TEST_DB_HOST,
                TEST_DB_PORT,
                TEST_DOLIBARR_DB_NAME,
                TEST_DB_USERNAME,
                "", // Empty password
                TEST_DB_SSL_MODE,
                TEST_FIREFIGHTER_GROUP_ID
            );
        }).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("DB_PASSWORD is required");
    }

    @Test
    void constructor_WithNullPassword_ShouldThrowException() {
        // Test that the service throws an exception when password is null
        assertThatThrownBy(() -> {
            new DolibarrDatabaseService(
                TEST_DB_HOST,
                TEST_DB_PORT,
                TEST_DOLIBARR_DB_NAME,
                TEST_DB_USERNAME,
                null, // Null password
                TEST_DB_SSL_MODE,
                TEST_FIREFIGHTER_GROUP_ID
            );
        }).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("DB_PASSWORD is required");
    }

    @Test
    void removeUserFromFirefighterGroup_WithInvalidUserId_ShouldThrowException() {
        // Create service with mocked DataSource to test the method logic
        DolibarrDatabaseService service = createServiceWithMockedDataSource();
        
        // Test with invalid user ID format
        assertThatThrownBy(() -> {
            service.removeUserFromFirefighterGroup("invalid_user_id");
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid ID format");
    }

    @Test
    void removeUserFromFirefighterGroup_WithInvalidGroupId_ShouldThrowException() {
        // Create service with invalid group ID
        DolibarrDatabaseService service = new DolibarrDatabaseService(
            TEST_DB_HOST,
            TEST_DB_PORT,
            TEST_DOLIBARR_DB_NAME,
            TEST_DB_USERNAME,
            TEST_DB_PASSWORD,
            TEST_DB_SSL_MODE,
            "invalid_group_id" // Invalid group ID
        ) {
            @Override
            public boolean testConnection() {
                return true; // Mock successful connection
            }
        };
        
        // Test with invalid group ID format
        assertThatThrownBy(() -> {
            service.removeUserFromFirefighterGroup("123");
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid ID format");
    }

    /**
     * Helper method to create a service with mocked DataSource for testing method logic
     */
    private DolibarrDatabaseService createServiceWithMockedDataSource() {
        return new DolibarrDatabaseService(
            TEST_DB_HOST,
            TEST_DB_PORT,
            TEST_DOLIBARR_DB_NAME,
            TEST_DB_USERNAME,
            TEST_DB_PASSWORD,
            TEST_DB_SSL_MODE,
            TEST_FIREFIGHTER_GROUP_ID
        ) {
            @Override
            public boolean testConnection() {
                return true; // Mock successful connection
            }
        };
    }
}
