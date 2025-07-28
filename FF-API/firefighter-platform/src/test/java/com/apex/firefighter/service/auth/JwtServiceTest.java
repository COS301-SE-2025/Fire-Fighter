package com.apex.firefighter.service.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private FirebaseToken firebaseToken;

    private JwtService jwtService;

    private final String TEST_JWT_SECRET = "test-secret-key-for-jwt-testing-must-be-long-enough";
    private final long TEST_JWT_EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(firebaseAuth);
        ReflectionTestUtils.setField(jwtService, "jwtSecret", TEST_JWT_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_JWT_EXPIRATION);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidJwtToken() {
        // Given
        String firebaseUid = "test-firebase-uid";
        String email = "test@example.com";
        boolean isAdmin = true;

        // When
        String token = jwtService.generateToken(firebaseUid, email, isAdmin);

        // Then
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
        
        // Verify token contents
        assertThat(jwtService.extractFirebaseUid(token)).isEqualTo(firebaseUid);
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
        assertThat(jwtService.extractIsAdmin(token)).isEqualTo(isAdmin);
    }

    @Test
    @DisplayName("Should validate token correctly")
    void shouldValidateTokenCorrectly() {
        // Given
        String firebaseUid = "test-firebase-uid";
        String email = "test@example.com";
        String token = jwtService.generateToken(firebaseUid, email, false);

        // When & Then
        assertThat(jwtService.validateToken(token, firebaseUid)).isTrue();
        assertThat(jwtService.validateToken(token, "wrong-uid")).isFalse();
    }

    @Test
    @DisplayName("Should detect expired token")
    void shouldDetectExpiredToken() {
        // Given - Create a token that's already expired (negative expiration)
        JwtService expiredTokenService = new JwtService(firebaseAuth);
        ReflectionTestUtils.setField(expiredTokenService, "jwtSecret", TEST_JWT_SECRET);
        ReflectionTestUtils.setField(expiredTokenService, "jwtExpiration", -1000L); // Already expired

        String expiredToken = expiredTokenService.generateToken("uid", "test@example.com", false);

        // When & Then
        assertThat(jwtService.isTokenExpired(expiredToken)).isTrue();
    }

    @Test
    @DisplayName("Should verify Firebase token successfully")
    void shouldVerifyFirebaseTokenSuccessfully() throws Exception {
        // Given
        String idToken = "valid-firebase-token";
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        // When
        FirebaseToken result = jwtService.verifyFirebaseToken(idToken);

        // Then
        assertThat(result).isEqualTo(firebaseToken);
        verify(firebaseAuth).verifyIdToken(idToken);
    }

    @Test
    @DisplayName("Should throw exception for invalid Firebase token")
    void shouldThrowExceptionForInvalidFirebaseToken() throws Exception {
        // Given
        String invalidToken = "invalid-firebase-token";
        when(firebaseAuth.verifyIdToken(invalidToken))
            .thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        assertThatThrownBy(() -> jwtService.verifyFirebaseToken(invalidToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Invalid token");
    }

    @Test
    @DisplayName("Should extract claims correctly")
    void shouldExtractClaimsCorrectly() {
        // Given
        String firebaseUid = "test-uid";
        String email = "admin@example.com";
        boolean isAdmin = true;
        String token = jwtService.generateToken(firebaseUid, email, isAdmin);

        // When & Then
        assertThat(jwtService.extractFirebaseUid(token)).isEqualTo(firebaseUid);
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
        assertThat(jwtService.extractIsAdmin(token)).isEqualTo(isAdmin);
        assertThat(jwtService.extractExpiration(token)).isAfter(new java.util.Date());
    }
}
