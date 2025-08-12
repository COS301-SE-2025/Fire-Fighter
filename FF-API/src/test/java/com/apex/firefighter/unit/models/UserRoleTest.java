package com.apex.firefighter.unit.models;

import com.apex.firefighter.model.Role;
import com.apex.firefighter.model.User;
import com.apex.firefighter.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    private UserRole userRole;
    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        userRole = new UserRole();
        
        testUser = new User();
        testUser.setUserId("test-user-123");
        testUser.setUsername("testuser");
        
        testRole = new Role();
        testRole.setName("ADMIN");
    }

    @Test
    void defaultConstructor_ShouldInitializeWithDefaults() {
        // Assert
        assertThat(userRole.getId()).isNull();
        assertThat(userRole.getUser()).isNull();
        assertThat(userRole.getRole()).isNull();
        assertThat(userRole.getAssignedAt()).isNotNull();
        assertThat(userRole.getAssignedBy()).isNull();
    }

    @Test
    void parameterizedConstructor_ShouldInitializeAllFields() {
        // Arrange
        String assignedBy = "admin-user";

        // Act
        UserRole paramUserRole = new UserRole(testUser, testRole, assignedBy);

        // Assert
        assertThat(paramUserRole.getId()).isNull();
        assertThat(paramUserRole.getUser()).isEqualTo(testUser);
        assertThat(paramUserRole.getRole()).isEqualTo(testRole);
        assertThat(paramUserRole.getAssignedAt()).isNotNull();
        assertThat(paramUserRole.getAssignedBy()).isEqualTo(assignedBy);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;
        String assignedBy = "admin-user";
        ZonedDateTime assignedAt = ZonedDateTime.now();

        // Act
        userRole.setId(id);
        userRole.setUser(testUser);
        userRole.setRole(testRole);
        userRole.setAssignedAt(assignedAt);
        userRole.setAssignedBy(assignedBy);

        // Assert
        assertThat(userRole.getId()).isEqualTo(id);
        assertThat(userRole.getUser()).isEqualTo(testUser);
        assertThat(userRole.getRole()).isEqualTo(testRole);
        assertThat(userRole.getAssignedAt()).isEqualTo(assignedAt);
        assertThat(userRole.getAssignedBy()).isEqualTo(assignedBy);
    }

    @Test
    void allFields_ShouldAcceptNullValues() {
        // Act
        userRole.setId(null);
        userRole.setUser(null);
        userRole.setRole(null);
        userRole.setAssignedAt(null);
        userRole.setAssignedBy(null);

        // Assert
        assertThat(userRole.getId()).isNull();
        assertThat(userRole.getUser()).isNull();
        assertThat(userRole.getRole()).isNull();
        assertThat(userRole.getAssignedAt()).isNull();
        assertThat(userRole.getAssignedBy()).isNull();
    }

    @Test
    void assignedAt_ShouldBeSetOnConstruction() {
        // Arrange
        ZonedDateTime beforeCreation = ZonedDateTime.now().minusSeconds(1);
        
        // Act
        UserRole newUserRole = new UserRole();
        ZonedDateTime afterCreation = ZonedDateTime.now().plusSeconds(1);

        // Assert
        assertThat(newUserRole.getAssignedAt()).isAfter(beforeCreation);
        assertThat(newUserRole.getAssignedAt()).isBefore(afterCreation);
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        // Act
        UserRole nullUserRole = new UserRole(null, null, null);

        // Assert
        assertThat(nullUserRole.getId()).isNull();
        assertThat(nullUserRole.getUser()).isNull();
        assertThat(nullUserRole.getRole()).isNull();
        assertThat(nullUserRole.getAssignedAt()).isNotNull();
        assertThat(nullUserRole.getAssignedBy()).isNull();
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        // Arrange
        userRole.setId(1L);
        userRole.setUser(testUser);
        userRole.setRole(testRole);
        userRole.setAssignedBy("admin-user");

        // Act
        String result = userRole.toString();

        // Assert
        assertThat(result).contains("UserRole{");
        assertThat(result).contains("id=1");
        assertThat(result).contains("user=test-user-123");
        assertThat(result).contains("role=ADMIN");
        assertThat(result).contains("assignedBy='admin-user'");
    }

    @Test
    void toString_WithNullValues_ShouldHandleGracefully() {
        // Act
        String result = userRole.toString();

        // Assert
        assertThat(result).contains("UserRole{");
        assertThat(result).contains("user=null");
        assertThat(result).contains("role=null");
    }

    @Test
    void id_ShouldAcceptVariousValues() {
        // Act & Assert
        userRole.setId(1L);
        assertThat(userRole.getId()).isEqualTo(1L);

        userRole.setId(100L);
        assertThat(userRole.getId()).isEqualTo(100L);

        userRole.setId(0L);
        assertThat(userRole.getId()).isEqualTo(0L);
    }

    @Test
    void assignedBy_ShouldBeSettable() {
        // Arrange
        String assignedBy = "super-admin";

        // Act
        userRole.setAssignedBy(assignedBy);

        // Assert
        assertThat(userRole.getAssignedBy()).isEqualTo(assignedBy);
    }

    @Test
    void assignedAt_ShouldBeSettable() {
        // Arrange
        ZonedDateTime assignedAt = ZonedDateTime.now().minusDays(1);

        // Act
        userRole.setAssignedAt(assignedAt);

        // Assert
        assertThat(userRole.getAssignedAt()).isEqualTo(assignedAt);
    }

    @Test
    void user_ShouldBeSettable() {
        // Arrange
        User newUser = new User();
        newUser.setUserId("new-user-456");

        // Act
        userRole.setUser(newUser);

        // Assert
        assertThat(userRole.getUser()).isEqualTo(newUser);
    }

    @Test
    void role_ShouldBeSettable() {
        // Arrange
        Role newRole = new Role();
        newRole.setName("FIREFIGHTER");

        // Act
        userRole.setRole(newRole);

        // Assert
        assertThat(userRole.getRole()).isEqualTo(newRole);
    }

    @Test
    void parameterizedConstructor_WithPartialNullValues_ShouldHandleGracefully() {
        // Act
        UserRole partialNullUserRole = new UserRole(testUser, null, "admin");

        // Assert
        assertThat(partialNullUserRole.getUser()).isEqualTo(testUser);
        assertThat(partialNullUserRole.getRole()).isNull();
        assertThat(partialNullUserRole.getAssignedBy()).isEqualTo("admin");
        assertThat(partialNullUserRole.getAssignedAt()).isNotNull();
    }
}
