package com.apex.firefighter.controller;

import com.apex.firefighter.model.ConnectionTest;
import com.apex.firefighter.service.DatabaseConnectionTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/test")
public class DatabaseTestController {

    private final DatabaseConnectionTestService testService;

    @Autowired
    public DatabaseTestController(DatabaseConnectionTestService testService) {
        this.testService = testService;
    }

    /**
     * Simple CORS test endpoint
     * GET /api/test/cors
     */
    @GetMapping("/cors")
    public ResponseEntity<String> corsTest() {
        return ResponseEntity.ok("CORS is working correctly! You can access this API from any device on the network.");
    }

    /**
     * Run comprehensive database test
     * GET /api/test/run
     */
    @GetMapping("/run")
    public ResponseEntity<String> runComprehensiveTest() {
        try {
            testService.runComprehensiveTest();
            return ResponseEntity.ok("Database connection test completed successfully! Check console for details.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Test failed: " + e.getMessage());
        }
    }

    /**
     * Create a new test entry
     * POST /api/test/create
     */
    @PostMapping("/create")
    public ResponseEntity<ConnectionTest> createTest(
            @RequestParam String testName,
            @RequestParam String testValue,
            @RequestParam Integer testNumber,
            @RequestParam Boolean isActive) {
        
        ConnectionTest test = testService.createTestEntry(testName, testValue, testNumber, isActive);
        return ResponseEntity.ok(test);
    }

    /**
     * Get all test entries
     * GET /api/test/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<ConnectionTest>> getAllTests() {
        List<ConnectionTest> tests = testService.getAllTests();
        return ResponseEntity.ok(tests);
    }

    /**
     * Get test by ID
     * GET /api/test/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConnectionTest> getTestById(@PathVariable Long id) {
        Optional<ConnectionTest> test = testService.getTestById(id);
        if (test.isPresent()) {
            return ResponseEntity.ok(test.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get active tests only
     * GET /api/test/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<ConnectionTest>> getActiveTests() {
        List<ConnectionTest> tests = testService.getActiveTests();
        return ResponseEntity.ok(tests);
    }

    /**
     * Search tests by name
     * GET /api/test/search?name=searchTerm
     */
    @GetMapping("/search")
    public ResponseEntity<List<ConnectionTest>> searchTests(@RequestParam String name) {
        List<ConnectionTest> tests = testService.searchTestsByName(name);
        return ResponseEntity.ok(tests);
    }

    /**
     * Update a test entry
     * PUT /api/test/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConnectionTest> updateTest(
            @PathVariable Long id,
            @RequestParam String testValue,
            @RequestParam Integer testNumber) {
        
        try {
            ConnectionTest updatedTest = testService.updateTestEntry(id, testValue, testNumber);
            return ResponseEntity.ok(updatedTest);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Toggle test status
     * PATCH /api/test/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ConnectionTest> toggleTestStatus(@PathVariable Long id) {
        try {
            ConnectionTest updatedTest = testService.toggleTestStatus(id);
            return ResponseEntity.ok(updatedTest);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a test entry
     * DELETE /api/test/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTest(@PathVariable Long id) {
        boolean deleted = testService.deleteTestById(id);
        if (deleted) {
            return ResponseEntity.ok("Test deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete all test entries
     * DELETE /api/test/all
     */
    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllTests() {
        testService.deleteAllTests();
        return ResponseEntity.ok("All tests deleted successfully");
    }

    /**
     * Count active tests
     * GET /api/test/count/active
     */
    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveTests() {
        Long count = testService.countActiveTests();
        return ResponseEntity.ok(count);
    }

    /**
     * Get tests in number range
     * GET /api/test/range?start=1&end=100
     */
    @GetMapping("/range")
    public ResponseEntity<List<ConnectionTest>> getTestsInRange(
            @RequestParam Integer start,
            @RequestParam Integer end) {
        
        List<ConnectionTest> tests = testService.getTestsInNumberRange(start, end);
        return ResponseEntity.ok(tests);
    }
} 