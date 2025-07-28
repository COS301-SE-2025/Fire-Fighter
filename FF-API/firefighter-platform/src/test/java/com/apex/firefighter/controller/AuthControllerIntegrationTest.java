package com.apex.firefighter.controller;

import com.apex.firefighter.service.auth.AuthenticationService;
import com.apex.firefighter.dto.AuthResponse;
import com.apex.firefighter.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Should authenticate user with valid Firebase token")
    void shouldAuthenticateUserWithValidFirebaseToken() throws Exception {
        // Setup MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Given
        String firebaseIdToken = "valid-firebase-token";
        String firebaseUid = "firebase-uid-123";
        String email = "test@example.com";
        String customJwt = "custom-jwt-token";

        User mockUser = new User();
        mockUser.setUserId(firebaseUid);
        mockUser.setEmail(email);
        mockUser.setUsername("Test User");

        AuthResponse mockResponse = new AuthResponse(customJwt, mockUser);
        
        when(authenticationService.verifyFirebaseTokenAndCreateJwt(firebaseIdToken))
            .thenReturn(mockResponse);

        AuthController.FirebaseLoginRequest request = new AuthController.FirebaseLoginRequest();
        request.setIdToken(firebaseIdToken);

        // When & Then
        mockMvc.perform(post("/api/auth/firebase-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(customJwt))
                .andExpect(jsonPath("$.user.userId").value(firebaseUid))
                .andExpect(jsonPath("$.user.email").value(email));

        verify(authenticationService).verifyFirebaseTokenAndCreateJwt(firebaseIdToken);
    }

    @Test
    @DisplayName("Should return bad request for invalid Firebase token")
    void shouldReturnBadRequestForInvalidFirebaseToken() throws Exception {
        // Setup MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Given
        String invalidToken = "invalid-firebase-token";
        when(authenticationService.verifyFirebaseTokenAndCreateJwt(invalidToken))
            .thenThrow(new RuntimeException("Invalid token"));

        AuthController.FirebaseLoginRequest request = new AuthController.FirebaseLoginRequest();
        request.setIdToken(invalidToken);

        // When & Then
        mockMvc.perform(post("/api/auth/firebase-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authenticationService).verifyFirebaseTokenAndCreateJwt(invalidToken);
    }
}
