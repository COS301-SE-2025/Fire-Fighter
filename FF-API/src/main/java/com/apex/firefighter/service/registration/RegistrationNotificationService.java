package com.apex.firefighter.service.registration;

import com.apex.firefighter.model.User;
import com.apex.firefighter.model.registration.SystemAccessRequest;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.GmailEmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for sending email notifications related to user registration
 */
@Service
public class RegistrationNotificationService {

    private final GmailEmailService emailService;
    private final UserRepository userRepository;

    @Autowired
    public RegistrationNotificationService(GmailEmailService emailService, UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    /**
     * Notify admins when a new registration request is submitted
     * This method runs asynchronously to avoid blocking the registration request
     */
    @Async
    public void notifyAdminsOfNewRegistration(SystemAccessRequest accessRequest) {
        try {
            List<User> admins = userRepository.findByIsAdminTrue();
            
            if (admins.isEmpty()) {
                System.out.println("⚠️ REGISTRATION NOTIFICATION: No admins found to notify");
                return;
            }

            System.out.println("📧 REGISTRATION NOTIFICATION: Notifying " + admins.size() + " admin(s) of new registration");

            for (User admin : admins) {
                try {
                    emailService.sendNewRegistrationNotification(
                        admin.getEmail(),
                        admin.getUsername(),
                        accessRequest
                    );
                    System.out.println("✅ Sent registration notification to admin: " + admin.getEmail());
                } catch (MessagingException e) {
                    System.err.println("❌ Failed to notify admin " + admin.getEmail() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ REGISTRATION NOTIFICATION FAILED: " + e.getMessage());
        }
    }

    /**
     * Notify user when their registration is approved
     * This method runs asynchronously to avoid blocking the approval request
     */
    @Async
    public void notifyUserOfApproval(User user, String approvedBy) {
        try {
            emailService.sendRegistrationApprovedNotification(
                user.getEmail(),
                user.getUsername(),
                approvedBy
            );
            System.out.println("✅ Sent approval notification to user: " + user.getEmail());
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send approval notification: " + e.getMessage());
        }
    }

    /**
     * Notify user when their registration is rejected
     * This method runs asynchronously to avoid blocking the rejection request
     */
    @Async
    public void notifyUserOfRejection(SystemAccessRequest accessRequest, String rejectedBy, String reason) {
        try {
            emailService.sendRegistrationRejectedNotification(
                accessRequest.getEmail(),
                accessRequest.getUsername(),
                rejectedBy,
                reason
            );
            System.out.println("✅ Sent rejection notification to user: " + accessRequest.getEmail());
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send rejection notification: " + e.getMessage());
        }
    }

    /**
     * Notify user when their department is changed
     * This method runs asynchronously to avoid blocking the department change request
     */
    @Async
    public void notifyUserOfDepartmentChange(User user, String oldDepartment, String newDepartment, String changedBy) {
        try {
            emailService.sendDepartmentChangeNotification(
                user.getEmail(),
                user.getUsername(),
                oldDepartment,
                newDepartment,
                changedBy
            );
            System.out.println("✅ Sent department change notification to user: " + user.getEmail());
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send department change notification: " + e.getMessage());
        }
    }

    /**
     * Notify user when their account status is changed
     * This method runs asynchronously to avoid blocking the status change request
     */
    @Async
    public void notifyUserOfStatusChange(User user, boolean isAuthorized, String changedBy) {
        try {
            emailService.sendAccountStatusChangeNotification(
                user.getEmail(),
                user.getUsername(),
                isAuthorized,
                changedBy
            );
            System.out.println("✅ Sent status change notification to user: " + user.getEmail());
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send status change notification: " + e.getMessage());
        }
    }
}
