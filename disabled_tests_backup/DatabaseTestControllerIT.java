package com.apex.firefighter.integration.api;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DatabaseTestControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    private Long testId;

    @BeforeEach
    @Transactional
    void setup() {
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/api/test/create")
                .queryParam("testName", "SampleTest")
                .queryParam("testValue", "InitialValue")
                .queryParam("testNumber", 42)
                .queryParam("isActive", true)
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").value(id -> testId = Long.valueOf(id.toString())); // just set testId here, don't assign the whole chain
    }

    @Test
    void testCors() {
        webTestClient.get()
            .uri("/api/test/cors")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(resp -> Assertions.assertTrue(new String(resp.getResponseBody()).contains("CORS is working")));
    }

    @Test
    void testRun() {
        webTestClient.get()
            .uri("/api/test/run")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testCreate() {
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/api/test/create")
                .queryParam("testName", "AnotherTest")
                .queryParam("testValue", "Value2")
                .queryParam("testNumber", 99)
                .queryParam("isActive", false)
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.testName").isEqualTo("AnotherTest");
    }

    @Test
    void testGetAll() {
        webTestClient.get()
            .uri("/api/test/all")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    @Test
    void testGetById() {
        webTestClient.get()
            .uri("/api/test/" + testId)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(testId.intValue());
    }

    @Test
    void testGetActive() {
        webTestClient.get()
            .uri("/api/test/active")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    @Test
    void testSearchByName() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/test/search")
                .queryParam("name", "SampleTest")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }

    @Test
    void testUpdateById() {
        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/api/test/" + testId)
                .queryParam("testValue", "UpdatedValue")
                .queryParam("testNumber", 77)
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.testValue").isEqualTo("UpdatedValue")
            .jsonPath("$.testNumber").isEqualTo(77);
    }

    @Test
    void testToggleById() {
        webTestClient.patch()
            .uri("/api/test/" + testId + "/toggle")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.isActive").exists();
    }

    @Test
    void testDeleteById() {
        webTestClient.delete()
            .uri("/api/test/" + testId)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(resp -> Assertions.assertTrue(new String(resp.getResponseBody()).contains("deleted")));
    }

    @Test
    void testDeleteAll() {
        webTestClient.delete()
            .uri("/api/test/all")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(resp -> Assertions.assertTrue(new String(resp.getResponseBody()).contains("All tests deleted")));
    }

    @Test
    void testCountActive() {
        webTestClient.get()
            .uri("/api/test/count/active")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(resp -> Assertions.assertTrue(new String(resp.getResponseBody()).matches("\\d+")));
    }

    @Test
    void testRange() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/test/range")
                .queryParam("start", 1)
                .queryParam("end", 100)
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray();
    }
}