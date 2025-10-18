package com.apex.firefighter.controller;

import com.apex.firefighter.dto.accessgroup.AccessGroupDto;
import com.apex.firefighter.service.accessgroup.AccessGroupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing access groups
 */
@RestController
@RequestMapping("/api")
public class AccessGroupController {

    private final AccessGroupService accessGroupService;

    @Autowired
    public AccessGroupController(AccessGroupService accessGroupService) {
        this.accessGroupService = accessGroupService;
    }

    /**
     * GET /api/access-groups : Get all groups
     */
    @GetMapping("/access-groups")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccessGroupDto>> getAllGroups() {
        List<AccessGroupDto> groups = accessGroupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    /**
     * POST /api/access-groups : Create group (Super Admin)
     */
    @PostMapping("/access-groups")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AccessGroupDto> createGroup(@Valid @RequestBody AccessGroupDto groupDto) {
        try {
            AccessGroupDto createdGroup = accessGroupService.createGroup(groupDto);
            return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/access-groups/{groupId} : Update group (Super Admin)
     */
    @PutMapping("/access-groups/{groupId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AccessGroupDto> updateGroup(
            @PathVariable String groupId,
            @Valid @RequestBody AccessGroupDto groupDto) {
        try {
            AccessGroupDto updatedGroup = accessGroupService.updateGroup(groupId, groupDto);
            return ResponseEntity.ok(updatedGroup);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/access-groups/{groupId} : Delete group (Super Admin)
     */
    @DeleteMapping("/access-groups/{groupId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteGroup(@PathVariable String groupId) {
        try {
            accessGroupService.deleteGroup(groupId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/users/{uid}/access-groups : Get user's groups
     */
    @GetMapping("/users/{uid}/access-groups")
    @PreAuthorize("hasRole('ADMIN') or #uid == principal.userId")
    public ResponseEntity<List<AccessGroupDto>> getUserGroups(@PathVariable String uid) {
        try {
            List<AccessGroupDto> groups = accessGroupService.getUserGroups(uid);
            return ResponseEntity.ok(groups);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/users/{uid}/access-groups : Add user to group (Admin)
     */
    @PostMapping("/users/{uid}/access-groups")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addUserToGroup(
            @PathVariable String uid,
            @RequestParam String groupId,
            @RequestParam(required = false) String assignedBy) {
        try {
            accessGroupService.addUserToGroup(uid, groupId, assignedBy);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/users/{uid}/access-groups/{groupId} : Remove from group (Admin)
     */
    @DeleteMapping("/users/{uid}/access-groups/{groupId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeUserFromGroup(
            @PathVariable String uid,
            @PathVariable String groupId) {
        try {
            accessGroupService.removeUserFromGroup(uid, groupId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}