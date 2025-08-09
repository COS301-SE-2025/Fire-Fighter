package com.apex.firefighter.unit.services;

import com.apex.firefighter.service.ai.GeminiAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeminiAIServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private GeminiAIService geminiAIService;

    private static final String TEST_API_KEY = "test-api-key";
    private static final String VALID_GEMINI_RESPONSE = """
        {
            "candidates": [
                {
                    "content": {
                        "parts": [
                            {
                                "text": "This is a test response from Gemini AI."
                            }
                        ]
                    }
                }
            ]
        }
        """;

    @BeforeEach
    void setUp() {
        // Set up the API key using reflection
        ReflectionTestUtils.setField(geminiAIService, "apiKey", TEST_API_KEY);
        
        // Set up the WebClient mock using reflection
        ReflectionTestUtils.setField(geminiAIService, "webClient", webClient);
    }

    // ==================== GENERATE RESPONSE TESTS ====================

    @Test
    void generateResponse_WithValidPrompt_ShouldReturnAIResponse() {
        // Arrange
        String prompt = "What is emergency access management?";
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(VALID_GEMINI_RESPONSE));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("This is a test response from Gemini AI.");
        verify(webClient).post();
        verify(requestBodyUriSpec).uri(contains("generateContent"));
        verify(requestBodySpec, times(2)).header(anyString(), anyString());
        verify(requestBodySpec).bodyValue(any());
    }

    @Test
    void generateResponse_WithWebClientResponseException_ShouldReturnErrorMessage() {
        // Arrange
        String prompt = "Test prompt";
        WebClientResponseException exception = WebClientResponseException.create(
            400, "Bad Request", null, "Invalid request".getBytes(), null);
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(exception));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("I'm sorry, I'm having trouble processing your request right now. Please try again later.");
        verify(webClient).post();
    }

    @Test
    void generateResponse_WithGenericException_ShouldReturnErrorMessage() {
        // Arrange
        String prompt = "Test prompt";
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("Network error")));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("I'm experiencing technical difficulties. Please try again later.");
        verify(webClient).post();
    }

    @Test
    void generateResponse_WithInvalidJsonResponse_ShouldReturnErrorMessage() {
        // Arrange
        String prompt = "Test prompt";
        String invalidResponse = "Invalid JSON response";
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(invalidResponse));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("I had trouble understanding the response. Please try again.");
        verify(webClient).post();
    }

    @Test
    void generateResponse_WithEmptyResponse_ShouldReturnDefaultMessage() {
        // Arrange
        String prompt = "Test prompt";
        String emptyResponse = """
            {
                "candidates": []
            }
            """;
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(emptyResponse));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("I couldn't generate a proper response. Please try rephrasing your question.");
        verify(webClient).post();
    }

    @Test
    void generateResponse_WithMissingTextInResponse_ShouldReturnDefaultMessage() {
        // Arrange
        String prompt = "Test prompt";
        String responseWithoutText = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "other": "no text field"
                                }
                            ]
                        }
                    }
                ]
            }
            """;
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(responseWithoutText));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("I couldn't generate a proper response. Please try rephrasing your question.");
        verify(webClient).post();
    }

    // ==================== GENERATE RESPONSE WITH CONTEXT TESTS ====================

    @Test
    void generateResponseWithContext_WithValidInputs_ShouldReturnContextualResponse() {
        // Arrange
        String prompt = "Show my tickets";
        String userRole = "Administrator";
        String context = "User has 3 active tickets";
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(VALID_GEMINI_RESPONSE));

        // Act
        String result = geminiAIService.generateResponseWithContext(prompt, userRole, context);

        // Assert
        assertThat(result).isEqualTo("This is a test response from Gemini AI.");
        verify(webClient).post();
    }

    @Test
    void generateResponseWithContext_WithNullContext_ShouldHandleGracefully() {
        // Arrange
        String prompt = "Show my tickets";
        String userRole = "User";
        String context = null;
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(VALID_GEMINI_RESPONSE));

        // Act
        String result = geminiAIService.generateResponseWithContext(prompt, userRole, context);

        // Assert
        assertThat(result).isEqualTo("This is a test response from Gemini AI.");
        verify(webClient).post();
    }

    @Test
    void generateResponseWithContext_WithEmptyContext_ShouldHandleGracefully() {
        // Arrange
        String prompt = "Show my tickets";
        String userRole = "User";
        String context = "";
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(VALID_GEMINI_RESPONSE));

        // Act
        String result = geminiAIService.generateResponseWithContext(prompt, userRole, context);

        // Assert
        assertThat(result).isEqualTo("This is a test response from Gemini AI.");
        verify(webClient).post();
    }

    // ==================== CONFIGURATION TESTS ====================

    @Test
    void isConfigured_WithValidApiKey_ShouldReturnTrue() {
        // Arrange
        ReflectionTestUtils.setField(geminiAIService, "apiKey", "valid-api-key");

        // Act
        boolean result = geminiAIService.isConfigured();

        // Assert
        assertThat(result).isTrue();
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
    void isConfigured_WithEmptyApiKey_ShouldReturnFalse() {
        // Arrange
        ReflectionTestUtils.setField(geminiAIService, "apiKey", "");

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

    // ==================== RESPONSE PARSING EDGE CASES ====================

    @Test
    void generateResponse_WithNestedJsonStructure_ShouldExtractTextCorrectly() {
        // Arrange
        String prompt = "Test prompt";
        String complexResponse = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "Complex response with nested data"
                                }
                            ]
                        },
                        "finishReason": "STOP"
                    }
                ]
            }
            """;

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(complexResponse));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("Complex response with nested data");
    }

    @Test
    void generateResponse_WithMultipleCandidates_ShouldReturnFirstCandidate() {
        // Arrange
        String prompt = "Test prompt";
        String multiCandidateResponse = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "First candidate response"
                                }
                            ]
                        }
                    },
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "Second candidate response"
                                }
                            ]
                        }
                    }
                ]
            }
            """;

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(multiCandidateResponse));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("First candidate response");
    }

    @Test
    void generateResponse_WithMultipleParts_ShouldReturnFirstPart() {
        // Arrange
        String prompt = "Test prompt";
        String multiPartResponse = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "First part of response"
                                },
                                {
                                    "text": "Second part of response"
                                }
                            ]
                        }
                    }
                ]
            }
            """;

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(multiPartResponse));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("First part of response");
    }

    @Test
    void generateResponse_WithWhitespaceInText_ShouldTrimResponse() {
        // Arrange
        String prompt = "Test prompt";
        String responseWithWhitespace = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "   Response with whitespace   "
                                }
                            ]
                        }
                    }
                ]
            }
            """;

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(responseWithWhitespace));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("Response with whitespace");
    }

    @Test
    void generateResponse_WithMissingContentField_ShouldReturnDefaultMessage() {
        // Arrange
        String prompt = "Test prompt";
        String responseWithoutContent = """
            {
                "candidates": [
                    {
                        "finishReason": "STOP"
                    }
                ]
            }
            """;

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(responseWithoutContent));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("I couldn't generate a proper response. Please try rephrasing your question.");
    }

    @Test
    void generateResponse_WithMissingPartsField_ShouldReturnDefaultMessage() {
        // Arrange
        String prompt = "Test prompt";
        String responseWithoutParts = """
            {
                "candidates": [
                    {
                        "content": {
                            "role": "model"
                        }
                    }
                ]
            }
            """;

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(responseWithoutParts));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("I couldn't generate a proper response. Please try rephrasing your question.");
    }

    @Test
    void generateResponse_WithEmptyPartsArray_ShouldReturnDefaultMessage() {
        // Arrange
        String prompt = "Test prompt";
        String responseWithEmptyParts = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": []
                        }
                    }
                ]
            }
            """;

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(responseWithEmptyParts));

        // Act
        String result = geminiAIService.generateResponse(prompt);

        // Assert
        assertThat(result).isEqualTo("I couldn't generate a proper response. Please try rephrasing your question.");
    }
}
