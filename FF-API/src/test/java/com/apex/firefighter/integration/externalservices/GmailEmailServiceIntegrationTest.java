package com.apex.firefighter.integration.externalservices;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.service.GmailEmailService;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.BodyPart;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class GmailEmailServiceIntegrationTest {

    static GreenMail greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));

    @Autowired
    private GmailEmailService gmailEmailService;

    @BeforeEach
    void setUp() {
    greenMail.start();
    }

    @AfterEach
    void tearDown() {
        greenMail.stop();
    }

    @Test
    void sendTicketsCsv_shouldSendEmailWithCsvAttachment() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("integration-user");
        user.setEmail("recipient@example.com");

        Ticket t1 = new Ticket("T-1", "Test ticket one", "user-1", "Fire", "+27123456789", 30);
        Ticket t2 = new Ticket("T-2", "Test ticket two", "user-2", "Medical", "+27111111111", 45);

        List<Ticket> tickets = List.of(t1, t2);
        String csv = gmailEmailService.exportTicketsToCsv(tickets);

        // Act
        gmailEmailService.sendTicketsCsv("recipient@example.com", csv, user);

        // Assert: check received messages
        MimeMessage[] received = greenMail.getReceivedMessages();
        assertThat(received).hasSize(1);

        MimeMessage msg = received[0];
        assertThat(msg.getSubject()).contains("Tickets Export Report");
        assertThat(msg.getAllRecipients()).isNotEmpty();

        Object content = msg.getContent();
        assertThat(content).isInstanceOf(MimeMultipart.class);

        MimeMultipart multipart = (MimeMultipart) content;
        int partCount = multipart.getCount();
        // Expect at least two parts: HTML body + attachment
        assertThat(partCount).isGreaterThanOrEqualTo(2);

        String htmlBody = null;
        String attachmentName = null;
        String attachmentContent = null;

        for (int i = 0; i < partCount; i++) {
            BodyPart part = multipart.getBodyPart(i);
            String disp = part.getDisposition();
            if (disp == null) {
                // likely the html body
                Object partContent = part.getContent();
                if (partContent instanceof String) {
                    htmlBody = (String) partContent;
                }
            } else if (disp.equalsIgnoreCase(BodyPart.ATTACHMENT)) {
                attachmentName = part.getFileName();
                byte[] bytes = part.getInputStream().readAllBytes();
                attachmentContent = new String(bytes, StandardCharsets.UTF_8);
            }
        }

        assertThat(htmlBody).isNotNull();
        assertThat(htmlBody).contains("FireFighter Platform");
        assertThat(attachmentName).isEqualTo("firefighter_tickets_export.csv");
        assertThat(attachmentContent).isEqualTo(csv);
    }
}
