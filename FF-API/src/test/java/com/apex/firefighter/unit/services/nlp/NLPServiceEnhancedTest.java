package com.apex.firefighter.unit.services.nlp;

import com.apex.firefighter.service.UserService;
import com.apex.firefighter.service.nlp.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NLPServiceEnhancedTest {

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

    // ==================== INPUT VALIDATION TESTS ====================

    @Test
    void processQuery_WithNullQuery_ShouldReturnValidationError() {
        // Act
        NLPService.NLPResponse response = nlpService.processQuery(null, "user123");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Query cannot be null or empty");
    }

    @Test
    void processQuery_WithEmptyQuery_ShouldReturnValidationError() {
        // Act
        NLPService.NLPResponse response = nlpService.processQuery("", "user123");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Query cannot be null or empty");
    }

    @Test
    void processQuery_WithWhitespaceOnlyQuery_ShouldReturnValidationError() {
        // Act
        NLPService.NLPResponse response = nlpService.processQuery("   ", "user123");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Query cannot be null or empty");
    }

    @Test
    void processQuery_WithNullUserId_ShouldReturnValidationError() {
        // Act
        NLPService.NLPResponse response = nlpService.processQuery("show tickets", null);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("User ID cannot be null or empty");
    }

    // ==================== ROLE DETERMINATION TESTS ====================

    @Test
    void processQuery_WithJWTAdminFlag_ShouldUseJWTRole() {
        // Arrange
        String query = "show all tickets";
        String userId = "user123";
        
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ALL_TICKETS, 0.9);
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.TICKET_LIST, "data", 1);

        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(intentRecognitionService.isIntentAllowed(intent.getType(), "ADMIN")).thenReturn(true);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(entityExtractionService.validateEntities(entities)).thenReturn(
            new EntityExtractionService.ValidationResult(true));
        when(queryProcessingService.processQuery(intent, entities, userId, true)).thenReturn(queryResult);
        when(responseGenerationService.generateResponse(queryResult)).thenReturn("Success");

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId, true);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        verify(userService, never()).getUserRole(userId); // Should not call database
        verify(queryProcessingService).processQuery(intent, entities, userId, true); // isAdmin = true
    }

    @Test
    void processQuery_WithoutJWTFlag_ShouldUseDatabaseRole() {
        // Arrange
        String query = "show my tickets";
        String userId = "user123";
        
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS, 0.9);
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            QueryProcessingService.QueryResultType.TICKET_LIST, "data", 1);

        when(userService.getUserRole(userId)).thenReturn("USER");
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(intentRecognitionService.isIntentAllowed(intent.getType(), "USER")).thenReturn(true);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(entityExtractionService.validateEntities(entities)).thenReturn(
            new EntityExtractionService.ValidationResult(true));
        when(queryProcessingService.processQuery(intent, entities, userId, false)).thenReturn(queryResult);
        when(responseGenerationService.generateResponse(queryResult)).thenReturn("Success");

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        verify(userService).getUserRole(userId);
        verify(queryProcessingService).processQuery(intent, entities, userId, false); // isAdmin = false
    }

    // ==================== CAPABILITIES TESTS ====================

    @Test
    void getCapabilities_WithAdminUser_ShouldReturnAdminCapabilities() {
        // Arrange
        String userId = "admin123";
        when(userService.getUserRole(userId)).thenReturn("ADMIN");

        // Act
        NLPService.NLPCapabilities capabilities = nlpService.getCapabilities(userId);

        // Assert
        assertThat(capabilities).isNotNull();
        assertThat(capabilities.isAvailable()).isTrue();
        assertThat(capabilities.isAdminAccess()).isTrue();
        assertThat(capabilities.getAccessLevel()).isEqualTo("ADMIN");
        assertThat(capabilities.getSupportedIntents()).contains("SHOW_ALL_TICKETS");
        assertThat(capabilities.getSupportedIntents()).contains("UPDATE_TICKET_STATUS");
    }

    @Test
    void getCapabilities_WithRegularUser_ShouldReturnUserCapabilities() {
        // Arrange
        String userId = "user123";
        when(userService.getUserRole(userId)).thenReturn("USER");

        // Act
        NLPService.NLPCapabilities capabilities = nlpService.getCapabilities(userId);

        // Assert
        assertThat(capabilities).isNotNull();
        assertThat(capabilities.isAvailable()).isTrue();
        assertThat(capabilities.isAdminAccess()).isFalse();
        assertThat(capabilities.getAccessLevel()).isEqualTo("USER");
        assertThat(capabilities.getSupportedIntents()).contains("SHOW_ACTIVE_TICKETS");
        assertThat(capabilities.getSupportedIntents()).doesNotContain("SHOW_ALL_TICKETS");
    }

    // ==================== SERVICE HEALTH TESTS ====================

    @Test
    void isServiceHealthy_WithAllServicesAvailable_ShouldReturnTrue() {
        // Arrange
        IntentRecognitionService.Intent testIntent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.GET_HELP, 0.9);
        EntityExtractionService.ExtractedEntities testEntities = new EntityExtractionService.ExtractedEntities();

        when(intentRecognitionService.recognizeIntent("help")).thenReturn(testIntent);
        when(entityExtractionService.extractEntities("test query")).thenReturn(testEntities);

        // Act
        boolean isHealthy = nlpService.isServiceHealthy();

        // Assert
        assertThat(isHealthy).isTrue();
    }

    @Test
    void isServiceHealthy_WithMissingIntentService_ShouldReturnFalse() {
        // Arrange
        nlpService.setIntentRecognitionService(null);

        // Act
        boolean isHealthy = nlpService.isServiceHealthy();

        // Assert
        assertThat(isHealthy).isFalse();
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    void processQuery_WithServiceException_ShouldReturnErrorResponse() {
        // Arrange
        String query = "show tickets";
        String userId = "user123";
        
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ALL_TICKETS, 0.9);
        
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(userService.getUserRole(userId)).thenThrow(new RuntimeException("Database error"));

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Permission denied");
    }

    @Test
    void processQuery_WithSecurityException_ShouldReturnAccessDeniedResponse() {
        // Arrange
        String query = "show tickets";
        String userId = "user123";
        
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ALL_TICKETS, 0.9);
        
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(userService.getUserRole(userId)).thenThrow(new SecurityException("Access denied"));

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Permission denied");
    }

    // ==================== ADMIN QUERY TESTS ====================

    @Test
    void processAdminQuery_WithValidQuery_ShouldProcessSuccessfully() {
        // Arrange
        String query = "show all tickets";
        String userId = "admin123";
        
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.SHOW_ALL_TICKETS, 0.9, query);
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.ValidationResult validation = new EntityExtractionService.ValidationResult(true);
        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            true, "Success", "admin data", QueryProcessingService.QueryResultType.TICKET_LIST);

        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(entityExtractionService.validateEntities(entities)).thenReturn(validation);
        when(queryProcessingService.processQuery(intent, entities, userId, true)).thenReturn(queryResult);
        when(responseGenerationService.generateResponse(queryResult)).thenReturn("Admin response");

        // Act
        NLPService.NLPResponse response = nlpService.processAdminQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Admin response");
        assertThat(response.getData()).isEqualTo("admin data");
    }

    @Test
    void processAdminQuery_WithCreateTicketIntent_ShouldSkipValidation() {
        // Arrange
        String query = "create emergency ticket";
        String userId = "admin123";
        
        IntentRecognitionService.Intent intent = new IntentRecognitionService.Intent(
            IntentRecognitionService.IntentType.CREATE_TICKET, 0.9, query);
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        QueryProcessingService.QueryResult queryResult = new QueryProcessingService.QueryResult(
            true, "Ticket created", "ticket data", QueryProcessingService.QueryResultType.OPERATION_RESULT);

        when(intentRecognitionService.recognizeIntent(query)).thenReturn(intent);
        when(entityExtractionService.extractEntities(query)).thenReturn(entities);
        when(queryProcessingService.processQuery(intent, entities, userId, true)).thenReturn(queryResult);
        when(responseGenerationService.generateResponse(queryResult)).thenReturn("Ticket created successfully");

        // Act
        NLPService.NLPResponse response = nlpService.processAdminQuery(query, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        verify(entityExtractionService, never()).validateEntities(entities); // Should skip validation
    }
}
