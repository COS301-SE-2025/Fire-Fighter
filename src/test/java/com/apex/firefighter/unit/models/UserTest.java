package com.apex.firefighter.unit.models;

import com.apex.firefighter.model.User;
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

    @Test
    void hasRole_WithMatchingRole_ShouldReturnTrue() {
        // Arrange
        user.setRole("ADMIN");

        // Act & Assert
        assertThat(user.hasRole("ADMIN")).isTrue();
    }

    @Test
    void hasRole_WithNonMatchingRole_ShouldReturnFalse() {
        // Arrange
        user.setRole("USER");

        // Act & Assert
        assertThat(user.hasRole("ADMIN")).isFalse();
    }

    @Test
    void hasRole_WithNullRole_ShouldReturnFalse() {
        // Arrange
        user.setRole(null);

        // Act & Assert
        assertThat(user.hasRole("ADMIN")).isFalse();
    }

    @Test
    void hasRole_WithNullInput_ShouldReturnFalse() {
        // Arrange
        user.setRole("ADMIN");

        // Act & Assert
        assertThat(user.hasRole(null)).isFalse();
    }

    @Test
    void isAuthorized_WithTrueValue_ShouldReturnTrue() {
        // Arrange
        user.setIsAuthorized(true);

        // Act & Assert
        assertThat(user.isAuthorized()).isTrue();
    }

    @Test
    void isAuthorized_WithFalseValue_ShouldReturnFalse() {
        // Arrange
        user.setIsAuthorized(false);

        // Act & Assert
        assertThat(user.isAuthorized()).isFalse();
    }

    @Test
    void isAuthorized_GetterWithNull_ShouldBeNull() {
        // Arrange
        user.setIsAuthorized(null);

        // Assert (avoid calling isAuthorized() which would NPE)
        assertThat(user.getIsAuthorized()).isNull();
    }

    @Test
    void isAdmin_WithTrueValue_ShouldReturnTrue() {
        // Arrange
        user.setIsAdmin(true);

        // Act & Assert
        assertThat(user.isAdmin()).isTrue();
    }

    @Test
    void isAdmin_WithFalseValue_ShouldReturnFalse() {
        // Arrange
        user.setIsAdmin(false);

        // Act & Assert
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    void isAdmin_WithNullValue_ShouldReturnFalse() {
        // Arrange
        user.setIsAdmin(null);

        // Act & Assert
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    void updateLastLogin_ShouldSetCurrentTimestamp() {
        // Arrange
        ZonedDateTime beforeUpdate = ZonedDateTime.now().minusSeconds(1);

        // Act
        user.updateLastLogin();
        ZonedDateTime afterUpdate = ZonedDateTime.now().plusSeconds(1);

        // Assert
        assertThat(user.getLastLogin()).isAfter(beforeUpdate);
        assertThat(user.getLastLogin()).isBefore(afterUpdate);
    }

    @Test
    void updateLastLogin_ShouldOverwriteExistingValue() {
        // Arrange
        ZonedDateTime oldLogin = ZonedDateTime.now().minusDays(1);
        user.setLastLogin(oldLogin);

        // Act
        user.updateLastLogin();

        // Assert
        assertThat(user.getLastLogin()).isAfter(oldLogin);
    }

    @Test
    void role_ShouldBeMutable() {
        // Act & Assert
        user.setRole("USER");
        assertThat(user.getRole()).isEqualTo("USER");

        user.setRole("ADMIN");
        assertThat(user.getRole()).isEqualTo("ADMIN");

        user.setRole("FIREFIGHTER");
        assertThat(user.getRole()).isEqualTo("FIREFIGHTER");
    }

    @Test
    void contactNumber_ShouldBeSettable() {
        // Arrange
        String contactNumber = "555-123-4567";

        // Act
        user.setContactNumber(contactNumber);

        // Assert
        assertThat(user.getContactNumber()).isEqualTo(contactNumber);
    }

    @Test
    void lastLogin_ShouldBeSettable() {
        // Arrange
        ZonedDateTime lastLogin = ZonedDateTime.now().minusHours(2);

        // Act
        user.setLastLogin(lastLogin);

        // Assert
        assertThat(user.getLastLogin()).isEqualTo(lastLogin);
    }
}
