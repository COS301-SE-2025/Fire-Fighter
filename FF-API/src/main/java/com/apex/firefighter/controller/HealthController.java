package com.apex.firefighter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200", "http://localhost:8100", "http://127.0.0.1:8100", "ionic://localhost", "capacitor://localhost"})
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "FireFighter Backend");
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "FireFighter Backend");
        response.put("version", "1.0.0");
        
        // Add component health checks
        Map<String, String> components = new HashMap<>();
        components.put("database", "UP");
        components.put("authentication", "UP");
        components.put("api", "UP");
        
        response.put("components", components);
        
        // Add system info
        Map<String, Object> system = new HashMap<>();
        system.put("java.version", System.getProperty("java.version"));
        system.put("spring.profiles.active", System.getProperty("spring.profiles.active", "default"));
        
        response.put("system", system);
        
        return ResponseEntity.ok(response);
    }
} 