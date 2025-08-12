package com.apex.firefighter.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Try to load from service account file first
                InputStream serviceAccount = null;
                GoogleCredentials credentials = null;
                
                try {
                    // Try to load from classpath resource
                    ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
                    if (resource.exists()) {
                        serviceAccount = resource.getInputStream();
                        credentials = GoogleCredentials.fromStream(serviceAccount);
                        System.out.println("🔥 Firebase credentials loaded from service account file");
                    }
                } catch (Exception e) {
                    System.out.println("⚠️  Service account file not found, trying application default credentials");
                }
                
                // Fallback to application default credentials
                if (credentials == null) {
                    try {
                        credentials = GoogleCredentials.getApplicationDefault();
                        System.out.println("🔥 Firebase credentials loaded from application default");
                    } catch (IOException e) {
                        System.out.println("⚠️  No Firebase credentials found. Firebase features will be disabled.");
                        return; // Exit gracefully without initializing Firebase
                    }
                }
                
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId("bwm-it-hub-firefighter") // Your Firebase project ID
                    .build();
                
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized successfully");
                
                if (serviceAccount != null) {
                    serviceAccount.close();
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to initialize Firebase: " + e.getMessage());
            System.out.println("🔧 Firebase features will be disabled. To enable:");
            System.out.println("   - Set firebase.enabled=true in application.properties");
            System.out.println("   - Provide firebase-service-account.json in classpath");
            System.out.println("   - Or set up Application Default Credentials");
        }
    }

    @Bean
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
    public FirebaseAuth firebaseAuth() {
        try {
            return FirebaseAuth.getInstance();
        } catch (IllegalStateException e) {
            System.out.println("⚠️  Firebase not initialized, creating mock FirebaseAuth");
            return null; // Return null if Firebase is not initialized
        }
    }
}
