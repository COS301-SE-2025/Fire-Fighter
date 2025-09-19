package com.apex.firefighter.service.changelog;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.GmailEmailService;
import com.apex.firefighter.service.changelog.dto.AnomalyReport;
import com.apex.firefighter.service.changelog.model.Changelog;
import com.apex.firefighter.service.changelog.repository.ChangelogRepository;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChangelogService {

    private final ChangelogRepository changelogRepository;
    private final GmailEmailService gmailEmailService;
    private final UserRepository userRepository;

    public ChangelogService(ChangelogRepository changelogRepository, GmailEmailService gmailEmailService, UserRepository userRepository) {
        this.changelogRepository = changelogRepository;
        this.gmailEmailService = gmailEmailService;
        this.userRepository = userRepository;
    }

    public void logChange(String entityName, String fieldName, String oldValue, String newValue, String changedBy) {
        Changelog changelog = new Changelog(entityName, fieldName, oldValue, newValue, changedBy, LocalDateTime.now());
        changelogRepository.save(changelog);
        checkForAnomalies(changelog);
    }

    private void checkForAnomalies(Changelog changelog) {
        // For now, we'll consider every change an anomaly for testing purposes.
        boolean isAnomaly = true;

        if (isAnomaly) {
            String reason = "This is a test anomaly. All changes are currently being flagged.";
            sendAnomalyNotification(changelog, reason);
        }
    }

    private void sendAnomalyNotification(Changelog changelog, String reason) {
        AnomalyReport anomalyReport = new AnomalyReport(
                changelog.getEntityName(),
                changelog.getFieldName(),
                changelog.getOldValue(),
                changelog.getNewValue(),
                changelog.getChangedBy(),
                changelog.getTimestamp(),
                reason
        );

        List<User> admins = userRepository.findByIsAdminTrue();
        for (User admin : admins) {
            try {
                gmailEmailService.sendAnomalyAlertEmail(admin.getEmail(), anomalyReport);
            } catch (MessagingException e) {
                System.err.println("Failed to send anomaly notification email to " + admin.getEmail() + ": " + e.getMessage());
            }
        }
    }
}
