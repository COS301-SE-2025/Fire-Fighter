package com.apex.firefighter.unit.dto;

import com.apex.firefighter.dto.TicketCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TicketCreateRequestTest {

    private TicketCreateRequest ticketCreateRequest;

    @BeforeEach
    void setUp() {
        ticketCreateRequest = new TicketCreateRequest();
    }

    @Test
    void defaultConstructor_ShouldInitializeWithNullValues() {
        // Assert
        assertThat(ticketCreateRequest.getTicketId()).isNull();
        assertThat(ticketCreateRequest.getDescription()).isNull();
        assertThat(ticketCreateRequest.getUserId()).isNull();
        assertThat(ticketCreateRequest.getEmergencyType()).isNull();
        assertThat(ticketCreateRequest.getEmergencyContact()).isNull();
        assertThat(ticketCreateRequest.getDuration()).isNull();
    }

    @Test
    void parameterizedConstructor_ShouldInitializeAllFields() {
        // Arrange
        String ticketId = "FIRE-2025-001";
        String description = "House fire on Main Street";
        String userId = "user123";
        String emergencyType = "FIRE";
        String emergencyContact = "+1-555-0123";
        String duration = "2 hours";

        // Act
        TicketCreateRequest request = new TicketCreateRequest(
            ticketId, description, userId, emergencyType, emergencyContact, duration
        );

        // Assert
        assertThat(request.getTicketId()).isEqualTo(ticketId);
        assertThat(request.getDescription()).isEqualTo(description);
        assertThat(request.getUserId()).isEqualTo(userId);
        assertThat(request.getEmergencyType()).isEqualTo(emergencyType);
        assertThat(request.getEmergencyContact()).isEqualTo(emergencyContact);
        assertThat(request.getDuration()).isEqualTo(duration);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        String ticketId = "MEDICAL-2025-002";
        String description = "Medical emergency at hospital";
        String userId = "user456";
        String emergencyType = "MEDICAL";
        String emergencyContact = "emergency@hospital.com";
        String duration = "1.5 hours";

        // Act
        ticketCreateRequest.setTicketId(ticketId);
        ticketCreateRequest.setDescription(description);
        ticketCreateRequest.setUserId(userId);
        ticketCreateRequest.setEmergencyType(emergencyType);
        ticketCreateRequest.setEmergencyContact(emergencyContact);
        ticketCreateRequest.setDuration(duration);

        // Assert
        assertThat(ticketCreateRequest.getTicketId()).isEqualTo(ticketId);
        assertThat(ticketCreateRequest.getDescription()).isEqualTo(description);
        assertThat(ticketCreateRequest.getUserId()).isEqualTo(userId);
        assertThat(ticketCreateRequest.getEmergencyType()).isEqualTo(emergencyType);
        assertThat(ticketCreateRequest.getEmergencyContact()).isEqualTo(emergencyContact);
        assertThat(ticketCreateRequest.getDuration()).isEqualTo(duration);
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        // Arrange
        ticketCreateRequest.setTicketId("RESCUE-2025-003");
        ticketCreateRequest.setDescription("Rescue operation");
        ticketCreateRequest.setUserId("user789");
        ticketCreateRequest.setEmergencyType("RESCUE");
        ticketCreateRequest.setEmergencyContact("rescue@team.com");
        ticketCreateRequest.setDuration("3 hours");

        // Act
        String result = ticketCreateRequest.toString();

        // Assert
        assertThat(result).contains("TicketCreateRequest{");
        assertThat(result).contains("ticketId='RESCUE-2025-003'");
        assertThat(result).contains("description='Rescue operation'");
        assertThat(result).contains("userId='user789'");
        assertThat(result).contains("emergencyType='RESCUE'");
        assertThat(result).contains("emergencyContact='rescue@team.com'");
        assertThat(result).contains("duration='3 hours'");
    }

    @Test
    void toString_WithNullValues_ShouldHandleGracefully() {
        // Act
        String result = ticketCreateRequest.toString();

        // Assert
        assertThat(result).contains("TicketCreateRequest{");
        assertThat(result).contains("ticketId='null'");
        assertThat(result).contains("description='null'");
        assertThat(result).contains("userId='null'");
        assertThat(result).contains("emergencyType='null'");
        assertThat(result).contains("emergencyContact='null'");
        assertThat(result).contains("duration='null'");
    }

    @Test
    void setTicketId_WithNullValue_ShouldAcceptNull() {
        // Act
        ticketCreateRequest.setTicketId(null);

        // Assert
        assertThat(ticketCreateRequest.getTicketId()).isNull();
    }

    @Test
    void setDescription_WithEmptyString_ShouldAcceptEmptyString() {
        // Act
        ticketCreateRequest.setDescription("");

        // Assert
        assertThat(ticketCreateRequest.getDescription()).isEqualTo("");
    }

    @Test
    void setUserId_WithWhitespace_ShouldAcceptWhitespace() {
        // Act
        ticketCreateRequest.setUserId("   ");

        // Assert
        assertThat(ticketCreateRequest.getUserId()).isEqualTo("   ");
    }

    @Test
    void setEmergencyType_WithValidTypes_ShouldAcceptAllTypes() {
        // Test FIRE
        ticketCreateRequest.setEmergencyType("FIRE");
        assertThat(ticketCreateRequest.getEmergencyType()).isEqualTo("FIRE");

        // Test MEDICAL
        ticketCreateRequest.setEmergencyType("MEDICAL");
        assertThat(ticketCreateRequest.getEmergencyType()).isEqualTo("MEDICAL");

        // Test RESCUE
        ticketCreateRequest.setEmergencyType("RESCUE");
        assertThat(ticketCreateRequest.getEmergencyType()).isEqualTo("RESCUE");

        // Test HAZMAT
        ticketCreateRequest.setEmergencyType("HAZMAT");
        assertThat(ticketCreateRequest.getEmergencyType()).isEqualTo("HAZMAT");
    }

    @Test
    void setEmergencyType_WithInvalidType_ShouldStillAccept() {
        // Act
        ticketCreateRequest.setEmergencyType("INVALID_TYPE");

        // Assert
        assertThat(ticketCreateRequest.getEmergencyType()).isEqualTo("INVALID_TYPE");
    }

    @Test
    void setEmergencyContact_WithEmailFormat_ShouldAccept() {
        // Act
        ticketCreateRequest.setEmergencyContact("emergency@example.com");

        // Assert
        assertThat(ticketCreateRequest.getEmergencyContact()).isEqualTo("emergency@example.com");
    }

    @Test
    void setEmergencyContact_WithPhoneFormat_ShouldAccept() {
        // Act
        ticketCreateRequest.setEmergencyContact("+1-555-123-4567");

        // Assert
        assertThat(ticketCreateRequest.getEmergencyContact()).isEqualTo("+1-555-123-4567");
    }

    @Test
    void setDuration_WithVariousFormats_ShouldAcceptAll() {
        // Test hours format
        ticketCreateRequest.setDuration("2 hours");
        assertThat(ticketCreateRequest.getDuration()).isEqualTo("2 hours");

        // Test minutes format
        ticketCreateRequest.setDuration("30 minutes");
        assertThat(ticketCreateRequest.getDuration()).isEqualTo("30 minutes");

        // Test decimal format
        ticketCreateRequest.setDuration("1.5 hours");
        assertThat(ticketCreateRequest.getDuration()).isEqualTo("1.5 hours");

        // Test numeric only
        ticketCreateRequest.setDuration("120");
        assertThat(ticketCreateRequest.getDuration()).isEqualTo("120");
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        // Act
        TicketCreateRequest request = new TicketCreateRequest(null, null, null, null, null, null);

        // Assert
        assertThat(request.getTicketId()).isNull();
        assertThat(request.getDescription()).isNull();
        assertThat(request.getUserId()).isNull();
        assertThat(request.getEmergencyType()).isNull();
        assertThat(request.getEmergencyContact()).isNull();
        assertThat(request.getDuration()).isNull();
    }

    @Test
    void parameterizedConstructor_WithEmptyStrings_ShouldAcceptEmptyStrings() {
        // Act
        TicketCreateRequest request = new TicketCreateRequest("", "", "", "", "", "");

        // Assert
        assertThat(request.getTicketId()).isEqualTo("");
        assertThat(request.getDescription()).isEqualTo("");
        assertThat(request.getUserId()).isEqualTo("");
        assertThat(request.getEmergencyType()).isEqualTo("");
        assertThat(request.getEmergencyContact()).isEqualTo("");
        assertThat(request.getDuration()).isEqualTo("");
    }

    @Test
    void allSetters_WithLongStrings_ShouldAcceptLongStrings() {
        // Arrange
        String longString = "a".repeat(1000);

        // Act
        ticketCreateRequest.setTicketId(longString);
        ticketCreateRequest.setDescription(longString);
        ticketCreateRequest.setUserId(longString);
        ticketCreateRequest.setEmergencyType(longString);
        ticketCreateRequest.setEmergencyContact(longString);
        ticketCreateRequest.setDuration(longString);

        // Assert
        assertThat(ticketCreateRequest.getTicketId()).isEqualTo(longString);
        assertThat(ticketCreateRequest.getDescription()).isEqualTo(longString);
        assertThat(ticketCreateRequest.getUserId()).isEqualTo(longString);
        assertThat(ticketCreateRequest.getEmergencyType()).isEqualTo(longString);
        assertThat(ticketCreateRequest.getEmergencyContact()).isEqualTo(longString);
        assertThat(ticketCreateRequest.getDuration()).isEqualTo(longString);
    }

    @Test
    void allSetters_WithSpecialCharacters_ShouldAcceptSpecialCharacters() {
        // Arrange
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

        // Act
        ticketCreateRequest.setTicketId(specialChars);
        ticketCreateRequest.setDescription(specialChars);
        ticketCreateRequest.setUserId(specialChars);
        ticketCreateRequest.setEmergencyType(specialChars);
        ticketCreateRequest.setEmergencyContact(specialChars);
        ticketCreateRequest.setDuration(specialChars);

        // Assert
        assertThat(ticketCreateRequest.getTicketId()).isEqualTo(specialChars);
        assertThat(ticketCreateRequest.getDescription()).isEqualTo(specialChars);
        assertThat(ticketCreateRequest.getUserId()).isEqualTo(specialChars);
        assertThat(ticketCreateRequest.getEmergencyType()).isEqualTo(specialChars);
        assertThat(ticketCreateRequest.getEmergencyContact()).isEqualTo(specialChars);
        assertThat(ticketCreateRequest.getDuration()).isEqualTo(specialChars);
    }
}


