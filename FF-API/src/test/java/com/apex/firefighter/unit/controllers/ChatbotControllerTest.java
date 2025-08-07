package com.apex.firefighter.controller;

import com.apex.firefighter.service.ai.ChatbotService;
import com.apex.firefighter.service.ai.ChatbotService.ChatbotResponse;
import com.apex.firefighter.service.ai.ChatbotService.ChatbotCapabilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.hamcrest.Matchers.containsString;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatbotController.class)
class ChatbotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatbotService chatbotService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String TEST_USER_ID = "test-user-123";
    private final String BASE_URL = "/api/chatbot";

    @BeforeEach
    void setUp() {
        // Setup can be added here if needed
    }

    @Test
    @WithMockUser
    void processQuery_WithValidInput_ShouldReturnChatbotResponse() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("query", "What are my active tickets?");
        request.put("userId", TEST_USER_ID);

        ChatbotResponse expectedResponse = new ChatbotResponse("You have 2 active tickets", true);
        when(chatbotService.processQuery("What are my active tickets?", TEST_USER_ID))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("You have 2 active tickets"))
                .andExpect(jsonPath("$.success").value(true));

        verify(chatbotService).processQuery("What are my active tickets?", TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void processQuery_WithEmptyQuery_ShouldReturnBadRequest() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("query", "");
        request.put("userId", TEST_USER_ID);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Query cannot be empty"))
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(chatbotService);
    }

    @Test
    @WithMockUser
    void processQuery_WithNullQuery_ShouldReturnBadRequest() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("userId", TEST_USER_ID);
        // query is null

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Query cannot be empty"))
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(chatbotService);
    }

    @Test
    @WithMockUser
    void processQuery_WithEmptyUserId_ShouldReturnBadRequest() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("query", "What are my tickets?");
        request.put("userId", "");

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User ID is required"))
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(chatbotService);
    }

    @Test
    @WithMockUser
    void processQuery_WithNullUserId_ShouldReturnBadRequest() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("query", "What are my tickets?");
        // userId is null

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User ID is required"))
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(chatbotService);
    }

    @Test
    @WithMockUser
    void processQuery_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("query", "What are my tickets?");
        request.put("userId", TEST_USER_ID);

        when(chatbotService.processQuery(anyString(), anyString()))
                .thenThrow(new RuntimeException("AI service error"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Internal server error"))
                .andExpect(jsonPath("$.success").value(false));

        verify(chatbotService).processQuery("What are my tickets?", TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void processAdminQuery_WithValidInput_ShouldReturnChatbotResponse() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("query", "Show all active tickets");
        request.put("userId", TEST_USER_ID);

        ChatbotResponse expectedResponse = new ChatbotResponse("There are 5 active tickets in the system", true);
        when(chatbotService.processAdminQuery("Show all active tickets", TEST_USER_ID))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/admin/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("There are 5 active tickets in the system"))
                .andExpect(jsonPath("$.success").value(true));

        verify(chatbotService).processAdminQuery("Show all active tickets", TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void processAdminQuery_WithEmptyQuery_ShouldReturnBadRequest() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("query", "");
        request.put("userId", TEST_USER_ID);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/admin/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Query cannot be empty"))
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(chatbotService);
    }

    @Test
    @WithMockUser
    void processAdminQuery_WhenNotAuthorized_ShouldReturnForbidden() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("query", "Show all tickets");
        request.put("userId", TEST_USER_ID);

        ChatbotResponse unauthorizedResponse = new ChatbotResponse("Administrator privileges required", false);
        when(chatbotService.processAdminQuery("Show all tickets", TEST_USER_ID))
                .thenReturn(unauthorizedResponse);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/admin/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Administrator privileges required"))
                .andExpect(jsonPath("$.success").value(false));

        verify(chatbotService).processAdminQuery("Show all tickets", TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void processAdminQuery_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("query", "Show all tickets");
        request.put("userId", TEST_USER_ID);

        when(chatbotService.processAdminQuery(anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/admin/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Internal server error"))
                .andExpect(jsonPath("$.success").value(false));

        verify(chatbotService).processAdminQuery("Show all tickets", TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getCapabilities_ShouldReturnChatbotCapabilities() throws Exception {
        // Arrange
        ChatbotCapabilities capabilities = new ChatbotCapabilities(true, false, "Personal ticket access", new String[]{"Show my tickets"});
        when(chatbotService.getCapabilities(TEST_USER_ID)).thenReturn(capabilities);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/capabilities/" + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(chatbotService).getCapabilities(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getCapabilities_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(chatbotService.getCapabilities(TEST_USER_ID)).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/capabilities/" + TEST_USER_ID))
                .andExpect(status().isInternalServerError());

        verify(chatbotService).getCapabilities(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getDebugContext_ShouldReturnDebugContext() throws Exception {
        // Arrange
        String debugContext = "Debug context information";
        when(chatbotService.getDebugContext("test query", TEST_USER_ID)).thenReturn(debugContext);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/debug/context/" + TEST_USER_ID)
                .param("query", "test query"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Debug context information"));

        verify(chatbotService).getDebugContext("test query", TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getDebugContext_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(chatbotService.getDebugContext("test query", TEST_USER_ID))
                .thenThrow(new RuntimeException("Debug error"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/debug/context/" + TEST_USER_ID)
                .param("query", "test query"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Error: Debug error")));

        verify(chatbotService).getDebugContext("test query", TEST_USER_ID);
    }
}
