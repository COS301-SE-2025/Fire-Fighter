package com.apex.firefighter.integration.externalservices;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.service.GmailEmailService;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest
@ActiveProfiles("test")
public class GmailEmailServiceIntegrationTest {

    @Autowired
    private GmailEmailService gmailEmailService;

    @Autowired
    private CapturingMailSender capturingMailSender;

    @BeforeEach
    void setUp() {
        // Clear any messages from previous tests
        capturingMailSender.getMessages().clear();
    }

    @Test
    void sendTicketsCsv_shouldSendEmailWithCsvAttachment() throws Exception {
        User user = new User();
        user.setUsername("integration-user");
        user.setEmail("recipient@example.com");

        Ticket t1 = new Ticket("T-1", "Test ticket one", "Active", "user-1", "Fire", "+27123456789");
        t1.setDuration(60); // Set duration to prevent NullPointerException
        Ticket t2 = new Ticket("T-2", "Test ticket two", "Active", "user-2", "Medical", "+27111111111");
        t2.setDuration(90); // Set duration to prevent NullPointerException

        List<Ticket> tickets = List.of(t1, t2);
        String csv = gmailEmailService.exportTicketsToCsv(tickets);

        // Act
        gmailEmailService.sendTicketsCsv("recipient@example.com", csv, user);

        // Assert - exactly one message captured
        List<MimeMessage> msgs = capturingMailSender.getMessages();
        assertThat(msgs).hasSize(1);

        MimeMessage msg = msgs.get(0);
        assertThat(msg.getSubject()).contains("Tickets Export Report");
        Object content = msg.getContent();
        assertThat(content).isInstanceOf(MimeMultipart.class);

        MimeMultipart multipart = (MimeMultipart) content;
        int partCount = multipart.getCount();
        assertThat(partCount).isGreaterThanOrEqualTo(2);

        String htmlBody = null;
        String attachmentName = null;
        String attachmentContent = null;

        for (int i = 0; i < partCount; i++) {
            BodyPart part = multipart.getBodyPart(i);
            String disp = part.getDisposition();

            if (disp == null) {
                Object partContent = part.getContent();
                if (partContent instanceof String) {
                    htmlBody = (String) partContent;
                } else if (partContent instanceof MimeMultipart) {
                    // Handle nested multipart
                    MimeMultipart nestedMultipart = (MimeMultipart) partContent;
                    for (int j = 0; j < nestedMultipart.getCount(); j++) {
                        BodyPart nestedPart = nestedMultipart.getBodyPart(j);
                        Object nestedContent = nestedPart.getContent();
                        if (nestedContent instanceof String) {
                            htmlBody = (String) nestedContent;
                            break;
                        }
                    }
                }
            } else if (BodyPart.ATTACHMENT.equalsIgnoreCase(disp)) {
                attachmentName = part.getFileName();
                byte[] bytes = part.getInputStream().readAllBytes();
                attachmentContent = new String(bytes, StandardCharsets.UTF_8);
            }
        }

        assertThat(htmlBody).isNotNull();
        assertThat(htmlBody).contains("CSV");
        assertThat(attachmentName).isEqualTo("firefighter_tickets_export.csv");
        assertThat(attachmentContent).isEqualTo(csv);
    }

    @Test
    void sendTicketCreationEmail_shouldSendHtmlEmail() throws Exception {
        User user = new User();
        user.setUsername("test-user");
        user.setEmail("user@example.com");

        Ticket ticket = new Ticket("T-123", "Emergency ticket", "Active", "user-1", "Fire", "+27123456789");
        ticket.setDuration(60); // Set duration to prevent NullPointerException

        // Act
        gmailEmailService.sendTicketCreationEmail("recipient@example.com", ticket, user);

        // Assert
        List<MimeMessage> msgs = capturingMailSender.getMessages();
        assertThat(msgs).hasSize(1);

        MimeMessage msg = msgs.get(0);
        assertThat(msg.getSubject()).contains("New Ticket Created: T-123");

        Object content = msg.getContent();
        String htmlContent = extractHtmlContent(content);
        assertThat(htmlContent).contains("FireFighter Platform");
        assertThat(htmlContent).contains("T-123");
        assertThat(htmlContent).contains("Fire");
    }

    @Test
    void sendTicketCompletionEmail_shouldSendHtmlEmail() throws Exception {
        User user = new User();
        user.setUsername("test-user");
        user.setEmail("user@example.com");

        Ticket ticket = new Ticket("T-456", "Completed ticket", "Completed", "user-2", "Medical", "+27111111111");

        // Act
        gmailEmailService.sendTicketCompletionEmail("recipient@example.com", ticket, user);

        // Assert
        List<MimeMessage> msgs = capturingMailSender.getMessages();
        assertThat(msgs).hasSize(1);

        MimeMessage msg = msgs.get(0);
        assertThat(msg.getSubject()).contains("Ticket Completed: T-456");

        Object content = msg.getContent();
        String htmlContent = extractHtmlContent(content);
        assertThat(htmlContent).contains("FireFighter Platform");
        assertThat(htmlContent).contains("T-456");
        assertThat(htmlContent).contains("completed");
        assertThat(htmlContent).contains("Medical");
    }

    @Test
    void sendTicketRevocationEmail_shouldSendHtmlEmail() throws Exception {
        User user = new User();
        user.setUsername("test-user");
        user.setEmail("user@example.com");

        Ticket ticket = new Ticket("T-789", "Revoked ticket", "Revoked", "user-3", "Rescue", "+27222222222");
        String reason = "Policy violation";

        // Act
        gmailEmailService.sendTicketRevocationEmail("recipient@example.com", ticket, user, reason);

        // Assert
        List<MimeMessage> msgs = capturingMailSender.getMessages();
        assertThat(msgs).hasSize(1);

        MimeMessage msg = msgs.get(0);
        assertThat(msg.getSubject()).contains("Ticket Revoked: T-789");

        Object content = msg.getContent();
        String htmlContent = extractHtmlContent(content);
        assertThat(htmlContent).contains("FireFighter Platform");
        assertThat(htmlContent).contains("T-789");
        assertThat(htmlContent).contains("revoked");
        assertThat(htmlContent).contains("Policy violation");
        assertThat(htmlContent).contains("Rescue");
    }

    @Test
    void sendFiveMinuteWarningEmail_shouldSendHtmlEmail() throws Exception {
        User user = new User();
        user.setUsername("test-user");
        user.setEmail("user@example.com");

        Ticket ticket = new Ticket("T-999", "Expiring ticket", "Active", "user-4", "Fire", "+27333333333");
        ticket.setDuration(60); // Set duration to prevent NullPointerException

        // Act
        gmailEmailService.sendFiveMinuteWarningEmail("recipient@example.com", ticket, user);

        // Assert
        List<MimeMessage> msgs = capturingMailSender.getMessages();
        assertThat(msgs).hasSize(1);

        MimeMessage msg = msgs.get(0);
        assertThat(msg.getSubject()).contains("Ticket Expiring Soon: T-999");

        Object content = msg.getContent();
        String htmlContent = extractHtmlContent(content);
        assertThat(htmlContent).contains("FireFighter Platform");
        assertThat(htmlContent).contains("T-999");
        assertThat(htmlContent).contains("expire");
        assertThat(htmlContent).contains("Fire");
    }

    /**
     * Helper method to extract HTML content from either String or MimeMultipart
     */
    private String extractHtmlContent(Object content) throws Exception {
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.getContentType().toLowerCase().contains("text/html")) {
                    Object partContent = part.getContent();
                    if (partContent instanceof String) {
                        return (String) partContent;
                    } else {
                        return extractHtmlContent(partContent); // Recursive call
                    }
                }
            }
            // If no HTML part found, return the first text part
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.getContentType().toLowerCase().contains("text")) {
                    Object partContent = part.getContent();
                    if (partContent instanceof String) {
                        return (String) partContent;
                    } else {
                        return extractHtmlContent(partContent); // Recursive call
                    }
                }
            }
        }
        return content.toString();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CapturingMailSender capturingMailSender() {
            return new CapturingMailSender();
        }

        @Bean
        @Primary
        public JavaMailSender javaMailSender(CapturingMailSender capturingMailSender) {
            return capturingMailSender;
        }
    }

    // Simple in-memory JavaMailSender that captures MimeMessages for assertions
    public static class CapturingMailSender implements JavaMailSender {
        private final List<MimeMessage> messages = new ArrayList<>();

        @Override
        public MimeMessage createMimeMessage() {
            try {
                return new jakarta.mail.internet.MimeMessage((jakarta.mail.Session) null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public MimeMessage createMimeMessage(java.io.InputStream contentStream) {
            try {
                return new jakarta.mail.internet.MimeMessage(null, contentStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void send(MimeMessage mimeMessage) {
            messages.add(mimeMessage);
        }

        @Override
        public void send(MimeMessage... mimeMessages) {
            for (MimeMessage m : mimeMessages) messages.add(m);
        }

        @Override
        public void send(MimeMessagePreparator mimeMessagePreparator) {
            try {
                MimeMessage message = createMimeMessage();
                mimeMessagePreparator.prepare(message);
                send(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void send(MimeMessagePreparator... mimeMessagePreparators) {
            for (MimeMessagePreparator p : mimeMessagePreparators) send(p);
        }

        public List<MimeMessage> getMessages() {
            return messages;
        }

        // The remaining JavaMailSender methods are not used by GmailEmailService and can be no-ops
        @Override public void send(org.springframework.mail.SimpleMailMessage simpleMessage) { }
        @Override public void send(org.springframework.mail.SimpleMailMessage... simpleMessages) { }
    }
}
