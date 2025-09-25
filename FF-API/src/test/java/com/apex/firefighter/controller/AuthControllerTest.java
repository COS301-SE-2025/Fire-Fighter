package com.apex.firefighter.controller;

import com.apex.firefighter.config.TestConfig;
import com.apex.firefighter.service.auth.AuthenticationService;
import com.apex.firefighter.service.auth.JwtService;
import com.apex.firefighter.dto.AuthResponse;
import com.apex.firefighter.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(value = AuthController.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return success message for test endpoint")
    void testEndpoint_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(get("/api/auth/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Firebase auth controller is working!"));
    }

    @Test
    @DisplayName("Should successfully exchange Firebase token for JWT")
    void firebaseLogin_WithValidToken_ShouldReturnAuthResponse() throws Exception {
        String validToken = "valid.firebase.token";
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setUserId("firebase-uid-123");
        
        AuthResponse mockResponse = new AuthResponse();
        mockResponse.setToken("jwt.token.here");
        mockResponse.setUser(mockUser);

        when(authenticationService.verifyFirebaseTokenAndCreateJwt(validToken))
                .thenReturn(mockResponse);

        Map<String, String> firebaseLoginRequest = Map.of("idToken", validToken);

        mockMvc.perform(post("/api/auth/firebase-login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firebaseLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    @DisplayName("Should return bad request for empty token")
    void firebaseLogin_WithEmptyToken_ShouldReturnBadRequest() throws Exception {
        Map<String, String> firebaseLoginRequest = Map.of("idToken", "");

        mockMvc.perform(post("/api/auth/firebase-login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firebaseLoginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return internal server error for service exception")
    void firebaseLogin_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        String invalidToken = "invalid.firebase.token";
        
        when(authenticationService.verifyFirebaseTokenAndCreateJwt(invalidToken))
                .thenThrow(new RuntimeException("Service exception"));

        Map<String, String> firebaseLoginRequest = Map.of("idToken", invalidToken);

        mockMvc.perform(post("/api/auth/firebase-login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firebaseLoginRequest)))
                .andExpect(status().isInternalServerError());
    }
}
