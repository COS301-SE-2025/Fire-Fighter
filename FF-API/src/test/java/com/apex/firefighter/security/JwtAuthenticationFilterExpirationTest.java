package com.apex.firefighter.security;

import com.apex.firefighter.service.auth.JwtService;
import com.google.firebase.auth.FirebaseAuth;
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

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterExpirationTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter writer;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return TOKEN_EXPIRED error for expired custom JWT")
    void shouldReturnTokenExpiredErrorForExpiredCustomJwt() throws Exception {
        // Given
        String expiredToken = "expired.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(request.getRequestURI()).thenReturn("/api/test");
        
        when(jwtService.isCustomJwt(expiredToken)).thenReturn(true);
        when(jwtService.extractFirebaseUid(expiredToken)).thenReturn("user123");
        when(jwtService.extractIsAdmin(expiredToken)).thenReturn(false);
        when(jwtService.validateToken(expiredToken, "user123")).thenReturn(false);
        when(jwtService.isTokenExpired(expiredToken)).thenReturn(true);

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("TOKEN_EXPIRED"));
        assertTrue(responseBody.contains("Your session has expired"));
        assertTrue(responseBody.contains("requiresReauth"));
        
        // Should not continue filter chain
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should return INVALID_TOKEN error for invalid custom JWT")
    void shouldReturnInvalidTokenErrorForInvalidCustomJwt() throws Exception {
        // Given
        String invalidToken = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(request.getRequestURI()).thenReturn("/api/test");
        
        when(jwtService.isCustomJwt(invalidToken)).thenReturn(true);
        when(jwtService.extractFirebaseUid(invalidToken)).thenReturn("user123");
        when(jwtService.extractIsAdmin(invalidToken)).thenReturn(false);
        when(jwtService.validateToken(invalidToken, "user123")).thenReturn(false);
        when(jwtService.isTokenExpired(invalidToken)).thenReturn(false); // Not expired, just invalid

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("INVALID_TOKEN"));
        assertTrue(responseBody.contains("Invalid authentication token"));
        assertTrue(responseBody.contains("requiresReauth"));
        
        // Should not continue filter chain
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle expired token exception and return TOKEN_EXPIRED error")
    void shouldHandleExpiredTokenExceptionAndReturnTokenExpiredError() throws Exception {
        // Given
        String expiredToken = "expired.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(request.getRequestURI()).thenReturn("/api/test");
        
        when(jwtService.isCustomJwt(expiredToken)).thenReturn(true);
        when(jwtService.extractFirebaseUid(expiredToken))
            .thenThrow(new RuntimeException("Token expired"));

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("TOKEN_EXPIRED"));
        
        // Should not continue filter chain
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue filter chain for valid custom JWT")
    void shouldContinueFilterChainForValidCustomJwt() throws Exception {
        // Given
        String validToken = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(request.getRequestURI()).thenReturn("/api/test");
        
        when(jwtService.isCustomJwt(validToken)).thenReturn(true);
        when(jwtService.extractFirebaseUid(validToken)).thenReturn("user123");
        when(jwtService.extractIsAdmin(validToken)).thenReturn(false);
        when(jwtService.validateToken(validToken, "user123")).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response, never()).setStatus(anyInt());
        verify(filterChain).doFilter(request, response);
    }
}