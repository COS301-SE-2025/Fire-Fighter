package com.apex.firefighter.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Profile("!test")
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
                    }
                } catch (Exception e) {
                    System.out.println("Service account file not found, trying application default credentials");
                }
                
                // Fallback to application default credentials
                if (credentials == null) {
                    credentials = GoogleCredentials.getApplicationDefault();
                }
                
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId("bwm-it-hub-firefighter") // Your Firebase project ID
                    .build();
                
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully");
                
                if (serviceAccount != null) {
                    serviceAccount.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
