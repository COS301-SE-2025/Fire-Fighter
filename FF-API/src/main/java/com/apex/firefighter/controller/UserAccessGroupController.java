package com.apex.firefighter.controller;

import com.apex.firefighter.service.accessgroup.UserAccessGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for managing user access groups
 * Provides endpoints for assigning and managing access groups for users
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Access Groups", description = "User access group management operations")
public class UserAccessGroupController {

    private final UserAccessGroupService accessGroupService;

    @Autowired
    public UserAccessGroupController(UserAccessGroupService accessGroupService) {
        this.accessGroupService = accessGroupService;
    }

    /**
     * GET USER'S ACCESS GROUPS
     * GET /api/users/{firebaseUid}/access-groups
     */
    @Operation(summary = "Get user's access groups",
               description = "Retrieve all access groups assigned to a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access groups retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{firebaseUid}/access-groups")
    public ResponseEntity<?> getUserAccessGroups(
            @Parameter(description = "User Firebase UID")
            @PathVariable String firebaseUid) {
        try {
            System.out.println("🔵 GET USER ACCESS GROUPS REQUEST: User=" + firebaseUid);
            
            List<String> groups = accessGroupService.getUserAccessGroups(firebaseUid);
            
            System.out.println("✅ RETRIEVED " + groups.size() + " ACCESS GROUPS");
            return ResponseEntity.ok(Map.of("groupIds", groups));
        } catch (Exception e) {
            System.err.println("❌ GET ACCESS GROUPS FAILED: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to retrieve access groups"));
        }
    }

    /**
     * UPDATE USER'S ACCESS GROUPS (ADMIN ONLY)
     * PUT /api/users/{firebaseUid}/admin/access-groups
     */
    @Operation(summary = "Update user's access groups (Admin Only)",
               description = "Replace all access groups for a user. Requires admin privileges.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access groups updated successfully"),
        @ApiResponse(responseCode = "403", description = "Admin privileges required"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{firebaseUid}/admin/access-groups")
    public ResponseEntity<?> updateUserAccessGroups(
            @Parameter(description = "Admin Firebase UID")
            @RequestHeader("X-Firebase-UID") String adminUid,
            @Parameter(description = "Target User Firebase UID")
            @PathVariable String firebaseUid,
            @Parameter(description = "List of group IDs to assign")
            @RequestBody Map<String, List<String>> requestBody) {
        try {
            System.out.println("🔵 UPDATE USER ACCESS GROUPS REQUEST:");
            System.out.println("  Admin: " + adminUid);
            System.out.println("  Target User: " + firebaseUid);
            
            List<String> groupIds = requestBody.get("groupIds");
            if (groupIds == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "groupIds field is required"));
            }
            
            System.out.println("  Groups: " + groupIds);
            
            List<String> updatedGroups = accessGroupService.updateUserAccessGroups(
                adminUid, 
                firebaseUid, 
                groupIds
            );
            
            System.out.println("✅ ACCESS GROUPS UPDATED SUCCESSFULLY");
            return ResponseEntity.ok(Map.of(
                "message", "Access groups updated successfully",
                "groupIds", updatedGroups
            ));
        } catch (SecurityException e) {
            System.err.println("⚠️ ACCESS DENIED: " + e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("❌ UPDATE FAILED: " + e.getMessage());
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to update access groups"));
        }
    }

    /**
     * ADD ACCESS GROUP TO USER (ADMIN ONLY)
     * POST /api/users/{firebaseUid}/admin/access-groups/{groupId}
     */
    @Operation(summary = "Add access group to user (Admin Only)",
               description = "Add a single access group to a user. Requires admin privileges.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access group added successfully"),
        @ApiResponse(responseCode = "403", description = "Admin privileges required"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{firebaseUid}/admin/access-groups/{groupId}")
    public ResponseEntity<?> addAccessGroupToUser(
            @Parameter(description = "Admin Firebase UID")
            @RequestHeader("X-Firebase-UID") String adminUid,
            @Parameter(description = "Target User Firebase UID")
            @PathVariable String firebaseUid,
            @Parameter(description = "Group ID to add")
            @PathVariable String groupId) {
        try {
            System.out.println("🔵 ADD ACCESS GROUP REQUEST:");
            System.out.println("  Admin: " + adminUid);
            System.out.println("  User: " + firebaseUid);
            System.out.println("  Group: " + groupId);
            
            accessGroupService.addAccessGroupToUser(adminUid, firebaseUid, groupId);
            
            System.out.println("✅ ACCESS GROUP ADDED");
            return ResponseEntity.ok(Map.of("message", "Access group added successfully"));
        } catch (SecurityException e) {
            System.err.println("⚠️ ACCESS DENIED: " + e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ ADD FAILED: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to add access group"));
        }
    }

    /**
     * REMOVE ACCESS GROUP FROM USER (ADMIN ONLY)
     * DELETE /api/users/{firebaseUid}/admin/access-groups/{groupId}
     */
    @Operation(summary = "Remove access group from user (Admin Only)",
               description = "Remove a single access group from a user. Requires admin privileges.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access group removed successfully"),
        @ApiResponse(responseCode = "403", description = "Admin privileges required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{firebaseUid}/admin/access-groups/{groupId}")
    public ResponseEntity<?> removeAccessGroupFromUser(
            @Parameter(description = "Admin Firebase UID")
            @RequestHeader("X-Firebase-UID") String adminUid,
            @Parameter(description = "Target User Firebase UID")
            @PathVariable String firebaseUid,
            @Parameter(description = "Group ID to remove")
            @PathVariable String groupId) {
        try {
            System.out.println("🔵 REMOVE ACCESS GROUP REQUEST:");
            System.out.println("  Admin: " + adminUid);
            System.out.println("  User: " + firebaseUid);
            System.out.println("  Group: " + groupId);
            
            accessGroupService.removeAccessGroupFromUser(adminUid, firebaseUid, groupId);
            
            System.out.println("✅ ACCESS GROUP REMOVED");
            return ResponseEntity.ok(Map.of("message", "Access group removed successfully"));
        } catch (SecurityException e) {
            System.err.println("⚠️ ACCESS DENIED: " + e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ REMOVE FAILED: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to remove access group"));
        }
    }
}
