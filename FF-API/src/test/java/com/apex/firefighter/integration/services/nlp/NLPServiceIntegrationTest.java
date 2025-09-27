package com.apex.firefighter.integration.services.nlp;

import com.apex.firefighter.service.UserService;
import com.apex.firefighter.service.nlp.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NLPServiceIntegrationTest {

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

    private NLPService nlpService;

    @BeforeEach
    void setUp() {
        nlpService = new NLPService();
        nlpService.setUserService(userService);
        nlpService.setIntentRecognitionService(intentRecognitionService);
        nlpService.setEntityExtractionService(entityExtractionService);
        nlpService.setQueryProcessingService(queryProcessingService);
        nlpService.setResponseGenerationService(responseGenerationService);
    }

    // ==================== COMPLETE PIPELINE INTEGRATION TESTS ====================

    @Test
    void processQuery_CompleteTicketCreationWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "create a fire emergency ticket for building collapse";
        String userId = "user123";
        
        // Mock intent recognition
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.CREATE_TICKET, 0.95, query);
        
        // Mock entity extraction
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        Map<EntityExtractionService.EntityType, List<EntityExtractionService.Entity>> entityMap = new HashMap<>();
        entityMap.put(EntityExtractionService.EntityType.EMERGENCY_TYPE, 
            List.of(new EntityExtractionService.Entity(EntityExtractionService.EntityType.EMERGENCY_TYPE, "fire", 0, 4)));
        entityMap.put(EntityExtractionService.EntityType.DESCRIPTION,
            List.of(new EntityExtractionService.Entity(EntityExtractionService.EntityType.DESCRIPTION, "building collapse", 0, 15)));
        entities.setAllEntities(entityMap);
        
        // Mock query processing
        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            true, "Ticket created successfully", "TICKET-001", 
            QueryProcessingService.QueryResultType.OPERATION_RESULT);
        
        // Mock response generation
        String expectedResponse = "Emergency ticket TICKET-001 has been created for fire emergency: building collapse";

        // Setup mocks
        when(userService.getUserRole(userId)).thenReturn("USER");
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(intentRecognitionService.isIntentAllowed(intent.getType(), "USER")).thenReturn(true);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(queryProcessingService.processQuery(intent, entities, userId, false)).thenReturn(queryResult);
        when(responseGenerationService.generateResponse(queryResult)).thenReturn(expectedResponse);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo(expectedResponse);
        assertThat(response.getData()).isEqualTo("TICKET-001");
        
        // Verify all services were called in correct order
        verify(userService).getUserRole(userId);
        verify(intentRecognitionService).recognizeIntent(query);
        verify(intentRecognitionService).isIntentAllowed(intent.getType(), "USER");
        verify(entityExtractionService).extractEntities(query);
        verify(queryProcessingService).processQuery(intent, entities, userId, false);
        verify(responseGenerationService).generateResponse(queryResult);
    }

    @Test
    void processQuery_CompleteTicketQueryWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "show my active tickets from last week";
        String userId = "user123";
        
        // Mock intent recognition
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS, 0.92, query);
        
        // Mock entity extraction
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        Map<EntityExtractionService.EntityType, List<EntityExtractionService.Entity>> entityMap = new HashMap<>();
        entityMap.put(EntityExtractionService.EntityType.DATE,
            List.of(new EntityExtractionService.Entity(EntityExtractionService.EntityType.DATE, "last week", 0, 9)));
        entities.setAllEntities(entityMap);
        
        // Mock validation
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);
        
        // Mock query processing
        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.TICKET_LIST, 
            List.of("TICKET-001", "TICKET-002"), 2);
        
        // Mock response generation
        String expectedResponse = "You have 2 active tickets from last week: TICKET-001, TICKET-002";

        // Setup mocks
        when(userService.getUserRole(userId)).thenReturn("USER");
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(intentRecognitionService.isIntentAllowed(intent.getType(), "USER")).thenReturn(true);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(entityExtractionService.validateEntities(entities)).thenReturn(validation);
        when(queryProcessingService.processQuery(intent, entities, userId, false)).thenReturn(queryResult);
        when(responseGenerationService.generateResponse(queryResult)).thenReturn(expectedResponse);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo(expectedResponse);
        
        // Verify complete workflow
        verify(userService).getUserRole(userId);
        verify(intentRecognitionService).recognizeIntent(query);
        verify(intentRecognitionService).isIntentAllowed(intent.getType(), "USER");
        verify(entityExtractionService).extractEntities(query);
        verify(entityExtractionService).validateEntities(entities);
        verify(queryProcessingService).processQuery(intent, entities, userId, false);
        verify(responseGenerationService).generateResponse(queryResult);
    }

    @Test
    void processAdminQuery_CompleteAdminWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "show all tickets with status pending from HR department";
        String userId = "admin123";
        
        // Mock intent recognition
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ALL_TICKETS, 0.88, query);
        
        // Mock entity extraction
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        Map<EntityExtractionService.EntityType, List<EntityExtractionService.Entity>> entityMap = new HashMap<>();
        entityMap.put(EntityExtractionService.EntityType.STATUS,
            List.of(new EntityExtractionService.Entity(EntityExtractionService.EntityType.STATUS, "pending", 0, 7)));
        entityMap.put(EntityExtractionService.EntityType.EMERGENCY_TYPE,
            List.of(new EntityExtractionService.Entity(EntityExtractionService.EntityType.LOCATION, "HR", 0, 2)));
        entities.setAllEntities(entityMap);
        
        // Mock validation
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);
        
        // Mock query processing
        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            true, "Query executed successfully", 
            List.of("TICKET-001", "TICKET-003", "TICKET-005"), 
            QueryProcessingService.QueryResultType.TICKET_LIST);
        
        // Mock response generation
        String expectedResponse = "Found 3 pending tickets from HR department: TICKET-001, TICKET-003, TICKET-005";

        // Setup mocks
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(entityExtractionService.validateEntities(entities)).thenReturn(validation);
        when(queryProcessingService.processQuery(intent, entities, userId, true)).thenReturn(queryResult);
        when(responseGenerationService.generateResponse(queryResult)).thenReturn(expectedResponse);

        // Act
        NLPService.NLPResponse response = nlpService.processAdminQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo(expectedResponse);
        
        // Verify admin workflow (no user role check)
        verify(userService, never()).getUserRole(userId);
        verify(intentRecognitionService).recognizeIntent(query);
        verify(entityExtractionService).extractEntities(query);
        verify(entityExtractionService).validateEntities(entities);
        verify(queryProcessingService).processQuery(intent, entities, userId, true); // isAdmin = true
        verify(responseGenerationService).generateResponse(queryResult);
    }

    // ==================== ERROR PROPAGATION INTEGRATION TESTS ====================

    @Test
    void processQuery_WithIntentRecognitionFailure_ShouldPropagateError() {
        // Arrange
        String query = "unintelligible query xyz abc";
        String userId = "user123";
        
        when(userService.getUserRole(userId)).thenReturn("USER");
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(null);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Could not understand query");
        
        // Verify pipeline stopped at intent recognition
        verify(userService).getUserRole(userId);
        verify(intentRecognitionService).recognizeIntent(query);
        verify(entityExtractionService, never()).extractEntities(any());
        verify(queryProcessingService, never()).processQuery(any(), any(), any(), anyBoolean());
        verify(responseGenerationService, never()).generateResponse(any());
    }

    @Test
    void processQuery_WithEntityValidationFailure_ShouldPropagateError() {
        // Arrange
        String query = "show tickets for invalid-ticket-id";
        String userId = "user123";
        
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.GET_TICKET_DETAILS, 0.8, query);
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(false);
        validation.setErrors(List.of("Invalid ticket ID format"));

        when(userService.getUserRole(userId)).thenReturn("USER");
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(intentRecognitionService.isIntentAllowed(intent.getType(), "USER")).thenReturn(true);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(entityExtractionService.validateEntities(entities)).thenReturn(validation);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Failed to extract or validate entities");
        
        // Verify pipeline stopped at entity validation
        verify(userService).getUserRole(userId);
        verify(intentRecognitionService).recognizeIntent(query);
        verify(intentRecognitionService).isIntentAllowed(intent.getType(), "USER");
        verify(entityExtractionService).extractEntities(query);
        verify(entityExtractionService).validateEntities(entities);
        verify(queryProcessingService, never()).processQuery(any(), any(), any(), anyBoolean());
        verify(responseGenerationService, never()).generateResponse(any());
    }

    @Test
    void processQuery_WithQueryProcessingFailure_ShouldPropagateError() {
        // Arrange
        String query = "show my tickets";
        String userId = "user123";
        
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS, 0.9, query);
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);
        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            false, "Database connection failed", null, QueryProcessingService.QueryResultType.ERROR);

        when(userService.getUserRole(userId)).thenReturn("USER");
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(intentRecognitionService.isIntentAllowed(intent.getType(), "USER")).thenReturn(true);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(entityExtractionService.validateEntities(entities)).thenReturn(validation);
        when(queryProcessingService.processQuery(intent, entities, userId, false)).thenReturn(queryResult);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Database connection failed");
        
        // Verify pipeline stopped at query processing
        verify(userService).getUserRole(userId);
        verify(intentRecognitionService).recognizeIntent(query);
        verify(intentRecognitionService).isIntentAllowed(intent.getType(), "USER");
        verify(entityExtractionService).extractEntities(query);
        verify(entityExtractionService).validateEntities(entities);
        verify(queryProcessingService).processQuery(intent, entities, userId, false);
        verify(responseGenerationService, never()).generateResponse(any());
    }

    // ==================== ROLE-BASED ACCESS INTEGRATION TESTS ====================

    @Test
    void processQuery_UserTryingAdminIntent_ShouldDenyAccess() {
        // Arrange
        String query = "show all tickets in the system";
        String userId = "user123";
        
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ALL_TICKETS, 0.9, query);

        when(userService.getUserRole(userId)).thenReturn("USER");
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(intentRecognitionService.isIntentAllowed(intent.getType(), "USER")).thenReturn(false);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Permission denied for intent");
        
        // Verify access control worked
        verify(userService).getUserRole(userId);
        verify(intentRecognitionService).recognizeIntent(query);
        verify(intentRecognitionService).isIntentAllowed(intent.getType(), "USER");
        verify(entityExtractionService, never()).extractEntities(any());
    }

    // ==================== SERVICE AVAILABILITY INTEGRATION TESTS ====================

    @Test
    void processQuery_WithMissingServices_ShouldHandleGracefully() {
        // Arrange
        nlpService.setEntityExtractionService(null);
        String query = "show my tickets";
        String userId = "user123";
        
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS, 0.9, query);

        when(userService.getUserRole(userId)).thenReturn("USER");
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(intentRecognitionService.isIntentAllowed(intent.getType(), "USER")).thenReturn(true);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Failed to extract or validate entities");
    }

    @Test
    void getCapabilities_WithServiceFailure_ShouldReturnNull() {
        // Arrange
        String userId = "user123";
        when(userService.getUserRole(userId)).thenThrow(new RuntimeException("Service unavailable"));

        // Act
        NLPService.NLPCapabilities capabilities = nlpService.getCapabilities(userId);

        // Assert
        assertThat(capabilities).isNull();
    }

    @Test
    void getSuggestions_WithServiceFailure_ShouldReturnNull() {
        // Arrange
        String userId = "user123";
        when(userService.getUserRole(userId)).thenThrow(new RuntimeException("Service unavailable"));

        // Act
        NLPService.NLPSuggestions suggestions = nlpService.getSuggestions(userId);

        // Assert
        assertThat(suggestions).isNull();
    }
}
