package com.apex.firefighter.controller;

import com.apex.firefighter.dto.registration.*;
import com.apex.firefighter.service.registration.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for handling user registration and approval workflows
 */
@RestController
@RequestMapping("/api/registration")
@Tag(name = "User Registration", description = "User registration and approval management endpoints")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Submit a new user registration request
     * POST /api/registration/submit
     */
    @Operation(summary = "Submit user registration",
               description = "Submit a new user registration request for admin approval")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registration submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data"),
        @ApiResponse(responseCode = "409", description = "User already exists or pending"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/submit")
    public ResponseEntity<?> submitRegistration(@Valid @RequestBody RegistrationRequestDto request) {
        try {
            System.out.println("🔵 REGISTRATION REQUEST:");
            System.out.println("  Firebase UID: " + request.getFirebaseUid());
            System.out.println("  Username: " + request.getUsername());
            System.out.println("  Email: " + request.getEmail());
            System.out.println("  Registration Method: " + request.getRegistrationMethod());
            
            PendingApprovalDto approval = registrationService.submitRegistrationRequest(request);
            
            System.out.println("✅ REGISTRATION SUBMITTED: ID=" + approval.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(approval);
        } catch (IllegalStateException e) {
            System.err.println("⚠️ REGISTRATION CONFLICT: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ REGISTRATION FAILED: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to submit registration: " + e.getMessage()));
        }
    }

    /**
     * Get all pending approval requests (Admin only)
     * GET /api/registration/admin/pending
     */
    @Operation(summary = "Get pending approvals",
               description = "Retrieve all pending user registration requests (Admin only)",
               security = @SecurityRequirement(name = "firebase"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pending approvals"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/admin/pending")
    public ResponseEntity<?> getPendingApprovals(
            @Parameter(description = "Admin Firebase UID") 
            @RequestHeader("X-Firebase-UID") String adminUid) {
        try {
            System.out.println("🔵 GET PENDING APPROVALS REQUEST:");
            System.out.println("  Admin UID: " + adminUid);
            
            List<PendingApprovalDto> pendingApprovals = registrationService.getPendingApprovals(adminUid);
            
            System.out.println("✅ RETRIEVED " + pendingApprovals.size() + " PENDING APPROVALS");
            return ResponseEntity.ok(pendingApprovals);
        } catch (SecurityException e) {
            System.err.println("⚠️ ACCESS DENIED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ GET PENDING APPROVALS FAILED: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve pending approvals"));
        }
    }

    /**
     * Approve a pending user registration (Admin only)
     * PUT /api/registration/admin/approve
     */
    @Operation(summary = "Approve user registration",
               description = "Approve a pending user registration and create user account (Admin only)",
               security = @SecurityRequirement(name = "firebase"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User approved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
        @ApiResponse(responseCode = "404", description = "Pending approval not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/admin/approve")
    public ResponseEntity<?> approveUser(
            @Parameter(description = "Admin Firebase UID") 
            @RequestHeader("X-Firebase-UID") String adminUid,
            @Valid @RequestBody ApprovalDecisionDto decision) {
        try {
            System.out.println("🔵 APPROVE USER REQUEST:");
            System.out.println("  Admin UID: " + adminUid);
            System.out.println("  Target UID: " + decision.getFirebaseUid());
            System.out.println("  Access Groups: " + decision.getAssignedAccessGroups());
            
            PendingApprovalDto approved = registrationService.approveUser(
                adminUid, 
                decision.getFirebaseUid(), 
                decision.getAssignedAccessGroups(),
                decision.getDepartment(),
                decision.getDolibarrId()
            );
            
            System.out.println("✅ USER APPROVED: " + approved.getUsername());
            return ResponseEntity.ok(approved);
        } catch (SecurityException e) {
            System.err.println("⚠️ ACCESS DENIED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ NOT FOUND: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ APPROVAL FAILED: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to approve user: " + e.getMessage()));
        }
    }

    /**
     * Reject a pending user registration (Admin only)
     * PUT /api/registration/admin/reject
     */
    @Operation(summary = "Reject user registration",
               description = "Reject a pending user registration request (Admin only)",
               security = @SecurityRequirement(name = "firebase"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User rejected successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
        @ApiResponse(responseCode = "404", description = "Pending approval not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/admin/reject")
    public ResponseEntity<?> rejectUser(
            @Parameter(description = "Admin Firebase UID") 
            @RequestHeader("X-Firebase-UID") String adminUid,
            @Valid @RequestBody ApprovalDecisionDto decision) {
        try {
            System.out.println("🔵 REJECT USER REQUEST:");
            System.out.println("  Admin UID: " + adminUid);
            System.out.println("  Target UID: " + decision.getFirebaseUid());
            System.out.println("  Reason: " + decision.getRejectionReason());
            
            PendingApprovalDto rejected = registrationService.rejectUser(
                adminUid, 
                decision.getFirebaseUid(), 
                decision.getRejectionReason()
            );
            
            System.out.println("✅ USER REJECTED: " + rejected.getUsername());
            return ResponseEntity.ok(rejected);
        } catch (SecurityException e) {
            System.err.println("⚠️ ACCESS DENIED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ NOT FOUND: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ REJECTION FAILED: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to reject user: " + e.getMessage()));
        }
    }

    /**
     * Delete a pending approval request (Admin only)
     * DELETE /api/registration/admin/pending/{firebaseUid}
     */
    @Operation(summary = "Delete pending approval",
               description = "Delete a pending approval request (Admin only)",
               security = @SecurityRequirement(name = "firebase"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pending approval deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
        @ApiResponse(responseCode = "404", description = "Pending approval not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/admin/pending/{firebaseUid}")
    public ResponseEntity<?> deletePendingApproval(
            @Parameter(description = "Admin Firebase UID") 
            @RequestHeader("X-Firebase-UID") String adminUid,
            @PathVariable String firebaseUid) {
        try {
            System.out.println("🔵 DELETE PENDING APPROVAL REQUEST:");
            System.out.println("  Admin UID: " + adminUid);
            System.out.println("  Target UID: " + firebaseUid);
            
            registrationService.deletePendingApproval(adminUid, firebaseUid);
            
            System.out.println("✅ PENDING APPROVAL DELETED");
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            System.err.println("⚠️ ACCESS DENIED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ NOT FOUND: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ DELETE FAILED: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete pending approval"));
        }
    }

    /**
     * Get user management statistics (Admin only)
     * GET /api/registration/admin/statistics
     */
    @Operation(summary = "Get user statistics",
               description = "Get comprehensive user management statistics (Admin only)",
               security = @SecurityRequirement(name = "firebase"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/admin/statistics")
    public ResponseEntity<?> getUserStatistics(
            @Parameter(description = "Admin Firebase UID") 
            @RequestHeader("X-Firebase-UID") String adminUid) {
        try {
            System.out.println("🔵 GET USER STATISTICS REQUEST:");
            System.out.println("  Admin UID: " + adminUid);
            
            UserManagementStatisticsDto stats = registrationService.getUserStatistics(adminUid);
            
            System.out.println("✅ STATISTICS RETRIEVED");
            return ResponseEntity.ok(stats);
        } catch (SecurityException e) {
            System.err.println("⚠️ ACCESS DENIED: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ GET STATISTICS FAILED: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve statistics"));
        }
    }

    /**
     * Check registration status for a Firebase UID
     * GET /api/registration/status/{firebaseUid}
     */
    @Operation(summary = "Check registration status",
               description = "Check if a user has a pending registration or is already registered")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/status/{firebaseUid}")
    public ResponseEntity<?> getRegistrationStatus(@PathVariable String firebaseUid) {
        try {
            System.out.println("🔵 CHECK REGISTRATION STATUS:");
            System.out.println("  Firebase UID: " + firebaseUid);
            
            Map<String, Object> status = registrationService.getRegistrationStatus(firebaseUid);
            
            System.out.println("✅ STATUS: " + status.get("status"));
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            System.err.println("❌ CHECK STATUS FAILED: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to check registration status"));
        }
    }
}
