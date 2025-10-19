package com.apex.firefighter.service.accessgroup;

import com.apex.firefighter.dto.accessgroup.AccessGroupDto;
import com.apex.firefighter.model.User;
import com.apex.firefighter.model.accessgroup.AccessGroup;
import com.apex.firefighter.model.accessgroup.UserAccessGroup;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.repository.accessgroup.AccessGroupRepository;
import com.apex.firefighter.repository.accessgroup.UserAccessGroupRepository;
import com.apex.firefighter.service.DolibarrUserGroupService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccessGroupService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Access Group Service Unit Tests")
class AccessGroupServiceTest {

    @Mock
    private AccessGroupRepository accessGroupRepository;

    @Mock
    private UserAccessGroupRepository userAccessGroupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DolibarrUserGroupService dolibarrUserGroupService;

    @InjectMocks
    private AccessGroupService accessGroupService;

    private AccessGroup mockGroup;
    private AccessGroupDto mockGroupDto;
    private User mockUser;
    private UserAccessGroup mockUserGroup;

    @BeforeEach
    void setUp() {
        // Setup mock access group
        mockGroup = new AccessGroup("FINANCIAL", "Financial Access", "Access to financial records");
        mockGroup.setId(1L);

        // Setup mock DTO
        mockGroupDto = new AccessGroupDto();
        mockGroupDto.setId(1L);
        mockGroupDto.setGroupId("FINANCIAL");
        mockGroupDto.setName("Financial Access");
        mockGroupDto.setDescription("Access to financial records");

        // Setup mock user
        mockUser = new User();
        mockUser.setUserId("user-123");
        mockUser.setUsername("Test User");
        mockUser.setDolibarrId("dolibarr-123");

        // Setup mock user-group assignment
        mockUserGroup = new UserAccessGroup();
        mockUserGroup.setId(1L);
        mockUserGroup.setUserId("user-123");
        mockUserGroup.setGroupId("FINANCIAL");
    }

    // ========================================
    // GET ALL GROUPS TESTS
    // ========================================

    @Test
    @DisplayName("Should return all access groups")
    void testGetAllGroups_Success() {
        // Arrange
        when(accessGroupRepository.findAll()).thenReturn(Arrays.asList(mockGroup));

        // Act
        List<AccessGroupDto> result = accessGroupService.getAllGroups();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("FINANCIAL", result.get(0).getGroupId());
        verify(accessGroupRepository, times(1)).findAll();
    }

    // ========================================
    // CREATE GROUP TESTS
    // ========================================

    @Test
    @DisplayName("Should successfully create new access group")
    void testCreateGroup_Success() {
        // Arrange
        when(accessGroupRepository.existsByGroupId("FINANCIAL")).thenReturn(false);
        when(accessGroupRepository.save(any(AccessGroup.class))).thenReturn(mockGroup);

        // Act
        AccessGroupDto result = accessGroupService.createGroup(mockGroupDto);

        // Assert
        assertNotNull(result);
        assertEquals("FINANCIAL", result.getGroupId());
        assertEquals("Financial Access", result.getName());
        verify(accessGroupRepository, times(1)).save(any(AccessGroup.class));
    }

    @Test
    @DisplayName("Should throw exception when group ID already exists")
    void testCreateGroup_DuplicateGroupId() {
        // Arrange
        when(accessGroupRepository.existsByGroupId("FINANCIAL")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> accessGroupService.createGroup(mockGroupDto));
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(accessGroupRepository, never()).save(any());
    }

    // ========================================
    // UPDATE GROUP TESTS
    // ========================================

    @Test
    @DisplayName("Should successfully update access group")
    void testUpdateGroup_Success() {
        // Arrange
        AccessGroupDto updateDto = new AccessGroupDto();
        updateDto.setGroupId("FINANCIAL");
        updateDto.setName("Updated Financial Access");
        updateDto.setDescription("Updated description");

        when(accessGroupRepository.findByGroupId("FINANCIAL")).thenReturn(Optional.of(mockGroup));
        when(accessGroupRepository.save(any(AccessGroup.class))).thenReturn(mockGroup);

        // Act
        AccessGroupDto result = accessGroupService.updateGroup("FINANCIAL", updateDto);

        // Assert
        assertNotNull(result);
        verify(accessGroupRepository, times(1)).save(any(AccessGroup.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent group")
    void testUpdateGroup_NotFound() {
        // Arrange
        when(accessGroupRepository.findByGroupId("NONEXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> accessGroupService.updateGroup("NONEXISTENT", mockGroupDto));
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    // ========================================
    // DELETE GROUP TESTS
    // ========================================

    @Test
    @DisplayName("Should successfully delete access group and user assignments")
    void testDeleteGroup_Success() {
        // Arrange
        doNothing().when(userAccessGroupRepository).deleteByGroupId("FINANCIAL");
        doNothing().when(accessGroupRepository).deleteByGroupId("FINANCIAL");

        // Act
        assertDoesNotThrow(() -> accessGroupService.deleteGroup("FINANCIAL"));

        // Assert
        verify(userAccessGroupRepository, times(1)).deleteByGroupId("FINANCIAL");
        verify(accessGroupRepository, times(1)).deleteByGroupId("FINANCIAL");
    }

    // ========================================
    // GET USER GROUPS TESTS
    // ========================================

    @Test
    @DisplayName("Should return user's access groups")
    void testGetUserGroups_Success() {
        // Arrange
        when(userAccessGroupRepository.findByUserId("user-123"))
            .thenReturn(Arrays.asList(mockUserGroup));
        when(accessGroupRepository.findByGroupId("FINANCIAL"))
            .thenReturn(Optional.of(mockGroup));

        // Act
        List<AccessGroupDto> result = accessGroupService.getUserGroups("user-123");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("FINANCIAL", result.get(0).getGroupId());
    }

    // ========================================
    // ADD USER TO GROUP TESTS
    // ========================================

    @Test
    @DisplayName("Should successfully add user to group with Dolibarr sync")
    void testAddUserToGroup_SuccessWithDolibarr() throws SQLException {
        // Arrange
        when(userRepository.findByUserId("user-123")).thenReturn(Optional.of(mockUser));
        when(accessGroupRepository.findByGroupId("FINANCIAL")).thenReturn(Optional.of(mockGroup));
        when(userAccessGroupRepository.existsByUserIdAndGroupId("user-123", "FINANCIAL"))
            .thenReturn(false);
        when(userAccessGroupRepository.save(any(UserAccessGroup.class))).thenReturn(mockUserGroup);
        doNothing().when(dolibarrUserGroupService).addUserToGroup(anyString(), anyString(), anyString());

        // Act
        assertDoesNotThrow(() -> 
            accessGroupService.addUserToGroup("user-123", "FINANCIAL", "admin-123"));

        // Assert
        verify(userAccessGroupRepository, times(1)).save(any(UserAccessGroup.class));
        verify(dolibarrUserGroupService, times(1))
            .addUserToGroup("dolibarr-123", "FINANCIAL", "admin-123");
    }

    @Test
    @DisplayName("Should add user to group without Dolibarr sync if no Dolibarr ID")
    void testAddUserToGroup_WithoutDolibarrId() throws SQLException {
        // Arrange
        User userWithoutDolibarr = new User();
        userWithoutDolibarr.setUserId("user-123");
        userWithoutDolibarr.setDolibarrId(null);

        when(userRepository.findByUserId("user-123")).thenReturn(Optional.of(userWithoutDolibarr));
        when(accessGroupRepository.findByGroupId("FINANCIAL")).thenReturn(Optional.of(mockGroup));
        when(userAccessGroupRepository.existsByUserIdAndGroupId("user-123", "FINANCIAL"))
            .thenReturn(false);
        when(userAccessGroupRepository.save(any(UserAccessGroup.class))).thenReturn(mockUserGroup);

        // Act
        assertDoesNotThrow(() -> 
            accessGroupService.addUserToGroup("user-123", "FINANCIAL", "admin-123"));

        // Assert
        verify(userAccessGroupRepository, times(1)).save(any(UserAccessGroup.class));
        verify(dolibarrUserGroupService, never()).addUserToGroup(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testAddUserToGroup_UserNotFound() {
        // Arrange
        when(userRepository.findByUserId("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> accessGroupService.addUserToGroup("nonexistent", "FINANCIAL", "admin"));
        
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Should throw exception when group not found")
    void testAddUserToGroup_GroupNotFound() {
        // Arrange
        when(userRepository.findByUserId("user-123")).thenReturn(Optional.of(mockUser));
        when(accessGroupRepository.findByGroupId("NONEXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> accessGroupService.addUserToGroup("user-123", "NONEXISTENT", "admin"));
        
        assertTrue(exception.getMessage().contains("Access group not found"));
    }

    @Test
    @DisplayName("Should throw exception when user already in group")
    void testAddUserToGroup_AlreadyInGroup() {
        // Arrange
        when(userRepository.findByUserId("user-123")).thenReturn(Optional.of(mockUser));
        when(accessGroupRepository.findByGroupId("FINANCIAL")).thenReturn(Optional.of(mockGroup));
        when(userAccessGroupRepository.existsByUserIdAndGroupId("user-123", "FINANCIAL"))
            .thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> accessGroupService.addUserToGroup("user-123", "FINANCIAL", "admin"));
        
        assertTrue(exception.getMessage().contains("already in group"));
    }

    // ========================================
    // REMOVE USER FROM GROUP TESTS
    // ========================================

    @Test
    @DisplayName("Should successfully remove user from group with Dolibarr sync")
    void testRemoveUserFromGroup_SuccessWithDolibarr() throws Exception {
        // Arrange
        when(userAccessGroupRepository.existsByUserIdAndGroupId("user-123", "FINANCIAL"))
            .thenReturn(true);
        when(userRepository.findByUserId("user-123")).thenReturn(Optional.of(mockUser));
        doNothing().when(dolibarrUserGroupService).removeUserFromGroup(anyString(), anyString());
        doNothing().when(userAccessGroupRepository).deleteByUserIdAndGroupId("user-123", "FINANCIAL");

        // Act
        assertDoesNotThrow(() -> 
            accessGroupService.removeUserFromGroup("user-123", "FINANCIAL"));

        // Assert
        verify(userAccessGroupRepository, times(1)).deleteByUserIdAndGroupId("user-123", "FINANCIAL");
        verify(dolibarrUserGroupService, times(1))
            .removeUserFromGroup("dolibarr-123", "FINANCIAL");
    }

    @Test
    @DisplayName("Should throw exception when user not in group")
    void testRemoveUserFromGroup_NotInGroup() {
        // Arrange
        when(userAccessGroupRepository.existsByUserIdAndGroupId("user-123", "FINANCIAL"))
            .thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> accessGroupService.removeUserFromGroup("user-123", "FINANCIAL"));
        
        assertTrue(exception.getMessage().contains("not in group"));
    }

    @Test
    @DisplayName("Should continue removal even if Dolibarr sync fails")
    void testRemoveUserFromGroup_DolibarrFailure() throws Exception {
        // Arrange
        when(userAccessGroupRepository.existsByUserIdAndGroupId("user-123", "FINANCIAL"))
            .thenReturn(true);
        when(userRepository.findByUserId("user-123")).thenReturn(Optional.of(mockUser));
        doThrow(new RuntimeException("Dolibarr error"))
            .when(dolibarrUserGroupService).removeUserFromGroup(anyString(), anyString());
        doNothing().when(userAccessGroupRepository).deleteByUserIdAndGroupId("user-123", "FINANCIAL");

        // Act - should not throw exception
        assertDoesNotThrow(() -> 
            accessGroupService.removeUserFromGroup("user-123", "FINANCIAL"));

        // Assert - local deletion still happens
        verify(userAccessGroupRepository, times(1)).deleteByUserIdAndGroupId("user-123", "FINANCIAL");
    }
}
