package com.apex.firefighter.unit.models;

import com.apex.firefighter.model.AccessRequest;
import com.apex.firefighter.model.AccessSession;
import com.apex.firefighter.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccessSessionTest {

    private AccessSession accessSession;
    private User testUser;
    private AccessRequest testAccessRequest;

    @BeforeEach
    void setUp() {
        accessSession = new AccessSession();
        
        testUser = new User();
        testUser.setUserId("test-user-123");
        testUser.setUsername("testuser");
        
        testAccessRequest = new AccessRequest();
        testAccessRequest.setId(1L);
        testAccessRequest.setTicketId("TICKET-001");
    }

    @Test
    void defaultConstructor_ShouldInitializeWithDefaults() {
        // Assert
        assertThat(accessSession.getId()).isNull();
        assertThat(accessSession.getSessionToken()).isNotNull();
        assertThat(accessSession.getStartTime()).isNull();
        assertThat(accessSession.getEndTime()).isNull();
        assertThat(accessSession.isActive()).isFalse();
        assertThat(accessSession.getAccessRequest()).isNull();
        assertThat(accessSession.getUser()).isNull();
    }

    @Test
    void parameterizedConstructor_ShouldInitializeAllFields() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        boolean active = true;

        // Act
        AccessSession paramAccessSession = new AccessSession(testUser, testAccessRequest, startTime, endTime, active);

        // Assert
        assertThat(paramAccessSession.getId()).isNull();
        assertThat(paramAccessSession.getSessionToken()).isNotNull();
        assertThat(paramAccessSession.getStartTime()).isEqualTo(startTime);
        assertThat(paramAccessSession.getEndTime()).isEqualTo(endTime);
        assertThat(paramAccessSession.isActive()).isEqualTo(active);
        assertThat(paramAccessSession.getAccessRequest()).isEqualTo(testAccessRequest);
        assertThat(paramAccessSession.getUser()).isEqualTo(testUser);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        String sessionToken = "session-token-123";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        boolean active = true;

        // Act
        accessSession.setSessionToken(sessionToken);
        accessSession.setStartTime(startTime);
        accessSession.setEndTime(endTime);
        accessSession.setActive(active);
        accessSession.setAccessRequest(testAccessRequest);
        accessSession.setUser(testUser);

        // Assert
        assertThat(accessSession.getId()).isNull(); // ID is auto-generated, starts as null
        assertThat(accessSession.getSessionToken()).isEqualTo(sessionToken);
        assertThat(accessSession.getStartTime()).isEqualTo(startTime);
        assertThat(accessSession.getEndTime()).isEqualTo(endTime);
        assertThat(accessSession.isActive()).isEqualTo(active);
        assertThat(accessSession.getAccessRequest()).isEqualTo(testAccessRequest);
        assertThat(accessSession.getUser()).isEqualTo(testUser);
    }

    @Test
    void allFields_ShouldAcceptNullValues() {
        // Act
        accessSession.setSessionToken(null);
        accessSession.setStartTime(null);
        accessSession.setEndTime(null);
        accessSession.setAccessRequest(null);
        accessSession.setUser(null);

        // Assert
        assertThat(accessSession.getId()).isNull(); // ID is auto-generated, starts as null
        assertThat(accessSession.getSessionToken()).isNull();
        assertThat(accessSession.getStartTime()).isNull();
        assertThat(accessSession.getEndTime()).isNull();
        assertThat(accessSession.getAccessRequest()).isNull();
        assertThat(accessSession.getUser()).isNull();
    }

    @Test
    void sessionToken_ShouldBeGeneratedOnConstruction() {
        // Act
        AccessSession newAccessSession = new AccessSession();

        // Assert
        assertThat(newAccessSession.getSessionToken()).isNotNull();
        assertThat(newAccessSession.getSessionToken()).isNotEmpty();
    }

    @Test
    void sessionToken_ShouldBeUniqueForEachInstance() {
        // Act
        AccessSession session1 = new AccessSession();
        AccessSession session2 = new AccessSession();

        // Assert
        assertThat(session1.getSessionToken()).isNotEqualTo(session2.getSessionToken());
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        // Act
        AccessSession nullAccessSession = new AccessSession(null, null, null, null, false);

        // Assert
        assertThat(nullAccessSession.getId()).isNull();
        assertThat(nullAccessSession.getSessionToken()).isNotNull();
        assertThat(nullAccessSession.getStartTime()).isNull();
        assertThat(nullAccessSession.getEndTime()).isNull();
        assertThat(nullAccessSession.isActive()).isFalse();
        assertThat(nullAccessSession.getAccessRequest()).isNull();
        assertThat(nullAccessSession.getUser()).isNull();
    }

    @Test
    void active_ShouldBeMutable() {
        // Act & Assert
        accessSession.setActive(true);
        assertThat(accessSession.isActive()).isTrue();

        accessSession.setActive(false);
        assertThat(accessSession.isActive()).isFalse();
    }

    @Test
    void startTime_ShouldBeSettable() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);

        // Act
        accessSession.setStartTime(startTime);

        // Assert
        assertThat(accessSession.getStartTime()).isEqualTo(startTime);
    }

    @Test
    void endTime_ShouldBeSettable() {
        // Arrange
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);

        // Act
        accessSession.setEndTime(endTime);

        // Assert
        assertThat(accessSession.getEndTime()).isEqualTo(endTime);
    }

    @Test
    void sessionToken_ShouldBeSettable() {
        // Arrange
        String sessionToken = "custom-session-token";

        // Act
        accessSession.setSessionToken(sessionToken);

        // Assert
        assertThat(accessSession.getSessionToken()).isEqualTo(sessionToken);
    }

    @Test
    void user_ShouldBeSettable() {
        // Arrange
        User newUser = new User();
        newUser.setUserId("new-user-456");

        // Act
        accessSession.setUser(newUser);

        // Assert
        assertThat(accessSession.getUser()).isEqualTo(newUser);
    }

    @Test
    void accessRequest_ShouldBeSettable() {
        // Arrange
        AccessRequest newAccessRequest = new AccessRequest();
        newAccessRequest.setId(2L);
        newAccessRequest.setTicketId("TICKET-002");

        // Act
        accessSession.setAccessRequest(newAccessRequest);

        // Assert
        assertThat(accessSession.getAccessRequest()).isEqualTo(newAccessRequest);
    }

    @Test
    void id_ShouldBeNullForNewInstances() {
        // Assert
        assertThat(accessSession.getId()).isNull();
    }

    @Test
    void parameterizedConstructor_WithPartialNullValues_ShouldHandleGracefully() {
        // Act
        AccessSession partialNullAccessSession = new AccessSession(testUser, null, LocalDateTime.now(), null, true);

        // Assert
        assertThat(partialNullAccessSession.getUser()).isEqualTo(testUser);
        assertThat(partialNullAccessSession.getAccessRequest()).isNull();
        assertThat(partialNullAccessSession.getStartTime()).isNotNull();
        assertThat(partialNullAccessSession.getEndTime()).isNull();
        assertThat(partialNullAccessSession.isActive()).isTrue();
        assertThat(partialNullAccessSession.getSessionToken()).isNotNull();
    }

    @Test
    void sessionToken_ShouldBeValidUUIDFormat() {
        // Act
        AccessSession newAccessSession = new AccessSession();

        // Assert
        String token = newAccessSession.getSessionToken();
        assertThat(token).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }
}
