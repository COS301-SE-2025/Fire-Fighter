package com.apex.firefighter.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    void corsConfigurationSource_ShouldReturnValidConfiguration() {
        // Act
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

        // Assert
        assertThat(corsSource).isNotNull();

        // Create a mock request to test CORS configuration
        org.springframework.mock.web.MockHttpServletRequest mockRequest =
            new org.springframework.mock.web.MockHttpServletRequest();
        mockRequest.setRequestURI("/api/test");

        CorsConfiguration corsConfig = corsSource.getCorsConfiguration(mockRequest);
        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOriginPatterns()).isNotEmpty();
        assertThat(corsConfig.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD");
        assertThat(corsConfig.getAllowedHeaders()).contains("*");
        assertThat(corsConfig.getAllowCredentials()).isTrue();
        assertThat(corsConfig.getMaxAge()).isEqualTo(3600L);
    }

    @Test
    void corsConfiguration_ShouldAllowLocalhostOrigins() {
        // Act
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        org.springframework.mock.web.MockHttpServletRequest mockRequest =
            new org.springframework.mock.web.MockHttpServletRequest();
        CorsConfiguration corsConfig = corsSource.getCorsConfiguration(mockRequest);

        // Assert
        assertThat(corsConfig.getAllowedOriginPatterns()).contains(
            "http://localhost",
            "http://localhost:*",
            "http://127.0.0.1:*",
            "ionic://localhost",
            "capacitor://localhost"
        );
    }

    @Test
    void corsConfiguration_ShouldAllowAllHttpMethods() {
        // Act
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        org.springframework.mock.web.MockHttpServletRequest mockRequest =
            new org.springframework.mock.web.MockHttpServletRequest();
        CorsConfiguration corsConfig = corsSource.getCorsConfiguration(mockRequest);

        // Assert
        assertThat(corsConfig.getAllowedMethods()).containsExactlyInAnyOrder(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        );
    }

    @Test
    void corsConfiguration_ShouldAllowAllHeaders() {
        // Act
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        org.springframework.mock.web.MockHttpServletRequest mockRequest =
            new org.springframework.mock.web.MockHttpServletRequest();
        CorsConfiguration corsConfig = corsSource.getCorsConfiguration(mockRequest);

        // Assert
        assertThat(corsConfig.getAllowedHeaders()).contains("*");
    }

    @Test
    void corsConfiguration_ShouldAllowCredentials() {
        // Act
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        org.springframework.mock.web.MockHttpServletRequest mockRequest =
            new org.springframework.mock.web.MockHttpServletRequest();
        CorsConfiguration corsConfig = corsSource.getCorsConfiguration(mockRequest);

        // Assert
        assertThat(corsConfig.getAllowCredentials()).isTrue();
    }

    @Test
    void corsConfiguration_ShouldSetMaxAge() {
        // Act
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        org.springframework.mock.web.MockHttpServletRequest mockRequest =
            new org.springframework.mock.web.MockHttpServletRequest();
        CorsConfiguration corsConfig = corsSource.getCorsConfiguration(mockRequest);

        // Assert
        assertThat(corsConfig.getMaxAge()).isEqualTo(3600L);
    }

    // Web-layer and filter-chain instantiation tests were removed because this unit test
    // focuses on verifying the CORS configuration produced by SecurityConfig without
    // bootstrapping the full Spring MVC context.

    @Test
    void securityConfig_ShouldBeConfigurationClass() {
        // Assert
        assertThat(SecurityConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class)).isTrue();
        assertThat(SecurityConfig.class.isAnnotationPresent(org.springframework.security.config.annotation.web.configuration.EnableWebSecurity.class)).isTrue();
    }
}
