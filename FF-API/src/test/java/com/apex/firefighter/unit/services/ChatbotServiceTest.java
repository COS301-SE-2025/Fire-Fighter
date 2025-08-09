package com.apex.firefighter.unit.services;

import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import com.apex.firefighter.service.ai.ChatbotService;
import com.apex.firefighter.service.ai.GeminiAIService;
import com.apex.firefighter.service.ai.TicketQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private GeminiAIService geminiAIService;

    @Mock
    private TicketQueryService ticketQueryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatbotService chatbotService;

    private User regularUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        regularUser = new User();
        regularUser.setUserId("test-user");
        regularUser.setUsername("testuser");
        regularUser.setEmail("test@example.com");
        regularUser.setIsAdmin(false);

        adminUser = new User();
        adminUser.setUserId("admin-user");
        adminUser.setUsername("adminuser");
        adminUser.setEmail("admin@example.com");
        adminUser.setIsAdmin(true);
    }

    // ==================== PROCESS QUERY TESTS ====================

    @Test
    void processQuery_WithValidUserAndQuery_ShouldReturnSuccessfulResponse() {
        // Arrange
        String query = "Show my tickets";
        String userId = "test-user";

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(regularUser));
        when(geminiAIService.isConfigured()).thenReturn(true);
        when(ticketQueryService.getUserTicketContext(query, userId)).thenReturn("You have 2 active tickets");
        when(geminiAIService.generateResponseWithContext(anyString(), anyString(), anyString()))
            .thenReturn("Here are your current tickets...");

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery(query, userId);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Here are your current tickets...");
        assertThat(response.getUserRole()).isEqualTo("User");
        assertThat(response.getTimestamp()).isNotNull();

        verify(userService).getUserByFirebaseUid(userId);
        verify(geminiAIService).isConfigured();
        verify(ticketQueryService).getUserTicketContext(query, userId);
        verify(geminiAIService).generateResponseWithContext(anyString(), eq("User"), anyString());
    }

    @Test
    void processQuery_WithAdminUser_ShouldReturnAdminResponse() {
        // Arrange
        String query = "Show system status";
        String userId = "admin-user";

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(adminUser));
        when(geminiAIService.isConfigured()).thenReturn(true);
        when(ticketQueryService.getUserTicketContext(query, userId)).thenReturn("Admin has 1 active ticket");
        when(geminiAIService.generateResponseWithContext(anyString(), anyString(), anyString()))
            .thenReturn("System status information...");

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery(query, userId);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("System status information...");
        assertThat(response.getUserRole()).isEqualTo("Administrator");

        verify(geminiAIService).generateResponseWithContext(anyString(), eq("Administrator"), contains("Note: You have admin privileges"));
    }

    @Test
    void processQuery_WithRequestCreationKeywords_ShouldUseRequestCreationContext() {
        // Arrange
        String query = "How do I create a new emergency request?";
        String userId = "test-user";

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(regularUser));
        when(geminiAIService.isConfigured()).thenReturn(true);
        when(ticketQueryService.getRequestCreationContext(query)).thenReturn("Request creation guidance");
        when(geminiAIService.generateResponseWithContext(anyString(), anyString(), anyString()))
            .thenReturn("To create a new request...");

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery(query, userId);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        verify(ticketQueryService).getRequestCreationContext(query);
        verify(ticketQueryService, never()).getUserTicketContext(anyString(), anyString());
    }

    @Test
    void processQuery_WithNullQuery_ShouldReturnError() {
        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery(null, "test-user");

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Please ask me a question");
        verifyNoInteractions(userService, geminiAIService, ticketQueryService);
    }

    @Test
    void processQuery_WithEmptyQuery_ShouldReturnError() {
        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery("", "test-user");

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Please ask me a question");
        verifyNoInteractions(userService, geminiAIService, ticketQueryService);
    }

    @Test
    void processQuery_WithWhitespaceQuery_ShouldReturnError() {
        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery("   ", "test-user");

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Please ask me a question");
        verifyNoInteractions(userService, geminiAIService, ticketQueryService);
    }

    @Test
    void processQuery_WithNullUserId_ShouldReturnError() {
        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery("Show my tickets", null);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("User authentication required");
        verifyNoInteractions(userService, geminiAIService, ticketQueryService);
    }

    @Test
    void processQuery_WithEmptyUserId_ShouldReturnError() {
        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery("Show my tickets", "");

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("User authentication required");
        verifyNoInteractions(userService, geminiAIService, ticketQueryService);
    }

    @Test
    void processQuery_WithAIServiceNotConfigured_ShouldReturnError() {
        // Arrange
        when(geminiAIService.isConfigured()).thenReturn(false);

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery("Show my tickets", "test-user");

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("AI service is not properly configured");
        verify(geminiAIService).isConfigured();
        verifyNoInteractions(userService, ticketQueryService);
    }

    @Test
    void processQuery_WithUserNotFound_ShouldReturnError() {
        // Arrange
        String userId = "nonexistent-user";
        when(geminiAIService.isConfigured()).thenReturn(true);
        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.empty());

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery("Show my tickets", userId);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("User not found");
        verify(geminiAIService).isConfigured();
        verify(userService).getUserByFirebaseUid(userId);
        verifyNoInteractions(ticketQueryService);
    }

    @Test
    void processQuery_WithException_ShouldReturnErrorResponse() {
        // Arrange
        String query = "Show my tickets";
        String userId = "test-user";

        when(geminiAIService.isConfigured()).thenReturn(true);
        when(userService.getUserByFirebaseUid(userId)).thenThrow(new RuntimeException("Database error"));

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery(query, userId);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("I'm experiencing technical difficulties");
    }

    // ==================== PROCESS ADMIN QUERY TESTS ====================

    @Test
    void processAdminQuery_WithValidAdmin_ShouldReturnAdminResponse() {
        // Arrange
        String query = "Show system-wide access logs";
        String userId = "admin-user";

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(adminUser));
        when(geminiAIService.generateResponseWithContext(anyString(), anyString(), anyString()))
            .thenReturn("System-wide access logs...");

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processAdminQuery(query, userId);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("System-wide access logs...");
        assertThat(response.getUserRole()).isEqualTo("Administrator");

        verify(userService).getUserByFirebaseUid(userId);
        verify(geminiAIService).generateResponseWithContext(anyString(), eq("Administrator"), anyString());
    }

    @Test
    void processAdminQuery_WithNonAdminUser_ShouldReturnError() {
        // Arrange
        String query = "Show system logs";
        String userId = "test-user";

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(regularUser));

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processAdminQuery(query, userId);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Administrator privileges required");

        verify(userService).getUserByFirebaseUid(userId);
        verifyNoInteractions(geminiAIService);
    }

    @Test
    void processAdminQuery_WithUserNotFound_ShouldReturnError() {
        // Arrange
        String query = "Show system logs";
        String userId = "nonexistent-user";

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.empty());

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processAdminQuery(query, userId);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Administrator privileges required");

        verify(userService).getUserByFirebaseUid(userId);
        verifyNoInteractions(geminiAIService);
    }

    @Test
    void processAdminQuery_WithException_ShouldReturnErrorResponse() {
        // Arrange
        String query = "Show system logs";
        String userId = "admin-user";

        when(userService.getUserByFirebaseUid(userId)).thenThrow(new RuntimeException("Database error"));

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processAdminQuery(query, userId);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Error processing admin query");
    }

    // ==================== GET CAPABILITIES TESTS ====================

    @Test
    void getCapabilities_WithRegularUser_ShouldReturnUserCapabilities() {
        // Arrange
        String userId = "test-user";
        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(regularUser));

        // Act
        ChatbotService.ChatbotCapabilities capabilities = chatbotService.getCapabilities(userId);

        // Assert
        assertThat(capabilities.isAvailable()).isTrue();
        assertThat(capabilities.isAdminAccess()).isFalse();
        assertThat(capabilities.getAccessLevel()).isEqualTo("Personal ticket access");
        assertThat(capabilities.getSuggestedQueries()).isNotNull();
        assertThat(capabilities.getSuggestedQueries()).contains("Show my access tickets");

        verify(userService).getUserByFirebaseUid(userId);
    }

    @Test
    void getCapabilities_WithAdminUser_ShouldReturnAdminCapabilities() {
        // Arrange
        String userId = "admin-user";
        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(adminUser));

        // Act
        ChatbotService.ChatbotCapabilities capabilities = chatbotService.getCapabilities(userId);

        // Assert
        assertThat(capabilities.isAvailable()).isTrue();
        assertThat(capabilities.isAdminAccess()).isTrue();
        assertThat(capabilities.getAccessLevel()).isEqualTo("Full system access");
        assertThat(capabilities.getSuggestedQueries()).isNotNull();
        assertThat(capabilities.getSuggestedQueries()).contains("Show system-wide access logs");

        verify(userService).getUserByFirebaseUid(userId);
    }

    @Test
    void getCapabilities_WithUserNotFound_ShouldReturnUnavailableCapabilities() {
        // Arrange
        String userId = "nonexistent-user";
        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.empty());

        // Act
        ChatbotService.ChatbotCapabilities capabilities = chatbotService.getCapabilities(userId);

        // Assert
        assertThat(capabilities.isAvailable()).isFalse();
        assertThat(capabilities.isAdminAccess()).isFalse();
        assertThat(capabilities.getAccessLevel()).isEqualTo("Authentication required");

        verify(userService).getUserByFirebaseUid(userId);
    }

    @Test
    void getCapabilities_WithException_ShouldReturnErrorCapabilities() {
        // Arrange
        String userId = "test-user";
        when(userService.getUserByFirebaseUid(userId)).thenThrow(new RuntimeException("Database error"));

        // Act
        ChatbotService.ChatbotCapabilities capabilities = chatbotService.getCapabilities(userId);

        // Assert
        assertThat(capabilities.isAvailable()).isFalse();
        assertThat(capabilities.isAdminAccess()).isFalse();
        assertThat(capabilities.getAccessLevel()).isEqualTo("Error determining capabilities");
    }

    // ==================== GET DEBUG CONTEXT TESTS ====================

    @Test
    void getDebugContext_WithValidUser_ShouldReturnDebugInformation() {
        // Arrange
        String query = "show my tickets";
        String userId = "test-user";

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(regularUser));
        when(ticketQueryService.getUserTicketContext(query, userId)).thenReturn("User has 2 tickets");

        // Act
        String debugContext = chatbotService.getDebugContext(query, userId);

        // Assert
        assertThat(debugContext).contains("DEBUG CONTEXT GENERATION:");
        assertThat(debugContext).contains("Query: " + query);
        assertThat(debugContext).contains("User ID: " + userId);
        assertThat(debugContext).contains("Is Admin: false");
        assertThat(debugContext).contains("Request Creation Keywords: false");
        assertThat(debugContext).contains("Generated Context:");

        verify(userService).getUserByFirebaseUid(userId);
        verify(ticketQueryService).getUserTicketContext(query, userId);
    }

    @Test
    void getDebugContext_WithAdminUser_ShouldReturnAdminDebugInformation() {
        // Arrange
        String query = "show system status";
        String userId = "admin-user";

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(adminUser));
        when(ticketQueryService.getUserTicketContext(query, userId)).thenReturn("Admin has 1 ticket");

        // Act
        String debugContext = chatbotService.getDebugContext(query, userId);

        // Assert
        assertThat(debugContext).contains("Is Admin: true");
        assertThat(debugContext).contains("Note: You have admin privileges");

        verify(userService).getUserByFirebaseUid(userId);
        verify(ticketQueryService).getUserTicketContext(query, userId);
    }

    @Test
    void getDebugContext_WithRequestCreationQuery_ShouldShowRequestCreationKeywords() {
        // Arrange
        String query = "how do I create a new emergency request";
        String userId = "test-user";

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(regularUser));
        when(ticketQueryService.getRequestCreationContext(query)).thenReturn("Request creation guidance");

        // Act
        String debugContext = chatbotService.getDebugContext(query, userId);

        // Assert
        assertThat(debugContext).contains("Request Creation Keywords: true");

        verify(userService).getUserByFirebaseUid(userId);
        verify(ticketQueryService).getRequestCreationContext(query);
        verify(ticketQueryService, never()).getUserTicketContext(anyString(), anyString());
    }

    @Test
    void getDebugContext_WithUserNotFound_ShouldReturnErrorDebug() {
        // Arrange
        String query = "show my tickets";
        String userId = "nonexistent-user";

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.empty());

        // Act
        String debugContext = chatbotService.getDebugContext(query, userId);

        // Assert
        assertThat(debugContext).contains("DEBUG: User not found for ID: " + userId);

        verify(userService).getUserByFirebaseUid(userId);
        verifyNoInteractions(ticketQueryService);
    }

    @Test
    void getDebugContext_WithException_ShouldReturnErrorMessage() {
        // Arrange
        String query = "show my tickets";
        String userId = "test-user";

        when(userService.getUserByFirebaseUid(userId)).thenThrow(new RuntimeException("Database error"));

        // Act
        String debugContext = chatbotService.getDebugContext(query, userId);

        // Assert
        assertThat(debugContext).contains("DEBUG ERROR: Database error");
    }

    // ==================== CHATBOT RESPONSE CLASS TESTS ====================

    @Test
    void chatbotResponse_WithBasicConstructor_ShouldSetFieldsCorrectly() {
        // Arrange
        String message = "Test message";
        boolean success = true;

        // Act
        ChatbotService.ChatbotResponse response = new ChatbotService.ChatbotResponse(message, success);

        // Assert
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.isSuccess()).isEqualTo(success);
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getUserRole()).isNull();
    }

    @Test
    void chatbotResponse_WithFullConstructor_ShouldSetAllFieldsCorrectly() {
        // Arrange
        String message = "Test message";
        boolean success = true;
        String userRole = "Administrator";
        LocalDateTime timestamp = LocalDateTime.now();

        // Act
        ChatbotService.ChatbotResponse response = new ChatbotService.ChatbotResponse(message, success, userRole, timestamp);

        // Assert
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.isSuccess()).isEqualTo(success);
        assertThat(response.getUserRole()).isEqualTo(userRole);
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void chatbotResponse_GetFormattedTimestamp_ShouldReturnFormattedString() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2023, 12, 25, 14, 30, 45);
        ChatbotService.ChatbotResponse response = new ChatbotService.ChatbotResponse("Test", true, "User", timestamp);

        // Act
        String formattedTimestamp = response.getFormattedTimestamp();

        // Assert
        assertThat(formattedTimestamp).isEqualTo("2023-12-25 14:30:45");
    }

    @Test
    void chatbotResponse_GetFormattedTimestamp_WithNullTimestamp_ShouldReturnEmptyString() {
        // Arrange
        ChatbotService.ChatbotResponse response = new ChatbotService.ChatbotResponse("Test", true, "User", null);

        // Act
        String formattedTimestamp = response.getFormattedTimestamp();

        // Assert
        assertThat(formattedTimestamp).isEmpty();
    }

    // ==================== CHATBOT CAPABILITIES CLASS TESTS ====================

    @Test
    void chatbotCapabilities_WithBasicConstructor_ShouldSetFieldsCorrectly() {
        // Arrange
        boolean available = true;
        boolean adminAccess = false;
        String accessLevel = "Personal access";

        // Act
        ChatbotService.ChatbotCapabilities capabilities = new ChatbotService.ChatbotCapabilities(available, adminAccess, accessLevel);

        // Assert
        assertThat(capabilities.isAvailable()).isEqualTo(available);
        assertThat(capabilities.isAdminAccess()).isEqualTo(adminAccess);
        assertThat(capabilities.getAccessLevel()).isEqualTo(accessLevel);
        assertThat(capabilities.getSuggestedQueries()).isNull();
    }

    @Test
    void chatbotCapabilities_WithFullConstructor_ShouldSetAllFieldsCorrectly() {
        // Arrange
        boolean available = true;
        boolean adminAccess = true;
        String accessLevel = "Full access";
        String[] suggestedQueries = {"Query 1", "Query 2"};

        // Act
        ChatbotService.ChatbotCapabilities capabilities = new ChatbotService.ChatbotCapabilities(available, adminAccess, accessLevel, suggestedQueries);

        // Assert
        assertThat(capabilities.isAvailable()).isEqualTo(available);
        assertThat(capabilities.isAdminAccess()).isEqualTo(adminAccess);
        assertThat(capabilities.getAccessLevel()).isEqualTo(accessLevel);
        assertThat(capabilities.getSuggestedQueries()).isEqualTo(suggestedQueries);
    }

    @Test
    void chatbotCapabilities_SettersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        ChatbotService.ChatbotCapabilities capabilities = new ChatbotService.ChatbotCapabilities(false, false, "None");
        String[] newQueries = {"New Query 1", "New Query 2"};

        // Act
        capabilities.setAvailable(true);
        capabilities.setAdminAccess(true);
        capabilities.setAccessLevel("Full access");
        capabilities.setSuggestedQueries(newQueries);

        // Assert
        assertThat(capabilities.isAvailable()).isTrue();
        assertThat(capabilities.isAdminAccess()).isTrue();
        assertThat(capabilities.getAccessLevel()).isEqualTo("Full access");
        assertThat(capabilities.getSuggestedQueries()).isEqualTo(newQueries);
    }
}
