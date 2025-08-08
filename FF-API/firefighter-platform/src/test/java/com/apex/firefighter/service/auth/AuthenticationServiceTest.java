package com.apex.firefighter.service.auth;

import com.apex.firefighter.dto.AuthResponse;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private FirebaseToken firebaseToken;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(userRepository, jwtService);
    }

    @Test
    @DisplayName("Should verify Firebase token and create JWT for new user")
    void shouldVerifyFirebaseTokenAndCreateJwtForNewUser() throws Exception {
        // Given
        String firebaseIdToken = "valid-firebase-id-token";
        String firebaseUid = "firebase-uid-123";
        String email = "newuser@example.com";
        String username = "newuser";
        String customJwt = "custom-jwt-token";

        when(jwtService.verifyFirebaseToken(firebaseIdToken)).thenReturn(firebaseToken);
        when(firebaseToken.getUid()).thenReturn(firebaseUid);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseToken.getName()).thenReturn(username);

        when(userRepository.findByUserId(firebaseUid)).thenReturn(Optional.empty());
        
        User newUser = new User(firebaseUid, username, email, null);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(jwtService.generateToken(firebaseUid, email, false)).thenReturn(customJwt);

        // When
        AuthResponse response = authenticationService.verifyFirebaseTokenAndCreateJwt(firebaseIdToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(customJwt);
        assertThat(response.getUser().getUserId()).isEqualTo(firebaseUid);
        assertThat(response.getUser().getEmail()).isEqualTo(email);

        verify(jwtService).verifyFirebaseToken(firebaseIdToken);
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(firebaseUid, email, false);
    }

    @Test
    @DisplayName("Should verify Firebase token and create JWT for existing user")
    void shouldVerifyFirebaseTokenAndCreateJwtForExistingUser() throws Exception {
        // Given
        String firebaseIdToken = "valid-firebase-id-token";
        String firebaseUid = "firebase-uid-123";
        String email = "existinguser@example.com";
        String customJwt = "custom-jwt-token";

        when(jwtService.verifyFirebaseToken(firebaseIdToken)).thenReturn(firebaseToken);
        when(firebaseToken.getUid()).thenReturn(firebaseUid);
        when(firebaseToken.getEmail()).thenReturn(email);

        User existingUser = new User(firebaseUid, "existinguser", email, "IT");
        existingUser.setIsAdmin(true);
        when(userRepository.findByUserId(firebaseUid)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(jwtService.generateToken(firebaseUid, email, true)).thenReturn(customJwt);

        // When
        AuthResponse response = authenticationService.verifyFirebaseTokenAndCreateJwt(firebaseIdToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(customJwt);
        assertThat(response.getUser().getUserId()).isEqualTo(firebaseUid);
        assertThat(response.getUser().getIsAdmin()).isTrue();

        verify(jwtService).verifyFirebaseToken(firebaseIdToken);
        verify(jwtService).generateToken(firebaseUid, email, true);
    }

    @Test
    @DisplayName("Should throw exception for invalid Firebase token")
    void shouldThrowExceptionForInvalidFirebaseToken() throws Exception {
        // Given
        String invalidToken = "invalid-firebase-token";
        when(jwtService.verifyFirebaseToken(invalidToken))
            .thenThrow(new RuntimeException("Invalid Firebase token"));

        // When & Then
        assertThatThrownBy(() -> authenticationService.verifyFirebaseTokenAndCreateJwt(invalidToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Invalid Firebase token");

        verify(jwtService).verifyFirebaseToken(invalidToken);
        verifyNoInteractions(userRepository);
    }
}