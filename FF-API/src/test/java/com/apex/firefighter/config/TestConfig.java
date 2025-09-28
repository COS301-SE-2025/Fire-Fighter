package com.apex.firefighter.config;

import com.apex.firefighter.service.DolibarrDatabaseService;
import com.apex.firefighter.service.auth.JwtService;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public FirebaseAuth firebaseAuth() {
        return mock(FirebaseAuth.class);
    }

    @Bean
    @Primary
    public DolibarrDatabaseService dolibarrDatabaseService() {
        return mock(DolibarrDatabaseService.class);
    }

    @Bean
    @Primary
    public JwtService jwtService() {
        return mock(JwtService.class);
    }
}
