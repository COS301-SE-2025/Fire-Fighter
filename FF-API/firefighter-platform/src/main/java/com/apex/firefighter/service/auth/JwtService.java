package com.apex.firefighter.service.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final FirebaseAuth firebaseAuth;

    public JwtService(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Verify Firebase ID token and extract user info
     */
    public FirebaseToken verifyFirebaseToken(String idToken) throws Exception {
        return firebaseAuth.verifyIdToken(idToken);
    }

    /**
     * Generate custom JWT token for your application
     */
    public String generateToken(String firebaseUid, String email, boolean isAdmin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("firebaseUid", firebaseUid);
        claims.put("email", email);
        claims.put("isAdmin", isAdmin);
        
        return createToken(claims, firebaseUid);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract Firebase UID from custom JWT
     */
    public String extractFirebaseUid(String token) {
        return extractClaim(token, claims -> claims.get("firebaseUid", String.class));
    }

    /**
     * Extract email from custom JWT
     */
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    /**
     * Check if user is admin from custom JWT
     */
    public Boolean extractIsAdmin(String token) {
        return extractClaim(token, claims -> claims.get("isAdmin", Boolean.class));
    }

    /**
     * Extract expiration date from custom JWT
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if custom JWT token is expired
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate custom JWT token
     */
    public Boolean validateToken(String token, String firebaseUid) {
        final String tokenFirebaseUid = extractFirebaseUid(token);
        return (tokenFirebaseUid.equals(firebaseUid) && !isTokenExpired(token));
    }
}
