package com.apex.firefighter.controller;

import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
        // Arrange
        when(userService.verifyOrCreateUser(TEST_USER_ID, "testuser", TEST_EMAIL, "Fire Department"))
                .thenReturn(testUser);

        // Act & Assert
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
        // Arrange
        when(userService.verifyOrCreateUser(TEST_USER_ID, "testuser", TEST_EMAIL, null))
                .thenReturn(testUser);

        // Act & Assert
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
        // Arrange
        when(userService.verifyOrCreateUser(anyString(), anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/verify")
                .param("firebaseUid", TEST_USER_ID)
                .param("username", "testuser")
                .param("email", TEST_EMAIL)
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).verifyOrCreateUser(TEST_USER_ID, "testuser", TEST_EMAIL, null);
    }

    // Note: There is no GET /api/users endpoint - users are accessed via specific endpoints

    @Test
    @WithMockUser
    void getUserInfo_WhenExists_ShouldReturnUser() throws Exception {
        // Arrange
        when(userService.getUserWithRoles(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        // Act & Assert
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
        // Arrange
        when(userService.getUserWithRoles(TEST_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID))
                .andExpect(status().isNotFound());

        verify(userService).getUserWithRoles(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getUserByEmail_WhenExists_ShouldReturnUser() throws Exception {
        // Arrange
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // Act & Assert
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
        // Arrange
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/email/" + TEST_EMAIL))
                .andExpect(status().isNotFound());

        verify(userService).getUserByEmail(TEST_EMAIL);
    }

    @Test
    @WithMockUser
    void authorizeUser_WhenSuccessful_ShouldReturnAuthorizedUser() throws Exception {
        // Arrange
        testUser.setIsAuthorized(true);
        when(userService.authorizeUser(TEST_USER_ID, "admin-123")).thenReturn(testUser);

        // Act & Assert
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
        // Arrange
        when(userService.authorizeUser(TEST_USER_ID, "admin-123"))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/authorize")
                .param("authorizedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).authorizeUser(TEST_USER_ID, "admin-123");
    }

    @Test
    @WithMockUser
    void revokeUserAuthorization_WhenSuccessful_ShouldReturnRevokedUser() throws Exception {
        // Arrange
        testUser.setIsAuthorized(false);
        when(userService.revokeUserAuthorization(TEST_USER_ID, "admin-123")).thenReturn(testUser);

        // Act & Assert
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
        // Arrange
        when(userService.revokeUserAuthorization(TEST_USER_ID, "admin-123"))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/revoke")
                .param("revokedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).revokeUserAuthorization(TEST_USER_ID, "admin-123");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenSuccessful_ShouldReturnUpdatedUser() throws Exception {
        // Arrange
        testUser.setContactNumber("987-654-3210");
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210")).thenReturn(testUser);

        // Act & Assert
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
        // Act & Assert - Missing required contactNumber parameter
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange - Test the specific "not found" error handling (line 216-217)
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new RuntimeException("User not found with Firebase UID: " + TEST_USER_ID));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WithNullContactNumber_ShouldHandleServiceError() throws Exception {
        // Arrange - Test the null check and trimming logic (lines 202-205)
        // When service returns null, it causes NullPointerException in controller
        when(userService.updateContactNumber(eq(TEST_USER_ID), anyString()))
                .thenReturn(null);

        // Act & Assert - This should result in 500 due to NullPointerException
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
        // Arrange - Test empty string handling after trim (lines 202-205)
        // When service returns null, it causes NullPointerException in controller
        // Note: The controller logic only trims if !contactNumber.trim().isEmpty(),
        // so whitespace-only strings are passed as-is
        when(userService.updateContactNumber(TEST_USER_ID, "   ")).thenReturn(null);

        // Act & Assert - This should result in 500 due to NullPointerException
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "   ")  // Only whitespace
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "   ");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WithValidContactNumber_ShouldTrimAndCallService() throws Exception {
        // Arrange - Test the trimming logic (lines 202-205)
        testUser.setContactNumber("123-456-7890");
        when(userService.updateContactNumber(TEST_USER_ID, "123-456-7890")).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "  123-456-7890  ")  // With leading/trailing spaces
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactNumber").value("123-456-7890"));

        verify(userService).updateContactNumber(TEST_USER_ID, "123-456-7890");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenNonRuntimeExceptionThrown_ShouldReturnInternalServerError() throws Exception {
        // Arrange - Test the generic Exception catch block (lines 220-226)
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new IllegalStateException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenRuntimeExceptionWithoutNotFoundMessage_ShouldReturnInternalServerError() throws Exception {
        // Arrange - Test RuntimeException that doesn't contain "not found" (lines 211-219)
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new RuntimeException("Some other database error"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WithNullContactNumber_ShouldSkipTrimming() throws Exception {
        // Arrange - Test the condition: if (contactNumber != null && !contactNumber.trim().isEmpty())
        // When contactNumber is empty string, the condition should be false and trimming should be skipped
        // The service returns null, which causes NullPointerException in the controller
        when(userService.updateContactNumber(TEST_USER_ID, "")).thenReturn(null);

        // Act & Assert - This should result in 500 due to NullPointerException
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "")  // Empty string - condition will be false
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        // Verify that empty string was passed to the service (condition was false, no trimming occurred)
        verify(userService).updateContactNumber(eq(TEST_USER_ID), eq(""));
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenGenericExceptionWithStackTrace_ShouldReturnInternalServerError() throws Exception {
        // Arrange - Test the generic Exception catch block (lines 220-226)
        // This should trigger the printStackTrace() and specific error logging
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new IllegalArgumentException("Invalid phone number format"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void updateContactNumber_WhenNullPointerExceptionInGenericCatch_ShouldReturnInternalServerError() throws Exception {
        // Arrange - Test another type of Exception to ensure the generic catch block is covered
        // This tests the printStackTrace() line and the "UNEXPECTED ERROR" logging
        when(userService.updateContactNumber(TEST_USER_ID, "987-654-3210"))
                .thenThrow(new NullPointerException("Null pointer in service"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/contact")
                .param("contactNumber", "987-654-3210")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userService).updateContactNumber(TEST_USER_ID, "987-654-3210");
    }

    @Test
    @WithMockUser
    void getAuthorizedUsers_ShouldReturnAuthorizedUsersList() throws Exception {
        // Arrange
        testUser.setIsAuthorized(true);
        List<User> authorizedUsers = Arrays.asList(testUser);
        when(userService.getAuthorizedUsers()).thenReturn(authorizedUsers);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/authorized"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$[0].authorized").value(true));

        verify(userService).getAuthorizedUsers();
    }

    // Note: getUnauthorizedUsers method doesn't exist in UserService
    // This test is commented out until the method is implemented

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

    // ===== AUTHORIZATION ENDPOINTS =====

    @Test
    @WithMockUser
    void checkAuthorization_WhenUserIsAuthorized_ShouldReturnTrue() throws Exception {
        // Arrange
        when(userService.isUserAuthorized(TEST_USER_ID)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID + "/authorized"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        verify(userService).isUserAuthorized(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void checkAuthorization_WhenUserIsNotAuthorized_ShouldReturnFalse() throws Exception {
        // Arrange
        when(userService.isUserAuthorized(TEST_USER_ID)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID + "/authorized"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        verify(userService).isUserAuthorized(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void checkRole_WhenUserHasRole_ShouldReturnTrue() throws Exception {
        // Arrange
        String roleName = "ADMIN";
        when(userService.hasRole(TEST_USER_ID, roleName)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID + "/roles/" + roleName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        verify(userService).hasRole(TEST_USER_ID, roleName);
    }

    @Test
    @WithMockUser
    void checkRole_WhenUserDoesNotHaveRole_ShouldReturnFalse() throws Exception {
        // Arrange
        String roleName = "ADMIN";
        when(userService.hasRole(TEST_USER_ID, roleName)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID + "/roles/" + roleName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        verify(userService).hasRole(TEST_USER_ID, roleName);
    }

    // ===== ADDITIONAL ADMIN ENDPOINTS =====

    @Test
    @WithMockUser
    void revokeAuthorization_WhenSuccessful_ShouldReturnRevokedUser() throws Exception {
        // Arrange
        testUser.setIsAuthorized(false);
        when(userService.revokeUserAuthorization(TEST_USER_ID, "admin-123")).thenReturn(testUser);

        // Act & Assert
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
        // Arrange
        when(userService.revokeUserAuthorization(TEST_USER_ID, "admin-123"))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID + "/revoke")
                .param("revokedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).revokeUserAuthorization(TEST_USER_ID, "admin-123");
    }

    @Test
    @WithMockUser
    void assignRole_WhenSuccessful_ShouldReturnUpdatedUser() throws Exception {
        // Arrange
        when(userService.assignRole(TEST_USER_ID, "ADMIN", "admin-123")).thenReturn(testUser);

        // Act & Assert
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
        // Arrange
        when(userService.assignRole(TEST_USER_ID, "INVALID_ROLE", "admin-123"))
                .thenThrow(new RuntimeException("Invalid role"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/" + TEST_USER_ID + "/roles")
                .param("roleName", "INVALID_ROLE")
                .param("assignedBy", "admin-123")
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userService).assignRole(TEST_USER_ID, "INVALID_ROLE", "admin-123");
    }

    // ===== QUERY ENDPOINTS =====

    @Test
    @WithMockUser
    void getUsersByDepartment_ShouldReturnUsersInDepartment() throws Exception {
        // Arrange
        String department = "Fire Department";
        List<User> departmentUsers = Arrays.asList(testUser);
        when(userService.getUsersByDepartment(department)).thenReturn(departmentUsers);

        // Act & Assert
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
        // Arrange
        String department = "Empty Department";
        when(userService.getUsersByDepartment(department)).thenReturn(Arrays.asList());

        // Act & Assert
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
        // Arrange
        String roleName = "FIREFIGHTER";
        testUser.setIsAuthorized(true);
        List<User> authorizedFirefighters = Arrays.asList(testUser);
        when(userService.getAuthorizedUsersByRole(roleName)).thenReturn(authorizedFirefighters);

        // Act & Assert
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
        // Arrange
        String roleName = "ADMIN";
        when(userService.getAuthorizedUsersByRole(roleName)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/authorized/role/" + roleName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService).getAuthorizedUsersByRole(roleName);
    }

    // ===== EDGE CASES AND ERROR SCENARIOS =====

    @Test
    @WithMockUser
    void verifyUser_WithEmptyUsername_ShouldStillWork() throws Exception {
        // Arrange
        when(userService.verifyOrCreateUser(TEST_USER_ID, "", TEST_EMAIL, null))
                .thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/verify")
                .param("firebaseUid", TEST_USER_ID)
                .param("username", "")
                .param("email", TEST_EMAIL)
                .with(csrf()))
                .andExpect(status().isOk());

        verify(userService).verifyOrCreateUser(TEST_USER_ID, "", TEST_EMAIL, null);
    }

    // Note: Comprehensive updateContactNumber tests are now above in the main section
}
