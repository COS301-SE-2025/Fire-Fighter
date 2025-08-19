package com.apex.firefighter.unit.models;

import com.apex.firefighter.model.AccessRequest;
import com.apex.firefighter.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccessRequestTest {

    private AccessRequest accessRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        accessRequest = new AccessRequest();
        
        testUser = new User();
        testUser.setUserId("test-user-123");
        testUser.setUsername("testuser");
    }

    @Test
    void defaultConstructor_ShouldInitializeWithDefaults() {
        // Assert
        assertThat(accessRequest.getId()).isNull();
        assertThat(accessRequest.getTicketId()).isNull();
        assertThat(accessRequest.getStatus()).isEqualTo(AccessRequest.RequestStatus.PENDING);
        assertThat(accessRequest.getRequestTime()).isNotNull();
        assertThat(accessRequest.getApprovedTime()).isNull();
        assertThat(accessRequest.getApprovedBy()).isNull();
        assertThat(accessRequest.getUser()).isNull();
    }

    @Test
    void parameterizedConstructor_ShouldInitializeAllFields() {
        // Arrange
        String ticketId = "TICKET-001";

        // Act
        AccessRequest paramAccessRequest = new AccessRequest(ticketId, testUser);

        // Assert
        assertThat(paramAccessRequest.getId()).isNull();
        assertThat(paramAccessRequest.getTicketId()).isEqualTo(ticketId);
        assertThat(paramAccessRequest.getStatus()).isEqualTo(AccessRequest.RequestStatus.PENDING);
        assertThat(paramAccessRequest.getRequestTime()).isNotNull();
        assertThat(paramAccessRequest.getApprovedTime()).isNull();
        assertThat(paramAccessRequest.getApprovedBy()).isNull();
        assertThat(paramAccessRequest.getUser()).isEqualTo(testUser);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;
        String ticketId = "TICKET-001";
        AccessRequest.RequestStatus status = AccessRequest.RequestStatus.APPROVED;
        ZonedDateTime requestTime = ZonedDateTime.now().minusHours(1);
        ZonedDateTime approvedTime = ZonedDateTime.now();
        String approvedBy = "admin-user";

        // Act
        accessRequest.setId(id);
        accessRequest.setTicketId(ticketId);
        accessRequest.setStatus(status);
        accessRequest.setRequestTime(requestTime);
        accessRequest.setApprovedTime(approvedTime);
        accessRequest.setApprovedBy(approvedBy);
        accessRequest.setUser(testUser);

        // Assert
        assertThat(accessRequest.getId()).isEqualTo(id);
        assertThat(accessRequest.getTicketId()).isEqualTo(ticketId);
        assertThat(accessRequest.getStatus()).isEqualTo(status);
        assertThat(accessRequest.getRequestTime()).isEqualTo(requestTime);
        assertThat(accessRequest.getApprovedTime()).isEqualTo(approvedTime);
        assertThat(accessRequest.getApprovedBy()).isEqualTo(approvedBy);
        assertThat(accessRequest.getUser()).isEqualTo(testUser);
    }

    @Test
    void allFields_ShouldAcceptNullValues() {
        // Act
        accessRequest.setId(null);
        accessRequest.setTicketId(null);
        accessRequest.setStatus(null);
        accessRequest.setRequestTime(null);
        accessRequest.setApprovedTime(null);
        accessRequest.setApprovedBy(null);
        accessRequest.setUser(null);

        // Assert
        assertThat(accessRequest.getId()).isNull();
        assertThat(accessRequest.getTicketId()).isNull();
        assertThat(accessRequest.getStatus()).isNull();
        assertThat(accessRequest.getRequestTime()).isNull();
        assertThat(accessRequest.getApprovedTime()).isNull();
        assertThat(accessRequest.getApprovedBy()).isNull();
        assertThat(accessRequest.getUser()).isNull();
    }

    @Test
    void requestTime_ShouldBeSetOnConstruction() {
        // Arrange
        ZonedDateTime beforeCreation = ZonedDateTime.now().minusSeconds(1);
        
        // Act
        AccessRequest newAccessRequest = new AccessRequest();
        ZonedDateTime afterCreation = ZonedDateTime.now().plusSeconds(1);

        // Assert
        assertThat(newAccessRequest.getRequestTime()).isAfter(beforeCreation);
        assertThat(newAccessRequest.getRequestTime()).isBefore(afterCreation);
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        // Act
        AccessRequest nullAccessRequest = new AccessRequest(null, null);

        // Assert
        assertThat(nullAccessRequest.getId()).isNull();
        assertThat(nullAccessRequest.getTicketId()).isNull();
        assertThat(nullAccessRequest.getStatus()).isEqualTo(AccessRequest.RequestStatus.PENDING);
        assertThat(nullAccessRequest.getRequestTime()).isNotNull();
        assertThat(nullAccessRequest.getApprovedTime()).isNull();
        assertThat(nullAccessRequest.getApprovedBy()).isNull();
        assertThat(nullAccessRequest.getUser()).isNull();
    }

    @Test
    void approve_ShouldUpdateStatusAndTimestamps() {
        // Arrange
        String approvedByUserId = "admin-user-123";
        ZonedDateTime beforeApproval = ZonedDateTime.now().minusSeconds(1);

        // Act
        accessRequest.approve(approvedByUserId);
        ZonedDateTime afterApproval = ZonedDateTime.now().plusSeconds(1);

        // Assert
        assertThat(accessRequest.getStatus()).isEqualTo(AccessRequest.RequestStatus.APPROVED);
        assertThat(accessRequest.getApprovedBy()).isEqualTo(approvedByUserId);
        assertThat(accessRequest.getApprovedTime()).isAfter(beforeApproval);
        assertThat(accessRequest.getApprovedTime()).isBefore(afterApproval);
    }

    @Test
    void deny_ShouldUpdateStatusAndTimestamps() {
        // Arrange
        String deniedByUserId = "admin-user-123";
        ZonedDateTime beforeDenial = ZonedDateTime.now().minusSeconds(1);

        // Act
        accessRequest.deny(deniedByUserId);
        ZonedDateTime afterDenial = ZonedDateTime.now().plusSeconds(1);

        // Assert
        assertThat(accessRequest.getStatus()).isEqualTo(AccessRequest.RequestStatus.DENIED);
        assertThat(accessRequest.getApprovedBy()).isEqualTo(deniedByUserId);
        assertThat(accessRequest.getApprovedTime()).isAfter(beforeDenial);
        assertThat(accessRequest.getApprovedTime()).isBefore(afterDenial);
    }

    @Test
    void revoke_ShouldUpdateStatusAndTimestamps() {
        // Arrange
        String revokedByUserId = "admin-user-123";
        ZonedDateTime beforeRevocation = ZonedDateTime.now().minusSeconds(1);

        // Act
        accessRequest.revoke(revokedByUserId);
        ZonedDateTime afterRevocation = ZonedDateTime.now().plusSeconds(1);

        // Assert
        assertThat(accessRequest.getStatus()).isEqualTo(AccessRequest.RequestStatus.REVOKED);
        assertThat(accessRequest.getApprovedBy()).isEqualTo(revokedByUserId);
        assertThat(accessRequest.getApprovedTime()).isAfter(beforeRevocation);
        assertThat(accessRequest.getApprovedTime()).isBefore(afterRevocation);
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        // Arrange
        accessRequest.setId(1L);
        accessRequest.setTicketId("TICKET-001");
        accessRequest.setStatus(AccessRequest.RequestStatus.APPROVED);
        accessRequest.setUser(testUser);

        // Act
        String result = accessRequest.toString();

        // Assert
        assertThat(result).contains("AccessRequest{");
        assertThat(result).contains("id=1");
        assertThat(result).contains("ticketId='TICKET-001'");
        assertThat(result).contains("status=APPROVED");
        assertThat(result).contains("user=test-user-123");
    }

    @Test
    void toString_WithNullValues_ShouldHandleGracefully() {
        // Act
        String result = accessRequest.toString();

        // Assert
        assertThat(result).contains("AccessRequest{");
        assertThat(result).contains("user=null");
    }

    @Test
    void requestStatus_Enum_ShouldHaveAllExpectedValues() {
        // Assert
        assertThat(AccessRequest.RequestStatus.PENDING).isNotNull();
        assertThat(AccessRequest.RequestStatus.APPROVED).isNotNull();
        assertThat(AccessRequest.RequestStatus.DENIED).isNotNull();
        assertThat(AccessRequest.RequestStatus.REVOKED).isNotNull();
    }

    @Test
    void status_ShouldBeMutable() {
        // Act & Assert
        accessRequest.setStatus(AccessRequest.RequestStatus.PENDING);
        assertThat(accessRequest.getStatus()).isEqualTo(AccessRequest.RequestStatus.PENDING);

        accessRequest.setStatus(AccessRequest.RequestStatus.APPROVED);
        assertThat(accessRequest.getStatus()).isEqualTo(AccessRequest.RequestStatus.APPROVED);

        accessRequest.setStatus(AccessRequest.RequestStatus.DENIED);
        assertThat(accessRequest.getStatus()).isEqualTo(AccessRequest.RequestStatus.DENIED);

        accessRequest.setStatus(AccessRequest.RequestStatus.REVOKED);
        assertThat(accessRequest.getStatus()).isEqualTo(AccessRequest.RequestStatus.REVOKED);
    }

    @Test
    void ticketId_ShouldBeSettable() {
        // Arrange
        String ticketId = "NEW-TICKET-456";

        // Act
        accessRequest.setTicketId(ticketId);

        // Assert
        assertThat(accessRequest.getTicketId()).isEqualTo(ticketId);
    }

    @Test
    void approvedBy_ShouldBeSettable() {
        // Arrange
        String approvedBy = "super-admin";

        // Act
        accessRequest.setApprovedBy(approvedBy);

        // Assert
        assertThat(accessRequest.getApprovedBy()).isEqualTo(approvedBy);
    }

    @Test
    void approvedTime_ShouldBeSettable() {
        // Arrange
        ZonedDateTime approvedTime = ZonedDateTime.now().minusHours(2);

        // Act
        accessRequest.setApprovedTime(approvedTime);

        // Assert
        assertThat(accessRequest.getApprovedTime()).isEqualTo(approvedTime);
    }

    @Test
    void requestTime_ShouldBeSettable() {
        // Arrange
        ZonedDateTime requestTime = ZonedDateTime.now().minusDays(1);

        // Act
        accessRequest.setRequestTime(requestTime);

        // Assert
        assertThat(accessRequest.getRequestTime()).isEqualTo(requestTime);
    }

    @Test
    void user_ShouldBeSettable() {
        // Arrange
        User newUser = new User();
        newUser.setUserId("new-user-456");

        // Act
        accessRequest.setUser(newUser);

        // Assert
        assertThat(accessRequest.getUser()).isEqualTo(newUser);
    }

    @Test
    void id_ShouldAcceptVariousValues() {
        // Act & Assert
        accessRequest.setId(1L);
        assertThat(accessRequest.getId()).isEqualTo(1L);

        accessRequest.setId(100L);
        assertThat(accessRequest.getId()).isEqualTo(100L);

        accessRequest.setId(0L);
        assertThat(accessRequest.getId()).isEqualTo(0L);
    }
}
