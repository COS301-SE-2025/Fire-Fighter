package com.apex.firefighter.security;

import com.apex.firefighter.service.auth.JwtService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        System.out.println("🔒 JWT FILTER: Processing request to: " + request.getRequestURI());
        System.out.println("🔒 JWT FILTER: Authorization header: " + (authHeader != null ? "Present" : "Missing"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("🔒 JWT FILTER: No Bearer token found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);
            System.out.println("🔒 JWT FILTER: Token extracted (first 20 chars): " + token.substring(0, Math.min(20, token.length())) + "...");
            
            // Check if this is a valid JWT format
            if (token.contains(".") && token.split("\\.").length == 3) {
                System.out.println("🔒 JWT FILTER: Token has valid JWT format");
                
                // First check if it's our custom JWT token
                if (jwtService.isCustomJwt(token)) {
                    System.out.println("🔒 JWT FILTER: Processing as custom JWT token");
                    // Handle custom JWT token
                    try {
                        String firebaseUid = jwtService.extractFirebaseUid(token);
                        Boolean isAdmin = jwtService.extractIsAdmin(token);
                        
                        System.out.println("🔒 JWT FILTER: Extracted from custom JWT - UID: " + firebaseUid + ", Admin: " + isAdmin);
                        
                        if (firebaseUid != null && jwtService.validateToken(token, firebaseUid)) {
                            System.out.println("🔒 JWT FILTER: ✅ Custom JWT token validated successfully for user: " + firebaseUid);
                            List<SimpleGrantedAuthority> authorities = isAdmin ?
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")) :
                                Collections.emptyList();

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    firebaseUid, null, authorities);
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);

                            // Set request attributes for controllers to access
                            request.setAttribute("firebaseUid", firebaseUid);
                            request.setAttribute("isAdmin", isAdmin != null ? isAdmin : false);

                            System.out.println("🔒 JWT FILTER: ✅ Authentication set in SecurityContext");
                            System.out.println("🔒 JWT FILTER: ✅ User: " + firebaseUid + ", Admin: " + isAdmin);
                        } else {
                            System.out.println("🔒 JWT FILTER: ❌ Custom JWT token validation failed");

                            // Check if token is expired specifically
                            if (firebaseUid != null && jwtService.isTokenExpired(token)) {
                                System.out.println("🔒 JWT FILTER: ❌ Token expired for user: " + firebaseUid);
                                sendTokenExpiredResponse(response);
                                return;
                            } else {
                                System.out.println("🔒 JWT FILTER: ❌ Token invalid for user: " + firebaseUid);
                                sendInvalidTokenResponse(response);
                                return;
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("🔒 JWT FILTER: Custom JWT token validation error: " + ex.getMessage());
                        logger.warn("Custom JWT token validation failed: " + ex.getMessage());

                        // Check if it's an expired token exception
                        if(ex.getMessage() != null && ex.getMessage().toLowerCase().contains("expired")){

                            sendTokenExpiredResponse(response);
                            return;

                        } else{

                            sendInvalidTokenResponse(response);
                            return;

                        }

                    }
                } else {
                    System.out.println("🔒 JWT FILTER: Processing as Firebase ID token");
                    // Try to verify as Firebase ID token
                    try {
                        FirebaseToken firebaseToken = jwtService.verifyFirebaseToken(token);
                        String firebaseUid = firebaseToken.getUid();
                        String email = firebaseToken.getEmail();
                        
                        System.out.println("🔒 JWT FILTER: Firebase token verified - UID: " + firebaseUid + ", Email: " + email);
                        
                        if (firebaseUid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            // Create authentication with Firebase UID as principal
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    firebaseUid, null, Collections.emptyList());
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            
                            // Add Firebase info to request attributes
                            request.setAttribute("firebaseUid", firebaseUid);
                            request.setAttribute("email", email);
                            
                            System.out.println("🔒 JWT FILTER: Firebase authentication set in SecurityContext for user: " + firebaseUid);
                        }
                    } catch (Exception e) {
                        System.out.println("🔒 JWT FILTER: Firebase token verification failed: " + e.getMessage());
                        logger.warn("Firebase token verification failed: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("🔒 JWT FILTER: Token does not have valid JWT format");
            }
        } catch (Exception e) {
            System.out.println("🔒 JWT FILTER: General error: " + e.getMessage());
            logger.error("Cannot set user authentication: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    //Send standardized token expired response
    private void sendTokenExpiredResponse(HttpServletResponse response) throws IOException {
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = "{"
            + "\"error\": \"TOKEN_EXPIRED\","
            + "\"message\": \"Your session has expired. Please log in again.\","
            + "\"timestamp\": \"" + java.time.Instant.now().toString() + "\","
            + "\"requiresReauth\": true"
            + "}";
            
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    //Send standardized invalid token response
    private void sendInvalidTokenResponse(HttpServletResponse response) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = "{"
            + "\"error\": \"INVALID_TOKEN\","
            + "\"message\": \"Invalid authentication token. Please log in again.\","
            + "\"timestamp\": \"" + java.time.Instant.now().toString() + "\","
            + "\"requiresReauth\": true"
            + "}";
            
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    
}