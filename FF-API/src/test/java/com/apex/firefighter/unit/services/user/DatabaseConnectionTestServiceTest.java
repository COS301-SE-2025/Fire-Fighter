package com.apex.firefighter.unit.services.user;

import com.apex.firefighter.model.ConnectionTest;
import com.apex.firefighter.repository.ConnectionTestRepository;
import com.apex.firefighter.service.user.DatabaseConnectionTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseConnectionTestServiceTest {

    @Mock
    private ConnectionTestRepository connectionTestRepository;

    @InjectMocks
    private DatabaseConnectionTestService databaseConnectionTestService;

    private ConnectionTest testConnectionTest;
    private List<ConnectionTest> testList;
    private final Long TEST_ID = 1L;
    private final String TEST_NAME = "Test Connection";
    private final String TEST_VALUE = "Test Value";
    private final Integer TEST_NUMBER = 100;
    private final Boolean IS_ACTIVE = true;

    @BeforeEach
    void setUp() {
        testConnectionTest = new ConnectionTest();
        testConnectionTest.setId(TEST_ID);
        testConnectionTest.setTestName(TEST_NAME);
        testConnectionTest.setTestValue(TEST_VALUE);
        testConnectionTest.setTestNumber(TEST_NUMBER);
        testConnectionTest.setIsActive(IS_ACTIVE);
        testConnectionTest.setCreatedAt(LocalDateTime.now());

        ConnectionTest secondTest = new ConnectionTest();
        secondTest.setId(2L);
        secondTest.setTestName("Second Test");
        secondTest.setTestValue("Second Value");
        secondTest.setTestNumber(200);
        secondTest.setIsActive(false);
        secondTest.setCreatedAt(LocalDateTime.now());

        testList = Arrays.asList(testConnectionTest, secondTest);
    }

    // ==================== CREATE OPERATION TESTS ====================

    @Test
    void createTestEntry_WithValidParameters_ShouldCreateAndReturnTest() {
        // Arrange
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testConnectionTest);

        // Act
        ConnectionTest result = databaseConnectionTestService.createTestEntry(TEST_NAME, TEST_VALUE, TEST_NUMBER, IS_ACTIVE);

        // Assert
        assertThat(result).isEqualTo(testConnectionTest);
        assertThat(result.getTestName()).isEqualTo(TEST_NAME);
        assertThat(result.getTestValue()).isEqualTo(TEST_VALUE);
        assertThat(result.getTestNumber()).isEqualTo(TEST_NUMBER);
        assertThat(result.getIsActive()).isEqualTo(IS_ACTIVE);
        
        verify(connectionTestRepository).save(any(ConnectionTest.class));
    }

    @Test
    void createTestEntry_WithNullParameters_ShouldHandleGracefully() {
        // Arrange
        ConnectionTest nullTest = new ConnectionTest();
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(nullTest);

        // Act
        ConnectionTest result = databaseConnectionTestService.createTestEntry(null, null, null, null);

        // Assert
        assertThat(result).isEqualTo(nullTest);
        verify(connectionTestRepository).save(any(ConnectionTest.class));
    }

    @Test
    void createTestEntry_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> databaseConnectionTestService.createTestEntry(TEST_NAME, TEST_VALUE, TEST_NUMBER, IS_ACTIVE))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(connectionTestRepository).save(any(ConnectionTest.class));
    }

    // ==================== READ OPERATION TESTS ====================

    @Test
    void getAllTests_ShouldReturnAllTests() {
        // Arrange
        when(connectionTestRepository.findAll()).thenReturn(testList);

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getAllTests();

        // Assert
        assertThat(result).isEqualTo(testList);
        assertThat(result).hasSize(2);
        verify(connectionTestRepository).findAll();
    }

    @Test
    void getAllTests_WithEmptyRepository_ShouldReturnEmptyList() {
        // Arrange
        when(connectionTestRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getAllTests();

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findAll();
    }

    @Test
    void getTestById_WithExistingId_ShouldReturnTest() {
        // Arrange
        when(connectionTestRepository.findById(TEST_ID)).thenReturn(Optional.of(testConnectionTest));

        // Act
        Optional<ConnectionTest> result = databaseConnectionTestService.getTestById(TEST_ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testConnectionTest);
        verify(connectionTestRepository).findById(TEST_ID);
    }

    @Test
    void getTestById_WithNonExistentId_ShouldReturnEmpty() {
        // Arrange
        when(connectionTestRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<ConnectionTest> result = databaseConnectionTestService.getTestById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findById(999L);
    }

    @Test
    void getTestById_WithNullId_ShouldHandleGracefully() {
        // Arrange
        when(connectionTestRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<ConnectionTest> result = databaseConnectionTestService.getTestById(null);

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findById(null);
    }

    @Test
    void getTestByName_WithExistingName_ShouldReturnTest() {
        // Arrange
        when(connectionTestRepository.findByTestName(TEST_NAME)).thenReturn(Optional.of(testConnectionTest));

        // Act
        Optional<ConnectionTest> result = databaseConnectionTestService.getTestByName(TEST_NAME);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testConnectionTest);
        verify(connectionTestRepository).findByTestName(TEST_NAME);
    }

    @Test
    void getTestByName_WithNonExistentName_ShouldReturnEmpty() {
        // Arrange
        when(connectionTestRepository.findByTestName("Non-existent")).thenReturn(Optional.empty());

        // Act
        Optional<ConnectionTest> result = databaseConnectionTestService.getTestByName("Non-existent");

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findByTestName("Non-existent");
    }

    @Test
    void getTestByName_WithNullName_ShouldHandleGracefully() {
        // Arrange
        when(connectionTestRepository.findByTestName(null)).thenReturn(Optional.empty());

        // Act
        Optional<ConnectionTest> result = databaseConnectionTestService.getTestByName(null);

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findByTestName(null);
    }

    @Test
    void getActiveTests_ShouldReturnActiveTests() {
        // Arrange
        List<ConnectionTest> activeTests = Arrays.asList(testConnectionTest);
        when(connectionTestRepository.findByIsActive(true)).thenReturn(activeTests);

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getActiveTests();

        // Assert
        assertThat(result).isEqualTo(activeTests);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(connectionTestRepository).findByIsActive(true);
    }

    @Test
    void getActiveTests_WithNoActiveTests_ShouldReturnEmptyList() {
        // Arrange
        when(connectionTestRepository.findByIsActive(true)).thenReturn(Collections.emptyList());

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getActiveTests();

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findByIsActive(true);
    }

    @Test
    void searchTestsByName_WithMatchingTerm_ShouldReturnMatchingTests() {
        // Arrange
        String searchTerm = "Test";
        when(connectionTestRepository.findByTestNameContainingIgnoreCase(searchTerm)).thenReturn(testList);

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.searchTestsByName(searchTerm);

        // Assert
        assertThat(result).isEqualTo(testList);
        assertThat(result).hasSize(2);
        verify(connectionTestRepository).findByTestNameContainingIgnoreCase(searchTerm);
    }

    @Test
    void searchTestsByName_WithNoMatches_ShouldReturnEmptyList() {
        // Arrange
        String searchTerm = "NoMatch";
        when(connectionTestRepository.findByTestNameContainingIgnoreCase(searchTerm)).thenReturn(Collections.emptyList());

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.searchTestsByName(searchTerm);

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findByTestNameContainingIgnoreCase(searchTerm);
    }

    @Test
    void searchTestsByName_WithEmptyTerm_ShouldCallRepository() {
        // Arrange
        when(connectionTestRepository.findByTestNameContainingIgnoreCase("")).thenReturn(Collections.emptyList());

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.searchTestsByName("");

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findByTestNameContainingIgnoreCase("");
    }

    // ==================== UPDATE OPERATION TESTS ====================

    @Test
    void updateTestEntry_WithValidIdAndParameters_ShouldUpdateAndReturnTest() {
        // Arrange
        String newValue = "Updated Value";
        Integer newNumber = 150;
        
        when(connectionTestRepository.findById(TEST_ID)).thenReturn(Optional.of(testConnectionTest));
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testConnectionTest);

        // Act
        ConnectionTest result = databaseConnectionTestService.updateTestEntry(TEST_ID, newValue, newNumber);

        // Assert
        assertThat(result).isEqualTo(testConnectionTest);
        verify(connectionTestRepository).findById(TEST_ID);
        verify(connectionTestRepository).save(testConnectionTest);
        // Verify that the test object was modified
        verify(connectionTestRepository).save(argThat(test -> 
            test.getTestValue().equals(newValue) && test.getTestNumber().equals(newNumber)));
    }

    @Test
    void updateTestEntry_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(connectionTestRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> databaseConnectionTestService.updateTestEntry(999L, "New Value", 150))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Test not found with ID: 999");

        verify(connectionTestRepository).findById(999L);
        verify(connectionTestRepository, never()).save(any(ConnectionTest.class));
    }

    @Test
    void updateTestEntry_WithNullParameters_ShouldUpdateWithNullValues() {
        // Arrange
        when(connectionTestRepository.findById(TEST_ID)).thenReturn(Optional.of(testConnectionTest));
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testConnectionTest);

        // Act
        ConnectionTest result = databaseConnectionTestService.updateTestEntry(TEST_ID, null, null);

        // Assert
        assertThat(result).isEqualTo(testConnectionTest);
        verify(connectionTestRepository).findById(TEST_ID);
        verify(connectionTestRepository).save(testConnectionTest);
    }

    @Test
    void toggleTestStatus_WithValidId_ShouldToggleStatusAndReturnTest() {
        // Arrange
        when(connectionTestRepository.findById(TEST_ID)).thenReturn(Optional.of(testConnectionTest));
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testConnectionTest);

        // Act
        ConnectionTest result = databaseConnectionTestService.toggleTestStatus(TEST_ID);

        // Assert
        assertThat(result).isEqualTo(testConnectionTest);
        verify(connectionTestRepository).findById(TEST_ID);
        verify(connectionTestRepository).save(testConnectionTest);
        // Verify that the status was toggled
        verify(connectionTestRepository).save(argThat(test -> test.getIsActive() != IS_ACTIVE));
    }

    @Test
    void toggleTestStatus_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(connectionTestRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> databaseConnectionTestService.toggleTestStatus(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Test not found with ID: 999");

        verify(connectionTestRepository).findById(999L);
        verify(connectionTestRepository, never()).save(any(ConnectionTest.class));
    }

    // ==================== DELETE OPERATION TESTS ====================

    @Test
    void deleteTestById_WithExistingId_ShouldDeleteAndReturnTrue() {
        // Arrange
        when(connectionTestRepository.existsById(TEST_ID)).thenReturn(true);
        doNothing().when(connectionTestRepository).deleteById(TEST_ID);

        // Act
        boolean result = databaseConnectionTestService.deleteTestById(TEST_ID);

        // Assert
        assertThat(result).isTrue();
        verify(connectionTestRepository).existsById(TEST_ID);
        verify(connectionTestRepository).deleteById(TEST_ID);
    }

    @Test
    void deleteTestById_WithNonExistentId_ShouldReturnFalse() {
        // Arrange
        when(connectionTestRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = databaseConnectionTestService.deleteTestById(999L);

        // Assert
        assertThat(result).isFalse();
        verify(connectionTestRepository).existsById(999L);
        verify(connectionTestRepository, never()).deleteById(999L);
    }

    @Test
    void deleteTestById_WithNullId_ShouldHandleGracefully() {
        // Arrange
        when(connectionTestRepository.existsById(null)).thenReturn(false);

        // Act
        boolean result = databaseConnectionTestService.deleteTestById(null);

        // Assert
        assertThat(result).isFalse();
        verify(connectionTestRepository).existsById(null);
        verify(connectionTestRepository, never()).deleteById(null);
    }

    @Test
    void deleteAllTests_ShouldDeleteAllTestsAndLogCount() {
        // Arrange
        when(connectionTestRepository.count()).thenReturn(5L);
        doNothing().when(connectionTestRepository).deleteAll();

        // Act
        databaseConnectionTestService.deleteAllTests();

        // Assert
        verify(connectionTestRepository).count();
        verify(connectionTestRepository).deleteAll();
    }

    @Test
    void deleteAllTests_WithEmptyRepository_ShouldStillCallDeleteAll() {
        // Arrange
        when(connectionTestRepository.count()).thenReturn(0L);
        doNothing().when(connectionTestRepository).deleteAll();

        // Act
        databaseConnectionTestService.deleteAllTests();

        // Assert
        verify(connectionTestRepository).count();
        verify(connectionTestRepository).deleteAll();
    }

    // ==================== COMPLEX OPERATIONS TESTS ====================

    @Test
    void getTestsInNumberRange_WithValidRange_ShouldReturnTestsInRange() {
        // Arrange
        Integer start = 50;
        Integer end = 150;
        List<ConnectionTest> rangeTests = Arrays.asList(testConnectionTest);
        when(connectionTestRepository.findByIsActiveTrueAndTestNumberBetween(start, end)).thenReturn(rangeTests);

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getTestsInNumberRange(start, end);

        // Assert
        assertThat(result).isEqualTo(rangeTests);
        assertThat(result).hasSize(1);
        verify(connectionTestRepository).findByIsActiveTrueAndTestNumberBetween(start, end);
    }

    @Test
    void getTestsInNumberRange_WithNoTestsInRange_ShouldReturnEmptyList() {
        // Arrange
        Integer start = 500;
        Integer end = 600;
        when(connectionTestRepository.findByIsActiveTrueAndTestNumberBetween(start, end)).thenReturn(Collections.emptyList());

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getTestsInNumberRange(start, end);

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findByIsActiveTrueAndTestNumberBetween(start, end);
    }

    @Test
    void getTestsInNumberRange_WithNullParameters_ShouldHandleGracefully() {
        // Arrange
        when(connectionTestRepository.findByIsActiveTrueAndTestNumberBetween(null, null)).thenReturn(Collections.emptyList());

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getTestsInNumberRange(null, null);

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findByIsActiveTrueAndTestNumberBetween(null, null);
    }

    @Test
    void countActiveTests_ShouldReturnActiveTestCount() {
        // Arrange
        when(connectionTestRepository.countByIsActive(true)).thenReturn(3L);

        // Act
        Long result = databaseConnectionTestService.countActiveTests();

        // Assert
        assertThat(result).isEqualTo(3L);
        verify(connectionTestRepository).countByIsActive(true);
    }

    @Test
    void countActiveTests_WithNoActiveTests_ShouldReturnZero() {
        // Arrange
        when(connectionTestRepository.countByIsActive(true)).thenReturn(0L);

        // Act
        Long result = databaseConnectionTestService.countActiveTests();

        // Assert
        assertThat(result).isEqualTo(0L);
        verify(connectionTestRepository).countByIsActive(true);
    }

    // ==================== COMPREHENSIVE TEST METHOD ====================

    @Test
    void runComprehensiveTest_ShouldExecuteAllOperationsSuccessfully() {
        // Arrange
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testConnectionTest);
        when(connectionTestRepository.findAll()).thenReturn(testList);
        when(connectionTestRepository.findById(any())).thenReturn(Optional.of(testConnectionTest));
        when(connectionTestRepository.findByTestName(any())).thenReturn(Optional.of(testConnectionTest));
        when(connectionTestRepository.findByIsActive(true)).thenReturn(Arrays.asList(testConnectionTest));
        when(connectionTestRepository.findByTestNameContainingIgnoreCase(any())).thenReturn(testList);
        when(connectionTestRepository.findByIsActiveTrueAndTestNumberBetween(any(), any())).thenReturn(testList);
        when(connectionTestRepository.countByIsActive(true)).thenReturn(2L);
        when(connectionTestRepository.existsById(any())).thenReturn(true);
        doNothing().when(connectionTestRepository).deleteById(any());

        // Act
        databaseConnectionTestService.runComprehensiveTest();

        // Assert - Verify that all operations were called
        verify(connectionTestRepository, atLeast(2)).save(any(ConnectionTest.class)); // CREATE operations
        verify(connectionTestRepository, atLeast(1)).findAll(); // READ all
        verify(connectionTestRepository, atLeast(1)).findById(any()); // READ by ID
        verify(connectionTestRepository, atLeast(1)).findByTestName(any()); // read by name
        verify(connectionTestRepository, atLeast(1)).findByIsActive(true); // read active
        verify(connectionTestRepository, atLeast(1)).findByTestNameContainingIgnoreCase(any()); // search
        verify(connectionTestRepository, atLeast(1)).findByIsActiveTrueAndTestNumberBetween(any(), any()); // range query
        verify(connectionTestRepository, atLeast(1)).countByIsActive(true); // count
        verify(connectionTestRepository, atLeast(1)).existsById(any()); // delete check
        verify(connectionTestRepository, atLeast(1)).deleteById(any()); // delete
    }

    @Test
    void runComprehensiveTest_WithRepositoryException_ShouldHandleGracefully() {
        // Arrange
        when(connectionTestRepository.save(any(ConnectionTest.class)))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act - Should not throw exception, should handle gracefully
        databaseConnectionTestService.runComprehensiveTest();

        // Assert
        verify(connectionTestRepository).save(any(ConnectionTest.class));
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void getAllTests_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(connectionTestRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> databaseConnectionTestService.getAllTests())
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(connectionTestRepository).findAll();
    }

    @Test
    void updateTestEntry_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(connectionTestRepository.findById(TEST_ID)).thenReturn(Optional.of(testConnectionTest));
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> databaseConnectionTestService.updateTestEntry(TEST_ID, "New Value", 150))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(connectionTestRepository).findById(TEST_ID);
        verify(connectionTestRepository).save(any(ConnectionTest.class));
    }

    @Test
    void deleteTestById_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(connectionTestRepository.existsById(TEST_ID)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(connectionTestRepository).deleteById(TEST_ID);

        // Act & Assert
        assertThatThrownBy(() -> databaseConnectionTestService.deleteTestById(TEST_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(connectionTestRepository).existsById(TEST_ID);
        verify(connectionTestRepository).deleteById(TEST_ID);
    }

    // ==================== INTEGRATION WORKFLOW TESTS ====================

    @Test
    void fullWorkflow_CreateReadUpdateDelete_ShouldWorkCorrectly() {
        // Arrange
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testConnectionTest);
        when(connectionTestRepository.findById(TEST_ID)).thenReturn(Optional.of(testConnectionTest));
        when(connectionTestRepository.existsById(TEST_ID)).thenReturn(true);
        doNothing().when(connectionTestRepository).deleteById(TEST_ID);

        // Act
        ConnectionTest created = databaseConnectionTestService.createTestEntry(TEST_NAME, TEST_VALUE, TEST_NUMBER, IS_ACTIVE);
        Optional<ConnectionTest> retrieved = databaseConnectionTestService.getTestById(TEST_ID);
        ConnectionTest updated = databaseConnectionTestService.updateTestEntry(TEST_ID, "Updated Value", 200);
        boolean deleted = databaseConnectionTestService.deleteTestById(TEST_ID);

        // Assert
        assertThat(created).isEqualTo(testConnectionTest);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(testConnectionTest);
        assertThat(updated).isEqualTo(testConnectionTest);
        assertThat(deleted).isTrue();

        verify(connectionTestRepository, times(2)).findById(TEST_ID); // Called by getTestById and updateTestEntry
        verify(connectionTestRepository, times(2)).save(any(ConnectionTest.class)); // Called by createTestEntry and updateTestEntry
        verify(connectionTestRepository).existsById(TEST_ID);
        verify(connectionTestRepository).deleteById(TEST_ID);
    }
}
