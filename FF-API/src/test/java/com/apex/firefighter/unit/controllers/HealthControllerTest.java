package com.apex.firefighter.unit.controllers;

import com.apex.firefighter.config.TestConfig;
import com.apex.firefighter.controller.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String BASE_URL = "/api";

    @Test
    @WithMockUser
    void healthCheck_ShouldReturnHealthStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("FireFighter Backend"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    void detailedHealthCheck_ShouldReturnDetailedHealthStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/health/detailed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("FireFighter Backend"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.components").exists())
                .andExpect(jsonPath("$.components.database").value("UP"))
                .andExpect(jsonPath("$.components.authentication").value("UP"))
                .andExpect(jsonPath("$.components.api").value("UP"))
                .andExpect(jsonPath("$.system").exists())
                .andExpect(jsonPath("$.system['java.version']").exists())
                .andExpect(jsonPath("$.system['spring.profiles.active']").exists());
    }

    @Test
    @WithMockUser
    void healthCheck_WithAuthentication_ShouldWork() throws Exception {
        // Health endpoints require authentication in this setup
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("FireFighter Backend"));
    }

    @Test
    @WithMockUser
    void detailedHealthCheck_WithAuthentication_ShouldWork() throws Exception {
        // Health endpoints require authentication in this setup
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/health/detailed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").exists())
                .andExpect(jsonPath("$.system").exists());
    }

    @Test
    @WithMockUser
    void healthCheck_ShouldHaveCorrectResponseStructure() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.service").isString())
                .andExpect(jsonPath("$.version").isString())
                // Should not have components or system info in basic health check
                .andExpect(jsonPath("$.components").doesNotExist())
                .andExpect(jsonPath("$.system").doesNotExist());
    }

    @Test
    @WithMockUser
    void detailedHealthCheck_ShouldHaveCorrectResponseStructure() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/health/detailed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.service").isString())
                .andExpect(jsonPath("$.version").isString())
                .andExpect(jsonPath("$.components").isMap())
                .andExpect(jsonPath("$.system").isMap())
                // Verify components structure
                .andExpect(jsonPath("$.components.database").isString())
                .andExpect(jsonPath("$.components.authentication").isString())
                .andExpect(jsonPath("$.components.api").isString())
                // Verify system structure
                .andExpect(jsonPath("$.system['java.version']").isString())
                .andExpect(jsonPath("$.system['spring.profiles.active']").isString());
    }

    @Test
    @WithMockUser
    void healthCheck_ShouldReturnConsistentData() throws Exception {
        // Perform multiple requests to ensure consistency
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get(BASE_URL + "/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.service").value("FireFighter Backend"))
                    .andExpect(jsonPath("$.version").value("1.0.0"));
        }
    }

    @Test
    @WithMockUser
    void detailedHealthCheck_ShouldReturnConsistentComponentStatus() throws Exception {
        // Perform multiple requests to ensure component status consistency
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get(BASE_URL + "/health/detailed"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.components.database").value("UP"))
                    .andExpect(jsonPath("$.components.authentication").value("UP"))
                    .andExpect(jsonPath("$.components.api").value("UP"));
        }
    }

    @Test
    @WithMockUser
    void healthCheck_ShouldHaveValidTimestamp() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString())
                // Timestamp should not be empty
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @WithMockUser
    void detailedHealthCheck_ShouldHaveValidSystemInfo() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/health/detailed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.system['java.version']").exists())
                .andExpect(jsonPath("$.system['java.version']").isString())
                .andExpect(jsonPath("$.system['java.version']").isNotEmpty())
                .andExpect(jsonPath("$.system['spring.profiles.active']").exists())
                .andExpect(jsonPath("$.system['spring.profiles.active']").isString());
    }
}
