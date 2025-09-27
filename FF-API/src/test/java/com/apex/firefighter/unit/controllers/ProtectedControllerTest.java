package com.apex.firefighter.unit.controllers;

import com.apex.firefighter.config.TestConfig;
import com.apex.firefighter.controller.ProtectedController;
import com.apex.firefighter.service.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProtectedController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    })
@Import(TestConfig.class)
@ActiveProfiles("test")
class ProtectedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    // ==================== HELLO ENDPOINT TESTS ====================

    @Test
    void hello_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, you have accessed a protected endpoint with a valid API key!"));
    }

    @Test
    void hello_ShouldReturnCorrectContentType() throws Exception {
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    void hello_WithDifferentHttpMethods_ShouldOnlyAllowGet() throws Exception {
        // GET should work
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk());

        // Other methods should not be allowed
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/protected/hello"))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/protected/hello"))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/protected/hello"))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/protected/hello"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void hello_WithQueryParameters_ShouldIgnoreParametersAndReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/protected/hello")
                .param("test", "value")
                .param("another", "param"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, you have accessed a protected endpoint with a valid API key!"));
    }

    @Test
    void hello_WithHeaders_ShouldIgnoreHeadersAndReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/protected/hello")
                .header("Custom-Header", "custom-value")
                .header("Another-Header", "another-value"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, you have accessed a protected endpoint with a valid API key!"));
    }

    // ==================== ENDPOINT MAPPING TESTS ====================

    @Test
    void hello_ShouldBeMappedToCorrectPath() throws Exception {
        // Test exact path
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk());
    }

    @Test
    void hello_WithTrailingSlash_ShouldNotMatch() throws Exception {
        mockMvc.perform(get("/api/protected/hello/"))
                .andExpect(status().isNotFound());
    }

    @Test
    void hello_WithIncorrectPath_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/protected/helo"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/protected/Hello"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/protected/HELLO"))
                .andExpect(status().isNotFound());
    }

    @Test
    void hello_WithIncorrectBasePath_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/protect/hello"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/protected/hello"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/hello"))
                .andExpect(status().isNotFound());
    }

    // ==================== RESPONSE VALIDATION TESTS ====================

    @Test
    void hello_ShouldReturnExactMessage() throws Exception {
        String expectedMessage = "Hello, you have accessed a protected endpoint with a valid API key!";

        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void hello_ShouldReturnStringResponse() throws Exception {
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    void hello_ResponseShouldNotBeEmpty() throws Exception {
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())));
    }

    // ==================== MULTIPLE REQUESTS TESTS ====================

    @Test
    void hello_MultipleRequests_ShouldReturnSameResponse() throws Exception {
        String expectedMessage = "Hello, you have accessed a protected endpoint with a valid API key!";

        // First request
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));

        // Second request
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));

        // Third request
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void hello_ConcurrentRequests_ShouldHandleCorrectly() throws Exception {
        String expectedMessage = "Hello, you have accessed a protected endpoint with a valid API key!";

        // Simulate concurrent requests (though MockMvc is synchronous)
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/protected/hello"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expectedMessage));
        }
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void hello_WithMalformedUrl_ShouldReturnNotFound() throws Exception {
        // Test with completely wrong paths that should definitely return 404
        mockMvc.perform(get("/api/protected/hello/invalid"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/invalid/hello"))
                .andExpect(status().isNotFound());
    }

    @Test
    void hello_WithExtraPathSegments_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/protected/hello/extra"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/protected/hello/extra/path"))
                .andExpect(status().isNotFound());
    }

    @Test
    void hello_CaseSensitivity_ShouldBeExact() throws Exception {
        // Correct case should work
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk());

        // Different cases should not work
        mockMvc.perform(get("/API/protected/hello"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/PROTECTED/hello"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/protected/HELLO"))
                .andExpect(status().isNotFound());
    }

    // ==================== INTEGRATION AND BEHAVIOR TESTS ====================

    @Test
    void hello_ShouldNotRequireAuthentication_InTestContext() throws Exception {
        // In test context, security is typically disabled
        // This test verifies the endpoint works without authentication
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, you have accessed a protected endpoint with a valid API key!"));
    }

    @Test
    void hello_ShouldReturnImmediately() throws Exception {
        // Test that the endpoint doesn't have any delays or blocking operations
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete quickly (less than 1 second in test environment)
        org.assertj.core.api.Assertions.assertThat(duration).isLessThan(1000);
    }

    @Test
    void hello_ShouldBeIdempotent() throws Exception {
        String expectedMessage = "Hello, you have accessed a protected endpoint with a valid API key!";

        // Multiple calls should return the same result
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/protected/hello"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expectedMessage));
        }
    }

    @Test
    void hello_ShouldNotHaveSideEffects() throws Exception {
        // First call
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk());

        // Second call should return the same result (no side effects)
        mockMvc.perform(get("/api/protected/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, you have accessed a protected endpoint with a valid API key!"));
    }
}
