package com.apex.firefighter.controller;

import com.apex.firefighter.model.UserPreferences;
import com.apex.firefighter.service.UserPreferencesService;
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
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPreferencesController.class)
class UserPreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPreferencesService userPreferencesService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPreferences testPreferences;
    private final String TEST_USER_ID = "test-user-123";
    private final String BASE_URL = "/api/user-preferences";

    @BeforeEach
    void setUp() {
        testPreferences = new UserPreferences();
        testPreferences.setUserId(TEST_USER_ID);
        testPreferences.setEmailNotificationsEnabled(true);
        testPreferences.setEmailTicketCreation(true);
        testPreferences.setEmailTicketCompletion(true);
        testPreferences.setEmailTicketRevocation(false);
        testPreferences.setEmailFiveMinuteWarning(true);
        testPreferences.setCreatedAt(ZonedDateTime.now());
        testPreferences.setUpdatedAt(ZonedDateTime.now());
    }

    @Test
    @WithMockUser
    void getUserPreferences_WhenExists_ShouldReturnPreferences() throws Exception {
        // Arrange
        when(userPreferencesService.getUserPreferences(TEST_USER_ID)).thenReturn(testPreferences);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.emailNotificationsEnabled").value(true))
                .andExpect(jsonPath("$.emailTicketCreation").value(true))
                .andExpect(jsonPath("$.emailTicketCompletion").value(true))
                .andExpect(jsonPath("$.emailTicketRevocation").value(false))
                .andExpect(jsonPath("$.emailFiveMinuteWarning").value(true));

        verify(userPreferencesService).getUserPreferences(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getUserPreferences_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(userPreferencesService.getUserPreferences(TEST_USER_ID))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/" + TEST_USER_ID))
                .andExpect(status().isInternalServerError());

        verify(userPreferencesService).getUserPreferences(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void updateUserPreferences_WithValidData_ShouldReturnUpdatedPreferences() throws Exception {
        // Arrange
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("emailNotificationsEnabled", false);
        requestBody.put("emailTicketCreation", false);
        requestBody.put("emailTicketCompletion", true);
        requestBody.put("emailTicketRevocation", true);
        requestBody.put("emailFiveMinuteWarning", false);

        testPreferences.setEmailNotificationsEnabled(false);
        testPreferences.setEmailTicketCreation(false);
        testPreferences.setEmailTicketRevocation(true);
        testPreferences.setEmailFiveMinuteWarning(false);

        when(userPreferencesService.updateUserPreferences(
                eq(TEST_USER_ID), eq(false), eq(false), eq(true), eq(true), eq(false)))
                .thenReturn(testPreferences);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.emailNotificationsEnabled").value(false))
                .andExpect(jsonPath("$.emailTicketCreation").value(false))
                .andExpect(jsonPath("$.emailTicketRevocation").value(true))
                .andExpect(jsonPath("$.emailFiveMinuteWarning").value(false));

        verify(userPreferencesService).updateUserPreferences(
                TEST_USER_ID, false, false, true, true, false);
    }

    @Test
    @WithMockUser
    void updateUserPreferences_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("emailNotificationsEnabled", true);

        when(userPreferencesService.updateUserPreferences(anyString(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/" + TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userPreferencesService).updateUserPreferences(anyString(), any(), any(), any(), any(), any());
    }

    // Note: updateSpecificPreference method doesn't exist in UserPreferencesService
    // These tests are commented out until the method is implemented

    @Test
    @WithMockUser
    void enableAllEmailNotifications_ShouldReturnUpdatedPreferences() throws Exception {
        // Arrange
        testPreferences.setEmailNotificationsEnabled(true);
        testPreferences.setEmailTicketCreation(true);
        testPreferences.setEmailTicketCompletion(true);
        testPreferences.setEmailTicketRevocation(true);
        testPreferences.setEmailFiveMinuteWarning(true);

        when(userPreferencesService.enableAllEmailNotifications(TEST_USER_ID))
                .thenReturn(testPreferences);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/" + TEST_USER_ID + "/enable-all")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.emailNotificationsEnabled").value(true))
                .andExpect(jsonPath("$.emailTicketCreation").value(true))
                .andExpect(jsonPath("$.emailTicketCompletion").value(true))
                .andExpect(jsonPath("$.emailTicketRevocation").value(true))
                .andExpect(jsonPath("$.emailFiveMinuteWarning").value(true));

        verify(userPreferencesService).enableAllEmailNotifications(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void enableAllEmailNotifications_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(userPreferencesService.enableAllEmailNotifications(TEST_USER_ID))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/" + TEST_USER_ID + "/enable-all")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userPreferencesService).enableAllEmailNotifications(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void disableAllEmailNotifications_ShouldReturnUpdatedPreferences() throws Exception {
        // Arrange
        testPreferences.setEmailNotificationsEnabled(false);
        testPreferences.setEmailTicketCreation(false);
        testPreferences.setEmailTicketCompletion(false);
        testPreferences.setEmailTicketRevocation(false);
        testPreferences.setEmailFiveMinuteWarning(false);

        when(userPreferencesService.disableAllEmailNotifications(TEST_USER_ID))
                .thenReturn(testPreferences);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/" + TEST_USER_ID + "/disable-all")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.emailNotificationsEnabled").value(false))
                .andExpect(jsonPath("$.emailTicketCreation").value(false))
                .andExpect(jsonPath("$.emailTicketCompletion").value(false))
                .andExpect(jsonPath("$.emailTicketRevocation").value(false))
                .andExpect(jsonPath("$.emailFiveMinuteWarning").value(false));

        verify(userPreferencesService).disableAllEmailNotifications(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void disableAllEmailNotifications_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(userPreferencesService.disableAllEmailNotifications(TEST_USER_ID))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/" + TEST_USER_ID + "/disable-all")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(userPreferencesService).disableAllEmailNotifications(TEST_USER_ID);
    }
}
