package com.apex.firefighter.unit.model;

import com.apex.firefighter.model.ConnectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionTestTest {

    private ConnectionTest connectionTest;
    private final String TEST_NAME = "Test Connection";
    private final String TEST_VALUE = "Test Value";
    private final Integer TEST_NUMBER = 100;
    private final Boolean IS_ACTIVE = true;

    @BeforeEach
    void setUp() {
        connectionTest = new ConnectionTest();
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    void defaultConstructor_ShouldInitializeWithDefaultValues() {
        // Act
        ConnectionTest test = new ConnectionTest();

        // Assert
        assertThat(test.getId()).isNull();
        assertThat(test.getTestName()).isNull();
        assertThat(test.getTestValue()).isNull();
        assertThat(test.getTestNumber()).isEqualTo(0);
        assertThat(test.getIsActive()).isTrue();
        assertThat(test.getCreatedAt()).isNotNull();
        assertThat(test.getUpdatedAt()).isNotNull();
        assertThat(test.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(test.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void parameterizedConstructor_ShouldInitializeWithProvidedValues() {
        // Act
        ConnectionTest test = new ConnectionTest(TEST_NAME, TEST_VALUE, TEST_NUMBER, IS_ACTIVE);

        // Assert
        assertThat(test.getTestName()).isEqualTo(TEST_NAME);
        assertThat(test.getTestValue()).isEqualTo(TEST_VALUE);
        assertThat(test.getTestNumber()).isEqualTo(TEST_NUMBER);
        assertThat(test.getIsActive()).isEqualTo(IS_ACTIVE);
        assertThat(test.getCreatedAt()).isNotNull();
        assertThat(test.getUpdatedAt()).isNotNull();
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldAcceptNullValues() {
        // Act
        ConnectionTest test = new ConnectionTest(null, null, null, null);

        // Assert
        assertThat(test.getTestName()).isNull();
        assertThat(test.getTestValue()).isNull();
        assertThat(test.getTestNumber()).isNull();
        assertThat(test.getIsActive()).isNull();
        assertThat(test.getCreatedAt()).isNotNull();
        assertThat(test.getUpdatedAt()).isNotNull();
    }

    @Test
    void parameterizedConstructor_WithFalseActive_ShouldSetActiveFalse() {
        // Act
        ConnectionTest test = new ConnectionTest(TEST_NAME, TEST_VALUE, TEST_NUMBER, false);

        // Assert
        assertThat(test.getIsActive()).isFalse();
    }

    // ==================== GETTER AND SETTER TESTS ====================

    @Test
    void setId_ShouldSetIdCorrectly() {
        // Arrange
        Long testId = 123L;

        // Act
        connectionTest.setId(testId);

        // Assert
        assertThat(connectionTest.getId()).isEqualTo(testId);
    }

    @Test
    void setId_WithNull_ShouldAcceptNull() {
        // Act
        connectionTest.setId(null);

        // Assert
        assertThat(connectionTest.getId()).isNull();
    }

    @Test
    void setTestName_ShouldSetNameCorrectly() {
        // Act
        connectionTest.setTestName(TEST_NAME);

        // Assert
        assertThat(connectionTest.getTestName()).isEqualTo(TEST_NAME);
    }

    @Test
    void setTestName_WithNull_ShouldAcceptNull() {
        // Act
        connectionTest.setTestName(null);

        // Assert
        assertThat(connectionTest.getTestName()).isNull();
    }

    @Test
    void setTestName_WithEmptyString_ShouldAcceptEmptyString() {
        // Act
        connectionTest.setTestName("");

        // Assert
        assertThat(connectionTest.getTestName()).isEqualTo("");
    }

    @Test
    void setTestValue_ShouldSetValueCorrectly() {
        // Act
        connectionTest.setTestValue(TEST_VALUE);

        // Assert
        assertThat(connectionTest.getTestValue()).isEqualTo(TEST_VALUE);
    }

    @Test
    void setTestValue_WithNull_ShouldAcceptNull() {
        // Act
        connectionTest.setTestValue(null);

        // Assert
        assertThat(connectionTest.getTestValue()).isNull();
    }

    @Test
    void setTestNumber_ShouldSetNumberCorrectly() {
        // Act
        connectionTest.setTestNumber(TEST_NUMBER);

        // Assert
        assertThat(connectionTest.getTestNumber()).isEqualTo(TEST_NUMBER);
    }

    @Test
    void setTestNumber_WithZero_ShouldAcceptZero() {
        // Act
        connectionTest.setTestNumber(0);

        // Assert
        assertThat(connectionTest.getTestNumber()).isEqualTo(0);
    }

    @Test
    void setTestNumber_WithNegative_ShouldAcceptNegative() {
        // Act
        connectionTest.setTestNumber(-100);

        // Assert
        assertThat(connectionTest.getTestNumber()).isEqualTo(-100);
    }

    @Test
    void setTestNumber_WithNull_ShouldAcceptNull() {
        // Act
        connectionTest.setTestNumber(null);

        // Assert
        assertThat(connectionTest.getTestNumber()).isNull();
    }

    @Test
    void setIsActive_ShouldSetActiveCorrectly() {
        // Act
        connectionTest.setIsActive(IS_ACTIVE);

        // Assert
        assertThat(connectionTest.getIsActive()).isEqualTo(IS_ACTIVE);
    }

    @Test
    void setIsActive_WithFalse_ShouldSetFalse() {
        // Act
        connectionTest.setIsActive(false);

        // Assert
        assertThat(connectionTest.getIsActive()).isFalse();
    }

    @Test
    void setIsActive_WithNull_ShouldAcceptNull() {
        // Act
        connectionTest.setIsActive(null);

        // Assert
        assertThat(connectionTest.getIsActive()).isNull();
    }

    @Test
    void setCreatedAt_ShouldSetCreatedAtCorrectly() {
        // Arrange
        LocalDateTime testTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

        // Act
        connectionTest.setCreatedAt(testTime);

        // Assert
        assertThat(connectionTest.getCreatedAt()).isEqualTo(testTime);
    }

    @Test
    void setCreatedAt_WithNull_ShouldAcceptNull() {
        // Act
        connectionTest.setCreatedAt(null);

        // Assert
        assertThat(connectionTest.getCreatedAt()).isNull();
    }

    @Test
    void setUpdatedAt_ShouldSetUpdatedAtCorrectly() {
        // Arrange
        LocalDateTime testTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

        // Act
        connectionTest.setUpdatedAt(testTime);

        // Assert
        assertThat(connectionTest.getUpdatedAt()).isEqualTo(testTime);
    }

    @Test
    void setUpdatedAt_WithNull_ShouldAcceptNull() {
        // Act
        connectionTest.setUpdatedAt(null);

        // Assert
        assertThat(connectionTest.getUpdatedAt()).isNull();
    }

    // ==================== PRE-UPDATE CALLBACK TESTS ====================

    @Test
    void preUpdate_ShouldUpdateTimestamp() {
        // Arrange
        LocalDateTime originalTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
        connectionTest.setUpdatedAt(originalTime);

        // Act
        connectionTest.preUpdate();

        // Assert
        assertThat(connectionTest.getUpdatedAt()).isAfter(originalTime);
        assertThat(connectionTest.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void preUpdate_CalledMultipleTimes_ShouldUpdateEachTime() throws InterruptedException {
        // Arrange
        connectionTest.preUpdate();
        LocalDateTime firstUpdate = connectionTest.getUpdatedAt();
        
        // Small delay to ensure different timestamps
        Thread.sleep(1);

        // Act
        connectionTest.preUpdate();
        LocalDateTime secondUpdate = connectionTest.getUpdatedAt();

        // Assert
        assertThat(secondUpdate).isAfter(firstUpdate);
    }

    // ==================== TOSTRING TESTS ====================

    @Test
    void toString_WithAllFieldsSet_ShouldContainAllFields() {
        // Arrange
        connectionTest.setId(1L);
        connectionTest.setTestName(TEST_NAME);
        connectionTest.setTestValue(TEST_VALUE);
        connectionTest.setTestNumber(TEST_NUMBER);
        connectionTest.setIsActive(IS_ACTIVE);
        LocalDateTime now = LocalDateTime.now();
        connectionTest.setCreatedAt(now);
        connectionTest.setUpdatedAt(now);

        // Act
        String result = connectionTest.toString();

        // Assert
        assertThat(result).contains("ConnectionTest{");
        assertThat(result).contains("id=1");
        assertThat(result).contains("testName='" + TEST_NAME + "'");
        assertThat(result).contains("testValue='" + TEST_VALUE + "'");
        assertThat(result).contains("testNumber=" + TEST_NUMBER);
        assertThat(result).contains("isActive=" + IS_ACTIVE);
        assertThat(result).contains("createdAt=" + now);
        assertThat(result).contains("updatedAt=" + now);
    }

    @Test
    void toString_WithNullFields_ShouldHandleNullsGracefully() {
        // Arrange
        connectionTest.setId(null);
        connectionTest.setTestName(null);
        connectionTest.setTestValue(null);
        connectionTest.setTestNumber(null);
        connectionTest.setIsActive(null);
        connectionTest.setCreatedAt(null);
        connectionTest.setUpdatedAt(null);

        // Act
        String result = connectionTest.toString();

        // Assert
        assertThat(result).contains("ConnectionTest{");
        assertThat(result).contains("id=null");
        assertThat(result).contains("testName='null'");
        assertThat(result).contains("testValue='null'");
        assertThat(result).contains("testNumber=null");
        assertThat(result).contains("isActive=null");
        assertThat(result).contains("createdAt=null");
        assertThat(result).contains("updatedAt=null");
    }

    @Test
    void toString_WithEmptyStrings_ShouldHandleEmptyStringsCorrectly() {
        // Arrange
        connectionTest.setTestName("");
        connectionTest.setTestValue("");

        // Act
        String result = connectionTest.toString();

        // Assert
        assertThat(result).contains("testName=''");
        assertThat(result).contains("testValue=''");
    }

    // ==================== OBJECT BEHAVIOR TESTS ====================

    @Test
    void defaultValues_ShouldBeSetCorrectly() {
        // Act
        ConnectionTest test = new ConnectionTest();

        // Assert
        assertThat(test.getTestNumber()).isEqualTo(0); // Default value from field initialization
        assertThat(test.getIsActive()).isTrue(); // Default value from field initialization
    }

    @Test
    void timestampInitialization_ShouldSetCurrentTime() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();

        // Act
        ConnectionTest test = new ConnectionTest();

        // Assert
        LocalDateTime after = LocalDateTime.now();
        assertThat(test.getCreatedAt()).isBetween(before, after);
        assertThat(test.getUpdatedAt()).isBetween(before, after);
    }

    @Test
    void parameterizedConstructor_ShouldCallDefaultConstructor() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();

        // Act
        ConnectionTest test = new ConnectionTest(TEST_NAME, TEST_VALUE, TEST_NUMBER, IS_ACTIVE);

        // Assert
        LocalDateTime after = LocalDateTime.now();
        // Should have timestamps set by default constructor
        assertThat(test.getCreatedAt()).isBetween(before, after);
        assertThat(test.getUpdatedAt()).isBetween(before, after);
        // Should have provided values
        assertThat(test.getTestName()).isEqualTo(TEST_NAME);
        assertThat(test.getTestValue()).isEqualTo(TEST_VALUE);
        assertThat(test.getTestNumber()).isEqualTo(TEST_NUMBER);
        assertThat(test.getIsActive()).isEqualTo(IS_ACTIVE);
    }

    // ==================== EDGE CASES AND BOUNDARY TESTS ====================

    @Test
    void setTestNumber_WithMaxValue_ShouldAcceptMaxValue() {
        // Act
        connectionTest.setTestNumber(Integer.MAX_VALUE);

        // Assert
        assertThat(connectionTest.getTestNumber()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void setTestNumber_WithMinValue_ShouldAcceptMinValue() {
        // Act
        connectionTest.setTestNumber(Integer.MIN_VALUE);

        // Assert
        assertThat(connectionTest.getTestNumber()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void setTestName_WithLongString_ShouldAcceptLongString() {
        // Arrange
        String longName = "A".repeat(1000);

        // Act
        connectionTest.setTestName(longName);

        // Assert
        assertThat(connectionTest.getTestName()).isEqualTo(longName);
        assertThat(connectionTest.getTestName()).hasSize(1000);
    }

    @Test
    void setTestValue_WithLongString_ShouldAcceptLongString() {
        // Arrange
        String longValue = "B".repeat(1000);

        // Act
        connectionTest.setTestValue(longValue);

        // Assert
        assertThat(connectionTest.getTestValue()).isEqualTo(longValue);
        assertThat(connectionTest.getTestValue()).hasSize(1000);
    }

    @Test
    void setTestName_WithSpecialCharacters_ShouldAcceptSpecialCharacters() {
        // Arrange
        String specialName = "Test!@#$%^&*()_+-={}[]|\\:;\"'<>?,./";

        // Act
        connectionTest.setTestName(specialName);

        // Assert
        assertThat(connectionTest.getTestName()).isEqualTo(specialName);
    }

    @Test
    void setTestValue_WithSpecialCharacters_ShouldAcceptSpecialCharacters() {
        // Arrange
        String specialValue = "Value!@#$%^&*()_+-={}[]|\\:;\"'<>?,./";

        // Act
        connectionTest.setTestValue(specialValue);

        // Assert
        assertThat(connectionTest.getTestValue()).isEqualTo(specialValue);
    }

    @Test
    void setTestName_WithUnicodeCharacters_ShouldAcceptUnicodeCharacters() {
        // Arrange
        String unicodeName = "Test测试тест🔥";

        // Act
        connectionTest.setTestName(unicodeName);

        // Assert
        assertThat(connectionTest.getTestName()).isEqualTo(unicodeName);
    }

    @Test
    void setTestValue_WithUnicodeCharacters_ShouldAcceptUnicodeCharacters() {
        // Arrange
        String unicodeValue = "Value测试тест🔥";

        // Act
        connectionTest.setTestValue(unicodeValue);

        // Assert
        assertThat(connectionTest.getTestValue()).isEqualTo(unicodeValue);
    }

    // ==================== IMMUTABILITY AND STATE TESTS ====================

    @Test
    void multipleSettersCall_ShouldMaintainLatestValues() {
        // Act
        connectionTest.setTestName("First Name");
        connectionTest.setTestName("Second Name");
        connectionTest.setTestName(TEST_NAME);

        connectionTest.setTestNumber(50);
        connectionTest.setTestNumber(75);
        connectionTest.setTestNumber(TEST_NUMBER);

        // Assert
        assertThat(connectionTest.getTestName()).isEqualTo(TEST_NAME);
        assertThat(connectionTest.getTestNumber()).isEqualTo(TEST_NUMBER);
    }

    @Test
    void settersAndGetters_ShouldBeConsistent() {
        // Act & Assert
        connectionTest.setId(123L);
        assertThat(connectionTest.getId()).isEqualTo(123L);

        connectionTest.setTestName(TEST_NAME);
        assertThat(connectionTest.getTestName()).isEqualTo(TEST_NAME);

        connectionTest.setTestValue(TEST_VALUE);
        assertThat(connectionTest.getTestValue()).isEqualTo(TEST_VALUE);

        connectionTest.setTestNumber(TEST_NUMBER);
        assertThat(connectionTest.getTestNumber()).isEqualTo(TEST_NUMBER);

        connectionTest.setIsActive(IS_ACTIVE);
        assertThat(connectionTest.getIsActive()).isEqualTo(IS_ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        connectionTest.setCreatedAt(now);
        assertThat(connectionTest.getCreatedAt()).isEqualTo(now);

        connectionTest.setUpdatedAt(now);
        assertThat(connectionTest.getUpdatedAt()).isEqualTo(now);
    }
}
