package com.apex.firefighter.service.nlp;

import com.apex.firefighter.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NLPServiceTest {

    private NLPService nlpService;

    @Mock
    private UserService userService;

    @Mock
    private IntentRecognitionService intentRecognitionService;

    @Mock
    private EntityExtractionService entityExtractionService;

    @Mock
    private QueryProcessingService queryProcessingService;

    @Mock
    private ResponseGenerationService responseGenerationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        nlpService = new NLPService();
        nlpService.setUserService(userService);
        nlpService.setIntentRecognitionService(intentRecognitionService);
        nlpService.setEntityExtractionService(entityExtractionService);
        nlpService.setQueryProcessingService(queryProcessingService);
        nlpService.setResponseGenerationService(responseGenerationService);
    }

    @Test
    void processQuery_WithValidQuery_ShouldReturnSuccessResponse() {
        // Arrange
        String query = "show my active tickets";
        String userId = "user123";
        String userRole = "USER";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS, 0.9);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);
        
        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.TICKET_LIST, "Test data", 1);
        
        String expectedResponse = "Here are your active tickets";

        when(userService.getUserRole(userId)).thenReturn(userRole);
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(intentRecognitionService.isIntentAllowed(intent.getType(), userRole)).thenReturn(true);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(entityExtractionService.validateEntities(entities)).thenReturn(validation);
        when(queryProcessingService.processQuery(intent, entities, userId, false)).thenReturn(queryResult);
        when(responseGenerationService.generateResponse(queryResult)).thenReturn(expectedResponse);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getMessage());
        assertEquals("Test data", response.getData());
    }

    @Test
    void processQuery_WithUnrecognizedIntent_ShouldReturnErrorResponse() {
        // Arrange
        String query = "invalid query";
        String userId = "user123";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.UNKNOWN, 0.1);

        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Could not understand query"));
    }

    @Test
    void processAdminQuery_WithNonAdminUser_ShouldReturnAccessDenied() {
        // Arrange
        String query = "show all tickets";
        String userId = "user123";
        String userRole = "USER";

        when(userService.getUserRole(userId)).thenReturn(userRole);
        when(userService.hasRole(userId, "ADMIN")).thenReturn(false);

        // Act
        NLPService.NLPResponse response = nlpService.processAdminQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Access denied"));
    }

    @Test
    void processAdminQuery_WithAdminUser_ShouldProcessSuccessfully() {
        // Arrange
        String query = "show all tickets";
        String userId = "admin123";
        String userRole = "ADMIN";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ALL_TICKETS, 0.9);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);
        
        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.TICKET_LIST, "All tickets data", 10);
        
        String expectedResponse = "Here are all tickets in the system";

        when(userService.getUserRole(userId)).thenReturn(userRole);
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(entityExtractionService.validateEntities(entities)).thenReturn(validation);
        when(queryProcessingService.processQuery(intent, entities, userId, true)).thenReturn(queryResult);
        when(responseGenerationService.generateResponse(queryResult)).thenReturn(expectedResponse);

        // Act
        NLPService.NLPResponse response = nlpService.processAdminQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getMessage());
        assertEquals("All tickets data", response.getData());
    }
}
