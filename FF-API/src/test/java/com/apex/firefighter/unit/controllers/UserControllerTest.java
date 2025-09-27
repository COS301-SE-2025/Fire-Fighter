package com.apex.firefighter.unit.controllers;

import com.apex.firefighter.config.TestConfig;
import com.apex.firefighter.controller.UserController;
import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import com.apex.firefighter.service.auth.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private final String TEST_USER_ID = "test-user-123";
    private final String TEST_EMAIL = "test@example.com";
    private final String BASE_URL = "/api/users";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(TEST_USER_ID);
        testUser.setUsername("testuser");
        testUser.setEmail(TEST_EMAIL);
        testUser.setDepartment("Fire Department");
        testUser.setContactNumber("123-456-7890");
        testUser.setIsAdmin(false);
        testUser.setIsAuthorized(true);
        testUser.setCreatedAt(ZonedDateTime.now());
    }

    @Test
    @WithMockUser
    void verifyUser_ShouldReturnVerifiedUser() throws Exception {
      
        when(userService.verifyOrCreateUser(TEST_USER_ID, "testuser", TEST_EMAIL, "Fire Department"))
                .thenReturn(testUser);

        mockMvc.perform(post(BASE_URL + "/verify")
                .param("firebaseUid", TEST_USER_ID)
                .param("username", "testuser")
                .param("email", TEST_EMAIL)
                .param("department", "Fire Department")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.department").value("Fire Department"));

        verify(userService).verifyOrCreateUser(TEST_USER_ID, "testuser", TEST_EMAIL, "Fire Department");
    }

    @Test
    @WithMockUser
    void verifyUser_WithoutDepartment_ShouldReturnVerifiedUser() throws Exception {
        
        when(userService.verifyOrCreateUser(TEST_USER_ID, "testuser", TEST_EMAIL, null))
                .thenReturn(testUser);

        mockMvc.perform(post(BASE_URL + "/verify")
                .param("firebaseUid", TEST_USER_ID)
                .param("username", "testuser")
                .param("email", TEST_EMAIL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID));

        verify(userService).verifyOrCreateUser(TEST_USER_ID, "testuser", TEST_EMAIL, null);
    }

    @Test
    @WithMockUser
    void verifyUser_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        
        when(userService.verifyOrCreateUser(anyString(), anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post(BASE_URL + "/verify")
                .param("firebaseUid", TEST_USER_ID)
                .param("username", "testuser")
                .param("email", TEST_EMAIL)
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).verifyOrCreateUser(TEST_USER_ID, "testuser", TEST_EMAIL, null);
    }

    @Test
    @WithMockUser
    void getUserInfo_WhenExists_ShouldReturnUser() throws Exception {
        
        when(userService.getUserWithRoles(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));

        verify(userService).getUserWithRoles(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getUserInfo_WhenNotExists_ShouldReturnNotFound() throws Exception {
        
        when(userService.getUserWithRoles(TEST_USER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID))
                .andExpect(status().isNotFound());

        verify(userService).getUserWithRoles(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getUserByEmail_WhenExists_ShouldReturnUser() throws Exception {
        
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get(BASE_URL + "/email/" + TEST_EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));

        verify(userService).getUserByEmail(TEST_EMAIL);
    }

    @Test
    @WithMockUser
    void getUserByEmail_WhenNotExists_ShouldReturnNotFound() throws Exception {
        
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/email/" + TEST_EMAIL))
                .andExpect(status().isNotFound());

        verify(userService).getUserByEmail(TEST_EMAIL);
    }

    @Test
    @WithMockUser
    void authorizeUser_WhenSuccessful_ShouldReturnAuthorizedUser() throws Exception {
        
        testUser.setIsAuthorized(true);
        when(userService.authorizeUser(TEST_USER_ID, "admin-123")).thenReturn(testUser);

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/authorize")
                .param("authorizedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.authorized").value(true));

        verify(userService).authorizeUser(TEST_USER_ID, "admin-123");
    }

    @Test
    @WithMockUser
    void authorizeUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        
        when(userService.authorizeUser(TEST_USER_ID, "admin-123"))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/authorize")
                .param("authorizedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).authorizeUser(TEST_USER_ID, "admin-123");
    }

    @Test
    @WithMockUser
    void revokeUserAuthorization_WhenSuccessful_ShouldReturnRevokedUser() throws Exception {
        
        testUser.setIsAuthorized(false);
        when(userService.revokeUserAuthorization(TEST_USER_ID, "admin-123")).thenReturn(testUser);

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/revoke")
                .param("revokedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.authorized").value(false));

        verify(userService).revokeUserAuthorization(TEST_USER_ID, "admin-123");
    }

    @Test
    @WithMockUser
    void revokeUserAuthorization_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        
        when(userService.revokeUserAuthorization(TEST_USER_ID, "admin-123"))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/revoke")
                .param("revokedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).revokeUserAuthorization(TEST_USER_ID, "admin-123");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenSuccessful_ShouldReturnUpdatedUser() throws Exception {
        
        testUser.setContactNumber("987-654-3210");
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210")).thenReturn(testUser);

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.contactNumber").value("987-654-3210"));

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WithMissingField_ShouldReturnBadRequest() throws Exception {
        
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenUserNotFound_ShouldReturnNotFound() throws Exception {

        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new RuntimeException("User not found with Firebase UID: " + TEST_USER_ID));

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WithNullContactNumber_ShouldHandleServiceError() throws Exception {
     
        when(userService.updateContactNumber(eq(TEST_USER_ID), anyString()))
                .thenReturn(null);

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        // The empty string should be passed to service
        verify(userService).updateContactNumber(eq(TEST_USER_ID), anyString());
    }

    @Test
    @WithMockUser
    void updateContactNumber_WithEmptyStringAfterTrim_ShouldHandleServiceError() throws Exception {
        
        when(userService.updateContactNumber(TEST_USER_ID, "   ")).thenReturn(null);

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "   ")  // Only whitespace
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "   ");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WithValidContactNumber_ShouldTrimAndCallService() throws Exception {
        
        testUser.setContactNumber("123-456-7890");
        when(userService.updateContactNumber(TEST_USER_ID, "123-456-7890")).thenReturn(testUser);

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "  123-456-7890  ")  
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactNumber").value("123-456-7890"));

        verify(userService).updateContactNumber(TEST_USER_ID, "123-456-7890");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenNonRuntimeExceptionThrown_ShouldReturnInternalServerError() throws Exception {
        
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new IllegalStateException("Unexpected error"));

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenRuntimeExceptionWithoutNotFoundMessage_ShouldReturnInternalServerError() throws Exception {
        
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new RuntimeException("Some other database error"));

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WithNullContactNumber_ShouldSkipTrimming() throws Exception {
        
        when(userService.updateContactNumber(TEST_USER_ID, "")).thenReturn(null);

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "")  
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(eq(TEST_USER_ID), eq(""));
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenGenericExceptionWithStackTrace_ShouldReturnInternalServerError() throws Exception {
       
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new IllegalArgumentException("Invalid phone number format"));

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenNullPointerExceptionInGenericCatch_ShouldReturnInternalServerError() throws Exception {
       
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new NullPointerException("Null pointer in service"));

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void getAuthorizedUsers_ShouldReturnAuthorizedUsersList() throws Exception {
        
        testUser.setIsAuthorized(true);
        List<User> authorizedUsers = Arrays.asList(testUser);
        when(userService.getAuthorizedUsers()).thenReturn(authorizedUsers);

        mockMvc.perform(get(BASE_URL + "/authorized"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$[0].authorized").value(true));

        verify(userService).getAuthorizedUsers();
    }

    @Test
    @WithMockUser
    void getUsersByRole_ShouldReturnUsersWithSpecificRole() throws Exception {
        // Arrange
        testUser.setRole("FIREFIGHTER");
        List<User> firefighters = Arrays.asList(testUser);
        when(userService.getUsersByRole("FIREFIGHTER")).thenReturn(firefighters);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/role/FIREFIGHTER"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID));

        verify(userService).getUsersByRole("FIREFIGHTER");
    }


    @Test
    @WithMockUser
    void checkAuthorization_WhenUserIsAuthorized_ShouldReturnTrue() throws Exception {
        
        when(userService.isUserAuthorized(TEST_USER_ID)).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID + "/authorized"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        verify(userService).isUserAuthorized(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void checkAuthorization_WhenUserIsNotAuthorized_ShouldReturnFalse() throws Exception {
        
        when(userService.isUserAuthorized(TEST_USER_ID)).thenReturn(false);

        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID + "/authorized"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        verify(userService).isUserAuthorized(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void checkRole_WhenUserHasRole_ShouldReturnTrue() throws Exception {
        
        String roleName = "ADMIN";
        when(userService.hasRole(TEST_USER_ID, roleName)).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID + "/roles/" + roleName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        verify(userService).hasRole(TEST_USER_ID, roleName);
    }

    @Test
    @WithMockUser
    void checkRole_WhenUserDoesNotHaveRole_ShouldReturnFalse() throws Exception {
        
        String roleName = "ADMIN";
        when(userService.hasRole(TEST_USER_ID, roleName)).thenReturn(false);

        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID + "/roles/" + roleName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        verify(userService).hasRole(TEST_USER_ID, roleName);
    }


    @Test
    @WithMockUser
    void revokeAuthorization_WhenSuccessful_ShouldReturnRevokedUser() throws Exception {
       
        testUser.setIsAuthorized(false);
        when(userService.revokeUserAuthorization(TEST_USER_ID, "admin-123")).thenReturn(testUser);

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/revoke")
                .param("revokedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.authorized").value(false));

        verify(userService).revokeUserAuthorization(TEST_USER_ID, "admin-123");
    }

    @Test
    @WithMockUser
    void revokeAuthorization_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        
        when(userService.revokeUserAuthorization(TEST_USER_ID, "admin-123"))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/revoke")
                .param("revokedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).revokeUserAuthorization(TEST_USER_ID, "admin-123");
    }

    @Test
    @WithMockUser
    void assignRole_WhenSuccessful_ShouldReturnUpdatedUser() throws Exception {
        
        when(userService.assignRole(TEST_USER_ID, "ADMIN", "admin-123")).thenReturn(testUser);

        mockMvc.perform(post(BASE_URL + "/" + TEST_USER_ID + "/roles")
                .param("roleName", "ADMIN")
                .param("assignedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID));

        verify(userService).assignRole(TEST_USER_ID, "ADMIN", "admin-123");
    }

    @Test
    @WithMockUser
    void assignRole_WhenInvalidRole_ShouldReturnBadRequest() throws Exception {

        when(userService.assignRole(TEST_USER_ID, "INVALID_ROLE", "admin-123"))
                .thenThrow(new RuntimeException("Invalid role"));

        mockMvc.perform(post(BASE_URL + "/" + TEST_USER_ID + "/roles")
                .param("roleName", "INVALID_ROLE")
                .param("assignedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userService).assignRole(TEST_USER_ID, "INVALID_ROLE", "admin-123");
    }


    @Test
    @WithMockUser
    void getUsersByDepartment_ShouldReturnUsersInDepartment() throws Exception {
        
        String department = "Fire Department";
        List<User> departmentUsers = Arrays.asList(testUser);
        when(userService.getUsersByDepartment(department)).thenReturn(departmentUsers);

        mockMvc.perform(get(BASE_URL + "/department/" + department))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$[0].department").value(department));

        verify(userService).getUsersByDepartment(department);
    }

    @Test
    @WithMockUser
    void getUsersByDepartment_WhenEmptyDepartment_ShouldReturnEmptyList() throws Exception {
        
        String department = "Empty Department";
        when(userService.getUsersByDepartment(department)).thenReturn(Arrays.asList());

        mockMvc.perform(get(BASE_URL + "/department/" + department))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService).getUsersByDepartment(department);
    }

    @Test
    @WithMockUser
    void getAuthorizedUsersByRole_ShouldReturnAuthorizedUsersWithRole() throws Exception {
        
        String roleName = "FIREFIGHTER";
        testUser.setIsAuthorized(true);
        List<User> authorizedFirefighters = Arrays.asList(testUser);
        when(userService.getAuthorizedUsersByRole(roleName)).thenReturn(authorizedFirefighters);

        mockMvc.perform(get(BASE_URL + "/authorized/role/" + roleName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$[0].authorized").value(true));

        verify(userService).getAuthorizedUsersByRole(roleName);
    }

    @Test
    @WithMockUser
    void getAuthorizedUsersByRole_WhenNoAuthorizedUsers_ShouldReturnEmptyList() throws Exception {
       
        String roleName = "ADMIN";
        when(userService.getAuthorizedUsersByRole(roleName)).thenReturn(Arrays.asList());

        mockMvc.perform(get(BASE_URL + "/authorized/role/" + roleName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService).getAuthorizedUsersByRole(roleName);
    }

    @Test
    @WithMockUser
    void verifyUser_WithEmptyUsername_ShouldStillWork() throws Exception {
        
        when(userService.verifyOrCreateUser(TEST_USER_ID, "", TEST_EMAIL, null))
                .thenReturn(testUser);

        mockMvc.perform(post(BASE_URL + "/verify")
                .param("firebaseUid", TEST_USER_ID)
                .param("username", "")
                .param("email", TEST_EMAIL)
                .with(csrf()))
                .andExpect(status().isOk());

        verify(userService).verifyOrCreateUser(TEST_USER_ID, "", TEST_EMAIL, null);
    }

    // REMOVED: Old Dolibarr ID self-management tests
    // These tests have been removed as the functionality is now admin-only

    // Admin-Only Dolibarr ID Management Tests

    @Test
    @WithMockUser(username = TEST_USER_ID)
    void updateUserDolibarrIdAsAdmin_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        String targetUserId = "target-user-123";
        String dolibarrId = "DOL123456";
        testUser.setDolibarrId(dolibarrId);

        when(userService.updateUserDolibarrIdAsAdmin(TEST_USER_ID, targetUserId, dolibarrId))
                .thenReturn(testUser);

        mockMvc.perform(put(BASE_URL + "/" + targetUserId + "/admin/dolibarr-id")
                .param("dolibarrId", dolibarrId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dolibarrId").value(dolibarrId));

        verify(userService).updateUserDolibarrIdAsAdmin(TEST_USER_ID, targetUserId, dolibarrId);
    }

    @Test
    @WithMockUser(username = TEST_USER_ID)
    void updateUserDolibarrIdAsAdmin_WithoutAdminPrivileges_ShouldReturnForbidden() throws Exception {
        String targetUserId = "target-user-123";
        String dolibarrId = "DOL123456";

        when(userService.updateUserDolibarrIdAsAdmin(TEST_USER_ID, targetUserId, dolibarrId))
                .thenThrow(new SecurityException("Administrator privileges required to manage Dolibarr UIDs"));

        mockMvc.perform(put(BASE_URL + "/" + targetUserId + "/admin/dolibarr-id")
                .param("dolibarrId", dolibarrId)
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService).updateUserDolibarrIdAsAdmin(TEST_USER_ID, targetUserId, dolibarrId);
    }

    @Test
    @WithMockUser(username = TEST_USER_ID)
    void getUserDolibarrIdAsAdmin_WithValidData_ShouldReturnDolibarrId() throws Exception {
        String targetUserId = "target-user-123";
        String dolibarrId = "DOL123456";

        when(userService.getUserDolibarrIdAsAdmin(TEST_USER_ID, targetUserId))
                .thenReturn(dolibarrId);

        mockMvc.perform(get(BASE_URL + "/" + targetUserId + "/admin/dolibarr-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dolibarrId").value(dolibarrId))
                .andExpect(jsonPath("$.firebaseUid").value(targetUserId));

        verify(userService).getUserDolibarrIdAsAdmin(TEST_USER_ID, targetUserId);
    }

    @Test
    @WithMockUser(username = TEST_USER_ID)
    void getUserDolibarrIdAsAdmin_WithoutAdminPrivileges_ShouldReturnForbidden() throws Exception {
        String targetUserId = "target-user-123";

        when(userService.getUserDolibarrIdAsAdmin(TEST_USER_ID, targetUserId))
                .thenThrow(new SecurityException("Administrator privileges required to access Dolibarr UIDs"));

        mockMvc.perform(get(BASE_URL + "/" + targetUserId + "/admin/dolibarr-id"))
                .andExpect(status().isForbidden());

        verify(userService).getUserDolibarrIdAsAdmin(TEST_USER_ID, targetUserId);
    }

    @Test
    void updateUserDolibarrIdAsAdmin_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        String targetUserId = "target-user-123";
        String dolibarrId = "DOL123456";

        mockMvc.perform(put(BASE_URL + "/" + targetUserId + "/admin/dolibarr-id")
                .param("dolibarrId", dolibarrId)
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updateUserDolibarrIdAsAdmin(any(), any(), any());
    }

    @Test
    void getUserDolibarrIdAsAdmin_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        String targetUserId = "target-user-123";

        mockMvc.perform(get(BASE_URL + "/" + targetUserId + "/admin/dolibarr-id"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getUserDolibarrIdAsAdmin(any(), any());
    }

}
