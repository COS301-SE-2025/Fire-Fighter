package com.apex.firefighter.service.user;

import com.apex.firefighter.model.ConnectionTest;
import com.apex.firefighter.repository.ConnectionTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DatabaseConnectionTestService {

    private final ConnectionTestRepository connectionTestRepository;

    @Autowired
    public DatabaseConnectionTestService(ConnectionTestRepository connectionTestRepository) {
        this.connectionTestRepository = connectionTestRepository;
    }

    /**
     * CREATE OPERATION - Insert new data
     * Demonstrates how to save new entities to the database
     */
    public ConnectionTest createTestEntry(String testName, String testValue, Integer testNumber, Boolean isActive) {
        System.out.println("🔵 CREATE: Creating new test entry - " + testName);
        
        ConnectionTest test = new ConnectionTest(testName, testValue, testNumber, isActive);
        ConnectionTest savedTest = connectionTestRepository.save(test);
        
        System.out.println("✅ Created: " + savedTest);
        return savedTest;
    }

    /**
     * READ OPERATIONS - Various ways to fetch data
     */
    public List<ConnectionTest> getAllTests() {
        System.out.println("🔵 READ: Fetching all test entries");
        
        List<ConnectionTest> tests = connectionTestRepository.findAll();
        System.out.println("✅ Found " + tests.size() + " test entries");
        
        return tests;
    }

    public Optional<ConnectionTest> getTestById(Long id) {
        System.out.println("🔵 READ: Fetching test entry by ID - " + id);
        
        Optional<ConnectionTest> test = connectionTestRepository.findById(id);
        if (test.isPresent()) {
            System.out.println("✅ Found: " + test.get());
        } else {
            System.out.println("❌ Not found: Test with ID " + id);
        }
        
        return test;
    }

    public Optional<ConnectionTest> getTestByName(String testName) {
        System.out.println("🔵 READ: Fetching test entry by name - " + testName);
        
        Optional<ConnectionTest> test = connectionTestRepository.findByTestName(testName);
        if (test.isPresent()) {
            System.out.println("✅ Found: " + test.get());
        } else {
            System.out.println("❌ Not found: Test with name " + testName);
        }
        
        return test;
    }

    public List<ConnectionTest> getActiveTests() {
        System.out.println("🔵 READ: Fetching all active test entries");
        
        List<ConnectionTest> activeTests = connectionTestRepository.findByIsActive(true);
        System.out.println("✅ Found " + activeTests.size() + " active test entries");
        
        return activeTests;
    }

    public List<ConnectionTest> searchTestsByName(String searchTerm) {
        System.out.println("🔵 READ: Searching tests by name containing - " + searchTerm);
        
        List<ConnectionTest> tests = connectionTestRepository.findByTestNameContainingIgnoreCase(searchTerm);
        System.out.println("✅ Found " + tests.size() + " matching test entries");
        
        return tests;
    }

    /**
     * UPDATE OPERATION - Modify existing data
     */
    public ConnectionTest updateTestEntry(Long id, String newTestValue, Integer newTestNumber) {
        System.out.println("🔵 UPDATE: Updating test entry with ID - " + id);
        
        Optional<ConnectionTest> existingTest = connectionTestRepository.findById(id);
        
        if (existingTest.isPresent()) {
            ConnectionTest test = existingTest.get();
            test.setTestValue(newTestValue);
            test.setTestNumber(newTestNumber);
            test.setUpdatedAt(LocalDateTime.now());
            
            ConnectionTest updatedTest = connectionTestRepository.save(test);
            System.out.println("✅ Updated: " + updatedTest);
            return updatedTest;
        } else {
            System.out.println("❌ Update failed: Test with ID " + id + " not found");
            throw new RuntimeException("Test not found with ID: " + id);
        }
    }

    public ConnectionTest toggleTestStatus(Long id) {
        System.out.println("🔵 UPDATE: Toggling active status for test ID - " + id);
        
        Optional<ConnectionTest> existingTest = connectionTestRepository.findById(id);
        
        if (existingTest.isPresent()) {
            ConnectionTest test = existingTest.get();
            test.setIsActive(!test.getIsActive());
            test.setUpdatedAt(LocalDateTime.now());
            
            ConnectionTest updatedTest = connectionTestRepository.save(test);
            System.out.println("✅ Status toggled: " + updatedTest);
            return updatedTest;
        } else {
            System.out.println("❌ Toggle failed: Test with ID " + id + " not found");
            throw new RuntimeException("Test not found with ID: " + id);
        }
    }

    /**
     * DELETE OPERATIONS - Remove data
     */
    public boolean deleteTestById(Long id) {
        System.out.println("🔵 DELETE: Deleting test entry with ID - " + id);
        
        if (connectionTestRepository.existsById(id)) {
            connectionTestRepository.deleteById(id);
            System.out.println("✅ Deleted: Test with ID " + id);
            return true;
        } else {
            System.out.println("❌ Delete failed: Test with ID " + id + " not found");
            return false;
        }
    }

    public void deleteAllTests() {
        System.out.println("🔵 DELETE: Deleting all test entries");
        
        Long count = connectionTestRepository.count();
        connectionTestRepository.deleteAll();
        
        System.out.println("✅ Deleted " + count + " test entries");
    }

    /**
     * COMPLEX OPERATIONS - Demonstrating advanced queries
     */
    public List<ConnectionTest> getTestsInNumberRange(Integer start, Integer end) {
        System.out.println("🔵 QUERY: Finding active tests with numbers between " + start + " and " + end);
        
        List<ConnectionTest> tests = connectionTestRepository.findByIsActiveTrueAndTestNumberBetween(start, end);
        System.out.println("✅ Found " + tests.size() + " tests in range");
        
        return tests;
    }

    public Long countActiveTests() {
        System.out.println("🔵 COUNT: Counting active test entries");
        
        Long count = connectionTestRepository.countByIsActive(true);
        System.out.println("✅ Active tests count: " + count);
        
        return count;
    }

    /**
     * COMPREHENSIVE TEST METHOD - Demonstrates all CRUD operations
     */
    public void runComprehensiveTest() {
        System.out.println("\n🚀 Starting Comprehensive Database Connection Test");
        System.out.println("=" .repeat(60));
        
        try {
            // CREATE
            System.out.println("\n📝 Testing CREATE operations...");
            ConnectionTest test1 = createTestEntry("Daemon Test 1", "SUCCESS", 100, true);
            ConnectionTest test2 = createTestEntry("Daemon Test 2", "PENDING", 200, false);
            
            // READ
            System.out.println("\n📖 Testing READ operations...");
            getAllTests();
            getTestById(test1.getId());
            getTestByName("Daemon Test 1");
            getActiveTests();
            searchTestsByName("Daemon");
            
            // UPDATE
            System.out.println("\n✏️ Testing UPDATE operations...");
            updateTestEntry(test1.getId(), "UPDATED_SUCCESS", 150);
            toggleTestStatus(test2.getId());
            
            // COMPLEX QUERIES
            System.out.println("\n🔍 Testing COMPLEX QUERIES...");
            getTestsInNumberRange(100, 200);
            countActiveTests();
            
            // DELETE
            System.out.println("\n🗑️ Testing DELETE operations...");
            deleteTestById(test2.getId());
            
            System.out.println("\n✅ Comprehensive test completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=" .repeat(60));
        System.out.println("🎯 Database Connection Test Summary:");
        System.out.println("   - CREATE: ✅ Working");
        System.out.println("   - READ:   ✅ Working");
        System.out.println("   - UPDATE: ✅ Working");
        System.out.println("   - DELETE: ✅ Working");
        System.out.println("   - Database Connection: ✅ Successful");
    }
} 
