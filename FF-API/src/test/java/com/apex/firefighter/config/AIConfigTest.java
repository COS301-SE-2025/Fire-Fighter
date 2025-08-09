package com.apex.firefighter.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AIConfigTest {

    private AIConfig aiConfig;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        aiConfig = new AIConfig();
        
        // Capture System.out and System.err
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @Test
    void validateConfiguration_WithValidApiKey_ShouldPrintSuccessMessage() {
        // Arrange
        ReflectionTestUtils.setField(aiConfig, "geminiApiKey", "valid-api-key-123");

        // Act
        aiConfig.validateConfiguration();

        // Assert
        String output = outputStream.toString();
        assertThat(output).contains("AI Configuration: Google Gemini API key is configured");
        
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void validateConfiguration_WithNullApiKey_ShouldPrintWarning() {
        // Arrange
        ReflectionTestUtils.setField(aiConfig, "geminiApiKey", null);

        // Act
        aiConfig.validateConfiguration();

        // Assert
        String errorOutput = errorStream.toString();
        assertThat(errorOutput).contains("WARNING: Google Gemini API key is not configured");
        assertThat(errorOutput).contains("Please set GOOGLE_GEMINI_API_KEY in your .env file");
        
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void validateConfiguration_WithEmptyApiKey_ShouldPrintWarning() {
        // Arrange
        ReflectionTestUtils.setField(aiConfig, "geminiApiKey", "");

        // Act
        aiConfig.validateConfiguration();

        // Assert
        String errorOutput = errorStream.toString();
        assertThat(errorOutput).contains("WARNING: Google Gemini API key is not configured");
        
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void validateConfiguration_WithWhitespaceApiKey_ShouldPrintWarning() {
        // Arrange
        ReflectionTestUtils.setField(aiConfig, "geminiApiKey", "   ");

        // Act
        aiConfig.validateConfiguration();

        // Assert
        String errorOutput = errorStream.toString();
        assertThat(errorOutput).contains("WARNING: Google Gemini API key is not configured");
        
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void validateConfiguration_WithPlaceholderApiKey_ShouldPrintWarning() {
        // Arrange
        ReflectionTestUtils.setField(aiConfig, "geminiApiKey", "${GOOGLE_GEMINI_API_KEY}");

        // Act
        aiConfig.validateConfiguration();

        // Assert
        String errorOutput = errorStream.toString();
        assertThat(errorOutput).contains("WARNING: Google Gemini API key is not configured");
        
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void aiWebClient_ShouldReturnConfiguredWebClient() {
        // Act
        WebClient webClient = aiConfig.aiWebClient();

        // Assert
        assertThat(webClient).isNotNull();
        assertThat(webClient).isInstanceOf(WebClient.class);
        
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void aiWebClient_ShouldReturnNewInstanceEachTime() {
        // Act
        WebClient webClient1 = aiConfig.aiWebClient();
        WebClient webClient2 = aiConfig.aiWebClient();

        // Assert
        assertThat(webClient1).isNotNull();
        assertThat(webClient2).isNotNull();
        assertThat(webClient1).isNotSameAs(webClient2);
        
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}
