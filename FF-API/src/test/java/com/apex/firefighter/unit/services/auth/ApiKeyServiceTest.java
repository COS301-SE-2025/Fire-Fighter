package com.apex.firefighter.unit.services.auth;

import com.apex.firefighter.model.ApiKey;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.ApiKeyRepository;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.auth.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApiKeyService apiKeyService;

    private User testUser;
    private ApiKey testApiKey;
    private final String USER_ID = "test-user-123";
    private final String GENERATED_API_KEY = "test-generated-api-key-value";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(USER_ID);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDepartment("IT");
        testUser.setIsAuthorized(true);

        testApiKey = new ApiKey();
        testApiKey.setApiKey(GENERATED_API_KEY);
        testApiKey.setUser(testUser);
        testApiKey.setIsActive(true);
    }

    // ==================== GENERATE API KEY TESTS ====================

    @Test
    void generateApiKeyForUser_WithValidUser_ShouldCreateAndReturnApiKey() {
        // Arrange
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));
        when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(testApiKey);

        // Mock the static ApiKeyGenerator.generateApiKey() method
        try (MockedStatic<com.apex.firefighter.service.auth.ApiKeyGenerator> mockedGenerator = 
             mockStatic(com.apex.firefighter.service.auth.ApiKeyGenerator.class)) {
            
            mockedGenerator.when(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey)
                          .thenReturn(GENERATED_API_KEY);

            // Act
            ApiKey result = apiKeyService.generateApiKeyForUser(USER_ID);

            // Assert
            assertThat(result).isEqualTo(testApiKey);
            assertThat(result.getApiKey()).isEqualTo(GENERATED_API_KEY);
            assertThat(result.getUser()).isEqualTo(testUser);
            
            verify(userRepository).findByUserId(USER_ID);
            verify(apiKeyRepository).save(any(ApiKey.class));
            mockedGenerator.verify(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey);
        }
    }

    @Test
    void generateApiKeyForUser_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> apiKeyService.generateApiKeyForUser(USER_ID))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found: " + USER_ID);

        verify(userRepository).findByUserId(USER_ID);
        verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }

    @Test
    void generateApiKeyForUser_WithNullUserId_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> apiKeyService.generateApiKeyForUser(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found: null");

        verify(userRepository).findByUserId(null);
        verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }

    @Test
    void generateApiKeyForUser_WithEmptyUserId_ShouldThrowException() {
        // Arrange
        String emptyUserId = "";
        when(userRepository.findByUserId(emptyUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> apiKeyService.generateApiKeyForUser(emptyUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found: " + emptyUserId);

        verify(userRepository).findByUserId(emptyUserId);
        verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }

    @Test
    void generateApiKeyForUser_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));
        when(apiKeyRepository.save(any(ApiKey.class))).thenThrow(new RuntimeException("Database error"));

        // Mock the static ApiKeyGenerator.generateApiKey() method
        try (MockedStatic<com.apex.firefighter.service.auth.ApiKeyGenerator> mockedGenerator = 
             mockStatic(com.apex.firefighter.service.auth.ApiKeyGenerator.class)) {
            
            mockedGenerator.when(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey)
                          .thenReturn(GENERATED_API_KEY);

            // Act & Assert
            assertThatThrownBy(() -> apiKeyService.generateApiKeyForUser(USER_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

            verify(userRepository).findByUserId(USER_ID);
            verify(apiKeyRepository).save(any(ApiKey.class));
            mockedGenerator.verify(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey);
        }
    }

    @Test
    void generateApiKeyForUser_WithUserRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByUserId(USER_ID)).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThatThrownBy(() -> apiKeyService.generateApiKeyForUser(USER_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database connection error");

        verify(userRepository).findByUserId(USER_ID);
        verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }

    // ==================== EDGE CASES AND VALIDATION TESTS ====================

    @Test
    void generateApiKeyForUser_WithValidUserButNullApiKeyGeneration_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));
        when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(testApiKey);

        // Mock the static ApiKeyGenerator.generateApiKey() method to return null
        try (MockedStatic<com.apex.firefighter.service.auth.ApiKeyGenerator> mockedGenerator = 
             mockStatic(com.apex.firefighter.service.auth.ApiKeyGenerator.class)) {
            
            mockedGenerator.when(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey)
                          .thenReturn(null);

            // Act
            ApiKey result = apiKeyService.generateApiKeyForUser(USER_ID);

            // Assert
            assertThat(result).isEqualTo(testApiKey);
            
            verify(userRepository).findByUserId(USER_ID);
            verify(apiKeyRepository).save(any(ApiKey.class));
            mockedGenerator.verify(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey);
        }
    }

    @Test
    void generateApiKeyForUser_WithValidUserButEmptyApiKeyGeneration_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));
        when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(testApiKey);

        // Mock the static ApiKeyGenerator.generateApiKey() method to return empty string
        try (MockedStatic<com.apex.firefighter.service.auth.ApiKeyGenerator> mockedGenerator = 
             mockStatic(com.apex.firefighter.service.auth.ApiKeyGenerator.class)) {
            
            mockedGenerator.when(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey)
                          .thenReturn("");

            // Act
            ApiKey result = apiKeyService.generateApiKeyForUser(USER_ID);

            // Assert
            assertThat(result).isEqualTo(testApiKey);
            
            verify(userRepository).findByUserId(USER_ID);
            verify(apiKeyRepository).save(any(ApiKey.class));
            mockedGenerator.verify(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey);
        }
    }

    @Test
    void generateApiKeyForUser_WithApiKeyGeneratorException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));

        // Mock the static ApiKeyGenerator.generateApiKey() method to throw exception
        try (MockedStatic<com.apex.firefighter.service.auth.ApiKeyGenerator> mockedGenerator = 
             mockStatic(com.apex.firefighter.service.auth.ApiKeyGenerator.class)) {
            
            mockedGenerator.when(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey)
                          .thenThrow(new RuntimeException("API key generation failed"));

            // Act & Assert
            assertThatThrownBy(() -> apiKeyService.generateApiKeyForUser(USER_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("API key generation failed");

            verify(userRepository).findByUserId(USER_ID);
            verify(apiKeyRepository, never()).save(any(ApiKey.class));
            mockedGenerator.verify(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey);
        }
    }

    // ==================== INTEGRATION WORKFLOW TESTS ====================

    @Test
    void generateApiKeyForUser_FullWorkflow_ShouldWorkCorrectly() {
        // Arrange
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
            ApiKey savedApiKey = invocation.getArgument(0);
            // Simulate database auto-generation of ID
            return savedApiKey;
        });

        // Mock the static ApiKeyGenerator.generateApiKey() method
        try (MockedStatic<com.apex.firefighter.service.auth.ApiKeyGenerator> mockedGenerator = 
             mockStatic(com.apex.firefighter.service.auth.ApiKeyGenerator.class)) {
            
            mockedGenerator.when(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey)
                          .thenReturn(GENERATED_API_KEY);

            // Act
            ApiKey result = apiKeyService.generateApiKeyForUser(USER_ID);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUser()).isEqualTo(testUser);
            
            verify(userRepository).findByUserId(USER_ID);
            verify(apiKeyRepository).save(any(ApiKey.class));
            mockedGenerator.verify(com.apex.firefighter.service.auth.ApiKeyGenerator::generateApiKey);
        }
    }
}
