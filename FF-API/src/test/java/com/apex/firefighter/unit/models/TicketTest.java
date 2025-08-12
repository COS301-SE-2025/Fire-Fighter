package com.apex.firefighter.unit.models;

import com.apex.firefighter.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TicketTest {

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
    }

    @Test
    void defaultConstructor_ShouldInitializeWithDefaults() {
        // Assert
        assertThat(ticket.getDateCreated()).isNotNull();
        assertThat(ticket.getRequestDate()).isNotNull();
        assertThat(ticket.getStatus()).isEqualTo("Active");
        assertThat(ticket.getDuration()).isEqualTo(60);
        assertThat(ticket.getFiveMinuteWarningSent()).isFalse();
    }

    @Test
    void parameterizedConstructor_ShouldInitializeAllFields() {
        // Arrange
        String ticketId = "TICKET-001";
        String description = "Test emergency";
        String userId = "user-123";
        String emergencyType = "FIRE";
        String emergencyContact = "911";
        Integer duration = 120;

        // Act
        Ticket paramTicket = new Ticket(ticketId, description, userId, emergencyType, emergencyContact, duration);

        // Assert
        assertThat(paramTicket.getTicketId()).isEqualTo(ticketId);
        assertThat(paramTicket.getDescription()).isEqualTo(description);
        assertThat(paramTicket.getUserId()).isEqualTo(userId);
        assertThat(paramTicket.getEmergencyType()).isEqualTo(emergencyType);
        assertThat(paramTicket.getEmergencyContact()).isEqualTo(emergencyContact);
        assertThat(paramTicket.getDuration()).isEqualTo(duration);
        assertThat(paramTicket.getDateCreated()).isNotNull();
        assertThat(paramTicket.getStatus()).isEqualTo("Active");
        assertThat(paramTicket.getFiveMinuteWarningSent()).isFalse();
    }

    @Test
    void legacyConstructor_ShouldInitializeWithNullDuration() {
        // Arrange
        String ticketId = "TICKET-001";
        String description = "Test emergency";
        String userId = "user-123";
        String emergencyType = "FIRE";
        String emergencyContact = "911";

        // Act
        Ticket legacyTicket = new Ticket(ticketId, description, userId, emergencyType, emergencyContact);

        // Assert
        assertThat(legacyTicket.getTicketId()).isEqualTo(ticketId);
        assertThat(legacyTicket.getDescription()).isEqualTo(description);
        assertThat(legacyTicket.getUserId()).isEqualTo(userId);
        assertThat(legacyTicket.getEmergencyType()).isEqualTo(emergencyType);
        assertThat(legacyTicket.getEmergencyContact()).isEqualTo(emergencyContact);
        assertThat(legacyTicket.getDuration()).isNull();
        assertThat(legacyTicket.getDateCreated()).isNotNull();
        assertThat(legacyTicket.getStatus()).isEqualTo("Active");
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        String ticketId = "TICKET-001";
        String description = "Emergency description";
        String status = "APPROVED";
        String userId = "user-123";
        String emergencyType = "MEDICAL";
        String emergencyContact = "emergency@test.com";
        String revokedBy = "admin-user";
        String rejectReason = "Policy violation";
        LocalDateTime dateCreated = LocalDateTime.now();
        LocalDateTime dateCompleted = LocalDateTime.now().plusHours(2);
        LocalDate requestDate = LocalDate.now();
        Integer duration = 120;
        Boolean fiveMinuteWarningSent = true;

        // Act
        ticket.setTicketId(ticketId);
        ticket.setDescription(description);
        ticket.setStatus(status);
        ticket.setUserId(userId);
        ticket.setEmergencyType(emergencyType);
        ticket.setEmergencyContact(emergencyContact);
        ticket.setRevokedBy(revokedBy);
        ticket.setRejectReason(rejectReason);
        ticket.setDateCreated(dateCreated);
        ticket.setDateCompleted(dateCompleted);
        ticket.setRequestDate(requestDate);
        ticket.setDuration(duration);
        ticket.setFiveMinuteWarningSent(fiveMinuteWarningSent);

        // Assert
        assertThat(ticket.getId()).isNull(); // ID is auto-generated, starts as null
        assertThat(ticket.getTicketId()).isEqualTo(ticketId);
        assertThat(ticket.getDescription()).isEqualTo(description);
        assertThat(ticket.getStatus()).isEqualTo(status);
        assertThat(ticket.getUserId()).isEqualTo(userId);
        assertThat(ticket.getEmergencyType()).isEqualTo(emergencyType);
        assertThat(ticket.getEmergencyContact()).isEqualTo(emergencyContact);
        assertThat(ticket.getRevokedBy()).isEqualTo(revokedBy);
        assertThat(ticket.getRejectReason()).isEqualTo(rejectReason);
        assertThat(ticket.getDateCreated()).isEqualTo(dateCreated);
        assertThat(ticket.getDateCompleted()).isEqualTo(dateCompleted);
        assertThat(ticket.getRequestDate()).isEqualTo(requestDate);
        assertThat(ticket.getDuration()).isEqualTo(duration);
        assertThat(ticket.getFiveMinuteWarningSent()).isEqualTo(fiveMinuteWarningSent);
    }

    @Test
    void allFields_ShouldAcceptNullValues() {
        // Act
        ticket.setTicketId(null);
        ticket.setDescription(null);
        ticket.setStatus(null);
        ticket.setUserId(null);
        ticket.setEmergencyType(null);
        ticket.setEmergencyContact(null);
        ticket.setRevokedBy(null);
        ticket.setRejectReason(null);
        ticket.setDateCreated(null);
        ticket.setDateCompleted(null);
        ticket.setRequestDate(null);
        ticket.setDuration(null);
        ticket.setFiveMinuteWarningSent(null);

        // Assert
        assertThat(ticket.getId()).isNull(); // ID is always null for new entities
        assertThat(ticket.getTicketId()).isNull();
        assertThat(ticket.getDescription()).isNull();
        assertThat(ticket.getStatus()).isNull();
        assertThat(ticket.getUserId()).isNull();
        assertThat(ticket.getEmergencyType()).isNull();
        assertThat(ticket.getEmergencyContact()).isNull();
        assertThat(ticket.getRevokedBy()).isNull();
        assertThat(ticket.getRejectReason()).isNull();
        assertThat(ticket.getDateCreated()).isNull();
        assertThat(ticket.getDateCompleted()).isNull();
        assertThat(ticket.getRequestDate()).isNull();
        assertThat(ticket.getDuration()).isNull();
        assertThat(ticket.getFiveMinuteWarningSent()).isNull();
    }

    @Test
    void dateCreated_ShouldBeSetOnConstruction() {
        // Arrange
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        
        // Act
        Ticket newTicket = new Ticket();
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);

        // Assert
        assertThat(newTicket.getDateCreated()).isAfter(beforeCreation);
        assertThat(newTicket.getDateCreated()).isBefore(afterCreation);
    }

    @Test
    void requestDate_ShouldBeSetOnConstruction() {
        // Arrange
        LocalDate today = LocalDate.now();
        
        // Act
        Ticket newTicket = new Ticket();

        // Assert
        assertThat(newTicket.getRequestDate()).isEqualTo(today);
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        // Act
        Ticket nullTicket = new Ticket(null, null, null, null, null, null);

        // Assert
        assertThat(nullTicket.getTicketId()).isNull();
        assertThat(nullTicket.getDescription()).isNull();
        assertThat(nullTicket.getUserId()).isNull();
        assertThat(nullTicket.getEmergencyType()).isNull();
        assertThat(nullTicket.getEmergencyContact()).isNull();
        assertThat(nullTicket.getDuration()).isNull();
        assertThat(nullTicket.getDateCreated()).isNotNull();
        assertThat(nullTicket.getStatus()).isEqualTo("Active");
        assertThat(nullTicket.getFiveMinuteWarningSent()).isFalse();
    }

    @Test
    void duration_ShouldAcceptPositiveValues() {
        // Act
        ticket.setDuration(60);

        // Assert
        assertThat(ticket.getDuration()).isEqualTo(60);
    }

    @Test
    void duration_ShouldAcceptZero() {
        // Act
        ticket.setDuration(0);

        // Assert
        assertThat(ticket.getDuration()).isEqualTo(0);
    }

    @Test
    void duration_ShouldAcceptNegativeValues() {
        // Act
        ticket.setDuration(-30);

        // Assert
        assertThat(ticket.getDuration()).isEqualTo(-30);
    }

    @Test
    void fiveMinuteWarningSent_ShouldAcceptTrueAndFalse() {
        // Act & Assert
        ticket.setFiveMinuteWarningSent(true);
        assertThat(ticket.getFiveMinuteWarningSent()).isTrue();

        ticket.setFiveMinuteWarningSent(false);
        assertThat(ticket.getFiveMinuteWarningSent()).isFalse();
    }

    @Test
    void status_ShouldBeMutable() {
        // Act & Assert
        ticket.setStatus("PENDING");
        assertThat(ticket.getStatus()).isEqualTo("PENDING");

        ticket.setStatus("APPROVED");
        assertThat(ticket.getStatus()).isEqualTo("APPROVED");

        ticket.setStatus("COMPLETED");
        assertThat(ticket.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void dateCompleted_ShouldBeSettable() {
        // Arrange
        LocalDateTime completionTime = LocalDateTime.now().plusHours(1);

        // Act
        ticket.setDateCompleted(completionTime);

        // Assert
        assertThat(ticket.getDateCompleted()).isEqualTo(completionTime);
    }

    @Test
    void revokedBy_ShouldBeSettable() {
        // Arrange
        String revokedBy = "admin-user-123";

        // Act
        ticket.setRevokedBy(revokedBy);

        // Assert
        assertThat(ticket.getRevokedBy()).isEqualTo(revokedBy);
    }

    @Test
    void rejectReason_ShouldBeSettable() {
        // Arrange
        String rejectReason = "Access denied due to policy violation";

        // Act
        ticket.setRejectReason(rejectReason);

        // Assert
        assertThat(ticket.getRejectReason()).isEqualTo(rejectReason);
    }
}
