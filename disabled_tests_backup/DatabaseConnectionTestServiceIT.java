package com.apex.firefighter.integration.services;

import com.apex.firefighter.model.ConnectionTest;
import com.apex.firefighter.repository.ConnectionTestRepository;
import com.apex.firefighter.service.DatabaseConnectionTestService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DatabaseConnectionTestServiceIT {

    @Autowired
    private DatabaseConnectionTestService dbTestService;

    @Autowired
    private ConnectionTestRepository connectionTestRepository;

    private ConnectionTest test1;
    private ConnectionTest test2;

    @BeforeEach
    @Transactional
    void setup() {
        connectionTestRepository.deleteAll();
        test1 = dbTestService.createTestEntry("Test Alpha", "OK", 10, true);
        test2 = dbTestService.createTestEntry("Test Beta", "FAIL", 20, false);
    }

    @Test
    void testRunComprehensiveTestExecutes() {
        Assertions.assertDoesNotThrow(() -> dbTestService.runComprehensiveTest());
    }

    @Test
    void testCreateAndGetTestEntry() {
        ConnectionTest created = dbTestService.createTestEntry("Test Gamma", "NEW", 30, true);
        Optional<ConnectionTest> found = dbTestService.getTestById(created.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Test Gamma", found.get().getTestName());
    }

    @Test
    void testGetAllTests() {
        List<ConnectionTest> all = dbTestService.getAllTests();
        Assertions.assertTrue(all.size() >= 2);
    }

    @Test
    void testGetTestByName() {
        Optional<ConnectionTest> found = dbTestService.getTestByName("Test Alpha");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(test1.getId(), found.get().getId());
    }

    @Test
    void testGetActiveTests() {
        List<ConnectionTest> active = dbTestService.getActiveTests();
        Assertions.assertTrue(active.stream().anyMatch(t -> t.getTestName().equals("Test Alpha")));
        Assertions.assertFalse(active.stream().anyMatch(t -> t.getTestName().equals("Test Beta")));
    }

    @Test
    void testSearchTestsByName() {
        List<ConnectionTest> results = dbTestService.searchTestsByName("Test");
        Assertions.assertTrue(results.size() >= 2);
    }

    @Test
    void testUpdateTestEntry() {
        ConnectionTest updated = dbTestService.updateTestEntry(test1.getId(), "UPDATED", 99);
        Assertions.assertEquals("UPDATED", updated.getTestValue());
        Assertions.assertEquals(99, updated.getTestNumber());
    }

    @Test
    void testToggleTestStatus() {
        Boolean before = test2.getIsActive();
        ConnectionTest toggled = dbTestService.toggleTestStatus(test2.getId());
        Assertions.assertNotEquals(before, toggled.getIsActive());
    }

    @Test
    void testDeleteTestById() {
        boolean deleted = dbTestService.deleteTestById(test2.getId());
        Assertions.assertTrue(deleted);
        Assertions.assertFalse(connectionTestRepository.existsById(test2.getId()));
    }

    @Test
    void testDeleteAllTests() {
        dbTestService.deleteAllTests();
        Assertions.assertEquals(0, connectionTestRepository.count());
    }

    @Test
    void testGetTestsInNumberRange() {
        List<ConnectionTest> inRange = dbTestService.getTestsInNumberRange(5, 15);
        Assertions.assertTrue(inRange.stream().anyMatch(t -> t.getTestName().equals("Test Alpha")));
    }

    @Test
    void testCountActiveTests() {
        Long count = dbTestService.countActiveTests();
        Assertions.assertTrue(count >= 1);
    }
}