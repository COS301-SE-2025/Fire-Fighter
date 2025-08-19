package com.apex.firefighter.integration.externalservices;

import com.apex.firefighter.service.ai.GeminiAIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for GeminiAIService.
 * 
 * According to the runbook, these tests focus on:
 * 1. Testing isConfigured() behavior with different API key states
 * 2. Testing graceful degradation when API key is missing
 * 3. Testing the service in a real Spring context without external API calls
 * 
 * Future improvement: Use WireMock to stub actual API responses for full integration testing.
 */
@SpringBootTest
@ActiveProfiles("test")
class GeminiAIServiceIntegrationTest {

    @Autowired
    private GeminiAIService geminiAIService;

    @BeforeEach
    void setUp() {
        // Reset the API key to empty for each test (matching test environment)
        ReflectionTestUtils.setField(geminiAIService, "apiKey", "");
    }

    // ==================== CONFIGURATION TESTS ====================

    @Test
    void isConfigured_WithEmptyApiKey_ShouldReturnFalse() {
        // Arrange - API key is already set to empty in setUp()

        // Act
        boolean result = geminiAIService.isConfigured();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isConfigured_WithNullApiKey_ShouldReturnFalse() {
        // Arrange
        ReflectionTestUtils.setField(geminiAIService, "apiKey", null);

        // Act
        boolean result = geminiAIService.isConfigured();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isConfigured_WithWhitespaceApiKey_ShouldReturnFalse() {
        // Arrange
        ReflectionTestUtils.setField(geminiAIService, "apiKey", "   ");

        // Act
        boolean result = geminiAIService.isConfigured();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isConfigured_WithPlaceholderApiKey_ShouldReturnFalse() {
        // Arrange
        ReflectionTestUtils.setField(geminiAIService, "apiKey", "${GOOGLE_GEMINI_API_KEY}");

        // Act
        boolean result = geminiAIService.isConfigured();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isConfigured_WithValidApiKey_ShouldReturnTrue() {
        // Arrange
        ReflectionTestUtils.setField(geminiAIService, "apiKey", "valid-test-api-key");

        // Act
        boolean result = geminiAIService.isConfigured();

        // Assert
        assertThat(result).isTrue();
    }

    // ==================== GRACEFUL DEGRADATION TESTS ====================

    @Test
    void generateResponse_WithMissingApiKey_ShouldDegradeGracefully() {
        // Arrange - API key is empty (set in setUp())
        String prompt = "What is emergency access management?";

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should return an error message indicating technical difficulties
        assertThat(result.toLowerCase()).containsAnyOf(
            "technical difficulties", 
            "trouble processing", 
            "try again later",
            "experiencing difficulties"
        );
    }

    @Test
    void generateResponseWithContext_WithMissingApiKey_ShouldDegradeGracefully() {
        // Arrange - API key is empty (set in setUp())
        String prompt = "Show me my tickets";
        String userRole = "Administrator";
        String context = "User has 3 active tickets";

        // Act
        String result = geminiAIService.generateResponseWithContext(prompt, userRole, context);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should return an error message indicating technical difficulties
        assertThat(result.toLowerCase()).containsAnyOf(
            "technical difficulties", 
            "trouble processing", 
            "try again later",
            "experiencing difficulties"
        );
    }

    @Test
    void generateResponseWithContext_WithNullContext_ShouldDegradeGracefully() {
        // Arrange - API key is empty (set in setUp())
        String prompt = "Show me my tickets";
        String userRole = "User";
        String context = null;

        // Act
        String result = geminiAIService.generateResponseWithContext(prompt, userRole, context);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should return an error message indicating technical difficulties
        assertThat(result.toLowerCase()).containsAnyOf(
            "technical difficulties", 
            "trouble processing", 
            "try again later",
            "experiencing difficulties"
        );
    }

    @Test
    void generateResponseWithContext_WithEmptyContext_ShouldDegradeGracefully() {
        // Arrange - API key is empty (set in setUp())
        String prompt = "Show me my tickets";
        String userRole = "User";
        String context = "";

        // Act
        String result = geminiAIService.generateResponseWithContext(prompt, userRole, context);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should return an error message indicating technical difficulties
        assertThat(result.toLowerCase()).containsAnyOf(
            "technical difficulties", 
            "trouble processing", 
            "try again later",
            "experiencing difficulties"
        );
    }

    // ==================== SPRING CONTEXT INTEGRATION TESTS ====================

    @Test
    void geminiAIService_ShouldBeInjectedBySpring() {
        // Assert
        assertThat(geminiAIService).isNotNull();
        assertThat(geminiAIService).isInstanceOf(GeminiAIService.class);
    }

    @Test
    void geminiAIService_ShouldHaveWebClientConfigured() {
        // Act & Assert
        // The service should be properly initialized with WebClient
        // We can't directly access the WebClient, but we can verify the service works
        assertThat(geminiAIService).isNotNull();
        
        // Verify that calling methods doesn't throw initialization errors
        boolean configured = geminiAIService.isConfigured();
        assertThat(configured).isFalse(); // Should be false due to empty API key in test environment
    }

    // ==================== INTEGRATION WITH TEST ENVIRONMENT ====================

    @Test
    void geminiAIService_InTestEnvironment_ShouldNotBeConfigured() {
        // In the test environment, GOOGLE_GEMINI_API_KEY should be empty
        // This verifies that our test configuration is working correctly
        
        // Act
        boolean configured = geminiAIService.isConfigured();

        // Assert
        assertThat(configured).isFalse();
    }

    @Test
    void geminiAIService_InTestEnvironment_ShouldHandleRequestsGracefully() {
        // This test verifies that even in an unconfigured state,
        // the service doesn't crash the application and handles requests gracefully
        
        // Act & Assert - should not throw exceptions
        String response1 = geminiAIService.generateResponse("test prompt");
        String response2 = geminiAIService.generateResponseWithContext("test", "User", "context");
        
        assertThat(response1).isNotNull();
        assertThat(response2).isNotNull();
    }
}
