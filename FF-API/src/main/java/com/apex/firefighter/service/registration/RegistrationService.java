package com.apex.firefighter.service.registration;

import com.apex.firefighter.dto.registration.*;
import com.apex.firefighter.model.User;
import com.apex.firefighter.model.registration.PendingApproval;
import com.apex.firefighter.model.registration.SystemAccessRequest;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.repository.registration.PendingApprovalRepository;
import com.apex.firefighter.repository.SystemAccessRequestRepository;
import com.apex.firefighter.service.DolibarrUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * Service for handling user registration and approval workflows
 */
@Service
@Transactional
public class RegistrationService {

    private final PendingApprovalRepository pendingApprovalRepository;
    private final UserRepository userRepository;
    private final RegistrationNotificationService notificationService;
    private final DolibarrUserGroupService dolibarrUserGroupService;
    private final SystemAccessRequestRepository systemAccessRequestRepository;

    @Autowired
    public RegistrationService(PendingApprovalRepository pendingApprovalRepository,
                             UserRepository userRepository,
                             RegistrationNotificationService notificationService,
                             DolibarrUserGroupService dolibarrUserGroupService,
                             SystemAccessRequestRepository systemAccessRequestRepository) {
        this.pendingApprovalRepository = pendingApprovalRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.dolibarrUserGroupService = dolibarrUserGroupService;
        this.systemAccessRequestRepository = systemAccessRequestRepository;
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

        // Check if pending approval already exists
        if (pendingApprovalRepository.existsByFirebaseUid(request.getFirebaseUid())) {
            throw new IllegalStateException("Registration request already pending for this Firebase UID");
        }

        if (pendingApprovalRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Registration request already pending for this email");
        }

        // Create pending approval
        PendingApproval approval = new PendingApproval();
        approval.setFirebaseUid(request.getFirebaseUid());
        approval.setUsername(request.getUsername());
        approval.setEmail(request.getEmail());
        approval.setDepartment(request.getDepartment());
        approval.setContactNumber(request.getContactNumber());
        approval.setRegistrationMethod(request.getRegistrationMethod());
        approval.setRequestedAccessGroups(request.getRequestedAccessGroups());
        approval.setBusinessJustification(request.getBusinessJustification());
        approval.setPriorityLevel(request.getPriorityLevel() != null ? request.getPriorityLevel() : "MEDIUM");
        approval.setDolibarrId(request.getDolibarrId());

        PendingApproval saved = pendingApprovalRepository.save(approval);
        System.out.println("✅ REGISTRATION SAVED: ID=" + saved.getId());

        // Send notification to admins
        notificationService.notifyAdminsOfNewRegistration(saved);

        return convertToDto(saved);
    }

    /**
     * Get all pending approvals (Admin only)
     */
    public List<PendingApprovalDto> getPendingApprovals(String adminUid) {
        System.out.println("🔵 GET PENDING APPROVALS: Admin=" + adminUid);

        // Verify admin
        verifyAdmin(adminUid);

        List<PendingApproval> pending = pendingApprovalRepository.findByStatusOrderByCreatedAtDesc("PENDING");
        System.out.println("✅ FOUND " + pending.size() + " PENDING APPROVALS");

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

        // Find pending approval
        PendingApproval approval = pendingApprovalRepository.findByFirebaseUidAndStatus(targetUid, "PENDING")
                .orElseThrow(() -> new IllegalArgumentException("No pending approval found for Firebase UID: " + targetUid));

        // Create user account
        User newUser = new User();
        newUser.setUserId(approval.getFirebaseUid());
        newUser.setUsername(approval.getUsername());
        newUser.setEmail(approval.getEmail());
        newUser.setDepartment(department != null ? department : approval.getDepartment());
        newUser.setContactNumber(approval.getContactNumber());
        newUser.setDolibarrId(dolibarrId != null ? dolibarrId : approval.getDolibarrId());
        newUser.setIsAuthorized(true);
        newUser.setRole("USER");
        newUser.setIsAdmin(false);
        newUser.setCreatedAt(ZonedDateTime.now());

        userRepository.save(newUser);
        System.out.println("✅ USER CREATED: " + newUser.getUsername());

        // Update approval status
        approval.setStatus("APPROVED");
        approval.setReviewedBy(adminUid);
        approval.setReviewedAt(ZonedDateTime.now());
        PendingApproval updated = pendingApprovalRepository.save(approval);

        // Send notification to user
        Optional<User> adminUser = userRepository.findByUserId(adminUid);
        String adminName = adminUser.map(User::getUsername).orElse("Administrator");
        notificationService.notifyUserOfApproval(newUser, adminName);

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

        // Find pending approval
        PendingApproval approval = pendingApprovalRepository.findByFirebaseUidAndStatus(targetUid, "PENDING")
                .orElseThrow(() -> new IllegalArgumentException("No pending approval found for Firebase UID: " + targetUid));

        // Update approval status
        approval.setStatus("REJECTED");
        approval.setReviewedBy(adminUid);
        approval.setReviewedAt(ZonedDateTime.now());
        approval.setRejectionReason(reason);
        PendingApproval updated = pendingApprovalRepository.save(approval);

        // Send notification to user
        Optional<User> adminUser = userRepository.findByUserId(adminUid);
        String adminName = adminUser.map(User::getUsername).orElse("Administrator");
        notificationService.notifyUserOfRejection(approval, adminName, reason);

        System.out.println("✅ USER REJECTED");
        return convertToDto(updated);
    }

    /**
     * Delete a pending approval (Admin only)
     */
    public void deletePendingApproval(String adminUid, String targetUid) {
        System.out.println("🔵 DELETE PENDING: Admin=" + adminUid + ", Target=" + targetUid);

        // Verify admin
        verifyAdmin(adminUid);

        // Find and delete pending approval
        PendingApproval approval = pendingApprovalRepository.findByFirebaseUid(targetUid)
                .orElseThrow(() -> new IllegalArgumentException("No pending approval found for Firebase UID: " + targetUid));

        pendingApprovalRepository.delete(approval);
        System.out.println("✅ PENDING APPROVAL DELETED");
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
        long pendingApprovals = pendingApprovalRepository.countByStatus("PENDING");
        long activeUsers = userRepository.countByIsAuthorized(true);
        long inactiveUsers = totalUsers - activeUsers;

        UserManagementStatisticsDto stats = new UserManagementStatisticsDto(
            totalUsers, adminUsers, regularUsers, pendingApprovals, activeUsers, inactiveUsers
        );

        System.out.println("✅ STATISTICS: Total=" + totalUsers + ", Pending=" + pendingApprovals);
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

        // Check if pending approval exists
        Optional<PendingApproval> pending = pendingApprovalRepository.findByFirebaseUid(firebaseUid);
        if (pending.isPresent()) {
            status.put("status", pending.get().getStatus());
            status.put("createdAt", pending.get().getCreatedAt());
            if ("REJECTED".equals(pending.get().getStatus())) {
                status.put("rejectionReason", pending.get().getRejectionReason());
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
     * Submit system access request details
     */
    public SystemAccessRequestDto submitSystemAccessRequest(SystemAccessRequestDto request) {
        System.out.println("🔵 SUBMIT SYSTEM ACCESS REQUEST: " + request.getFirebaseUid());

        // Check if user or pending approval exists
        Optional<PendingApproval> pending = pendingApprovalRepository.findByFirebaseUid(request.getFirebaseUid());
        if (pending.isEmpty()) {
            throw new IllegalArgumentException("No pending registration found for this Firebase UID");
        }

        // Check if access request already exists
        if (systemAccessRequestRepository.existsByFirebaseUid(request.getFirebaseUid())) {
            throw new IllegalStateException("System access request already exists for this Firebase UID");
        }

        // Create system access request
        SystemAccessRequest accessRequest = new SystemAccessRequest();
        accessRequest.setFirebaseUid(request.getFirebaseUid());
        accessRequest.setRequestPriority(request.getRequestPriority());
        accessRequest.setRequestDepartment(request.getRequestDepartment());
        accessRequest.setPhoneNumber(request.getPhoneNumber());
        accessRequest.setJustification(request.getJustification());

        SystemAccessRequest saved = systemAccessRequestRepository.save(accessRequest);
        System.out.println("✅ SYSTEM ACCESS REQUEST SAVED: ID=" + saved.getRequestId());

        return request;
    }

    /**
     * Get system access request by Firebase UID
     */
    public Optional<SystemAccessRequest> getSystemAccessRequest(String firebaseUid) {
        return systemAccessRequestRepository.findByFirebaseUid(firebaseUid);
    }

    /**
     * Convert PendingApproval entity to DTO
     */
    private PendingApprovalDto convertToDto(PendingApproval approval) {
        PendingApprovalDto dto = new PendingApprovalDto();
        dto.setId(approval.getId());
        dto.setFirebaseUid(approval.getFirebaseUid());
        dto.setUsername(approval.getUsername());
        dto.setEmail(approval.getEmail());
        dto.setDepartment(approval.getDepartment());
        dto.setContactNumber(approval.getContactNumber());
        dto.setRegistrationMethod(approval.getRegistrationMethod());
        dto.setRequestedAccessGroups(approval.getRequestedAccessGroups());
        dto.setBusinessJustification(approval.getBusinessJustification());
        dto.setPriorityLevel(approval.getPriorityLevel());
        dto.setStatus(approval.getStatus());
        dto.setCreatedAt(approval.getCreatedAt());
        dto.setReviewedBy(approval.getReviewedBy());
        dto.setReviewedAt(approval.getReviewedAt());
        
        // Include system access request data if available
        Optional<SystemAccessRequest> accessRequest = systemAccessRequestRepository.findByFirebaseUid(approval.getFirebaseUid());
        if (accessRequest.isPresent()) {
            SystemAccessRequest req = accessRequest.get();
            dto.setSystemAccessPriority(req.getRequestPriority());
            dto.setSystemAccessDepartment(req.getRequestDepartment());
            dto.setSystemAccessPhoneNumber(req.getPhoneNumber());
            dto.setSystemAccessJustification(req.getJustification());
        }
        dto.setDolibarrId(approval.getDolibarrId());
        return dto;
    }
}
