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
        
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(null);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Could not understand query");
        
        // Verify pipeline stopped at intent recognition
        verify(intentRecognitionService).recognizeIntent(query);
        verify(userService, never()).getUserRole(anyString()); // Should not be called since intent recognition failed
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

    // ==================== MISSING NON-ADMIN FUNCTION INTEGRATION TESTS ====================

    @Test
    void processQuery_ShowTickets_CompleteUserWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "show my tickets";
        String userId = "user123";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_TICKETS, 0.92, query);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);

        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.TICKET_LIST,
            List.of("TICKET-001", "TICKET-002"), 2);

        String expectedResponse = "Here are your tickets: TICKET-001, TICKET-002";

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
    void processQuery_ShowRejectedTickets_CompleteUserWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "show rejected tickets";
        String userId = "user123";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_REJECTED_TICKETS, 0.89, query);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);

        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.TICKET_LIST,
            List.of("REJECTED-001", "REJECTED-002"), 2);

        String expectedResponse = "Here are your rejected tickets: REJECTED-001, REJECTED-002";

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
    void processQuery_SearchTickets_CompleteUserWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "search for active hr-emergency tickets";
        String userId = "user123";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SEARCH_TICKETS, 0.91, query);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        Map<EntityExtractionService.EntityType, List<EntityExtractionService.Entity>> entityMap = new HashMap<>();
        entityMap.put(EntityExtractionService.EntityType.STATUS,
            List.of(new EntityExtractionService.Entity(EntityExtractionService.EntityType.STATUS, "active", 0, 6)));
        entityMap.put(EntityExtractionService.EntityType.EMERGENCY_TYPE,
            List.of(new EntityExtractionService.Entity(EntityExtractionService.EntityType.EMERGENCY_TYPE, "hr-emergency", 0, 12)));
        entities.setAllEntities(entityMap);

        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);

        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.TICKET_LIST,
            List.of("HR-001", "HR-002"), 2);

        String expectedResponse = "Found 2 tickets matching your search criteria";

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
    void processQuery_GetTicketDetails_CompleteUserWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "get details for ticket #123";
        String userId = "user123";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.GET_TICKET_DETAILS, 0.94, query);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        Map<EntityExtractionService.EntityType, List<EntityExtractionService.Entity>> entityMap = new HashMap<>();
        entityMap.put(EntityExtractionService.EntityType.TICKET_ID,
            List.of(new EntityExtractionService.Entity(EntityExtractionService.EntityType.TICKET_ID, "#123", 0, 4)));
        entities.setAllEntities(entityMap);

        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);

        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.TICKET_DETAILS,
            "Ticket details for #123", 1);

        String expectedResponse = "Here are the details for ticket #123";

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
    void processQuery_ShowRecentActivity_CompleteUserWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "show recent activity";
        String userId = "user123";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_RECENT_ACTIVITY, 0.93, query);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);

        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.TICKET_LIST,
            List.of("Recent activity data"), 1);

        String expectedResponse = "📊 Recent Activity 📊\n\nHere are your recent tickets...";

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
    void processQuery_ShowEmergencyTypes_CompleteUserWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "show emergency types";
        String userId = "user123";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_EMERGENCY_TYPES, 0.96, query);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);

        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.HELP,
            "Emergency types help content", 1);

        String expectedResponse = "🚨 Available Emergency Types 🚨\n\n• hr-emergency\n• financial-emergency\n• management-emergency\n• logistics-emergency";

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
    void processQuery_RequestEmergencyAccessHelp_CompleteUserWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "how do I request emergency access";
        String userId = "user123";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.REQUEST_EMERGENCY_ACCESS_HELP, 0.88, query);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);

        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.HELP,
            "Emergency access help content", 1);

        String expectedResponse = "🔐 Emergency Access Request Guide 🔐\n\nHow to Request Emergency Access:\n\n1. Create an Emergency Ticket...";

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
    void processQuery_ShowMyAccessLevel_CompleteUserWorkflow_ShouldWorkEndToEnd() {
        // Arrange
        String query = "what is my access level";
        String userId = "user123";

        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_MY_ACCESS_LEVEL, 0.90, query);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);

        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.HELP,
            "Access level information", 1);

        String expectedResponse = "👤 Current Access Level 👤\n\nYour current access level: Standard User\n\nActive Emergency Tickets: None";

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
