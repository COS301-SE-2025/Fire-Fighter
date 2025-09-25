package com.apex.firefighter.service;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class GmailEmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String exportTicketsToCsv(List<Ticket> tickets) {
        StringBuilder sb = new StringBuilder();
        sb.append("TicketId,Description,Status,DateCreated,UserId,EmergencyType,EmergencyContact,Duration\n");
        for (Ticket ticket : tickets) {
            sb.append(safe(ticket.getTicketId())).append(",")
              .append(safe(ticket.getDescription())).append(",")
              .append(safe(ticket.getStatus())).append(",")
              .append(safe(ticket.getDateCreated())).append(",")
              .append(safe(ticket.getUserId())).append(",")
              .append(safe(ticket.getEmergencyType())).append(",")
              .append(safe(ticket.getEmergencyContact())).append(",")
              .append(safe(ticket.getDuration())).append("\n");
        }
        return sb.toString();
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString().replaceAll(",", " ");
    }

    public void sendTicketsCsv(String recipientEmail, String csvContent, User user) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("FireFighter Platform - Tickets Export Report");

            // Create professional HTML email content
            String htmlContent = createProfessionalEmailContent(csvContent, user);
            helper.setText(htmlContent, true); // true indicates HTML content

            // Add CSV attachment with proper content type and encoding
            ByteArrayResource csvResource = new ByteArrayResource(csvContent.getBytes(StandardCharsets.UTF_8));
            helper.addAttachment("firefighter_tickets_export.csv", csvResource, "text/csv");

            mailSender.send(message);
            System.out.println("Email sent successfully to " + recipientEmail + " (" + csvContent.length() + " bytes CSV)");

        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates professional HTML email content for ticket export
     */
    private String createProfessionalEmailContent(String csvContent, User user) {
        // Count tickets in CSV (subtract 1 for header row)
        int ticketCount = csvContent.split("\n").length - 1;
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
        double fileSizeKB = csvContent.length() / 1024.0;

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>FireFighter Platform - Tickets Export</title>");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("    line-height: 1.6;");
        html.append("    color: #333;");
        html.append("    max-width: 600px;");
        html.append("    margin: 0 auto;");
        html.append("    padding: 20px;");
        html.append("    background-color: #f8f9fa;");
        html.append("}");
        html.append(".email-container {");
        html.append("    background-color: #ffffff;");
        html.append("    border-radius: 12px;");
        html.append("    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);");
        html.append("    overflow: hidden;");
        html.append("}");
        html.append(".header {");
        html.append("    background: linear-gradient(135deg, #06437d 0%, #04365a 100%);");
        html.append("    color: white;");
        html.append("    padding: 30px;");
        html.append("    text-align: center;");
        html.append("    border-radius: 12px 12px 0 0;");
        html.append("}");
        html.append(".header h1 {");
        html.append("    margin: 0;");
        html.append("    font-size: 28px;");
        html.append("    font-weight: 600;");
        html.append("}");
        html.append(".header .subtitle {");
        html.append("    margin: 8px 0 0 0;");
        html.append("    font-size: 16px;");
        html.append("    opacity: 0.9;");
        html.append("}");
        html.append(".content {");
        html.append("    padding: 25px 30px 40px 30px;");
        html.append("}");
        html.append(".greeting {");
        html.append("    font-size: 18px;");
        html.append("    margin-bottom: 20px;");
        html.append("    color: #2c3e50;");
        html.append("}");
        html.append(".info-box {");
        html.append("    background-color: #f8f9fa;");
        html.append("    border-left: 4px solid #06437d;");
        html.append("    padding: 20px;");
        html.append("    margin: 25px 0;");
        html.append("    border-radius: 0 8px 8px 0;");
        html.append("}");
        html.append(".info-item {");
        html.append("    display: flex;");
        html.append("    justify-content: space-between;");
        html.append("    margin-bottom: 12px;");
        html.append("    padding-bottom: 8px;");
        html.append("    border-bottom: 1px solid #e9ecef;");
        html.append("}");
        html.append(".info-item:last-child {");
        html.append("    margin-bottom: 0;");
        html.append("    border-bottom: none;");
        html.append("}");
        html.append(".info-label {");
        html.append("    font-weight: 600;");
        html.append("    color: #495057;");
        html.append("}");
        html.append(".info-value {");
        html.append("    color: #06437d;");
        html.append("    font-weight: 500;");
        html.append("}");

        html.append(".footer {");
        html.append("    background-color: #f8f9fa;");
        html.append("    padding: 25px 30px;");
        html.append("    text-align: center;");
        html.append("    border-top: 1px solid #e9ecef;");
        html.append("}");
        html.append(".footer p {");
        html.append("    margin: 5px 0;");
        html.append("    color: #6c757d;");
        html.append("    font-size: 14px;");
        html.append("}");
        html.append(".security-notice {");
        html.append("    background-color: #fff3cd;");
        html.append("    border: 1px solid #ffeaa7;");
        html.append("    border-radius: 8px;");
        html.append("    padding: 15px;");
        html.append("    margin: 20px 0;");
        html.append("    font-size: 14px;");
        html.append("    color: #856404;");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"email-container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>FireFighter Platform</h1>");
        html.append("<p class=\"subtitle\">Emergency Response Management System</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");

        // Create personalized greeting
        String greeting = "Hello";
        if (user != null && user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            greeting = "Hello, " + user.getUsername();
        }

        html.append("<div class=\"greeting\">").append(greeting).append(",</div>");
        html.append("<p>Your requested tickets export has been successfully generated and is ready for download.</p>");
        html.append("<div class=\"info-box\">");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Export Generated: </span>");
        html.append("<span class=\"info-value\">").append(currentDateTime).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Total Tickets: </span>");
        html.append("<span class=\"info-value\">").append(ticketCount).append(" records</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">File Format: </span>");
        html.append("<span class=\"info-value\">CSV (Comma Separated Values)</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">File Size: </span>");
        html.append("<span class=\"info-value\">").append(String.format("%.2f KB", fileSizeKB)).append("</span>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class=\"security-notice\">");
        html.append("<strong>Security Notice:</strong> This export contains sensitive emergency response data. ");
        html.append("Please handle this information according to your organization's data protection policies ");
        html.append("and applicable privacy regulations.");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class=\"footer\">");
        html.append("<p><strong>FireFighter Emergency Response Platform</strong></p>");
        html.append("<p>Automated Export System | Generated on ").append(currentDateTime).append("</p>");
        html.append("<p style=\"font-size: 12px; margin-top: 15px;\">");
        html.append("This is an automated message. Please do not reply to this email.");
        html.append("</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Send ticket creation notification email
     */
    public void sendTicketCreationEmail(String recipientEmail, Ticket ticket, User user) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("FireFighter Platform - New Ticket Created: " + ticket.getTicketId());

            String htmlContent = createTicketCreationEmailContent(ticket, user);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Ticket creation email sent successfully to " + recipientEmail + " for ticket " + ticket.getTicketId());

        } catch (Exception e) {
            System.err.println("Ticket creation email failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Send ticket completion notification email
     */
    public void sendTicketCompletionEmail(String recipientEmail, Ticket ticket, User user) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("FireFighter Platform - Ticket Completed: " + ticket.getTicketId());

            String htmlContent = createTicketCompletionEmailContent(ticket, user);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Ticket completion email sent successfully to " + recipientEmail + " for ticket " + ticket.getTicketId());

        } catch (Exception e) {
            System.err.println("Ticket completion email failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Send ticket revocation notification email
     */
    public void sendTicketRevocationEmail(String recipientEmail, Ticket ticket, User user, String reason) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("FireFighter Platform - Ticket Revoked: " + ticket.getTicketId());

            String htmlContent = createTicketRevocationEmailContent(ticket, user, reason);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Ticket revocation email sent successfully to " + recipientEmail + " for ticket " + ticket.getTicketId());

        } catch (Exception e) {
            System.err.println("Ticket revocation email failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Send five-minute warning notification email
     */
    public void sendFiveMinuteWarningEmail(String recipientEmail, Ticket ticket, User user) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("FireFighter Platform - Ticket Expiring Soon: " + ticket.getTicketId());

            String htmlContent = createFiveMinuteWarningEmailContent(ticket, user);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Five-minute warning email sent successfully to " + recipientEmail + " for ticket " + ticket.getTicketId());

        } catch (Exception e) {
            System.err.println("Five-minute warning email failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates professional HTML email content for ticket creation notification
     */
    private String createTicketCreationEmailContent(Ticket ticket, User user) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
        String ticketDateTime = ticket.getDateCreated().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));

        StringBuilder html = new StringBuilder();
        html.append(getEmailHeader("Ticket Created"));
        html.append("<div class=\"email-container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>FireFighter Platform</h1>");
        html.append("<p class=\"subtitle\">Emergency Response Management System</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");

        String greeting = "Hello";
        if (user != null && user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            greeting = "Hello, " + user.getUsername();
        }

        html.append("<div class=\"greeting\">").append(greeting).append(",</div>");
        html.append("<p>A new emergency ticket has been created and is now active in the system.</p>");
        html.append("<div class=\"info-box\">");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Ticket ID: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getTicketId()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Emergency Type: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getEmergencyType()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Created: </span>");
        html.append("<span class=\"info-value\">").append(ticketDateTime).append("</span>");
        html.append("</div>");
        if (ticket.getDuration() != null) {
            html.append("<div class=\"info-item\">");
            html.append("<span class=\"info-label\">Duration: </span>");
            html.append("<span class=\"info-value\">").append(ticket.getDuration()).append(" minutes</span>");
            html.append("</div>");
        }
        if (ticket.getDescription() != null && !ticket.getDescription().trim().isEmpty()) {
            html.append("<div class=\"info-item\">");
            html.append("<span class=\"info-label\">Description: </span>");
            html.append("<span class=\"info-value\">").append(ticket.getDescription()).append("</span>");
            html.append("</div>");
        }
        html.append("</div>");
        html.append("<div class=\"security-notice\">");
        html.append("<strong>Important:</strong> This ticket is now active and emergency response procedures are in effect. ");
        html.append("Please ensure you have the necessary access and follow all safety protocols.");
        html.append("</div>");
        html.append("</div>");
        html.append(getEmailFooter(currentDateTime));
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Creates professional HTML email content for ticket completion notification
     */
    private String createTicketCompletionEmailContent(Ticket ticket, User user) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
        String ticketDateTime = ticket.getDateCreated().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
        String completionDateTime = ticket.getDateCompleted() != null ?
            ticket.getDateCompleted().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm")) : currentDateTime;

        StringBuilder html = new StringBuilder();
        html.append(getEmailHeader("Ticket Completed"));
        html.append("<div class=\"email-container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>FireFighter Platform</h1>");
        html.append("<p class=\"subtitle\">Emergency Response Management System</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");

        String greeting = "Hello";
        if (user != null && user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            greeting = "Hello, " + user.getUsername();
        }

        html.append("<div class=\"greeting\">").append(greeting).append(",</div>");
        html.append("<p>Your emergency ticket has been completed and is now closed.</p>");
        html.append("<div class=\"info-box\">");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Ticket ID: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getTicketId()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Emergency Type: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getEmergencyType()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Created: </span>");
        html.append("<span class=\"info-value\">").append(ticketDateTime).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Completed: </span>");
        html.append("<span class=\"info-value\">").append(completionDateTime).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Status: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getStatus()).append("</span>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class=\"security-notice\">");
        html.append("<strong>Notice:</strong> This emergency response has been completed. ");
        html.append("If you have any questions or concerns about this ticket, please contact your system administrator.");
        html.append("</div>");
        html.append("</div>");
        html.append(getEmailFooter(currentDateTime));
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Creates professional HTML email content for ticket revocation notification
     */
    private String createTicketRevocationEmailContent(Ticket ticket, User user, String reason) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
        String ticketDateTime = ticket.getDateCreated().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
        String revocationDateTime = ticket.getDateCompleted() != null ?
            ticket.getDateCompleted().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm")) : currentDateTime;

        StringBuilder html = new StringBuilder();
        html.append(getEmailHeader("Ticket Revoked"));
        html.append("<div class=\"email-container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>FireFighter Platform</h1>");
        html.append("<p class=\"subtitle\">Emergency Response Management System</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");

        String greeting = "Hello";
        if (user != null && user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            greeting = "Hello, " + user.getUsername();
        }

        html.append("<div class=\"greeting\">").append(greeting).append(",</div>");
        html.append("<p>Your emergency ticket has been revoked by an administrator.</p>");
        html.append("<div class=\"info-box\">");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Ticket ID: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getTicketId()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Emergency Type: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getEmergencyType()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Created: </span>");
        html.append("<span class=\"info-value\">").append(ticketDateTime).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Revoked: </span>");
        html.append("<span class=\"info-value\">").append(revocationDateTime).append("</span>");
        html.append("</div>");
        if (reason != null && !reason.trim().isEmpty()) {
            html.append("<div class=\"info-item\">");
            html.append("<span class=\"info-label\">Reason: </span>");
            html.append("<span class=\"info-value\">").append(reason).append("</span>");
            html.append("</div>");
        }
        html.append("</div>");
        html.append("<div class=\"security-notice\">");
        html.append("<strong>Important:</strong> This ticket has been revoked and is no longer active. ");
        html.append("If you believe this was done in error or have questions, please contact your system administrator immediately.");
        html.append("</div>");
        html.append("</div>");
        html.append(getEmailFooter(currentDateTime));
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Creates professional HTML email content for five-minute warning notification
     */
    private String createFiveMinuteWarningEmailContent(Ticket ticket, User user) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
        String ticketDateTime = ticket.getDateCreated().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));

        // Use default duration of 60 minutes if duration is null
        int durationMinutes = ticket.getDuration() != null ? ticket.getDuration() : 60;
        LocalDateTime expirationTime = ticket.getDateCreated().plusMinutes(durationMinutes);
        String expirationDateTime = expirationTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));

        StringBuilder html = new StringBuilder();
        html.append(getEmailHeader("Ticket Expiring Soon"));
        html.append("<div class=\"email-container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>FireFighter Platform</h1>");
        html.append("<p class=\"subtitle\">Emergency Response Management System</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");

        String greeting = "Hello";
        if (user != null && user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            greeting = "Hello, " + user.getUsername();
        }

        html.append("<div class=\"greeting\">").append(greeting).append(",</div>");
        html.append("<p><strong>URGENT:</strong> Your emergency ticket will expire in approximately 5 minutes.</p>");
        html.append("<div class=\"info-box\">");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Ticket ID: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getTicketId()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Emergency Type: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getEmergencyType()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Created: </span>");
        html.append("<span class=\"info-value\">").append(ticketDateTime).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Expires: </span>");
        html.append("<span class=\"info-value\">").append(expirationDateTime).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Duration: </span>");
        html.append("<span class=\"info-value\">").append(durationMinutes).append(" minutes</span>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class=\"security-notice\">");
        html.append("<strong>Action Required:</strong> This ticket will automatically close when it expires. ");
        html.append("Please ensure all emergency response activities are completed before expiration. ");
        html.append("Contact your system administrator if you need assistance.");
        html.append("</div>");
        html.append("</div>");
        html.append(getEmailFooter(currentDateTime));
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Creates the common email header with CSS styles
     */
    private String getEmailHeader(String title) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>FireFighter Platform - ").append(title).append("</title>");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("    line-height: 1.6;");
        html.append("    color: #333;");
        html.append("    max-width: 600px;");
        html.append("    margin: 0 auto;");
        html.append("    padding: 20px;");
        html.append("    background-color: #f8f9fa;");
        html.append("}");
        html.append(".email-container {");
        html.append("    background-color: #ffffff;");
        html.append("    border-radius: 12px;");
        html.append("    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);");
        html.append("    overflow: hidden;");
        html.append("    border: 1px solid #e9ecef;");
        html.append("}");
        html.append(".header {");
        html.append("    background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);");
        html.append("    color: white;");
        html.append("    padding: 30px 25px;");
        html.append("    text-align: center;");
        html.append("}");
        html.append(".header h1 {");
        html.append("    margin: 0 0 8px 0;");
        html.append("    font-size: 28px;");
        html.append("    font-weight: 700;");
        html.append("    letter-spacing: -0.5px;");
        html.append("}");
        html.append(".subtitle {");
        html.append("    margin: 0;");
        html.append("    font-size: 14px;");
        html.append("    opacity: 0.9;");
        html.append("    font-weight: 400;");
        html.append("}");
        html.append(".content {");
        html.append("    padding: 30px 25px;");
        html.append("}");
        html.append(".greeting {");
        html.append("    font-size: 18px;");
        html.append("    font-weight: 600;");
        html.append("    color: #2c3e50;");
        html.append("    margin-bottom: 20px;");
        html.append("}");
        html.append(".info-box {");
        html.append("    background-color: #f8f9fa;");
        html.append("    border: 1px solid #e9ecef;");
        html.append("    border-radius: 8px;");
        html.append("    padding: 20px;");
        html.append("    margin: 20px 0;");
        html.append("}");
        html.append(".info-item {");
        html.append("    display: flex;");
        html.append("    justify-content: space-between;");
        html.append("    align-items: center;");
        html.append("    padding: 8px 0;");
        html.append("    border-bottom: 1px solid #e9ecef;");
        html.append("}");
        html.append(".info-item:last-child {");
        html.append("    border-bottom: none;");
        html.append("}");
        html.append(".info-label {");
        html.append("    font-weight: 600;");
        html.append("    color: #495057;");
        html.append("    flex: 0 0 auto;");
        html.append("}");
        html.append(".info-value {");
        html.append("    color: #212529;");
        html.append("    font-weight: 500;");
        html.append("    text-align: right;");
        html.append("    flex: 1 1 auto;");
        html.append("    margin-left: 15px;");
        html.append("}");
        html.append(".security-notice {");
        html.append("    background-color: #fff3cd;");
        html.append("    border: 1px solid #ffeaa7;");
        html.append("    border-radius: 6px;");
        html.append("    padding: 15px;");
        html.append("    margin: 20px 0;");
        html.append("    font-size: 14px;");
        html.append("    color: #856404;");
        html.append("}");
        html.append(".footer {");
        html.append("    background-color: #f8f9fa;");
        html.append("    padding: 20px 25px;");
        html.append("    text-align: center;");
        html.append("    border-top: 1px solid #e9ecef;");
        html.append("    font-size: 12px;");
        html.append("    color: #6c757d;");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        return html.toString();
    }

    /**
     * Creates the common email footer
     */
    private String getEmailFooter(String currentDateTime) {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"footer\">");
        html.append("<p><strong>FireFighter Emergency Response Platform</strong></p>");
        html.append("<p>Automated Notification System | Generated on ").append(currentDateTime).append("</p>");
        html.append("<p style=\"font-size: 12px; margin-top: 15px;\">");
        html.append("This is an automated message. Please do not reply to this email.");
        html.append("</p>");
        html.append("</div>");
        return html.toString();
    }

    /**
     * Send suspicious group change notification email to all admins
     */
    public void sendSuspiciousGroupChangeNotificationEmail(String recipientEmail, User user, String ticketId, String oldGroup, String newGroup, String reason, String suspicionLevel) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("FireFighter Platform - Group Change Alert (" + suspicionLevel + " Risk data): " + user.getUsername());

            String htmlContent = createSuspiciousGroupChangeEmailContent(user, ticketId, oldGroup, newGroup, reason, suspicionLevel);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Group change notification email sent successfully to " + recipientEmail + " for user " + user.getUsername());

        } catch (Exception e) {
            System.err.println("Group change notification email failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates professional HTML email content for suspicious group change notification
     */
    private String createSuspiciousGroupChangeEmailContent(User user, String ticketId, String oldGroup, String newGroup, String reason, String suspicionLevel) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));

        StringBuilder html = new StringBuilder();
        html.append(getEmailHeader("User Group Change Alert"));
        html.append("<div class=\"email-container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>FireFighter Platform</h1>");
        html.append("<p class=\"subtitle\">Emergency Response Management System</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");

        html.append("<div class=\"greeting\">SECURITY ALERT - Administrator Notification,</div>");
        html.append("<p><strong>ACTIVITY DETECTED:</strong> A user group change has been detected in the Dolibarr ERP system following ticket creation.</p>");
        html.append("<p><strong>Risk Level: <span style=\"color: " + getRiskLevelColor(suspicionLevel) + "; font-weight: bold;\">" + suspicionLevel + "</span></strong></p>");
        
        html.append("<div class=\"info-box\">");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">User: </span>");
        html.append("<span class=\"info-value\">").append(user.getUsername()).append(" (").append(user.getEmail()).append(")</span>");
        html.append("</div>");
        if (user.getDolibarrId() != null) {
            html.append("<div class=\"info-item\">");
            html.append("<span class=\"info-label\">Dolibarr ID: </span>");
            html.append("<span class=\"info-value\">").append(user.getDolibarrId()).append("</span>");
            html.append("</div>");
        }
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Department: </span>");
        html.append("<span class=\"info-value\">").append(user.getDepartment() != null ? user.getDepartment() : "N/A").append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Related Ticket: </span>");
        html.append("<span class=\"info-value\">").append(ticketId).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Previous Group: </span>");
        html.append("<span class=\"info-value\">").append(oldGroup != null ? oldGroup : "None").append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">New Group: </span>");
        html.append("<span class=\"info-value\">").append(newGroup).append("</span>");
        html.append("</div>");
        if (reason != null && !reason.trim().isEmpty()) {
            html.append("<div class=\"info-item\">");
            html.append("<span class=\"info-label\">Reason: </span>");
            html.append("<span class=\"info-value\">").append(reason).append("</span>");
            html.append("</div>");
        }
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Change Time: </span>");
        html.append("<span class=\"info-value\">").append(currentDateTime).append("</span>");
        html.append("</div>");
        html.append("</div>");
        
        html.append("<div class=\"security-notice\">");
        html.append("<strong>⚠️ POTENTIAL ACTION REQUIRED:</strong> This group change has been flagged as suspicious due to security sensitivity. ");
        html.append("Risk Level: <strong>" + suspicionLevel + "</strong>. ");
        html.append("Please immediately verify that this change is legitimate and authorized. ");
        html.append("Review the associated ticket, contact the user directly, and investigate any unauthorized access. ");
        html.append("If this change appears suspicious or unauthorized, take immediate security measures.");
        html.append("</div>");
        
        html.append("</div>");
        html.append(getEmailFooter(currentDateTime));
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Send anomaly detection notification email to admins
     */
    public void sendAnomalyDetectionNotificationEmail(String recipientEmail, User user, Ticket ticket, String anomalyType, String anomalyDetails, String riskLevel) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("FireFighter Platform - ANOMALY DETECTED (" + riskLevel + " Risk): " + getAnomalyTypeDescription(anomalyType));

            String htmlContent = createAnomalyDetectionEmailContent(user, ticket, anomalyType, anomalyDetails, riskLevel);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Anomaly detection email sent successfully to " + recipientEmail + " for user " + user.getUsername());

        } catch (Exception e) {
            System.err.println("Anomaly detection email failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates professional HTML email content for anomaly detection notification
     */
    private String createAnomalyDetectionEmailContent(User user, Ticket ticket, String anomalyType, String anomalyDetails, String riskLevel) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
        String ticketDateTime = ticket.getDateCreated().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));

        StringBuilder html = new StringBuilder();
        html.append(getEmailHeader("Anomaly Detection Alert"));
        html.append("<div class=\"email-container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>FireFighter Platform</h1>");
        html.append("<p class=\"subtitle\">Emergency Response Management System</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");

        html.append("<div class=\"greeting\"> SECURITY ALERT - Anomalous Behavior Detected,</div>");
        html.append("<p><strong>ANOMALY DETECTED:</strong> Potentially suspicious user behavior has been identified in the emergency response system.</p>");
        html.append("<p><strong>Anomaly Type: ").append(getAnomalyTypeDescription(anomalyType)).append("</strong></p>");
        html.append("<p><strong>Risk Level: <span style=\"color: " + getRiskLevelColor(riskLevel) + "; font-weight: bold;\">" + riskLevel + "</span></strong></p>");
        
        html.append("<div class=\"info-box\">");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">User: </span>");
        html.append("<span class=\"info-value\">").append(user.getUsername()).append(" (").append(user.getEmail()).append(")</span>");
        html.append("</div>");
        if (user.getDepartment() != null) {
            html.append("<div class=\"info-item\">");
            html.append("<span class=\"info-label\">Department: </span>");
            html.append("<span class=\"info-value\">").append(user.getDepartment()).append("</span>");
            html.append("</div>");
        }
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Related Ticket: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getTicketId()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Emergency Type: </span>");
        html.append("<span class=\"info-value\">").append(ticket.getEmergencyType()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Ticket Created: </span>");
        html.append("<span class=\"info-value\">").append(ticketDateTime).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Anomaly Details: </span>");
        html.append("<span class=\"info-value\">").append(anomalyDetails).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-item\">");
        html.append("<span class=\"info-label\">Detection Time: </span>");
        html.append("<span class=\"info-value\">").append(currentDateTime).append("</span>");
        html.append("</div>");
        html.append("</div>");
        
        html.append("<div class=\"security-notice\">");
        html.append(getSecurityNoticeForAnomalyType(anomalyType, riskLevel));
        html.append("</div>");
        
        html.append("</div>");
        html.append(getEmailFooter(currentDateTime));
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Get human-readable description for anomaly types
     */
    private String getAnomalyTypeDescription(String anomalyType) {
        if (anomalyType == null) {
            return "Unknown Anomaly Type";
        }
        
        return switch (anomalyType) {
            case "FREQUENT_REQUESTS" -> "Excessive Request Frequency";
            case "DORMANT_USER_ACTIVITY" -> "Dormant Account Sudden Activity";
            case "OFF_HOURS_ACTIVITY" -> "Off-Hours System Access";
            default -> "Unknown Anomaly Type";
        };
    }

    /**
     * Get appropriate security notice based on anomaly type and risk level
     */
    private String getSecurityNoticeForAnomalyType(String anomalyType, String riskLevel) {
        String baseNotice = "<strong>⚠️ IMMEDIATE ACTION REQUIRED:</strong> ";
        
        if (anomalyType == null) {
            anomalyType = "UNKNOWN";
        }
        
        String specificNotice = switch (anomalyType) {
            case "DORMANT_USER_ACTIVITY" -> 
                "A previously dormant user account has suddenly become active and performed rapid actions. " +
                "This pattern is highly indicative of account compromise or unauthorized access. " +
                "<strong>Immediately verify the user's identity and investigate potential security breach.</strong>";
            
            case "FREQUENT_REQUESTS" -> 
                "A user has exceeded normal request frequency thresholds, which may indicate automated attacks, " +
                "system abuse, or compromised credentials being used for malicious purposes. " +
                "<strong>Review the user's recent activity and consider temporary access restrictions.</strong>";
            
            case "OFF_HOURS_ACTIVITY" -> 
                "System access has been detected outside of normal business hours. While this may be legitimate " +
                "emergency response activity, it should be verified to ensure authorized use. " +
                "<strong>Confirm with the user that this access was intentional and authorized.</strong>";
            
            default -> 
                "Anomalous behavior has been detected that deviates from normal usage patterns. " +
                "<strong>Please investigate this activity immediately to ensure system security.</strong>";
        };

        String actionGuidance = " Risk Level: <strong>" + riskLevel + "</strong>. " +
            "Contact the user directly, review system logs, and take appropriate security measures based on your findings.";

        return baseNotice + specificNotice + actionGuidance;
    }

    /**
     * Get color code for risk level display
     */
    private String getRiskLevelColor(String suspicionLevel) {
        return switch (suspicionLevel) {
            case "HIGH" -> "#dc3545"; // Red
            case "MEDIUM" -> "#fd7e14"; // Orange
            case "LOW" -> "#ffc107"; // Yellow
            default -> "#6c757d"; // Gray
        };
    }

}
