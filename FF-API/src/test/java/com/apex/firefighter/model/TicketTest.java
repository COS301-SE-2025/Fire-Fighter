package com.apex.firefighter.model;

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
        assertThat(ticket.getStatus()).isEqualTo("PENDING");
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
        LocalDateTime dateCreated = LocalDateTime.now();
        LocalDateTime dateCompleted = LocalDateTime.now().plusHours(2);
        LocalDate requestDate = LocalDate.now();
        Integer duration = 120;

        // Act
        ticket.setTicketId(ticketId);
        ticket.setDescription(description);
        ticket.setStatus(status);
        ticket.setUserId(userId);
        ticket.setEmergencyType(emergencyType);
        ticket.setEmergencyContact(emergencyContact);
        ticket.setRevokedBy(revokedBy);
        ticket.setDateCreated(dateCreated);
        ticket.setDateCompleted(dateCompleted);
        ticket.setRequestDate(requestDate);
        ticket.setDuration(duration);

        // Assert
        assertThat(ticket.getId()).isNull(); // ID is auto-generated, starts as null
        assertThat(ticket.getTicketId()).isEqualTo(ticketId);
        assertThat(ticket.getDescription()).isEqualTo(description);
        assertThat(ticket.getStatus()).isEqualTo(status);
        assertThat(ticket.getUserId()).isEqualTo(userId);
        assertThat(ticket.getEmergencyType()).isEqualTo(emergencyType);
        assertThat(ticket.getEmergencyContact()).isEqualTo(emergencyContact);
        assertThat(ticket.getRevokedBy()).isEqualTo(revokedBy);
        assertThat(ticket.getDateCreated()).isEqualTo(dateCreated);
        assertThat(ticket.getDateCompleted()).isEqualTo(dateCompleted);
        assertThat(ticket.getRequestDate()).isEqualTo(requestDate);
        assertThat(ticket.getDuration()).isEqualTo(duration);
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        // Arrange
        ticket.setTicketId("TICKET-001");
        ticket.setDescription("Test emergency");
        ticket.setStatus("PENDING");

        // Act
        String result = ticket.toString();

        // Assert
        assertThat(result).contains("Ticket{");
        assertThat(result).contains("ticketId='TICKET-001'");
        assertThat(result).contains("description='Test emergency'");
        assertThat(result).contains("status='PENDING'");
    }

    @Test
    void equals_WithSameTicketId_ShouldReturnTrue() {
        // Arrange
        Ticket ticket1 = new Ticket();
        ticket1.setTicketId("TICKET-001");
        
        Ticket ticket2 = new Ticket();
        ticket2.setTicketId("TICKET-001");

        // Act & Assert
        assertThat(ticket1.equals(ticket2)).isTrue();
        assertThat(ticket1.hashCode()).isEqualTo(ticket2.hashCode());
    }

    @Test
    void equals_WithDifferentTicketId_ShouldReturnFalse() {
        // Arrange
        Ticket ticket1 = new Ticket();
        ticket1.setTicketId("TICKET-001");
        
        Ticket ticket2 = new Ticket();
        ticket2.setTicketId("TICKET-002");

        // Act & Assert
        assertThat(ticket1.equals(ticket2)).isFalse();
    }

    @Test
    void equals_WithNullTicketId_ShouldHandleCorrectly() {
        // Arrange
        Ticket ticket1 = new Ticket();
        ticket1.setTicketId(null);
        
        Ticket ticket2 = new Ticket();
        ticket2.setTicketId(null);

        // Act & Assert
        assertThat(ticket1.equals(ticket2)).isTrue();
    }

    @Test
    void equals_WithNull_ShouldReturnFalse() {
        // Act & Assert
        assertThat(ticket.equals(null)).isFalse();
    }

    @Test
    void equals_WithDifferentClass_ShouldReturnFalse() {
        // Act & Assert
        assertThat(ticket.equals("not a ticket")).isFalse();
    }

    @Test
    void equals_WithSameInstance_ShouldReturnTrue() {
        // Act & Assert
        assertThat(ticket.equals(ticket)).isTrue();
    }

    @Test
    void hashCode_WithSameTicketId_ShouldBeEqual() {
        // Arrange
        Ticket ticket1 = new Ticket();
        ticket1.setTicketId("TICKET-001");
        
        Ticket ticket2 = new Ticket();
        ticket2.setTicketId("TICKET-001");

        // Act & Assert
        assertThat(ticket1.hashCode()).isEqualTo(ticket2.hashCode());
    }

    @Test
    void hashCode_WithNullTicketId_ShouldNotThrowException() {
        // Arrange
        ticket.setTicketId(null);

        // Act & Assert
        assertThat(ticket.hashCode()).isNotNull();
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
        ticket.setDateCreated(null);
        ticket.setDateCompleted(null);
        ticket.setRequestDate(null);
        ticket.setDuration(null);

        // Assert
        assertThat(ticket.getId()).isNull(); // ID is always null for new entities
        assertThat(ticket.getTicketId()).isNull();
        assertThat(ticket.getDescription()).isNull();
        assertThat(ticket.getStatus()).isNull();
        assertThat(ticket.getUserId()).isNull();
        assertThat(ticket.getEmergencyType()).isNull();
        assertThat(ticket.getEmergencyContact()).isNull();
        assertThat(ticket.getRevokedBy()).isNull();
        assertThat(ticket.getDateCreated()).isNull();
        assertThat(ticket.getDateCompleted()).isNull();
        assertThat(ticket.getRequestDate()).isNull();
        assertThat(ticket.getDuration()).isNull();
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
}
