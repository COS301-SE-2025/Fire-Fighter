package com.apex.firefighter.service.ai;

import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatbotServiceTest {

    @Mock
    private GeminiAIService geminiAIService;

    @Mock
    private TicketQueryService ticketQueryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatbotService chatbotService;

    @Test
    void testProcessQuery_ValidUser_ReturnsResponse() {
        // Arrange
        String userId = "test-user-123";
        String query = "Show my tickets";

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        mockUser.setIsAdmin(false);

        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(mockUser));
        when(geminiAIService.isConfigured()).thenReturn(true);
        when(ticketQueryService.getUserTicketContext(query, userId)).thenReturn("You have 2 active tickets");
        when(geminiAIService.generateResponseWithContext(anyString(), anyString(), anyString()))
            .thenReturn("Here are your current tickets...");

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery(query, userId);

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getMessage());
        assertEquals("User", response.getUserRole());
    }

    @Test
    void testProcessQuery_EmptyQuery_ReturnsError() {
        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery("", "user123");

        // Assert
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Please ask me a question"));
    }

    @Test
    void testProcessQuery_UserNotFound_ReturnsError() {
        // Arrange
        String userId = "nonexistent-user";
        when(geminiAIService.isConfigured()).thenReturn(true);
        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.empty());

        // Act
        ChatbotService.ChatbotResponse response = chatbotService.processQuery("Show my tickets", userId);

        // Assert
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("User not found"));
    }

    @Test
    void testGetCapabilities_RegularUser_ReturnsUserCapabilities() {
        // Arrange
        String userId = "test-user-123";
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setIsAdmin(false);
        
        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(mockUser));

        // Act
        ChatbotService.ChatbotCapabilities capabilities = chatbotService.getCapabilities(userId);

        // Assert
        assertTrue(capabilities.isAvailable());
        assertFalse(capabilities.isAdminAccess());
        assertEquals("Personal ticket access", capabilities.getAccessLevel());
    }

    @Test
    void testGetCapabilities_AdminUser_ReturnsAdminCapabilities() {
        // Arrange
        String userId = "admin-user-123";
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setIsAdmin(true);
        
        when(userService.getUserByFirebaseUid(userId)).thenReturn(Optional.of(mockUser));

        // Act
        ChatbotService.ChatbotCapabilities capabilities = chatbotService.getCapabilities(userId);

        // Assert
        assertTrue(capabilities.isAvailable());
        assertTrue(capabilities.isAdminAccess());
        assertEquals("Full system access", capabilities.getAccessLevel());
    }
}
