package com.apex.firefighter.security;

import com.apex.firefighter.service.auth.JwtService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private FirebaseToken firebaseToken;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should process valid Firebase token")
    void shouldProcessValidFirebaseToken() throws Exception {
        // Given
        String token = "valid.firebase.token";
        String firebaseUid = "firebase-uid-123";
        String email = "user@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.verifyFirebaseToken(token)).thenReturn(firebaseToken);
        when(firebaseToken.getUid()).thenReturn(firebaseUid);
        when(firebaseToken.getEmail()).thenReturn(email);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .isEqualTo(firebaseUid);

        verify(request).setAttribute("firebaseUid", firebaseUid);
        verify(request).setAttribute("email", email);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should process valid custom JWT token")
    void shouldProcessValidCustomJwtToken() throws Exception {
        // Given
        String token = "valid.custom.jwt";
        String firebaseUid = "firebase-uid-123";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.verifyFirebaseToken(token))
            .thenThrow(new RuntimeException("Not a Firebase token"));
        when(jwtService.extractFirebaseUid(token)).thenReturn(firebaseUid);
        when(jwtService.extractIsAdmin(token)).thenReturn(true);
        when(jwtService.validateToken(token, firebaseUid)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .isEqualTo(firebaseUid);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
            .hasSize(1);

        verify(request).setAttribute("firebaseUid", firebaseUid);
        verify(request).setAttribute("isAdmin", true);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should skip authentication when no Authorization header")
    void shouldSkipAuthenticationWhenNoAuthorizationHeader() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should skip authentication when Authorization header doesn't start with Bearer")
    void shouldSkipAuthenticationWhenAuthorizationHeaderInvalid() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should handle invalid tokens gracefully")
    void shouldHandleInvalidTokensGracefully() throws Exception {
        // Given
        String invalidToken = "invalid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        
        // Only stub what will actually be called
        when(jwtService.extractFirebaseUid(invalidToken))
            .thenThrow(new RuntimeException("Invalid JWT token"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
