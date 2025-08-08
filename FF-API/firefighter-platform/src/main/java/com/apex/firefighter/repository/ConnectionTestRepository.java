package com.apex.firefighter.repository;

import com.apex.firefighter.model.ConnectionTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionTestRepository extends JpaRepository<ConnectionTest, Long> {
    
    // Standard Spring Data JPA method naming
    List<ConnectionTest> findByIsActive(Boolean isActive);
    
    // Find by test name (case-sensitive)
    Optional<ConnectionTest> findByTestName(String testName);
    
    // Find by test name containing (case-insensitive)
    List<ConnectionTest> findByTestNameContainingIgnoreCase(String testName);
    
    // Find by test number greater than
    List<ConnectionTest> findByTestNumberGreaterThan(Integer testNumber);
    
    // Find active tests with test number in range
    List<ConnectionTest> findByIsActiveTrueAndTestNumberBetween(Integer start, Integer end);
    
    // Custom JPQL query
    @Query("SELECT ct FROM ConnectionTest ct WHERE ct.testValue = :value AND ct.isActive = true")
    List<ConnectionTest> findActiveTestsByValue(@Param("value") String value);
    
    // Native SQL query example
    @Query(value = "SELECT * FROM connection_test WHERE test_name ILIKE %:name% ORDER BY created_at DESC", 
           nativeQuery = true)
    List<ConnectionTest> findByTestNameLikeNative(@Param("name") String name);
    
    // Count active tests
    Long countByIsActive(Boolean isActive);
} 