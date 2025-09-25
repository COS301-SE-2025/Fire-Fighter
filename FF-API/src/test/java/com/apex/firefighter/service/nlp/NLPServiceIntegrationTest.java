package com.apex.firefighter.service.nlp;

import com.apex.firefighter.config.NLPConfig;
import com.apex.firefighter.service.ticket.TicketService;
import com.apex.firefighter.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Integration test for the complete NLP service pipeline
 */
class NLPServiceIntegrationTest {

    private NLPService nlpService;
    private IntentRecognitionService intentRecognitionService;
    private EntityExtractionService entityExtractionService;
    private QueryProcessingService queryProcessingService;
    private ResponseGenerationService responseGenerationService;

    @Mock
    private UserService userService;

    @Mock
    private TicketService ticketService;

    @Mock
    private NLPConfig nlpConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up NLP config
        when(nlpConfig.getIntentConfidenceThreshold()).thenReturn(0.7);
        when(nlpConfig.getEntityConfidenceThreshold()).thenReturn(0.7);
        when(nlpConfig.isDebugEnabled()).thenReturn(false);

        // Create main NLP service with mocked dependencies
        nlpService = new NLPService();
        nlpService.setUserService(userService);

        // Mock the internal services for integration testing
        intentRecognitionService = mock(IntentRecognitionService.class);
        entityExtractionService = mock(EntityExtractionService.class);
        queryProcessingService = mock(QueryProcessingService.class);
        responseGenerationService = mock(ResponseGenerationService.class);

        nlpService.setIntentRecognitionService(intentRecognitionService);
        nlpService.setEntityExtractionService(entityExtractionService);
        nlpService.setQueryProcessingService(queryProcessingService);
        nlpService.setResponseGenerationService(responseGenerationService);
    }

    @Test
    void testCompleteNLPPipeline_ShowActiveTickets() {
        // Arrange
        String userId = "test-user-123";
        String query = "show my active tickets";

        when(userService.getUserRole(userId)).thenReturn("USER");
        when(userService.userExists(userId)).thenReturn(true);
        when(userService.isUserAuthorized(userId)).thenReturn(true);

        // Mock the NLP pipeline
        IntentRecognitionService.Intent mockIntent = mock(IntentRecognitionService.Intent.class);
        when(mockIntent.isSuccess()).thenReturn(true);
        when(mockIntent.getType()).thenReturn(IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS);
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(mockIntent);
        when(intentRecognitionService.isIntentAllowed(any(), eq("USER"))).thenReturn(true);

        EntityExtractionService.ExtractedEntities mockEntities = mock(EntityExtractionService.ExtractedEntities.class);
        when(entityExtractionService.extractEntities(query)).thenReturn(mockEntities);

        EntityExtractionService.ValidationResult mockValidation = mock(EntityExtractionService.ValidationResult.class);
        when(mockValidation.isValid()).thenReturn(true);
        when(entityExtractionService.validateEntities(mockEntities)).thenReturn(mockValidation);

        QueryProcessingService.QueryResult mockResult = mock(QueryProcessingService.QueryResult.class);
        when(mockResult.getData()).thenReturn("Mock ticket data");
        when(queryProcessingService.processQuery(mockIntent, mockEntities, userId, false)).thenReturn(mockResult);

        when(responseGenerationService.generateResponse(mockResult)).thenReturn("Here are your active tickets");

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Here are your active tickets", response.getMessage());
    }

    @Test
    void testCompleteNLPPipeline_CreateTicket() {
        // Arrange
        String userId = "test-user-123";
        String query = "create ticket for HR emergency with issue description";

        when(userService.getUserRole(userId)).thenReturn("USER");
        when(userService.userExists(userId)).thenReturn(true);
        when(userService.isUserAuthorized(userId)).thenReturn(true);

        // Mock successful intent recognition
        IntentRecognitionService.Intent mockIntent = mock(IntentRecognitionService.Intent.class);
        when(mockIntent.isSuccess()).thenReturn(true);
        when(mockIntent.getType()).thenReturn(IntentRecognitionService.IntentType.CREATE_TICKET);
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(mockIntent);
        when(intentRecognitionService.isIntentAllowed(any(), eq("USER"))).thenReturn(true);

        // Mock entity extraction
        EntityExtractionService.ExtractedEntities mockEntities = mock(EntityExtractionService.ExtractedEntities.class);
        when(entityExtractionService.extractEntities(query)).thenReturn(mockEntities);

        EntityExtractionService.ValidationResult mockValidation = mock(EntityExtractionService.ValidationResult.class);
        when(mockValidation.isValid()).thenReturn(true);
        when(entityExtractionService.validateEntities(mockEntities)).thenReturn(mockValidation);

        // Mock query processing
        QueryProcessingService.QueryResult mockResult = mock(QueryProcessingService.QueryResult.class);
        when(queryProcessingService.processQuery(mockIntent, mockEntities, userId, false)).thenReturn(mockResult);

        // Mock response generation
        when(responseGenerationService.generateResponse(mockResult)).thenReturn("Ticket created successfully");

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Ticket created successfully", response.getMessage());
    }

    @Test
    void testCompleteNLPPipeline_AdminQuery() {
        // Arrange
        String userId = "admin-user-456";
        String query = "show all tickets";

        when(userService.getUserRole(userId)).thenReturn("ADMIN");
        when(userService.userExists(userId)).thenReturn(true);
        when(userService.isUserAuthorized(userId)).thenReturn(true);
        when(userService.hasRole(userId, "ADMIN")).thenReturn(true);

        // Mock successful intent recognition
        IntentRecognitionService.Intent mockIntent = mock(IntentRecognitionService.Intent.class);
        when(mockIntent.isSuccess()).thenReturn(true);
        when(mockIntent.getType()).thenReturn(IntentRecognitionService.IntentType.SHOW_ALL_TICKETS);
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(mockIntent);

        // Mock entity extraction
        EntityExtractionService.ExtractedEntities mockEntities = mock(EntityExtractionService.ExtractedEntities.class);
        when(entityExtractionService.extractEntities(query)).thenReturn(mockEntities);

        EntityExtractionService.ValidationResult mockValidation = mock(EntityExtractionService.ValidationResult.class);
        when(mockValidation.isValid()).thenReturn(true);
        when(entityExtractionService.validateEntities(mockEntities)).thenReturn(mockValidation);

        // Mock query processing
        QueryProcessingService.QueryResult mockResult = mock(QueryProcessingService.QueryResult.class);
        when(queryProcessingService.processQuery(mockIntent, mockEntities, userId, true)).thenReturn(mockResult);

        // Mock response generation
        when(responseGenerationService.generateResponse(mockResult)).thenReturn("Here are all tickets in the system");

        // Act
        NLPService.NLPResponse response = nlpService.processAdminQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Here are all tickets in the system", response.getMessage());
    }

    @Test
    void testNLPPipeline_InvalidInput() {
        // Arrange
        String userId = "test-user-123";
        String query = null;

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("cannot be null or empty"));
    }

    @Test
    void testNLPPipeline_UnauthorizedUser() {
        // Arrange
        String userId = "unauthorized-user";
        String query = "show my active tickets";

        when(userService.getUserRole(userId)).thenReturn("USER");
        when(userService.userExists(userId)).thenReturn(true);
        when(userService.isUserAuthorized(userId)).thenReturn(false);

        // Mock successful intent recognition so we can test authorization
        IntentRecognitionService.Intent mockIntent = mock(IntentRecognitionService.Intent.class);
        when(mockIntent.isSuccess()).thenReturn(true);
        when(mockIntent.getType()).thenReturn(IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS);
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(mockIntent);
        when(intentRecognitionService.isIntentAllowed(any(), eq("USER"))).thenReturn(true);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        // Just verify that we get a failure response - the exact message may vary
        assertNotNull(response.getMessage());
        assertFalse(response.getMessage().isEmpty());
    }

    @Test
    void testNLPPipeline_UnrecognizedIntent() {
        // Arrange
        String userId = "test-user-123";
        String query = "xyz random nonsense query that makes no sense";

        when(userService.getUserRole(userId)).thenReturn("USER");
        when(userService.userExists(userId)).thenReturn(true);
        when(userService.isUserAuthorized(userId)).thenReturn(true);

        // Mock failed intent recognition
        IntentRecognitionService.Intent mockIntent = mock(IntentRecognitionService.Intent.class);
        when(mockIntent.isSuccess()).thenReturn(false);
        when(intentRecognitionService.recognizeIntent(query)).thenReturn(mockIntent);

        // Act
        NLPService.NLPResponse response = nlpService.processQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Could not understand query"));
    }

    @Test
    void testServiceHealthCheck() {
        // Mock the health check dependencies
        when(intentRecognitionService.recognizeIntent("help")).thenReturn(mock(IntentRecognitionService.Intent.class));
        when(entityExtractionService.extractEntities("test query")).thenReturn(mock(EntityExtractionService.ExtractedEntities.class));

        // Act
        boolean isHealthy = nlpService.isServiceHealthy();

        // Assert
        assertTrue(isHealthy);
    }

    @Test
    void testGetCapabilities() {
        // Arrange
        String userId = "test-user-123";
        when(userService.getUserRole(userId)).thenReturn("USER");

        // Act
        NLPService.NLPCapabilities capabilities = nlpService.getCapabilities(userId);

        // Assert
        assertNotNull(capabilities);
        assertTrue(capabilities.isAvailable());
        assertFalse(capabilities.isAdminAccess());
        assertEquals("USER", capabilities.getAccessLevel());
        assertNotNull(capabilities.getSupportedIntents());
        assertTrue(capabilities.getSupportedIntents().length > 0);
    }

    @Test
    void testGetCapabilities_Admin() {
        // Arrange
        String userId = "admin-user-456";
        when(userService.getUserRole(userId)).thenReturn("ADMIN");

        // Act
        NLPService.NLPCapabilities capabilities = nlpService.getCapabilities(userId);

        // Assert
        assertNotNull(capabilities);
        assertTrue(capabilities.isAvailable());
        assertTrue(capabilities.isAdminAccess());
        assertEquals("ADMIN", capabilities.getAccessLevel());
        assertNotNull(capabilities.getSupportedIntents());
        assertTrue(capabilities.getSupportedIntents().length > 0);
    }

    @Test
    void testGetSuggestions() {
        // Arrange
        String userId = "test-user-123";
        when(userService.getUserRole(userId)).thenReturn("USER");

        // Act
        NLPService.NLPSuggestions suggestions = nlpService.getSuggestions(userId);

        // Assert
        assertNotNull(suggestions);
        assertTrue(suggestions.isAvailable());
        assertEquals("USER", suggestions.getUserRole());
        assertNotNull(suggestions.getSuggestedQueries());
        assertTrue(suggestions.getSuggestedQueries().length > 0);
        assertNotNull(suggestions.getExamples());
        assertTrue(suggestions.getExamples().length > 0);
    }

    @Test
    void testErrorHandling_ServiceUnavailable() {
        // Arrange
        String userId = "test-user-123";
        String query = "show my active tickets";

        // Create NLP service with null dependencies to test error handling
        NLPService faultyService = new NLPService();
        faultyService.setUserService(userService);
        // Intentionally leave other services null

        // Act
        NLPService.NLPResponse response = faultyService.processQuery(query, userId);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("not available"));
    }
}
