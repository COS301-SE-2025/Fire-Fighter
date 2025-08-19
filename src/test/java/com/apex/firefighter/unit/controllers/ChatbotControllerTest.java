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
import static org.hamcrest.Matchers.hasSize;

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
    void setUp() {}

    @Test
    @WithMockUser
    void processQuery_WithValidInput_ShouldReturnChatbotResponse() throws Exception {
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "What are my active tickets?");
        request.put("userId", TEST_USER_ID);

        ChatbotResponse expectedResponse = new ChatbotResponse("You have 2 active tickets", true);
        when(chatbotService.processQuery("What are my active tickets?", TEST_USER_ID))
                .thenReturn(expectedResponse);

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
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "");
        request.put("userId", TEST_USER_ID);

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
        
        Map<String, String> request = new HashMap<>();
        request.put("userId", TEST_USER_ID);
        
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
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "What are my tickets?");
        request.put("userId", "");

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
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "What are my tickets?");
        
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
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "What are my tickets?");
        request.put("userId", TEST_USER_ID);

        when(chatbotService.processQuery(anyString(), anyString()))
                .thenThrow(new RuntimeException("AI service error"));

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
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "Show all active tickets");
        request.put("userId", TEST_USER_ID);

        ChatbotResponse expectedResponse = new ChatbotResponse("There are 5 active tickets in the system", true);
        when(chatbotService.processAdminQuery("Show all active tickets", TEST_USER_ID))
                .thenReturn(expectedResponse);

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
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "");
        request.put("userId", TEST_USER_ID);

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
    void processAdminQuery_WithNullUserId_ShouldReturnBadRequest() throws Exception {
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "What are my tickets?");
        
        mockMvc.perform(post(BASE_URL + "/admin/query")
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
    void processAdminQuery_WhenNotAuthorized_ShouldReturnForbidden() throws Exception {
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "Show all tickets");
        request.put("userId", TEST_USER_ID);

        ChatbotResponse unauthorizedResponse = new ChatbotResponse("Administrator privileges required", false);
        when(chatbotService.processAdminQuery("Show all tickets", TEST_USER_ID))
                .thenReturn(unauthorizedResponse);

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
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "Show all tickets");
        request.put("userId", TEST_USER_ID);

        when(chatbotService.processAdminQuery(anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

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
        
        ChatbotCapabilities capabilities = new ChatbotCapabilities(true, false, "Personal ticket access", new String[]{"Show my tickets"});
        when(chatbotService.getCapabilities(TEST_USER_ID)).thenReturn(capabilities);

        mockMvc.perform(get(BASE_URL + "/capabilities/" + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(chatbotService).getCapabilities(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getCapabilities_WithEmptyUserId_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get(BASE_URL + "/capabilities/ "))  
                .andExpect(status().isBadRequest());

        verifyNoInteractions(chatbotService);
    }

    @Test
    @WithMockUser
    void getCapabilities_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        
        when(chatbotService.getCapabilities(TEST_USER_ID)).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get(BASE_URL + "/capabilities/" + TEST_USER_ID))
                .andExpect(status().isInternalServerError());

        verify(chatbotService).getCapabilities(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getDebugContext_ShouldReturnDebugContext() throws Exception {
        
        String debugContext = "Debug context information";
        when(chatbotService.getDebugContext("test query", TEST_USER_ID)).thenReturn(debugContext);

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
        
        when(chatbotService.getDebugContext("test query", TEST_USER_ID))
                .thenThrow(new RuntimeException("Debug error"));

        mockMvc.perform(get(BASE_URL + "/debug/context/" + TEST_USER_ID)
                .param("query", "test query"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Error: Debug error")));

        verify(chatbotService).getDebugContext("test query", TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void healthCheck_ShouldReturnHealthyStatus() throws Exception {
        // Mock the Gemini service as configured
        when(chatbotService.isGeminiConfigured()).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("healthy"))
                .andExpect(jsonPath("$.service").value("AI Chatbot"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.services").exists())
                .andExpect(jsonPath("$.services.geminiAI").value("UP"))
                .andExpect(jsonPath("$.services.ticketQuery").value("UP"))
                .andExpect(jsonPath("$.services.database").value("UP"));

        verify(chatbotService).isGeminiConfigured();
    }

    @Test
    @WithMockUser
    void healthCheck_ResponseStructure_ShouldContainAllRequiredFields() throws Exception {
        // Mock the Gemini service as configured
        when(chatbotService.isGeminiConfigured()).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.service").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.services").exists())
                .andExpect(jsonPath("$.*", hasSize(5))); // Now we have 5 fields instead of 4

        verify(chatbotService).isGeminiConfigured();
    }

    @Test
    @WithMockUser
    void healthCheck_WithGeminiNotConfigured_ShouldReturnUnhealthyStatus() throws Exception {
        // Mock the Gemini service as not configured
        when(chatbotService.isGeminiConfigured()).thenReturn(false);

        mockMvc.perform(get(BASE_URL + "/health"))
                .andExpect(status().isServiceUnavailable()) // 503
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("unhealthy"))
                .andExpect(jsonPath("$.service").value("AI Chatbot"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.services").exists())
                .andExpect(jsonPath("$.services.geminiAI").value("DOWN - API key not configured"))
                .andExpect(jsonPath("$.services.ticketQuery").value("UP"))
                .andExpect(jsonPath("$.services.database").value("UP"));

        verify(chatbotService).isGeminiConfigured();
    }

    @Test
    @WithMockUser
    void getSuggestions_ForRegularUser_ShouldReturnUserSuggestions() throws Exception {
         
        ChatbotCapabilities userCapabilities = new ChatbotCapabilities(true, false, 
            "Personal ticket access",
            new String[]{"Show my tickets", "What are my tasks?"}
        );
        when(chatbotService.getCapabilities(TEST_USER_ID)).thenReturn(userCapabilities);

        mockMvc.perform(get(BASE_URL + "/suggestions/" + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.userRole").value("User"))
                .andExpect(jsonPath("$.accessLevel").value("Personal ticket access"))
                .andExpect(jsonPath("$.suggestedQueries").isArray())
                .andExpect(jsonPath("$.suggestedQueries[0]").value("Show my tickets"))
                .andExpect(jsonPath("$.suggestedQueries[1]").value("What are my tasks?"))
                .andExpect(jsonPath("$.examples").isArray())
                .andExpect(jsonPath("$.examples[0]").value("What tickets am I assigned to?"))
                .andExpect(jsonPath("$.examples[1]").value("Do I have any urgent tasks?"))
                .andExpect(jsonPath("$.examples[2]").value("How do I update my ticket status?"))
                .andExpect(jsonPath("$.examples[3]").value("Show my recent activity"))
                .andExpect(jsonPath("$.examples[4]").value("Help me with emergency procedures"));

        verify(chatbotService).getCapabilities(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getSuggestions_ForAdminUser_ShouldReturnAdminSuggestions() throws Exception {
        
        ChatbotCapabilities adminCapabilities = new ChatbotCapabilities(true, true, "Full system access",
            new String[]{"Show all tickets", "System overview"}
        );
        when(chatbotService.getCapabilities(TEST_USER_ID)).thenReturn(adminCapabilities);

        mockMvc.perform(get(BASE_URL + "/suggestions/" + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.userRole").value("Administrator"))
                .andExpect(jsonPath("$.accessLevel").value("Full system access"))
                .andExpect(jsonPath("$.suggestedQueries").isArray())
                .andExpect(jsonPath("$.suggestedQueries[0]").value("Show all tickets"))
                .andExpect(jsonPath("$.suggestedQueries[1]").value("System overview"))
                .andExpect(jsonPath("$.examples").isArray())
                .andExpect(jsonPath("$.examples[0]").value("How many active fire emergencies do we have?"))
                .andExpect(jsonPath("$.examples[1]").value("Show me today's ticket summary"))
                .andExpect(jsonPath("$.examples[2]").value("Which tickets need immediate attention?"))
                .andExpect(jsonPath("$.examples[3]").value("What's the average response time this week?"))
                .andExpect(jsonPath("$.examples[4]").value("Export current active tickets"));

        verify(chatbotService).getCapabilities(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getSuggestions_WithNullSuggestedQueries_ShouldReturnEmptyArray() throws Exception {
        
        ChatbotCapabilities capabilitiesWithNullQueries = new ChatbotCapabilities(true,false,
            "Personal ticket access"
        );
        when(chatbotService.getCapabilities(TEST_USER_ID)).thenReturn(capabilitiesWithNullQueries);

        mockMvc.perform(get(BASE_URL + "/suggestions/" + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.userRole").value("User"))
                .andExpect(jsonPath("$.accessLevel").value("Personal ticket access"))
                .andExpect(jsonPath("$.suggestedQueries").isArray())
                .andExpect(jsonPath("$.suggestedQueries").isEmpty())
                .andExpect(jsonPath("$.examples").isArray());

        verify(chatbotService).getCapabilities(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getSuggestions_WithEmptyUserId_ShouldReturnBadRequest() throws Exception {
        
        mockMvc.perform(get(BASE_URL + "/suggestions/ "))  
                .andExpect(status().isBadRequest());

        verifyNoInteractions(chatbotService);
    }

    @Test
    @WithMockUser
    void getSuggestions_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        
        when(chatbotService.getCapabilities(TEST_USER_ID))
                .thenThrow(new RuntimeException("Service unavailable"));

        mockMvc.perform(get(BASE_URL + "/suggestions/" + TEST_USER_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.error").value("Unable to retrieve suggestions"));

        verify(chatbotService).getCapabilities(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getSuggestions_WithUnavailableCapabilities_ShouldReturnUnavailableResponse() throws Exception {
        
        ChatbotCapabilities unavailableCapabilities = new ChatbotCapabilities(false, false,
            "Authentication required",
            new String[]{}
        );
        when(chatbotService.getCapabilities(TEST_USER_ID)).thenReturn(unavailableCapabilities);

        mockMvc.perform(get(BASE_URL + "/suggestions/" + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.userRole").value("User"))
                .andExpect(jsonPath("$.accessLevel").value("Authentication required"))
                .andExpect(jsonPath("$.suggestedQueries").isArray())
                .andExpect(jsonPath("$.suggestedQueries").isEmpty())
                .andExpect(jsonPath("$.examples").isArray());

        verify(chatbotService).getCapabilities(TEST_USER_ID);
    }

}
