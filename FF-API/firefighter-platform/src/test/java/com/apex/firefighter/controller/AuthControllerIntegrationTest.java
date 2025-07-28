package com.apex.firefighter.controller;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FirebaseAuth firebaseAuth;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should authenticate user with valid Firebase token")
    void shouldAuthenticateUserWithValidFirebaseToken() throws Exception {
        // Given
        String firebaseIdToken = "valid-firebase-token";
        String firebaseUid = "firebase-uid-123";
        String email = "test@example.com";
        String name = "Test User";

        FirebaseToken mockFirebaseToken = mock(FirebaseToken.class);
        when(firebaseAuth.verifyIdToken(firebaseIdToken)).thenReturn(mockFirebaseToken);
        when(mockFirebaseToken.getUid()).thenReturn(firebaseUid);
        when(mockFirebaseToken.getEmail()).thenReturn(email);
        when(mockFirebaseToken.getName()).thenReturn(name);

        AuthController.FirebaseLoginRequest request = new AuthController.FirebaseLoginRequest();
        request.setIdToken(firebaseIdToken);

        // When & Then
        mockMvc.perform(post("/api/auth/firebase-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.userId").value(firebaseUid))
                .andExpect(jsonPath("$.user.email").value(email));

        // Verify user was created in database
        User savedUser = userRepository.findByUserId(firebaseUid).orElse(null);
        assert savedUser != null;
        assert savedUser.getEmail().equals(email);
    }

    @Test
    @DisplayName("Should return bad request for invalid Firebase token")
    void shouldReturnBadRequestForInvalidFirebaseToken() throws Exception {
        // Given
        String invalidToken = "invalid-firebase-token";
        when(firebaseAuth.verifyIdToken(invalidToken))
            .thenThrow(new RuntimeException("Invalid token"));

        AuthController.FirebaseLoginRequest request = new AuthController.FirebaseLoginRequest();
        request.setIdToken(invalidToken);

        // When & Then
        mockMvc.perform(post("/api/auth/firebase-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
