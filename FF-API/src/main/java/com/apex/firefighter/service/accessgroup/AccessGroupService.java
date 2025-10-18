package com.apex.firefighter.service.accessgroup;

import com.apex.firefighter.model.accessgroup.AccessGroup;
import com.apex.firefighter.model.accessgroup.UserAccessGroup;
import com.apex.firefighter.repository.accessgroup.AccessGroupRepository;
import com.apex.firefighter.repository.accessgroup.UserAccessGroupRepository;
import com.apex.firefighter.dto.accessgroup.AccessGroupDto;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.DolibarrUserGroupService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.SQLException;

/**
 * Service for managing access groups and user-group assignments
 */
@Service
@Transactional
public class AccessGroupService {

    private final AccessGroupRepository accessGroupRepository;
    private final UserAccessGroupRepository userAccessGroupRepository;
    private final UserRepository userRepository;
    private final DolibarrUserGroupService dolibarrUserGroupService;

    @Autowired
    public AccessGroupService(
            AccessGroupRepository accessGroupRepository,
            UserAccessGroupRepository userAccessGroupRepository,
            UserRepository userRepository,
            DolibarrUserGroupService dolibarrUserGroupService) {
        this.accessGroupRepository = accessGroupRepository;
        this.userAccessGroupRepository = userAccessGroupRepository;
        this.userRepository = userRepository;
        this.dolibarrUserGroupService = dolibarrUserGroupService;
    }

    /**
     * Get all access groups
     */
    public List<AccessGroupDto> getAllGroups() {
        return accessGroupRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new access group
     */
    public AccessGroupDto createGroup(AccessGroupDto dto) {
        // Validate group ID doesn't already exist
        if (accessGroupRepository.existsByGroupId(dto.getGroupId())) {
            throw new IllegalArgumentException("Group ID already exists: " + dto.getGroupId());
        }

        // Create new access group
        AccessGroup group = new AccessGroup(dto.getGroupId(), dto.getName(), dto.getDescription());
        AccessGroup savedGroup = accessGroupRepository.save(group);
        return convertToDto(savedGroup);
    }

    /**
     * Update an existing access group
     */
    public AccessGroupDto updateGroup(String groupId, AccessGroupDto dto) {
        AccessGroup group = accessGroupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Access group not found: " + groupId));

        // Update fields
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());

        AccessGroup updatedGroup = accessGroupRepository.save(group);
        return convertToDto(updatedGroup);
    }

    /**
     * Delete an access group
     */
    public void deleteGroup(String groupId) {
        // Remove all user assignments first
        userAccessGroupRepository.deleteByGroupId(groupId);
        
        // Delete the group
        accessGroupRepository.deleteByGroupId(groupId);
    }

    /**
     * Get all groups for a user
     */
    public List<AccessGroupDto> getUserGroups(String userId) {
        return userAccessGroupRepository.findByUserId(userId).stream()
                .map(userGroup -> accessGroupRepository.findByGroupId(userGroup.getGroupId())
                        .map(this::convertToDto)
                        .orElse(null))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Add a user to a group
     */
    public void addUserToGroup(String userId, String groupId, String assignedBy) {
        // Verify user exists
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // Verify group exists
        AccessGroup group = accessGroupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Access group not found: " + groupId));

        // Check if user is already in group
        if (userAccessGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalStateException("User is already in group: " + groupId);
        }

        // Add user to group
        UserAccessGroup userGroup = new UserAccessGroup();
        userGroup.setUserId(userId);
        userGroup.setGroupId(groupId); // Verified by findByGroupId above
        userGroup.setAssignedBy(assignedBy);
        userGroup.setAssignedAt(ZonedDateTime.now());
        userAccessGroupRepository.save(userGroup);

        // Sync with Dolibarr if user has Dolibarr ID
        if (user.getDolibarrId() != null) {
            try {
                dolibarrUserGroupService.addUserToGroup(user.getDolibarrId(), groupId, assignedBy);
            } catch (RuntimeException | SQLException e) {
                // Log error but don't fail the operation
                System.err.println("Failed to sync user group to Dolibarr: " + e.getMessage());
            }
        }
    }

    /**
     * Remove a user from a group
     */
    public void removeUserFromGroup(String userId, String groupId) {
        // Verify assignment exists
        if (!userAccessGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new EntityNotFoundException("User is not in group: " + groupId);
        }

        // Remove from Dolibarr if user has Dolibarr ID
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        if (user.getDolibarrId() != null) {
            try {
                dolibarrUserGroupService.removeUserFromGroup(user.getDolibarrId(), groupId);
            } catch (RuntimeException | SQLException e) {
                // Log error but don't fail the operation
                System.err.println("Failed to sync user group removal from Dolibarr: " + e.getMessage());
            }
        }

        // Remove user from group
        userAccessGroupRepository.deleteByUserIdAndGroupId(userId, groupId);
    }

    /**
     * Convert AccessGroup entity to DTO
     */
    private AccessGroupDto convertToDto(AccessGroup group) {
        AccessGroupDto dto = new AccessGroupDto();
        dto.setId(group.getId());
        dto.setGroupId(group.getGroupId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        return dto;
    }
}