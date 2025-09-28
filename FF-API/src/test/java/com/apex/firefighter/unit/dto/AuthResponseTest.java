package com.apex.firefighter.unit.dto;

import com.apex.firefighter.dto.AuthResponse;
import com.apex.firefighter.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResponseTest {

    private AuthResponse authResponse;
    private User testUser;
    private final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("test-user-123");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDepartment("IT");
        testUser.setIsAuthorized(true);

        authResponse = new AuthResponse();
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    void defaultConstructor_ShouldCreateEmptyAuthResponse() {
        // Act
        AuthResponse response = new AuthResponse();

        // Assert
        assertThat(response.getToken()).isNull();
        assertThat(response.getUser()).isNull();
    }

    @Test
    void parameterizedConstructor_ShouldCreateAuthResponseWithValues() {
        // Act
        AuthResponse response = new AuthResponse(TEST_TOKEN, testUser);

        // Assert
        assertThat(response.getToken()).isEqualTo(TEST_TOKEN);
        assertThat(response.getUser()).isEqualTo(testUser);
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldAcceptNullValues() {
        // Act
        AuthResponse response = new AuthResponse(null, null);

        // Assert
        assertThat(response.getToken()).isNull();
        assertThat(response.getUser()).isNull();
    }

    @Test
    void parameterizedConstructor_WithPartialNullValues_ShouldAcceptPartialNulls() {
        // Act
        AuthResponse responseWithNullToken = new AuthResponse(null, testUser);
        AuthResponse responseWithNullUser = new AuthResponse(TEST_TOKEN, null);

        // Assert
        assertThat(responseWithNullToken.getToken()).isNull();
        assertThat(responseWithNullToken.getUser()).isEqualTo(testUser);

        assertThat(responseWithNullUser.getToken()).isEqualTo(TEST_TOKEN);
        assertThat(responseWithNullUser.getUser()).isNull();
    }

    // ==================== GETTER AND SETTER TESTS ====================

    @Test
    void setToken_ShouldSetTokenCorrectly() {
        // Act
        authResponse.setToken(TEST_TOKEN);

        // Assert
        assertThat(authResponse.getToken()).isEqualTo(TEST_TOKEN);
    }

    @Test
    void setToken_WithNull_ShouldAcceptNull() {
        // Arrange
        authResponse.setToken(TEST_TOKEN);

        // Act
        authResponse.setToken(null);

        // Assert
        assertThat(authResponse.getToken()).isNull();
    }

    @Test
    void setToken_WithEmptyString_ShouldAcceptEmptyString() {
        // Act
        authResponse.setToken("");

        // Assert
        assertThat(authResponse.getToken()).isEqualTo("");
    }

    @Test
    void setToken_WithLongToken_ShouldAcceptLongToken() {
        // Arrange
        String longToken = "a".repeat(1000);

        // Act
        authResponse.setToken(longToken);

        // Assert
        assertThat(authResponse.getToken()).isEqualTo(longToken);
        assertThat(authResponse.getToken()).hasSize(1000);
    }

    @Test
    void setUser_ShouldSetUserCorrectly() {
        // Act
        authResponse.setUser(testUser);

        // Assert
        assertThat(authResponse.getUser()).isEqualTo(testUser);
    }

    @Test
    void setUser_WithNull_ShouldAcceptNull() {
        // Arrange
        authResponse.setUser(testUser);

        // Act
        authResponse.setUser(null);

        // Assert
        assertThat(authResponse.getUser()).isNull();
    }

    @Test
    void setUser_WithDifferentUser_ShouldUpdateUser() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setUserId("another-user-456");
        anotherUser.setUsername("anotheruser");

        authResponse.setUser(testUser);

        // Act
        authResponse.setUser(anotherUser);

        // Assert
        assertThat(authResponse.getUser()).isEqualTo(anotherUser);
        assertThat(authResponse.getUser()).isNotEqualTo(testUser);
    }

    // ==================== GETTER CONSISTENCY TESTS ====================

    @Test
    void gettersAndSetters_ShouldBeConsistent() {
        // Act & Assert
        authResponse.setToken(TEST_TOKEN);
        assertThat(authResponse.getToken()).isEqualTo(TEST_TOKEN);

        authResponse.setUser(testUser);
        assertThat(authResponse.getUser()).isEqualTo(testUser);
    }

    @Test
    void multipleSettersCall_ShouldMaintainLatestValues() {
        // Arrange
        String firstToken = "first.token.value";
        String secondToken = "second.token.value";
        
        User firstUser = new User();
        firstUser.setUserId("first-user");
        
        User secondUser = new User();
        secondUser.setUserId("second-user");

        // Act
        authResponse.setToken(firstToken);
        authResponse.setUser(firstUser);
        
        authResponse.setToken(secondToken);
        authResponse.setUser(secondUser);

        // Assert
        assertThat(authResponse.getToken()).isEqualTo(secondToken);
        assertThat(authResponse.getUser()).isEqualTo(secondUser);
        assertThat(authResponse.getToken()).isNotEqualTo(firstToken);
        assertThat(authResponse.getUser()).isNotEqualTo(firstUser);
    }

    // ==================== OBJECT BEHAVIOR TESTS ====================

    @Test
    void authResponse_ShouldMaintainObjectIdentity() {
        // Arrange
        authResponse.setToken(TEST_TOKEN);
        authResponse.setUser(testUser);

        // Act
        AuthResponse sameReference = authResponse;

        // Assert
        assertThat(sameReference).isSameAs(authResponse);
        assertThat(sameReference.getToken()).isEqualTo(authResponse.getToken());
        assertThat(sameReference.getUser()).isEqualTo(authResponse.getUser());
    }

    @Test
    void authResponse_ShouldAllowIndependentInstances() {
        // Arrange
        AuthResponse response1 = new AuthResponse(TEST_TOKEN, testUser);
        AuthResponse response2 = new AuthResponse(TEST_TOKEN, testUser);

        // Act
        response1.setToken("modified.token");

        // Assert
        assertThat(response1.getToken()).isEqualTo("modified.token");
        assertThat(response2.getToken()).isEqualTo(TEST_TOKEN);
        assertThat(response1).isNotSameAs(response2);
    }

    // ==================== EDGE CASES AND SPECIAL VALUES ====================

    @Test
    void setToken_WithSpecialCharacters_ShouldAcceptSpecialCharacters() {
        // Arrange
        String specialToken = "token!@#$%^&*()_+-={}[]|\\:;\"'<>?,./";

        // Act
        authResponse.setToken(specialToken);

        // Assert
        assertThat(authResponse.getToken()).isEqualTo(specialToken);
    }

    @Test
    void setToken_WithUnicodeCharacters_ShouldAcceptUnicodeCharacters() {
        // Arrange
        String unicodeToken = "token测试тест🔥";

        // Act
        authResponse.setToken(unicodeToken);

        // Assert
        assertThat(authResponse.getToken()).isEqualTo(unicodeToken);
    }

    @Test
    void setToken_WithWhitespace_ShouldPreserveWhitespace() {
        // Arrange
        String tokenWithSpaces = "  token with spaces  ";

        // Act
        authResponse.setToken(tokenWithSpaces);

        // Assert
        assertThat(authResponse.getToken()).isEqualTo(tokenWithSpaces);
    }

    @Test
    void setToken_WithNewlines_ShouldPreserveNewlines() {
        // Arrange
        String tokenWithNewlines = "token\nwith\nnewlines";

        // Act
        authResponse.setToken(tokenWithNewlines);

        // Assert
        assertThat(authResponse.getToken()).isEqualTo(tokenWithNewlines);
    }

    // ==================== REALISTIC JWT TOKEN TESTS ====================

    @Test
    void setToken_WithRealisticJWTToken_ShouldHandleCorrectly() {
        // Arrange
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                         "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
                         "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        // Act
        authResponse.setToken(jwtToken);

        // Assert
        assertThat(authResponse.getToken()).isEqualTo(jwtToken);
        assertThat(authResponse.getToken()).contains(".");
        assertThat(authResponse.getToken().split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    void setToken_WithMalformedJWTToken_ShouldStillAccept() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token";

        // Act
        authResponse.setToken(malformedToken);

        // Assert
        assertThat(authResponse.getToken()).isEqualTo(malformedToken);
    }

    // ==================== USER OBJECT INTERACTION TESTS ====================

    @Test
    void setUser_WithCompleteUserObject_ShouldPreserveAllUserData() {
        // Arrange
        User completeUser = new User();
        completeUser.setUserId("complete-user-789");
        completeUser.setUsername("completeuser");
        completeUser.setEmail("complete@example.com");
        completeUser.setDepartment("Engineering");
        completeUser.setContactNumber("123-456-7890");
        completeUser.setIsAuthorized(true);
        completeUser.setIsAdmin(false);

        // Act
        authResponse.setUser(completeUser);

        // Assert
        User retrievedUser = authResponse.getUser();
        assertThat(retrievedUser).isEqualTo(completeUser);
        assertThat(retrievedUser.getUserId()).isEqualTo("complete-user-789");
        assertThat(retrievedUser.getUsername()).isEqualTo("completeuser");
        assertThat(retrievedUser.getEmail()).isEqualTo("complete@example.com");
        assertThat(retrievedUser.getDepartment()).isEqualTo("Engineering");
        assertThat(retrievedUser.getContactNumber()).isEqualTo("123-456-7890");
        assertThat(retrievedUser.getIsAuthorized()).isTrue();
        assertThat(retrievedUser.isAdmin()).isFalse();
    }

    @Test
    void setUser_WithMinimalUserObject_ShouldAcceptMinimalUser() {
        // Arrange
        User minimalUser = new User();
        minimalUser.setUserId("minimal-user");

        // Act
        authResponse.setUser(minimalUser);

        // Assert
        User retrievedUser = authResponse.getUser();
        assertThat(retrievedUser).isEqualTo(minimalUser);
        assertThat(retrievedUser.getUserId()).isEqualTo("minimal-user");
        assertThat(retrievedUser.getUsername()).isNull();
        assertThat(retrievedUser.getEmail()).isNull();
    }

    // ==================== INTEGRATION AND WORKFLOW TESTS ====================

    @Test
    void authResponse_CompleteWorkflow_ShouldWorkCorrectly() {
        // Arrange
        String initialToken = "initial.token";
        String updatedToken = "updated.token";
        
        User initialUser = new User();
        initialUser.setUserId("initial-user");
        
        User updatedUser = new User();
        updatedUser.setUserId("updated-user");

        // Act - Complete workflow
        AuthResponse response = new AuthResponse();
        
        // Set initial values
        response.setToken(initialToken);
        response.setUser(initialUser);
        
        // Verify initial state
        assertThat(response.getToken()).isEqualTo(initialToken);
        assertThat(response.getUser()).isEqualTo(initialUser);
        
        // Update values
        response.setToken(updatedToken);
        response.setUser(updatedUser);
        
        // Verify updated state
        assertThat(response.getToken()).isEqualTo(updatedToken);
        assertThat(response.getUser()).isEqualTo(updatedUser);
        
        // Verify old values are not retained
        assertThat(response.getToken()).isNotEqualTo(initialToken);
        assertThat(response.getUser()).isNotEqualTo(initialUser);
    }

    @Test
    void authResponse_ConstructorVsSetters_ShouldProduceSameResult() {
        // Arrange & Act
        AuthResponse constructorResponse = new AuthResponse(TEST_TOKEN, testUser);
        
        AuthResponse setterResponse = new AuthResponse();
        setterResponse.setToken(TEST_TOKEN);
        setterResponse.setUser(testUser);

        // Assert
        assertThat(constructorResponse.getToken()).isEqualTo(setterResponse.getToken());
        assertThat(constructorResponse.getUser()).isEqualTo(setterResponse.getUser());
    }

    @Test
    void authResponse_MultipleInstances_ShouldBeIndependent() {
        // Arrange
        AuthResponse response1 = new AuthResponse(TEST_TOKEN, testUser);
        AuthResponse response2 = new AuthResponse("different.token", testUser);
        AuthResponse response3 = new AuthResponse();

        // Act
        response3.setToken(TEST_TOKEN);
        response3.setUser(testUser);

        // Assert
        assertThat(response1.getToken()).isEqualTo(response3.getToken());
        assertThat(response1.getUser()).isEqualTo(response3.getUser());
        
        assertThat(response1.getToken()).isNotEqualTo(response2.getToken());
        assertThat(response1.getUser()).isEqualTo(response2.getUser()); // Same user reference
        
        // Verify independence
        response1.setToken("modified.token");
        assertThat(response2.getToken()).isEqualTo("different.token");
        assertThat(response3.getToken()).isEqualTo(TEST_TOKEN);
    }
}
