package com.apex.firefighter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class SimpleAuthController {
    
    public SimpleAuthController() {
        System.out.println("🔵 SIMPLE AUTH CONTROLLER: Constructor called!");
    }

    @GetMapping("/simple-test")
    public ResponseEntity<String> simpleTest() {
        System.out.println("🔵 SIMPLE AUTH CONTROLLER: Test endpoint called");
        return ResponseEntity.ok("Simple auth controller is working!");
    }
}
