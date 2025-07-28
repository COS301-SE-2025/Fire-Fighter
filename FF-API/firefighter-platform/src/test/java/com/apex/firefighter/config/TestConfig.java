package com.apex.firefighter.config;

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
}
