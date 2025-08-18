package com.apex.firefighter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@org.springframework.test.context.ActiveProfiles("test")
public class SwaggerIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testSwaggerUIAccessible() {
        String url = "http://localhost:" + port + "/swagger-ui.html";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Should redirect to swagger-ui/index.html
        assertTrue(response.getStatusCode() == HttpStatus.OK || 
                  response.getStatusCode() == HttpStatus.FOUND);
    }

    @Test
    public void testApiDocsAccessible() {
        String url = "http://localhost:" + port + "/api-docs";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("openapi"));
        assertTrue(response.getBody().contains("FireFighter Platform API"));
    }

    @Test
    public void testApiDocsContainsTicketsEndpoints() {
        String url = "http://localhost:" + port + "/api-docs";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        
        // Check for tickets endpoints
        assertTrue(body.contains("/api/tickets"));
        assertTrue(body.contains("Tickets"));
        assertTrue(body.contains("Emergency ticket management"));
    }

    @Test
    public void testApiDocsContainsUsersEndpoints() {
        String url = "http://localhost:" + port + "/api-docs";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        
        // Check for users endpoints
        assertTrue(body.contains("/api/users"));
        assertTrue(body.contains("Users"));
        assertTrue(body.contains("User management"));
    }

    @Test
    public void testApiDocsContainsSecurityScheme() {
        String url = "http://localhost:" + port + "/api-docs";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        
        // Check for security configuration
        assertTrue(body.contains("bearerAuth"));
        assertTrue(body.contains("JWT"));
    }
}
