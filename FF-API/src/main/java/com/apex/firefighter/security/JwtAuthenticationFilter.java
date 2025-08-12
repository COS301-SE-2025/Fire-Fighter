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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);
            
            // Try to verify as Firebase ID token first
            if (token.contains(".") && token.split("\\.").length == 3) {
                try {
                    FirebaseToken firebaseToken = jwtService.verifyFirebaseToken(token);
                    String firebaseUid = firebaseToken.getUid();
                    String email = firebaseToken.getEmail();
                    
                    if (firebaseUid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Create authentication with Firebase UID as principal
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                firebaseUid, null, Collections.emptyList());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        // Add Firebase info to request attributes
                        request.setAttribute("firebaseUid", firebaseUid);
                        request.setAttribute("email", email);
                    }
                } catch (Exception e) {
                    // If Firebase token verification fails, try custom JWT
                    try {
                        String firebaseUid = jwtService.extractFirebaseUid(token);
                        Boolean isAdmin = jwtService.extractIsAdmin(token);
                        
                        if (firebaseUid != null && jwtService.validateToken(token, firebaseUid)) {
                            List<SimpleGrantedAuthority> authorities = isAdmin ? 
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")) : 
                                Collections.emptyList();
                            
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    firebaseUid, null, authorities);
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            
                            request.setAttribute("firebaseUid", firebaseUid);
                            request.setAttribute("isAdmin", isAdmin);
                        }
                    } catch (Exception ex) {
                        logger.warn("JWT token validation failed: " + ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}