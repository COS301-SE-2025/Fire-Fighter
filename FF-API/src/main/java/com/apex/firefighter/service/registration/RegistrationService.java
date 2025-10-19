package com.apex.firefighter.service.registration;

import com.apex.firefighter.dto.registration.*;
import com.apex.firefighter.model.User;
import com.apex.firefighter.model.registration.SystemAccessRequest;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.repository.SystemAccessRequestRepository;
import com.apex.firefighter.service.DolibarrUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * Service for handling user registration and approval workflows
 * Uses system_access_requests table as the single source of truth for new user registrations
 */
@Service
@Transactional
public class RegistrationService {

    private final SystemAccessRequestRepository systemAccessRequestRepository;
    private final UserRepository userRepository;
    private final RegistrationNotificationService notificationService;
    private final DolibarrUserGroupService dolibarrUserGroupService;

    @Autowired
    public RegistrationService(SystemAccessRequestRepository systemAccessRequestRepository,
                             UserRepository userRepository,
                             RegistrationNotificationService notificationService,
                             DolibarrUserGroupService dolibarrUserGroupService) {
        this.systemAccessRequestRepository = systemAccessRequestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.dolibarrUserGroupService = dolibarrUserGroupService;
    }

    /**
     * Submit a new registration request
     */
    public PendingApprovalDto submitRegistrationRequest(RegistrationRequestDto request) {
        System.out.println("🔵 SUBMIT REGISTRATION: " + request.getEmail());

        // Check if user already exists
        Optional<User> existingUser = userRepository.findByUserId(request.getFirebaseUid());
        if (existingUser.isPresent()) {
            throw new IllegalStateException("User already registered with this Firebase UID");
        }

        // Check if email is already registered
        Optional<User> emailUser = userRepository.findByEmail(request.getEmail());
        if (emailUser.isPresent()) {
            throw new IllegalStateException("User already registered with this email");
        }

        // Check if pending access request already exists
        if (systemAccessRequestRepository.existsByFirebaseUid(request.getFirebaseUid())) {
            throw new IllegalStateException("Registration request already pending for this Firebase UID");
        }

        if (systemAccessRequestRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Registration request already pending for this email");
        }

        // Create system access request
        SystemAccessRequest accessRequest = new SystemAccessRequest();
        accessRequest.setFirebaseUid(request.getFirebaseUid());
        accessRequest.setUsername(request.getUsername());
        accessRequest.setEmail(request.getEmail());
        accessRequest.setRequestDepartment(request.getDepartment());
        accessRequest.setPhoneNumber(request.getContactNumber());
        accessRequest.setRegistrationMethod(request.getRegistrationMethod());
        accessRequest.setRequestedAccessGroups(request.getRequestedAccessGroups());
        accessRequest.setJustification(request.getBusinessJustification());
        accessRequest.setRequestPriority(request.getPriorityLevel() != null ? request.getPriorityLevel() : "MEDIUM");
        accessRequest.setDolibarrId(request.getDolibarrId());

        SystemAccessRequest saved = systemAccessRequestRepository.save(accessRequest);
        System.out.println("✅ REGISTRATION SAVED: ID=" + saved.getRequestId());

        // Create user account immediately with is_authorized = false
        // This allows the user to exist in the system but not access it until approved
        User newUser = new User();
        newUser.setUserId(request.getFirebaseUid());
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setDepartment(request.getDepartment());
        newUser.setContactNumber(request.getContactNumber());
        newUser.setIsAuthorized(false); // NOT authorized until admin approves
        newUser.setRole("USER");
        newUser.setIsAdmin(false);
        newUser.setCreatedAt(ZonedDateTime.now());

        userRepository.save(newUser);
        System.out.println("✅ USER CREATED (UNAUTHORIZED): " + newUser.getUsername());

        // Send notification to admins
        notificationService.notifyAdminsOfNewRegistration(saved);

        return convertToDto(saved);
    }

    /**
     * Get all pending access requests (Admin only)
     */
    public List<PendingApprovalDto> getPendingApprovals(String adminUid) {
        System.out.println("🔵 GET PENDING REQUESTS: Admin=" + adminUid);

        // Verify admin
        verifyAdmin(adminUid);

        List<SystemAccessRequest> pending = systemAccessRequestRepository.findByStatusOrderByCreatedAtDesc("PENDING");
        System.out.println("✅ FOUND " + pending.size() + " PENDING REQUESTS");

        return pending.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Approve a user registration (Admin only)
     */
    public PendingApprovalDto approveUser(String adminUid, String targetUid, 
                                         List<String> assignedAccessGroups,
                                         String department, String dolibarrId) {
        System.out.println("🔵 APPROVE USER: Admin=" + adminUid + ", Target=" + targetUid);

        // Verify admin
        verifyAdmin(adminUid);

        // Find pending access request
        SystemAccessRequest accessRequest = systemAccessRequestRepository.findByFirebaseUidAndStatus(targetUid, "PENDING")
                .orElseThrow(() -> new IllegalArgumentException("No pending access request found for Firebase UID: " + targetUid));

        // Find and update existing user (created during registration with is_authorized = false)
        User user = userRepository.findByUserId(accessRequest.getFirebaseUid())
                .orElseThrow(() -> new IllegalArgumentException("User not found for Firebase UID: " + targetUid));

        // Update user with approval details
        user.setDepartment(department != null ? department : accessRequest.getRequestDepartment());
        user.setDolibarrId(dolibarrId != null ? dolibarrId : accessRequest.getDolibarrId());
        user.setIsAuthorized(true); // NOW authorized!

        userRepository.save(user);
        System.out.println("✅ USER AUTHORIZED: " + user.getUsername());

        // Update request status
        accessRequest.setStatus("APPROVED");
        accessRequest.setReviewedBy(adminUid);
        accessRequest.setReviewedAt(ZonedDateTime.now());
        SystemAccessRequest updated = systemAccessRequestRepository.save(accessRequest);

        // Send notification to user
        Optional<User> adminUser = userRepository.findByUserId(adminUid);
        String adminName = adminUser.map(User::getUsername).orElse("Administrator");
        notificationService.notifyUserOfApproval(user, adminName);

        // Sync with Dolibarr if dolibarrId is provided
        if (dolibarrId != null && !dolibarrId.trim().isEmpty() && 
            assignedAccessGroups != null && !assignedAccessGroups.isEmpty()) {
            try {
                System.out.println("🔄 Syncing user with Dolibarr ID: " + dolibarrId);
                
                // Sync user's assigned access groups to Dolibarr
                for (String groupId : assignedAccessGroups) {
                    try {
                        dolibarrUserGroupService.addUserToGroup(dolibarrId, groupId, adminUid);
                        System.out.println("✅ Added user to Dolibarr group: " + groupId);
                    } catch (SQLException e) {
                        System.err.println("⚠️ Failed to add user to Dolibarr group " + groupId + ": " + e.getMessage());
                        // Continue with other groups even if one fails
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error syncing with Dolibarr: " + e.getMessage());
                // Don't fail the approval process if Dolibarr sync fails
            }
        }

        System.out.println("✅ APPROVAL COMPLETED");
        return convertToDto(updated);
    }

    /**
     * Reject a user registration (Admin only)
     */
    public PendingApprovalDto rejectUser(String adminUid, String targetUid, String reason) {
        System.out.println("🔵 REJECT USER: Admin=" + adminUid + ", Target=" + targetUid);

        // Verify admin
        verifyAdmin(adminUid);

        // Find pending access request
        SystemAccessRequest accessRequest = systemAccessRequestRepository.findByFirebaseUidAndStatus(targetUid, "PENDING")
                .orElseThrow(() -> new IllegalArgumentException("No pending access request found for Firebase UID: " + targetUid));

        // Update request status
        accessRequest.setStatus("REJECTED");
        accessRequest.setReviewedBy(adminUid);
        accessRequest.setReviewedAt(ZonedDateTime.now());
        accessRequest.setRejectionReason(reason);
        SystemAccessRequest updated = systemAccessRequestRepository.save(accessRequest);

        // Send notification to user
        Optional<User> adminUser = userRepository.findByUserId(adminUid);
        String adminName = adminUser.map(User::getUsername).orElse("Administrator");
        notificationService.notifyUserOfRejection(accessRequest, adminName, reason);

        System.out.println("✅ USER REJECTED");
        return convertToDto(updated);
    }

    /**
     * Delete a pending access request (Admin only)
     */
    public void deletePendingApproval(String adminUid, String targetUid) {
        System.out.println("🔵 DELETE PENDING: Admin=" + adminUid + ", Target=" + targetUid);

        // Verify admin
        verifyAdmin(adminUid);

        // Find and delete pending access request
        SystemAccessRequest accessRequest = systemAccessRequestRepository.findByFirebaseUid(targetUid)
                .orElseThrow(() -> new IllegalArgumentException("No pending access request found for Firebase UID: " + targetUid));

        systemAccessRequestRepository.delete(accessRequest);
        System.out.println("✅ PENDING ACCESS REQUEST DELETED");
    }

    /**
     * Get user management statistics (Admin only)
     */
    public UserManagementStatisticsDto getUserStatistics(String adminUid) {
        System.out.println("🔵 GET STATISTICS: Admin=" + adminUid);

        // Verify admin
        verifyAdmin(adminUid);

        long totalUsers = userRepository.count();
        long adminUsers = userRepository.countByIsAdmin(true);
        long regularUsers = totalUsers - adminUsers;
        long pendingRequests = systemAccessRequestRepository.countByStatus("PENDING");
        long activeUsers = userRepository.countByIsAuthorized(true);
        long inactiveUsers = totalUsers - activeUsers;

        UserManagementStatisticsDto stats = new UserManagementStatisticsDto(
            totalUsers, adminUsers, regularUsers, pendingRequests, activeUsers, inactiveUsers
        );

        System.out.println("✅ STATISTICS: Total=" + totalUsers + ", Pending=" + pendingRequests);
        return stats;
    }

    /**
     * Get registration status for a Firebase UID
     */
    public Map<String, Object> getRegistrationStatus(String firebaseUid) {
        System.out.println("🔵 CHECK STATUS: UID=" + firebaseUid);

        Map<String, Object> status = new HashMap<>();

        // Check if user exists
        Optional<User> user = userRepository.findByUserId(firebaseUid);
        if (user.isPresent()) {
            status.put("status", "REGISTERED");
            status.put("authorized", user.get().getIsAuthorized());
            status.put("isAdmin", user.get().getIsAdmin());
            return status;
        }

        // Check if access request exists
        Optional<SystemAccessRequest> accessRequest = systemAccessRequestRepository.findByFirebaseUid(firebaseUid);
        if (accessRequest.isPresent()) {
            status.put("status", accessRequest.get().getStatus());
            status.put("createdAt", accessRequest.get().getCreatedAt());
            if ("REJECTED".equals(accessRequest.get().getStatus())) {
                status.put("rejectionReason", accessRequest.get().getRejectionReason());
            }
            return status;
        }

        status.put("status", "NOT_REGISTERED");
        return status;
    }

    /**
     * Verify that the requesting user is an admin
     */
    private void verifyAdmin(String adminUid) {
        Optional<User> admin = userRepository.findByUserId(adminUid);
        if (admin.isEmpty() || !admin.get().getIsAdmin()) {
            throw new SecurityException("Access denied: Admin privileges required");
        }
    }

    /**
     * Submit or update system access request details
     * This is called after initial registration to add priority, justification, and access groups
     */
    public SystemAccessRequestDto submitSystemAccessRequest(SystemAccessRequestDto request) {
        System.out.println("🔵 UPDATE SYSTEM ACCESS REQUEST: " + request.getFirebaseUid());

        // Find existing access request (should exist from registration)
        Optional<SystemAccessRequest> existingRequest = systemAccessRequestRepository.findByFirebaseUid(request.getFirebaseUid());
        
        if (existingRequest.isEmpty()) {
            throw new IllegalArgumentException("No pending registration found for this Firebase UID. Please register first.");
        }

        SystemAccessRequest accessRequest = existingRequest.get();
        
        // Update with additional information from access request form
        if (request.getRequestPriority() != null) {
            accessRequest.setRequestPriority(request.getRequestPriority());
        }
        if (request.getRequestDepartment() != null) {
            accessRequest.setRequestDepartment(request.getRequestDepartment());
        }
        if (request.getPhoneNumber() != null) {
            accessRequest.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getJustification() != null) {
            accessRequest.setJustification(request.getJustification());
        }
        if (request.getRequestedAccessGroups() != null && !request.getRequestedAccessGroups().isEmpty()) {
            accessRequest.setRequestedAccessGroups(request.getRequestedAccessGroups());
        }

        SystemAccessRequest saved = systemAccessRequestRepository.save(accessRequest);
        System.out.println("✅ SYSTEM ACCESS REQUEST UPDATED: ID=" + saved.getRequestId());
        System.out.println("  Priority: " + saved.getRequestPriority());
        System.out.println("  Justification: " + (saved.getJustification() != null ? saved.getJustification().substring(0, Math.min(50, saved.getJustification().length())) + "..." : "None"));
        System.out.println("  Access Groups: " + saved.getRequestedAccessGroups());

        return request;
    }

    /**
     * Get system access request by Firebase UID
     */
    public Optional<SystemAccessRequest> getSystemAccessRequest(String firebaseUid) {
        return systemAccessRequestRepository.findByFirebaseUid(firebaseUid);
    }

    /**
     * Convert SystemAccessRequest entity to DTO
     */
    private PendingApprovalDto convertToDto(SystemAccessRequest accessRequest) {
        PendingApprovalDto dto = new PendingApprovalDto();
        dto.setId(accessRequest.getRequestId());
        dto.setFirebaseUid(accessRequest.getFirebaseUid());
        dto.setUsername(accessRequest.getUsername());
        dto.setEmail(accessRequest.getEmail());
        dto.setDepartment(accessRequest.getRequestDepartment());
        dto.setContactNumber(accessRequest.getPhoneNumber());
        dto.setRegistrationMethod(accessRequest.getRegistrationMethod());
        dto.setRequestedAccessGroups(accessRequest.getRequestedAccessGroups());
        dto.setBusinessJustification(accessRequest.getJustification());
        dto.setPriorityLevel(accessRequest.getRequestPriority());
        dto.setStatus(accessRequest.getStatus());
        dto.setCreatedAt(accessRequest.getCreatedAt());
        dto.setReviewedBy(accessRequest.getReviewedBy());
        dto.setReviewedAt(accessRequest.getReviewedAt());
        dto.setDolibarrId(accessRequest.getDolibarrId());
        
        // Set system access fields (for backward compatibility with DTO)
        dto.setSystemAccessPriority(accessRequest.getRequestPriority());
        dto.setSystemAccessDepartment(accessRequest.getRequestDepartment());
        dto.setSystemAccessPhoneNumber(accessRequest.getPhoneNumber());
        dto.setSystemAccessJustification(accessRequest.getJustification());
        
        return dto;
    }
}
