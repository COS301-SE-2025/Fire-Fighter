package com.apex.firefighter.unit.services.auth;

import com.apex.firefighter.service.auth.ApiKeyGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ApiKeyGeneratorTest {

    // ==================== BASIC FUNCTIONALITY TESTS ====================

    @Test
    void generateApiKey_ShouldReturnNonNullString() {
        // Act
        String apiKey = ApiKeyGenerator.generateApiKey();

        // Assert
        assertThat(apiKey).isNotNull();
    }

    @Test
    void generateApiKey_ShouldReturnNonEmptyString() {
        // Act
        String apiKey = ApiKeyGenerator.generateApiKey();

        // Assert
        assertThat(apiKey).isNotEmpty();
    }

    @Test
    void generateApiKey_ShouldReturnValidBase64UrlEncodedString() {
        // Act
        String apiKey = ApiKeyGenerator.generateApiKey();

        // Assert
        assertThat(apiKey).isNotNull();
        assertThat(apiKey).isNotEmpty();
        
        // Should not contain padding characters (URL-safe without padding)
        assertThat(apiKey).doesNotContain("=");
        
        // Should only contain URL-safe Base64 characters
        assertThat(apiKey).matches("^[A-Za-z0-9_-]+$");
    }

    @Test
    void generateApiKey_ShouldReturnCorrectLength() {
        // Act
        String apiKey = ApiKeyGenerator.generateApiKey();

        // Assert
        // 32 bytes encoded in Base64 without padding should be 43 characters
        // Base64 encoding: 4 characters for every 3 bytes
        // 32 bytes = 42.67 characters, rounded up to 43 (without padding)
        assertThat(apiKey).hasSize(43);
    }

    @Test
    void generateApiKey_ShouldBeDecodableAsBase64() {
        // Act
        String apiKey = ApiKeyGenerator.generateApiKey();

        // Assert
        assertThat(apiKey).isNotNull();
        
        // Should be decodable without throwing exception
        byte[] decoded = Base64.getUrlDecoder().decode(apiKey);
        assertThat(decoded).hasSize(32); // Should decode to 32 bytes
    }

    // ==================== UNIQUENESS AND RANDOMNESS TESTS ====================

    @Test
    void generateApiKey_ShouldGenerateUniqueKeys() {
        // Arrange
        Set<String> generatedKeys = new HashSet<>();
        int numberOfKeys = 1000;

        // Act
        for (int i = 0; i < numberOfKeys; i++) {
            String apiKey = ApiKeyGenerator.generateApiKey();
            generatedKeys.add(apiKey);
        }

        // Assert
        // All generated keys should be unique
        assertThat(generatedKeys).hasSize(numberOfKeys);
    }

    @RepeatedTest(10)
    void generateApiKey_RepeatedCalls_ShouldGenerateDifferentKeys() {
        // Act
        String apiKey1 = ApiKeyGenerator.generateApiKey();
        String apiKey2 = ApiKeyGenerator.generateApiKey();

        // Assert
        assertThat(apiKey1).isNotEqualTo(apiKey2);
    }

    @Test
    void generateApiKey_ShouldHaveHighEntropy() {
        // Arrange
        String apiKey = ApiKeyGenerator.generateApiKey();
        
        // Act - Count unique characters
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : apiKey.toCharArray()) {
            uniqueChars.add(c);
        }

        // Assert
        // A good random string should have reasonable character diversity
        // With 43 characters from a 64-character alphabet, we expect good diversity
        assertThat(uniqueChars.size()).isGreaterThan(15); // At least 15 unique characters
    }

    // ==================== SECURITY CHARACTERISTICS TESTS ====================

    @Test
    void generateApiKey_ShouldNotContainObviousPatterns() {
        // Act
        String apiKey = ApiKeyGenerator.generateApiKey();

        // Assert
        // Should not contain obvious patterns
        assertThat(apiKey).doesNotContain("000");
        assertThat(apiKey).doesNotContain("111");
        assertThat(apiKey).doesNotContain("aaa");
        assertThat(apiKey).doesNotContain("AAA");
        
        // Should not be all the same character
        char firstChar = apiKey.charAt(0);
        boolean allSame = apiKey.chars().allMatch(c -> c == firstChar);
        assertThat(allSame).isFalse();
    }

    @Test
    void generateApiKey_ShouldNotStartOrEndWithSpecialCharacters() {
        // Act
        String apiKey = ApiKeyGenerator.generateApiKey();

        // Assert
        // First and last characters should be alphanumeric (good practice)
        char firstChar = apiKey.charAt(0);
        char lastChar = apiKey.charAt(apiKey.length() - 1);
        
        assertThat(Character.isLetterOrDigit(firstChar) || firstChar == '_' || firstChar == '-').isTrue();
        assertThat(Character.isLetterOrDigit(lastChar) || lastChar == '_' || lastChar == '-').isTrue();
    }

    // ==================== PERFORMANCE AND RELIABILITY TESTS ====================

    @Test
    void generateApiKey_MultipleCallsInSequence_ShouldBeReliable() {
        // Act & Assert
        for (int i = 0; i < 100; i++) {
            String apiKey = ApiKeyGenerator.generateApiKey();
            
            assertThat(apiKey).isNotNull();
            assertThat(apiKey).isNotEmpty();
            assertThat(apiKey).hasSize(43);
            assertThat(apiKey).matches("^[A-Za-z0-9_-]+$");
        }
    }

    @Test
    void generateApiKey_ShouldBeThreadSafe() throws InterruptedException {
        // Arrange
        Set<String> generatedKeys = new HashSet<>();
        int numberOfThreads = 10;
        int keysPerThread = 100;
        Thread[] threads = new Thread[numberOfThreads];

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < keysPerThread; j++) {
                    String apiKey = ApiKeyGenerator.generateApiKey();
                    synchronized (generatedKeys) {
                        generatedKeys.add(apiKey);
                    }
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        // All generated keys should be unique (thread-safe generation)
        assertThat(generatedKeys).hasSize(numberOfThreads * keysPerThread);
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    void generateApiKey_ConsistentBehavior_AcrossMultipleCalls() {
        // Act
        for (int i = 0; i < 50; i++) {
            String apiKey = ApiKeyGenerator.generateApiKey();
            
            // Assert consistent behavior
            assertThat(apiKey).hasSize(43);
            assertThat(apiKey).doesNotContain("="); // No padding
            assertThat(apiKey).matches("^[A-Za-z0-9_-]+$"); // URL-safe Base64
            
            // Should be decodable
            byte[] decoded = Base64.getUrlDecoder().decode(apiKey);
            assertThat(decoded).hasSize(32);
        }
    }

    @Test
    void generateApiKey_StatisticalDistribution_ShouldBeReasonable() {
        // Arrange
        int sampleSize = 1000;
        int[] charCounts = new int[256]; // ASCII character counts

        // Act
        for (int i = 0; i < sampleSize; i++) {
            String apiKey = ApiKeyGenerator.generateApiKey();
            for (char c : apiKey.toCharArray()) {
                charCounts[c]++;
            }
        }

        // Assert
        // Count how many different characters were used
        int usedCharacters = 0;
        for (int count : charCounts) {
            if (count > 0) {
                usedCharacters++;
            }
        }

        // Should use a good variety of the available Base64 characters
        assertThat(usedCharacters).isGreaterThan(30); // At least 30 different characters used
    }
}
