package com.apex.firefighter.controller;

import com.apex.firefighter.service.nlp.NLPService;
import com.apex.firefighter.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NLPControllerTest {

    private NLPController nlpController;

    @Mock
    private NLPService nlpService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest httpRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        nlpController = new NLPController();
        nlpController.setNlpService(nlpService);
        nlpController.setUserService(userService);
    }

    @Test
    void processQuery_WithValidAuthentication_ShouldReturnSuccessResponse() {
        // Arrange
        String firebaseUid = "test-user-123";
        String query = "show my active tickets";
        
        NLPController.NLPQueryRequest request = new NLPController.NLPQueryRequest(query);
        NLPService.NLPResponse expectedResponse = new NLPService.NLPResponse("Here are your active tickets", true);

        when(httpRequest.getAttribute("firebaseUid")).thenReturn(firebaseUid);
        when(httpRequest.getAttribute("isAdmin")).thenReturn(null);
        when(userService.userExists(firebaseUid)).thenReturn(true);
        when(userService.isUserAuthorized(firebaseUid)).thenReturn(true);
        when(nlpService.processQuery(query, firebaseUid, null)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<NLPService.NLPResponse> response = nlpController.processQuery(request, httpRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Here are your active tickets", response.getBody().getMessage());
    }

    @Test
    void processQuery_WithMissingAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        String query = "show my active tickets";
        NLPController.NLPQueryRequest request = new NLPController.NLPQueryRequest(query);

        when(httpRequest.getAttribute("firebaseUid")).thenReturn(null);

        // Act
        ResponseEntity<NLPService.NLPResponse> response = nlpController.processQuery(request, httpRequest);

        // Assert
        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Authentication required"));
    }





    @Test
    void processQuery_WithEmptyQuery_ShouldReturnBadRequest() {
        // Arrange
        String firebaseUid = "test-user-123";
        NLPController.NLPQueryRequest request = new NLPController.NLPQueryRequest("");

        when(httpRequest.getAttribute("firebaseUid")).thenReturn(firebaseUid);
        when(userService.userExists(firebaseUid)).thenReturn(true);
        when(userService.isUserAuthorized(firebaseUid)).thenReturn(true);

        // Act
        ResponseEntity<NLPService.NLPResponse> response = nlpController.processQuery(request, httpRequest);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Query cannot be empty"));
    }
}
