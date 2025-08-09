package com.apex.firefighter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void defaultConstructor_ShouldInitializeWithDefaults() {
        // Assert
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getIsAuthorized()).isFalse();
        assertThat(user.getIsAdmin()).isFalse();
    }

    @Test
    void parameterizedConstructor_ShouldInitializeAllFields() {
        // Arrange
        String userId = "firebase-uid-123";
        String username = "testuser";
        String email = "test@example.com";
        String department = "IT";

        // Act
        User paramUser = new User(userId, username, email, department);

        // Assert
        assertThat(paramUser.getUserId()).isEqualTo(userId);
        assertThat(paramUser.getUsername()).isEqualTo(username);
        assertThat(paramUser.getEmail()).isEqualTo(email);
        assertThat(paramUser.getDepartment()).isEqualTo(department);
        assertThat(paramUser.getCreatedAt()).isNotNull();
        assertThat(paramUser.getIsAuthorized()).isFalse();
        assertThat(paramUser.getIsAdmin()).isFalse();
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        String userId = "firebase-uid-123";
        String username = "testuser";
        String email = "test@example.com";
        String department = "IT";
        String role = "ADMIN";
        String contactNumber = "123-456-7890";
        ZonedDateTime lastLogin = ZonedDateTime.now();

        // Act
        user.setUserId(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setDepartment(department);
        user.setRole(role);
        user.setContactNumber(contactNumber);
        user.setLastLogin(lastLogin);
        user.setIsAuthorized(true);
        user.setIsAdmin(true);

        // Assert
        assertThat(user.getUserId()).isEqualTo(userId);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getDepartment()).isEqualTo(department);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getContactNumber()).isEqualTo(contactNumber);
        assertThat(user.getLastLogin()).isEqualTo(lastLogin);
        assertThat(user.getIsAuthorized()).isTrue();
        assertThat(user.getIsAdmin()).isTrue();
    }

    @Test
    void setCreatedAt_ShouldUpdateCreatedAt() {
        // Arrange
        ZonedDateTime newCreatedAt = ZonedDateTime.now().minusDays(1);

        // Act
        user.setCreatedAt(newCreatedAt);

        // Assert
        assertThat(user.getCreatedAt()).isEqualTo(newCreatedAt);
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        // Arrange
        user.setUserId("test-uid");
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // Act
        String result = user.toString();

        // Assert
        assertThat(result).contains("User{");
        assertThat(result).contains("userId='test-uid'");
        assertThat(result).contains("username='testuser'");
        assertThat(result).contains("email='test@example.com'");
    }

    @Test
    void equals_WithSameUserId_ShouldReturnTrue() {
        // Arrange
        User user1 = new User();
        user1.setUserId("same-id");
        
        User user2 = new User();
        user2.setUserId("same-id");

        // Act & Assert
        assertThat(user1.equals(user2)).isTrue();
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void equals_WithDifferentUserId_ShouldReturnFalse() {
        // Arrange
        User user1 = new User();
        user1.setUserId("id-1");
        
        User user2 = new User();
        user2.setUserId("id-2");

        // Act & Assert
        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    void equals_WithNullUserId_ShouldHandleCorrectly() {
        // Arrange
        User user1 = new User();
        user1.setUserId(null);
        
        User user2 = new User();
        user2.setUserId(null);

        // Act & Assert
        assertThat(user1.equals(user2)).isTrue();
    }

    @Test
    void equals_WithNull_ShouldReturnFalse() {
        // Act & Assert
        assertThat(user.equals(null)).isFalse();
    }

    @Test
    void equals_WithDifferentClass_ShouldReturnFalse() {
        // Act & Assert
        assertThat(user.equals("not a user")).isFalse();
    }

    @Test
    void equals_WithSameInstance_ShouldReturnTrue() {
        // Act & Assert
        assertThat(user.equals(user)).isTrue();
    }

    @Test
    void hashCode_WithSameUserId_ShouldBeEqual() {
        // Arrange
        User user1 = new User();
        user1.setUserId("same-id");
        
        User user2 = new User();
        user2.setUserId("same-id");

        // Act & Assert
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void hashCode_WithNullUserId_ShouldNotThrowException() {
        // Arrange
        user.setUserId(null);

        // Act & Assert
        assertThat(user.hashCode()).isNotNull();
    }

    @Test
    void booleanFields_ShouldHandleNullValues() {
        // Act
        user.setIsAuthorized(null);
        user.setIsAdmin(null);

        // Assert
        assertThat(user.getIsAuthorized()).isNull();
        assertThat(user.getIsAdmin()).isNull();
    }

    @Test
    void allFields_ShouldAcceptNullValues() {
        // Act
        user.setUserId(null);
        user.setUsername(null);
        user.setEmail(null);
        user.setDepartment(null);
        user.setRole(null);
        user.setContactNumber(null);
        user.setLastLogin(null);
        user.setCreatedAt(null);

        // Assert
        assertThat(user.getUserId()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getDepartment()).isNull();
        assertThat(user.getRole()).isNull();
        assertThat(user.getContactNumber()).isNull();
        assertThat(user.getLastLogin()).isNull();
        assertThat(user.getCreatedAt()).isNull();
    }

    @Test
    void createdAt_ShouldBeSetOnConstruction() {
        // Arrange
        ZonedDateTime beforeCreation = ZonedDateTime.now().minusSeconds(1);
        
        // Act
        User newUser = new User();
        ZonedDateTime afterCreation = ZonedDateTime.now().plusSeconds(1);

        // Assert
        assertThat(newUser.getCreatedAt()).isAfter(beforeCreation);
        assertThat(newUser.getCreatedAt()).isBefore(afterCreation);
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        // Act
        User nullUser = new User(null, null, null, null);

        // Assert
        assertThat(nullUser.getUserId()).isNull();
        assertThat(nullUser.getUsername()).isNull();
        assertThat(nullUser.getEmail()).isNull();
        assertThat(nullUser.getDepartment()).isNull();
        assertThat(nullUser.getCreatedAt()).isNotNull();
        assertThat(nullUser.getIsAuthorized()).isFalse();
        assertThat(nullUser.getIsAdmin()).isFalse();
    }
}
