<<<<<<< Updated upstream
=======
package com.apex.firefighter.unit.services;

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

    private ConnectionTest testEntry;
    private final Long TEST_ID = 1L;
    private final String TEST_NAME = "Test Entry";
    private final String TEST_VALUE = "SUCCESS";
    private final Integer TEST_NUMBER = 100;
    private final Boolean IS_ACTIVE = true;

    @BeforeEach
    void setUp() {
        testEntry = new ConnectionTest();
        testEntry.setId(TEST_ID);
        testEntry.setTestName(TEST_NAME);
        testEntry.setTestValue(TEST_VALUE);
        testEntry.setTestNumber(TEST_NUMBER);
        testEntry.setIsActive(IS_ACTIVE);
        testEntry.setCreatedAt(LocalDateTime.now());
        testEntry.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== CREATE OPERATION TESTS ====================

    @Test
    void createTestEntry_ShouldCreateAndReturnNewEntry() {
        // Arrange
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testEntry);

        // Act
        ConnectionTest result = databaseConnectionTestService.createTestEntry(TEST_NAME, TEST_VALUE, TEST_NUMBER, IS_ACTIVE);

        // Assert
        assertThat(result).isEqualTo(testEntry);
        verify(connectionTestRepository).save(any(ConnectionTest.class));
    }

    @Test
    void createTestEntry_WithNullValues_ShouldHandleGracefully() {
        // Arrange
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testEntry);

        // Act
        ConnectionTest result = databaseConnectionTestService.createTestEntry(null, null, null, null);

        // Assert
        assertThat(result).isEqualTo(testEntry);
        verify(connectionTestRepository).save(any(ConnectionTest.class));
    }

    // ==================== READ OPERATION TESTS ====================

    @Test
    void getAllTests_ShouldReturnAllEntries() {
        // Arrange
        List<ConnectionTest> testEntries = Arrays.asList(testEntry);
        when(connectionTestRepository.findAll()).thenReturn(testEntries);

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getAllTests();

        // Assert
        assertThat(result).isEqualTo(testEntries);
        assertThat(result).hasSize(1);
        verify(connectionTestRepository).findAll();
    }

    @Test
    void getAllTests_WithEmptyDatabase_ShouldReturnEmptyList() {
        // Arrange
        when(connectionTestRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getAllTests();

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findAll();
    }

    @Test
    void getTestById_WithExistingId_ShouldReturnEntry() {
        // Arrange
        when(connectionTestRepository.findById(TEST_ID)).thenReturn(Optional.of(testEntry));

        // Act
        Optional<ConnectionTest> result = databaseConnectionTestService.getTestById(TEST_ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testEntry);
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
    void getTestByName_WithExistingName_ShouldReturnEntry() {
        // Arrange
        when(connectionTestRepository.findByTestName(TEST_NAME)).thenReturn(Optional.of(testEntry));

        // Act
        Optional<ConnectionTest> result = databaseConnectionTestService.getTestByName(TEST_NAME);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testEntry);
        verify(connectionTestRepository).findByTestName(TEST_NAME);
    }

    @Test
    void getTestByName_WithNonExistentName_ShouldReturnEmpty() {
        // Arrange
        when(connectionTestRepository.findByTestName("NonExistent")).thenReturn(Optional.empty());

        // Act
        Optional<ConnectionTest> result = databaseConnectionTestService.getTestByName("NonExistent");

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findByTestName("NonExistent");
    }

    @Test
    void getActiveTests_ShouldReturnActiveEntries() {
        // Arrange
        List<ConnectionTest> activeTests = Arrays.asList(testEntry);
        when(connectionTestRepository.findByIsActive(true)).thenReturn(activeTests);

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getActiveTests();

        // Assert
        assertThat(result).isEqualTo(activeTests);
        assertThat(result).hasSize(1);
        verify(connectionTestRepository).findByIsActive(true);
    }

    @Test
    void searchTestsByName_ShouldReturnMatchingEntries() {
        // Arrange
        String searchTerm = "Test";
        List<ConnectionTest> matchingTests = Arrays.asList(testEntry);
        when(connectionTestRepository.findByTestNameContainingIgnoreCase(searchTerm)).thenReturn(matchingTests);

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.searchTestsByName(searchTerm);

        // Assert
        assertThat(result).isEqualTo(matchingTests);
        assertThat(result).hasSize(1);
        verify(connectionTestRepository).findByTestNameContainingIgnoreCase(searchTerm);
    }

    @Test
    void searchTestsByName_WithNoMatches_ShouldReturnEmptyList() {
        // Arrange
        String searchTerm = "NoMatch";
        when(connectionTestRepository.findByTestNameContainingIgnoreCase(searchTerm)).thenReturn(Arrays.asList());

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.searchTestsByName(searchTerm);

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findByTestNameContainingIgnoreCase(searchTerm);
    }

    // ==================== UPDATE OPERATION TESTS ====================

    @Test
    void updateTestEntry_WithExistingId_ShouldUpdateAndReturnEntry() {
        // Arrange
        String newTestValue = "UPDATED";
        Integer newTestNumber = 200;
        when(connectionTestRepository.findById(TEST_ID)).thenReturn(Optional.of(testEntry));
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testEntry);

        // Act
        ConnectionTest result = databaseConnectionTestService.updateTestEntry(TEST_ID, newTestValue, newTestNumber);

        // Assert
        assertThat(result).isEqualTo(testEntry);
        verify(connectionTestRepository).findById(TEST_ID);
        verify(connectionTestRepository).save(testEntry);
    }

    @Test
    void updateTestEntry_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(connectionTestRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> databaseConnectionTestService.updateTestEntry(999L, "NEW_VALUE", 300))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Test not found with ID: 999");

        verify(connectionTestRepository).findById(999L);
        verify(connectionTestRepository, never()).save(any(ConnectionTest.class));
    }

    @Test
    void toggleTestStatus_WithExistingId_ShouldToggleStatusAndReturnEntry() {
        // Arrange
        when(connectionTestRepository.findById(TEST_ID)).thenReturn(Optional.of(testEntry));
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testEntry);

        // Act
        ConnectionTest result = databaseConnectionTestService.toggleTestStatus(TEST_ID);

        // Assert
        assertThat(result).isEqualTo(testEntry);
        verify(connectionTestRepository).findById(TEST_ID);
        verify(connectionTestRepository).save(testEntry);
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
    void deleteTestById_WithExistingId_ShouldReturnTrue() {
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
    void deleteAllTests_ShouldDeleteAllEntries() {
        // Arrange
        when(connectionTestRepository.count()).thenReturn(5L);
        doNothing().when(connectionTestRepository).deleteAll();

        // Act
        databaseConnectionTestService.deleteAllTests();

        // Assert
        verify(connectionTestRepository).count();
        verify(connectionTestRepository).deleteAll();
    }

    // ==================== COMPLEX OPERATION TESTS ====================

    @Test
    void getTestsInNumberRange_ShouldReturnTestsInRange() {
        // Arrange
        Integer start = 50;
        Integer end = 150;
        List<ConnectionTest> testsInRange = Arrays.asList(testEntry);
        when(connectionTestRepository.findByIsActiveTrueAndTestNumberBetween(start, end)).thenReturn(testsInRange);

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getTestsInNumberRange(start, end);

        // Assert
        assertThat(result).isEqualTo(testsInRange);
        assertThat(result).hasSize(1);
        verify(connectionTestRepository).findByIsActiveTrueAndTestNumberBetween(start, end);
    }

    @Test
    void getTestsInNumberRange_WithNoTestsInRange_ShouldReturnEmptyList() {
        // Arrange
        Integer start = 500;
        Integer end = 600;
        when(connectionTestRepository.findByIsActiveTrueAndTestNumberBetween(start, end)).thenReturn(Arrays.asList());

        // Act
        List<ConnectionTest> result = databaseConnectionTestService.getTestsInNumberRange(start, end);

        // Assert
        assertThat(result).isEmpty();
        verify(connectionTestRepository).findByIsActiveTrueAndTestNumberBetween(start, end);
    }

    @Test
    void countActiveTests_ShouldReturnActiveCount() {
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

    // ==================== COMPREHENSIVE TEST ====================

    @Test
    void runComprehensiveTest_ShouldExecuteAllOperations() {
        // Arrange
        when(connectionTestRepository.save(any(ConnectionTest.class))).thenReturn(testEntry);
        when(connectionTestRepository.findAll()).thenReturn(Arrays.asList(testEntry));
        when(connectionTestRepository.findById(any(Long.class))).thenReturn(Optional.of(testEntry));
        when(connectionTestRepository.findByTestName(anyString())).thenReturn(Optional.of(testEntry));
        when(connectionTestRepository.findByIsActive(true)).thenReturn(Arrays.asList(testEntry));
        when(connectionTestRepository.findByTestNameContainingIgnoreCase(anyString())).thenReturn(Arrays.asList(testEntry));
        when(connectionTestRepository.findByIsActiveTrueAndTestNumberBetween(anyInt(), anyInt())).thenReturn(Arrays.asList(testEntry));
        when(connectionTestRepository.countByIsActive(true)).thenReturn(1L);
        when(connectionTestRepository.existsById(any(Long.class))).thenReturn(true);
        doNothing().when(connectionTestRepository).deleteById(any(Long.class));

        // Act
        databaseConnectionTestService.runComprehensiveTest();

        // Assert
        verify(connectionTestRepository, atLeastOnce()).save(any(ConnectionTest.class));
        verify(connectionTestRepository, atLeastOnce()).findAll();
        verify(connectionTestRepository, atLeastOnce()).findById(any(Long.class));
        verify(connectionTestRepository, atLeastOnce()).findByTestName(anyString());
        verify(connectionTestRepository, atLeastOnce()).findByIsActive(true);
        verify(connectionTestRepository, atLeastOnce()).findByTestNameContainingIgnoreCase(anyString());
        verify(connectionTestRepository, atLeastOnce()).findByIsActiveTrueAndTestNumberBetween(anyInt(), anyInt());
        verify(connectionTestRepository, atLeastOnce()).countByIsActive(true);
        verify(connectionTestRepository, atLeastOnce()).existsById(any(Long.class));
        verify(connectionTestRepository, atLeastOnce()).deleteById(any(Long.class));
    }
}
>>>>>>> Stashed changes
