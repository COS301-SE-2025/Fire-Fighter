package com.apex.firefighter.service.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
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

    public JwtService(@Autowired(required = false) FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    private SecretKey getSigningKey() {
        // Ensure the key is at least 256 bits (32 characters) for HS256
        if (jwtSecret.length() < 32) {
            // Pad the secret to meet minimum requirements
            String paddedSecret = jwtSecret + "0".repeat(32 - jwtSecret.length());
            return Keys.hmacShaKeyFor(paddedSecret.getBytes());
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Verify Firebase ID token and extract user info
     */
    public FirebaseToken verifyFirebaseToken(String idToken) throws Exception {
        if (firebaseAuth == null) {
            System.out.println("❌ FirebaseAuth is null - Firebase not properly initialized");
            throw new RuntimeException("Firebase authentication not available - FirebaseAuth bean is null");
        }
        
        try {
            System.out.println("🔍 Verifying Firebase token...");
            FirebaseToken token = firebaseAuth.verifyIdToken(idToken);
            System.out.println("✅ Firebase token verified successfully for user: " + token.getEmail());
            return token;
        } catch (Exception e) {
            System.out.println("❌ Firebase token verification failed: " + e.getMessage());
            throw e;
        }
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
     * Check if token is a custom JWT (has our custom claims)
     * Custom JWTs will have 'firebaseUid' claim, Firebase tokens won't
     */
    public boolean isCustomJwt(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("firebaseUid") != null;
        } catch (Exception e) {
            return false;
        }
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
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            // Token is expired if ExpiredJwtException is thrown
            System.out.println("🔒 JWT SERVICE: Token expired - " + e.getMessage());
            return true;
        } catch (Exception e) {
            // Any other parsing error also indicates invalid/expired token
            System.out.println("🔒 JWT SERVICE: Token parsing error - " + e.getMessage());
            return true;
        }
    }


    /**
     * Validate custom JWT token
     */
    public Boolean validateToken(String token, String firebaseUid) {
        final String tokenFirebaseUid = extractFirebaseUid(token);
        return (tokenFirebaseUid.equals(firebaseUid) && !isTokenExpired(token));
    }
}
