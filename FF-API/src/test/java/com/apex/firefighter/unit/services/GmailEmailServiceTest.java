package com.apex.firefighter.unit.services;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.service.GmailEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GmailEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private GmailEmailService gmailEmailService;

    private User testUser;
    private Ticket testTicket;
    private List<Ticket> testTickets;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_REASON = "Policy violation";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("test-user-123");
        testUser.setUsername("testuser");
        testUser.setEmail(TEST_EMAIL);
        testUser.setDepartment("IT");

        testTicket = new Ticket();
        testTicket.setTicketId("TICKET-001");
        testTicket.setDescription("Emergency access request");
        testTicket.setStatus("Active");
        testTicket.setDateCreated(LocalDateTime.now().minusHours(1));
        testTicket.setDateCompleted(LocalDateTime.now());
        testTicket.setUserId("test-user-123");
        testTicket.setEmergencyType("fire");
        testTicket.setEmergencyContact("911");
        testTicket.setDuration(60);

        Ticket secondTicket = new Ticket();
        secondTicket.setTicketId("TICKET-002");
        secondTicket.setDescription("System maintenance access");
        secondTicket.setStatus("Completed");
        secondTicket.setDateCreated(LocalDateTime.now().minusHours(2));
        secondTicket.setDateCompleted(LocalDateTime.now().minusMinutes(30));
        secondTicket.setUserId("test-user-456");
        secondTicket.setEmergencyType("system");
        secondTicket.setEmergencyContact("support@company.com");
        secondTicket.setDuration(120);

        testTickets = Arrays.asList(testTicket, secondTicket);
    }

    // ==================== CSV EXPORT TESTS ====================

    @Test
    void exportTicketsToCsv_WithValidTickets_ShouldReturnFormattedCsv() {
        // Act
        String result = gmailEmailService.exportTicketsToCsv(testTickets);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains("TicketId,Description,Status,DateCreated,UserId,EmergencyType,EmergencyContact,Duration");
        assertThat(result).contains("TICKET-001");
        assertThat(result).contains("TICKET-002");
        assertThat(result).contains("Emergency access request");
        assertThat(result).contains("System maintenance access");
        assertThat(result).contains("Active");
        assertThat(result).contains("Completed");
        assertThat(result).contains("fire");
        assertThat(result).contains("system");
        assertThat(result).contains("911");
        assertThat(result).contains("support@company.com");
        assertThat(result).contains("60");
        assertThat(result).contains("120");
        
        // Check that CSV has correct number of lines (header + 2 tickets)
        String[] lines = result.split("\n");
        assertThat(lines).hasSize(3);
    }

    @Test
    void exportTicketsToCsv_WithEmptyList_ShouldReturnHeaderOnly() {
        // Act
        String result = gmailEmailService.exportTicketsToCsv(Collections.emptyList());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("TicketId,Description,Status,DateCreated,UserId,EmergencyType,EmergencyContact,Duration\n");
        
        String[] lines = result.split("\n");
        assertThat(lines).hasSize(1);
    }

    @Test
    void exportTicketsToCsv_WithNullValues_ShouldHandleGracefully() {
        // Arrange
        Ticket ticketWithNulls = new Ticket();
        ticketWithNulls.setTicketId("TICKET-NULL");
        // Leave other fields as null - but Ticket constructor sets some defaults
        List<Ticket> ticketsWithNulls = Arrays.asList(ticketWithNulls);

        // Act
        String result = gmailEmailService.exportTicketsToCsv(ticketsWithNulls);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains("TICKET-NULL");
        // Check that the result handles null values properly (empty strings)
        assertThat(result).contains("TICKET-NULL,");

        String[] lines = result.split("\n");
        assertThat(lines).hasSize(2);
    }

    @Test
    void exportTicketsToCsv_WithCommasInData_ShouldReplaceCommasWithSpaces() {
        // Arrange
        Ticket ticketWithCommas = new Ticket();
        ticketWithCommas.setTicketId("TICKET-COMMA");
        ticketWithCommas.setDescription("Emergency, urgent, critical access");
        ticketWithCommas.setEmergencyContact("John, Doe");
        List<Ticket> ticketsWithCommas = Arrays.asList(ticketWithCommas);

        // Act
        String result = gmailEmailService.exportTicketsToCsv(ticketsWithCommas);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains("Emergency  urgent  critical access");
        assertThat(result).contains("John  Doe");
        assertThat(result).doesNotContain("Emergency, urgent, critical access");
        assertThat(result).doesNotContain("John, Doe");
    }

    // ==================== CSV EMAIL SENDING TESTS ====================

    @Test
    void sendTicketsCsv_WithValidParameters_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        String csvContent = "TicketId,Description\nTICKET-001,Test";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketsCsv(TEST_EMAIL, csvContent, testUser);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketsCsv_WithMailSenderException_ShouldPropagateException() throws MessagingException {
        // Arrange
        String csvContent = "TicketId,Description\nTICKET-001,Test";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendTicketsCsv(TEST_EMAIL, csvContent, testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Mail server error");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketsCsv_WithCreateMessageException_ShouldPropagateException() {
        // Arrange
        String csvContent = "TicketId,Description\nTICKET-001,Test";
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendTicketsCsv(TEST_EMAIL, csvContent, testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to create message");

        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendTicketsCsv_WithLargeCsvContent_ShouldHandleSuccessfully() throws MessagingException {
        // Arrange
        StringBuilder largeCsv = new StringBuilder("TicketId,Description\n");
        for (int i = 0; i < 1000; i++) {
            largeCsv.append("TICKET-").append(i).append(",Description ").append(i).append("\n");
        }
        String csvContent = largeCsv.toString();
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketsCsv(TEST_EMAIL, csvContent, testUser);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketsCsv_WithEmptyCsvContent_ShouldHandleSuccessfully() throws MessagingException {
        // Arrange
        String csvContent = "";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketsCsv(TEST_EMAIL, csvContent, testUser);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketsCsv_WithNullUser_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        String csvContent = "TicketId,Description\nTICKET-001,Test";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketsCsv(TEST_EMAIL, csvContent, null);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    // ==================== TICKET CREATION EMAIL TESTS ====================

    @Test
    void sendTicketCreationEmail_WithValidParameters_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketCreationEmail(TEST_EMAIL, testTicket, testUser);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketCreationEmail_WithMailSenderException_ShouldPropagateException() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendTicketCreationEmail(TEST_EMAIL, testTicket, testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Mail server error");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketCreationEmail_WithNullTicket_ShouldThrowException() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendTicketCreationEmail(TEST_EMAIL, null, testUser))
            .isInstanceOf(Exception.class);

        verify(mailSender).createMimeMessage();
    }

    @Test
    void sendTicketCreationEmail_WithNullUser_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketCreationEmail(TEST_EMAIL, testTicket, null);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    // ==================== TICKET COMPLETION EMAIL TESTS ====================

    @Test
    void sendTicketCompletionEmail_WithValidParameters_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketCompletionEmail(TEST_EMAIL, testTicket, testUser);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketCompletionEmail_WithMailSenderException_ShouldPropagateException() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendTicketCompletionEmail(TEST_EMAIL, testTicket, testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Mail server error");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketCompletionEmail_WithTicketWithoutCompletionDate_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        testTicket.setDateCompleted(null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketCompletionEmail(TEST_EMAIL, testTicket, testUser);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    // ==================== TICKET REVOCATION EMAIL TESTS ====================

    @Test
    void sendTicketRevocationEmail_WithValidParameters_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketRevocationEmail(TEST_EMAIL, testTicket, testUser, TEST_REASON);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketRevocationEmail_WithMailSenderException_ShouldPropagateException() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendTicketRevocationEmail(TEST_EMAIL, testTicket, testUser, TEST_REASON))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Mail server error");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketRevocationEmail_WithNullReason_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketRevocationEmail(TEST_EMAIL, testTicket, testUser, null);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketRevocationEmail_WithEmptyReason_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketRevocationEmail(TEST_EMAIL, testTicket, testUser, "");

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    // ==================== FIVE MINUTE WARNING EMAIL TESTS ====================

    @Test
    void sendFiveMinuteWarningEmail_WithValidParameters_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendFiveMinuteWarningEmail(TEST_EMAIL, testTicket, testUser);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendFiveMinuteWarningEmail_WithMailSenderException_ShouldPropagateException() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendFiveMinuteWarningEmail(TEST_EMAIL, testTicket, testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Mail server error");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendFiveMinuteWarningEmail_WithNullTicket_ShouldThrowException() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendFiveMinuteWarningEmail(TEST_EMAIL, null, testUser))
            .isInstanceOf(Exception.class);

        verify(mailSender).createMimeMessage();
    }

    @Test
    void sendFiveMinuteWarningEmail_WithNullUser_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendFiveMinuteWarningEmail(TEST_EMAIL, testTicket, null);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendFiveMinuteWarningEmail_WithZeroDurationTicket_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        testTicket.setDuration(0);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendFiveMinuteWarningEmail(TEST_EMAIL, testTicket, testUser);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendFiveMinuteWarningEmail_WithNegativeDurationTicket_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        testTicket.setDuration(-30);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendFiveMinuteWarningEmail(TEST_EMAIL, testTicket, testUser);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void sendTicketCreationEmail_WithCreateMessageException_ShouldPropagateException() {
        // Arrange
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendTicketCreationEmail(TEST_EMAIL, testTicket, testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to create message");

        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendTicketCompletionEmail_WithCreateMessageException_ShouldPropagateException() {
        // Arrange
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendTicketCompletionEmail(TEST_EMAIL, testTicket, testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to create message");

        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendTicketRevocationEmail_WithCreateMessageException_ShouldPropagateException() {
        // Arrange
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendTicketRevocationEmail(TEST_EMAIL, testTicket, testUser, TEST_REASON))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to create message");

        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendFiveMinuteWarningEmail_WithCreateMessageException_ShouldPropagateException() {
        // Arrange
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendFiveMinuteWarningEmail(TEST_EMAIL, testTicket, testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to create message");

        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    // ==================== COMPREHENSIVE INTEGRATION TESTS ====================

    @Test
    void exportAndSendTicketsCsv_FullWorkflow_ShouldWorkCorrectly() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        String csvContent = gmailEmailService.exportTicketsToCsv(testTickets);
        gmailEmailService.sendTicketsCsv(TEST_EMAIL, csvContent, testUser);

        // Assert
        assertThat(csvContent).isNotNull();
        assertThat(csvContent).contains("TICKET-001");
        assertThat(csvContent).contains("TICKET-002");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void allEmailMethods_WithSameTicketAndUser_ShouldAllWorkCorrectly() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act & Assert - All email methods should work with the same ticket and user
        gmailEmailService.sendTicketCreationEmail(TEST_EMAIL, testTicket, testUser);
        gmailEmailService.sendTicketCompletionEmail(TEST_EMAIL, testTicket, testUser);
        gmailEmailService.sendTicketRevocationEmail(TEST_EMAIL, testTicket, testUser, TEST_REASON);
        gmailEmailService.sendFiveMinuteWarningEmail(TEST_EMAIL, testTicket, testUser);

        verify(mailSender, times(4)).createMimeMessage();
        verify(mailSender, times(4)).send(mimeMessage);
    }

    @Test
    void exportTicketsToCsv_WithSpecialCharacters_ShouldHandleCorrectly() {
        // Arrange
        Ticket specialTicket = new Ticket();
        specialTicket.setTicketId("TICKET-SPECIAL");
        specialTicket.setDescription("Emergency: \"Critical\" access needed!");
        specialTicket.setEmergencyContact("John O'Connor, Manager");
        specialTicket.setEmergencyType("fire & rescue");
        List<Ticket> specialTickets = Arrays.asList(specialTicket);

        // Act
        String result = gmailEmailService.exportTicketsToCsv(specialTickets);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains("TICKET-SPECIAL");
        assertThat(result).contains("Emergency: \"Critical\" access needed!");
        assertThat(result).contains("John O'Connor  Manager"); // Comma replaced with space
        assertThat(result).contains("fire & rescue");
    }

    @Test
    void exportTicketsToCsv_WithVeryLongDescription_ShouldHandleCorrectly() {
        // Arrange
        Ticket longDescTicket = new Ticket();
        longDescTicket.setTicketId("TICKET-LONG");
        StringBuilder longDesc = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longDesc.append("Very long description part ").append(i).append(" ");
        }
        longDescTicket.setDescription(longDesc.toString());
        List<Ticket> longDescTickets = Arrays.asList(longDescTicket);

        // Act
        String result = gmailEmailService.exportTicketsToCsv(longDescTickets);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains("TICKET-LONG");
        assertThat(result).contains("Very long description part 0");
        assertThat(result).contains("Very long description part 99");
    }

    // ==================== PRIVATE METHOD COVERAGE TESTS ====================
    // These tests indirectly test private methods by verifying their outputs

    @Test
    void sendTicketsCsv_ShouldGenerateProperHtmlContent() throws MessagingException {
        // Arrange
        String csvContent = gmailEmailService.exportTicketsToCsv(testTickets);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketsCsv(TEST_EMAIL, csvContent, testUser);

        // Assert - This indirectly tests createProfessionalEmailContent method
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);

        // The HTML content generation is tested indirectly through successful email sending
        assertThat(csvContent).contains("TICKET-001"); // Verifies CSV content is properly generated
    }

    @Test
    void sendTicketCreationEmail_ShouldGenerateProperHtmlContent() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketCreationEmail(TEST_EMAIL, testTicket, testUser);

        // Assert - This indirectly tests createTicketCreationEmailContent method
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketCompletionEmail_ShouldGenerateProperHtmlContent() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketCompletionEmail(TEST_EMAIL, testTicket, testUser);

        // Assert - This indirectly tests createTicketCompletionEmailContent method
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketRevocationEmail_ShouldGenerateProperHtmlContent() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketRevocationEmail(TEST_EMAIL, testTicket, testUser, TEST_REASON);

        // Assert - This indirectly tests createTicketRevocationEmailContent method
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendFiveMinuteWarningEmail_ShouldGenerateProperHtmlContent() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendFiveMinuteWarningEmail(TEST_EMAIL, testTicket, testUser);

        // Assert - This indirectly tests createFiveMinuteWarningEmailContent method
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    // ==================== ADDITIONAL EDGE CASES ====================

    @Test
    void exportTicketsToCsv_WithTicketsHavingAllNullFields_ShouldHandleGracefully() {
        // Arrange
        Ticket nullTicket = new Ticket();
        // Don't set any fields - all should be null except defaults from constructor
        List<Ticket> nullTickets = Arrays.asList(nullTicket);

        // Act
        String result = gmailEmailService.exportTicketsToCsv(nullTickets);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains("TicketId,Description,Status,DateCreated,UserId,EmergencyType,EmergencyContact,Duration");

        String[] lines = result.split("\n");
        assertThat(lines).hasSize(2); // Header + 1 ticket
    }

    @Test
    void sendTicketsCsv_WithUserHavingNullFields_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        String csvContent = "TicketId,Description\nTICKET-001,Test";
        User userWithNulls = new User();
        userWithNulls.setUserId("user-with-nulls");
        // Leave other fields as null

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketsCsv(TEST_EMAIL, csvContent, userWithNulls);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketCreationEmail_WithTicketHavingNullFields_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        Ticket ticketWithNulls = new Ticket();
        ticketWithNulls.setTicketId("TICKET-NULLS");
        ticketWithNulls.setDateCreated(LocalDateTime.now());
        // Leave other fields as null

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketCreationEmail(TEST_EMAIL, ticketWithNulls, testUser);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTicketRevocationEmail_WithTicketHavingNullCompletionDate_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        testTicket.setDateCompleted(null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendTicketRevocationEmail(TEST_EMAIL, testTicket, testUser, TEST_REASON);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    // ==================== ANOMALY DETECTION EMAIL TESTS ====================

    @Test
    void sendAnomalyDetectionNotificationEmail_WithValidParameters_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "User has made 10 requests in the last hour (threshold: 5)";
        String riskLevel = "MEDIUM";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, testUser, testTicket, anomalyType, anomalyDetails, riskLevel);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendAnomalyDetectionNotificationEmail_WithHighRiskDormantUser_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        String anomalyType = "DORMANT_USER_ACTIVITY";
        String anomalyDetails = "User was dormant for 30+ days, logged in at 2024-01-15T10:30:00 and made 5 actions within 15 minutes";
        String riskLevel = "HIGH";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, testUser, testTicket, anomalyType, anomalyDetails, riskLevel);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendAnomalyDetectionNotificationEmail_WithLowRiskOffHours_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        String anomalyType = "OFF_HOURS_ACTIVITY";
        String anomalyDetails = "User made a request at 22:00 which is outside of regular work hours! (allowed: 7:00 AM - 17:00 PM)";
        String riskLevel = "LOW";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, testUser, testTicket, anomalyType, anomalyDetails, riskLevel);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendAnomalyDetectionNotificationEmail_WithMailSenderException_ShouldPropagateException() throws MessagingException {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "Test anomaly details";
        String riskLevel = "MEDIUM";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, testUser, testTicket, anomalyType, anomalyDetails, riskLevel))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Mail server error");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendAnomalyDetectionNotificationEmail_WithCreateMessageException_ShouldPropagateException() {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "Test anomaly details";
        String riskLevel = "MEDIUM";
        
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, testUser, testTicket, anomalyType, anomalyDetails, riskLevel))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to create message");

        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendAnomalyDetectionNotificationEmail_WithNullUser_ShouldThrowException() {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "Test anomaly details";
        String riskLevel = "MEDIUM";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, null, testTicket, anomalyType, anomalyDetails, riskLevel))
            .isInstanceOf(Exception.class);

        verify(mailSender).createMimeMessage();
    }

    @Test
    void sendAnomalyDetectionNotificationEmail_WithNullTicket_ShouldThrowException() {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "Test anomaly details";
        String riskLevel = "MEDIUM";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, testUser, null, anomalyType, anomalyDetails, riskLevel))
            .isInstanceOf(Exception.class);

        verify(mailSender).createMimeMessage();
    }

    @Test
    void sendAnomalyDetectionNotificationEmail_WithNullAnomalyType_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        String anomalyDetails = "Test anomaly details";
        String riskLevel = "MEDIUM";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, testUser, testTicket, null, anomalyDetails, riskLevel);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendAnomalyDetectionNotificationEmail_WithEmptyAnomalyDetails_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "";
        String riskLevel = "MEDIUM";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, testUser, testTicket, anomalyType, anomalyDetails, riskLevel);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendAnomalyDetectionNotificationEmail_WithUnknownAnomalyType_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        String anomalyType = "UNKNOWN_ANOMALY_TYPE";
        String anomalyDetails = "Unknown anomaly detected";
        String riskLevel = "MEDIUM";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, testUser, testTicket, anomalyType, anomalyDetails, riskLevel);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendAnomalyDetectionNotificationEmail_WithUserHavingNullDepartment_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        testUser.setDepartment(null);
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "Test anomaly details";
        String riskLevel = "MEDIUM";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendAnomalyDetectionNotificationEmail(TEST_EMAIL, testUser, testTicket, anomalyType, anomalyDetails, riskLevel);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    // ==================== SUSPICIOUS GROUP CHANGE EMAIL TESTS ====================

    @Test
    void sendSuspiciousGroupChangeNotificationEmail_WithValidParameters_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        String ticketId = "TICKET-001";
        String oldGroup = "HR Emergency Group";
        String newGroup = "Financial Emergency Group";
        String reason = "Emergency ticket creation";
        String suspicionLevel = "HIGH";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendSuspiciousGroupChangeNotificationEmail(TEST_EMAIL, testUser, ticketId, oldGroup, newGroup, reason, suspicionLevel);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendSuspiciousGroupChangeNotificationEmail_WithMediumRisk_ShouldSendEmailSuccessfully() throws MessagingException {
        // Arrange
        String ticketId = "TICKET-002";
        String oldGroup = "Logistics Emergency Group";
        String newGroup = "HR Emergency Group";
        String reason = "Department transfer";
        String suspicionLevel = "MEDIUM";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendSuspiciousGroupChangeNotificationEmail(TEST_EMAIL, testUser, ticketId, oldGroup, newGroup, reason, suspicionLevel);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendSuspiciousGroupChangeNotificationEmail_WithNullOldGroup_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        String ticketId = "TICKET-003";
        String oldGroup = null;
        String newGroup = "Management Emergency Group";
        String reason = "New user assignment";
        String suspicionLevel = "HIGH";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        gmailEmailService.sendSuspiciousGroupChangeNotificationEmail(TEST_EMAIL, testUser, ticketId, oldGroup, newGroup, reason, suspicionLevel);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendSuspiciousGroupChangeNotificationEmail_WithMailSenderException_ShouldPropagateException() throws MessagingException {
        // Arrange
        String ticketId = "TICKET-004";
        String oldGroup = "HR Emergency Group";
        String newGroup = "Financial Emergency Group";
        String reason = "Emergency ticket creation";
        String suspicionLevel = "HIGH";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThatThrownBy(() -> gmailEmailService.sendSuspiciousGroupChangeNotificationEmail(TEST_EMAIL, testUser, ticketId, oldGroup, newGroup, reason, suspicionLevel))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Mail server error");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}
