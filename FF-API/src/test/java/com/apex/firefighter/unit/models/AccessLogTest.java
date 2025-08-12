package com.apex.firefighter.unit.models;

import com.apex.firefighter.model.AccessLog;
import com.apex.firefighter.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccessLogTest {

    private AccessLog accessLog;
    private User testUser;

    @BeforeEach
    void setUp() {
        accessLog = new AccessLog();
        
        testUser = new User();
        testUser.setUserId("test-user-123");
        testUser.setUsername("testuser");
    }

    @Test
    void defaultConstructor_ShouldInitializeWithDefaults() {
        // Assert
        assertThat(accessLog.getId()).isNull();
        assertThat(accessLog.getAction()).isNull();
        assertThat(accessLog.getTimestamp()).isNull();
        assertThat(accessLog.getTicketId()).isNull();
        assertThat(accessLog.getSessionId()).isNull();
        assertThat(accessLog.getUser()).isNull();
    }

    @Test
    void parameterizedConstructor_ShouldInitializeAllFields() {
        // Arrange
        String action = "Granted Access";
        String ticketId = "TICKET-001";
        LocalDateTime timestamp = LocalDateTime.now();

        // Act
        AccessLog paramAccessLog = new AccessLog(testUser, action, ticketId, timestamp);

        // Assert
        assertThat(paramAccessLog.getId()).isNull();
        assertThat(paramAccessLog.getUser()).isEqualTo(testUser);
        assertThat(paramAccessLog.getAction()).isEqualTo(action);
        assertThat(paramAccessLog.getTicketId()).isEqualTo(ticketId);
        assertThat(paramAccessLog.getTimestamp()).isEqualTo(timestamp);
        assertThat(paramAccessLog.getSessionId()).isNull();
    }

    @Test
    void parameterizedConstructor_WithSessionId_ShouldInitializeAllFields() {
        // Arrange
        String action = "Revoked Access";
        String ticketId = "TICKET-001";
        LocalDateTime timestamp = LocalDateTime.now();
        Long sessionId = 1L;

        // Act
        AccessLog paramAccessLog = new AccessLog(testUser, action, ticketId, timestamp, sessionId);

        // Assert
        assertThat(paramAccessLog.getId()).isNull();
        assertThat(paramAccessLog.getUser()).isEqualTo(testUser);
        assertThat(paramAccessLog.getAction()).isEqualTo(action);
        assertThat(paramAccessLog.getTicketId()).isEqualTo(ticketId);
        assertThat(paramAccessLog.getTimestamp()).isEqualTo(timestamp);
        assertThat(paramAccessLog.getSessionId()).isEqualTo(sessionId);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        String action = "Access Requested";
        LocalDateTime timestamp = LocalDateTime.now();
        String ticketId = "TICKET-001";
        Long sessionId = 1L;

        // Act
        accessLog.setAction(action);
        accessLog.setTimestamp(timestamp);
        accessLog.setTicketId(ticketId);
        accessLog.setSessionId(sessionId);
        accessLog.setUser(testUser);

        // Assert
        assertThat(accessLog.getId()).isNull(); // ID is auto-generated, starts as null
        assertThat(accessLog.getAction()).isEqualTo(action);
        assertThat(accessLog.getTimestamp()).isEqualTo(timestamp);
        assertThat(accessLog.getTicketId()).isEqualTo(ticketId);
        assertThat(accessLog.getSessionId()).isEqualTo(sessionId);
        assertThat(accessLog.getUser()).isEqualTo(testUser);
    }

    @Test
    void allFields_ShouldAcceptNullValues() {
        // Act
        accessLog.setAction(null);
        accessLog.setTimestamp(null);
        accessLog.setTicketId(null);
        accessLog.setSessionId(null);
        accessLog.setUser(null);

        // Assert
        assertThat(accessLog.getId()).isNull(); // ID is auto-generated, starts as null
        assertThat(accessLog.getAction()).isNull();
        assertThat(accessLog.getTimestamp()).isNull();
        assertThat(accessLog.getTicketId()).isNull();
        assertThat(accessLog.getSessionId()).isNull();
        assertThat(accessLog.getUser()).isNull();
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        // Act
        AccessLog nullAccessLog = new AccessLog(null, null, null, null);

        // Assert
        assertThat(nullAccessLog.getId()).isNull();
        assertThat(nullAccessLog.getUser()).isNull();
        assertThat(nullAccessLog.getAction()).isNull();
        assertThat(nullAccessLog.getTicketId()).isNull();
        assertThat(nullAccessLog.getTimestamp()).isNull();
        assertThat(nullAccessLog.getSessionId()).isNull();
    }

    @Test
    void parameterizedConstructor_WithSessionIdAndNullValues_ShouldHandleGracefully() {
        // Act
        AccessLog nullAccessLog = new AccessLog(null, null, null, null, null);

        // Assert
        assertThat(nullAccessLog.getId()).isNull();
        assertThat(nullAccessLog.getUser()).isNull();
        assertThat(nullAccessLog.getAction()).isNull();
        assertThat(nullAccessLog.getTicketId()).isNull();
        assertThat(nullAccessLog.getTimestamp()).isNull();
        assertThat(nullAccessLog.getSessionId()).isNull();
    }

    @Test
    void action_ShouldBeSettable() {
        // Arrange
        String action = "Access Granted";

        // Act
        accessLog.setAction(action);

        // Assert
        assertThat(accessLog.getAction()).isEqualTo(action);
    }

    @Test
    void timestamp_ShouldBeSettable() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now().minusHours(1);

        // Act
        accessLog.setTimestamp(timestamp);

        // Assert
        assertThat(accessLog.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void ticketId_ShouldBeSettable() {
        // Arrange
        String ticketId = "NEW-TICKET-456";

        // Act
        accessLog.setTicketId(ticketId);

        // Assert
        assertThat(accessLog.getTicketId()).isEqualTo(ticketId);
    }

    @Test
    void sessionId_ShouldBeSettable() {
        // Arrange
        Long sessionId = 5L;

        // Act
        accessLog.setSessionId(sessionId);

        // Assert
        assertThat(accessLog.getSessionId()).isEqualTo(sessionId);
    }

    @Test
    void user_ShouldBeSettable() {
        // Arrange
        User newUser = new User();
        newUser.setUserId("new-user-456");

        // Act
        accessLog.setUser(newUser);

        // Assert
        assertThat(accessLog.getUser()).isEqualTo(newUser);
    }

    @Test
    void id_ShouldBeNullForNewInstances() {
        // Assert
        assertThat(accessLog.getId()).isNull(); // ID is auto-generated, starts as null
    }

    @Test
    void sessionId_ShouldAcceptVariousValues() {
        // Act & Assert
        accessLog.setSessionId(1L);
        assertThat(accessLog.getSessionId()).isEqualTo(1L);

        accessLog.setSessionId(100L);
        assertThat(accessLog.getSessionId()).isEqualTo(100L);

        accessLog.setSessionId(0L);
        assertThat(accessLog.getSessionId()).isEqualTo(0L);
    }

    @Test
    void action_ShouldAcceptVariousValues() {
        // Act & Assert
        accessLog.setAction("Access Requested");
        assertThat(accessLog.getAction()).isEqualTo("Access Requested");

        accessLog.setAction("Access Granted");
        assertThat(accessLog.getAction()).isEqualTo("Access Granted");

        accessLog.setAction("Access Revoked");
        assertThat(accessLog.getAction()).isEqualTo("Access Revoked");

        accessLog.setAction("Access Denied");
        assertThat(accessLog.getAction()).isEqualTo("Access Denied");
    }

    @Test
    void parameterizedConstructor_WithPartialNullValues_ShouldHandleGracefully() {
        // Act
        AccessLog partialNullAccessLog = new AccessLog(testUser, "Test Action", null, LocalDateTime.now());

        // Assert
        assertThat(partialNullAccessLog.getUser()).isEqualTo(testUser);
        assertThat(partialNullAccessLog.getAction()).isEqualTo("Test Action");
        assertThat(partialNullAccessLog.getTicketId()).isNull();
        assertThat(partialNullAccessLog.getTimestamp()).isNotNull();
        assertThat(partialNullAccessLog.getSessionId()).isNull();
    }

    @Test
    void parameterizedConstructor_WithSessionIdAndPartialNullValues_ShouldHandleGracefully() {
        // Act
        AccessLog partialNullAccessLog = new AccessLog(testUser, "Test Action", "TICKET-001", null, 1L);

        // Assert
        assertThat(partialNullAccessLog.getUser()).isEqualTo(testUser);
        assertThat(partialNullAccessLog.getAction()).isEqualTo("Test Action");
        assertThat(partialNullAccessLog.getTicketId()).isEqualTo("TICKET-001");
        assertThat(partialNullAccessLog.getTimestamp()).isNull();
        assertThat(partialNullAccessLog.getSessionId()).isEqualTo(1L);
    }
}
