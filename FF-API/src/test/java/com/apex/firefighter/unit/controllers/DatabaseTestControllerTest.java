package com.apex.firefighter.unit.controllers;

import com.apex.firefighter.controller.DatabaseTestController;
import com.apex.firefighter.model.ConnectionTest;
import com.apex.firefighter.service.user.DatabaseConnectionTestService;
import com.apex.firefighter.service.auth.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DatabaseTestController.class, 
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    })
class DatabaseTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatabaseConnectionTestService testService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private ConnectionTest testConnectionTest;
    private List<ConnectionTest> testList;

    @BeforeEach
    void setUp() {
        testConnectionTest = new ConnectionTest();
        testConnectionTest.setId(1L);
        testConnectionTest.setTestName("Test Connection");
        testConnectionTest.setTestValue("Test Value");
        testConnectionTest.setTestNumber(100);
        testConnectionTest.setIsActive(true);
        testConnectionTest.setCreatedAt(LocalDateTime.now());

        ConnectionTest secondTest = new ConnectionTest();
        secondTest.setId(2L);
        secondTest.setTestName("Second Test");
        secondTest.setTestValue("Second Value");
        secondTest.setTestNumber(200);
        secondTest.setIsActive(false);
        secondTest.setCreatedAt(LocalDateTime.now());

        testList = Arrays.asList(testConnectionTest, secondTest);
    }

    // ==================== CORS TEST ENDPOINT ====================

    @Test
    void corsTest_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(get("/api/test/cors"))
                .andExpect(status().isOk())
                .andExpect(content().string("CORS is working correctly! You can access this API from any device on the network."));
    }

    // ==================== COMPREHENSIVE TEST ENDPOINT ====================

    @Test
    void runComprehensiveTest_WhenSuccessful_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(testService).runComprehensiveTest();

        // Act & Assert
        mockMvc.perform(get("/api/test/run"))
                .andExpect(status().isOk())
                .andExpect(content().string("Database connection test completed successfully! Check console for details."));

        verify(testService).runComprehensiveTest();
    }

    @Test
    void runComprehensiveTest_WhenExceptionThrown_ShouldReturnErrorMessage() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Database connection failed")).when(testService).runComprehensiveTest();

        // Act & Assert
        mockMvc.perform(get("/api/test/run"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Test failed: Database connection failed"));

        verify(testService).runComprehensiveTest();
    }

    // ==================== CREATE TEST ENDPOINT ====================

    @Test
    void createTest_WithValidParameters_ShouldReturnCreatedTest() throws Exception {
        // Arrange
        when(testService.createTestEntry("Test Name", "Test Value", 100, true))
                .thenReturn(testConnectionTest);

        // Act & Assert
        mockMvc.perform(post("/api/test/create")
                .param("testName", "Test Name")
                .param("testValue", "Test Value")
                .param("testNumber", "100")
                .param("isActive", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.testName").value("Test Connection"))
                .andExpect(jsonPath("$.testValue").value("Test Value"))
                .andExpect(jsonPath("$.testNumber").value(100))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(testService).createTestEntry("Test Name", "Test Value", 100, true);
    }

    @Test
    void createTest_WithMissingParameters_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/test/create")
                .param("testName", "Test Name"))
                .andExpect(status().isBadRequest());

        verify(testService, never()).createTestEntry(anyString(), anyString(), anyInt(), anyBoolean());
    }

    @Test
    void createTest_WithInvalidNumberParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/test/create")
                .param("testName", "Test Name")
                .param("testValue", "Test Value")
                .param("testNumber", "invalid")
                .param("isActive", "true"))
                .andExpect(status().isBadRequest());

        verify(testService, never()).createTestEntry(anyString(), anyString(), anyInt(), anyBoolean());
    }

    @Test
    void createTest_WithInvalidBooleanParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/test/create")
                .param("testName", "Test Name")
                .param("testValue", "Test Value")
                .param("testNumber", "100")
                .param("isActive", "invalid"))
                .andExpect(status().isBadRequest());

        verify(testService, never()).createTestEntry(anyString(), anyString(), anyInt(), anyBoolean());
    }

    // ==================== GET ALL TESTS ENDPOINT ====================

    @Test
    void getAllTests_ShouldReturnAllTests() throws Exception {
        // Arrange
        when(testService.getAllTests()).thenReturn(testList);

        // Act & Assert
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].testName").value("Test Connection"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].testName").value("Second Test"));

        verify(testService).getAllTests();
    }

    @Test
    void getAllTests_WithEmptyList_ShouldReturnEmptyArray() throws Exception {
        // Arrange
        when(testService.getAllTests()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(testService).getAllTests();
    }

    // ==================== GET TEST BY ID ENDPOINT ====================

    @Test
    void getTestById_WithExistingId_ShouldReturnTest() throws Exception {
        // Arrange
        when(testService.getTestById(1L)).thenReturn(Optional.of(testConnectionTest));

        // Act & Assert
        mockMvc.perform(get("/api/test/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.testName").value("Test Connection"))
                .andExpect(jsonPath("$.testValue").value("Test Value"));

        verify(testService).getTestById(1L);
    }

    @Test
    void getTestById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(testService.getTestById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/test/999"))
                .andExpect(status().isNotFound());

        verify(testService).getTestById(999L);
    }

    @Test
    void getTestById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/invalid"))
                .andExpect(status().isBadRequest());

        verify(testService, never()).getTestById(anyLong());
    }

    // ==================== GET ACTIVE TESTS ENDPOINT ====================

    @Test
    void getActiveTests_ShouldReturnActiveTests() throws Exception {
        // Arrange
        List<ConnectionTest> activeTests = Arrays.asList(testConnectionTest);
        when(testService.getActiveTests()).thenReturn(activeTests);

        // Act & Assert
        mockMvc.perform(get("/api/test/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(testService).getActiveTests();
    }

    @Test
    void getActiveTests_WithNoActiveTests_ShouldReturnEmptyArray() throws Exception {
        // Arrange
        when(testService.getActiveTests()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/test/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(testService).getActiveTests();
    }

    // ==================== SEARCH TESTS ENDPOINT ====================

    @Test
    void searchTests_WithValidName_ShouldReturnMatchingTests() throws Exception {
        // Arrange
        List<ConnectionTest> searchResults = Arrays.asList(testConnectionTest);
        when(testService.searchTestsByName("Test")).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/api/test/search")
                .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].testName").value("Test Connection"));

        verify(testService).searchTestsByName("Test");
    }

    @Test
    void searchTests_WithMissingNameParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/search"))
                .andExpect(status().isBadRequest());

        verify(testService, never()).searchTestsByName(anyString());
    }

    @Test
    void searchTests_WithEmptyName_ShouldCallService() throws Exception {
        // Arrange
        when(testService.searchTestsByName("")).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/test/search")
                .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(testService).searchTestsByName("");
    }

    // ==================== UPDATE TEST ENDPOINT ====================

    @Test
    void updateTest_WithValidParameters_ShouldReturnUpdatedTest() throws Exception {
        // Arrange
        ConnectionTest updatedTest = new ConnectionTest();
        updatedTest.setId(1L);
        updatedTest.setTestValue("Updated Value");
        updatedTest.setTestNumber(150);
        
        when(testService.updateTestEntry(1L, "Updated Value", 150)).thenReturn(updatedTest);

        // Act & Assert
        mockMvc.perform(put("/api/test/1")
                .param("testValue", "Updated Value")
                .param("testNumber", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.testValue").value("Updated Value"))
                .andExpect(jsonPath("$.testNumber").value(150));

        verify(testService).updateTestEntry(1L, "Updated Value", 150);
    }

    @Test
    void updateTest_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(testService.updateTestEntry(999L, "Updated Value", 150))
                .thenThrow(new RuntimeException("Test not found"));

        // Act & Assert
        mockMvc.perform(put("/api/test/999")
                .param("testValue", "Updated Value")
                .param("testNumber", "150"))
                .andExpect(status().isNotFound());

        verify(testService).updateTestEntry(999L, "Updated Value", 150);
    }

    @Test
    void updateTest_WithMissingParameters_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/api/test/1")
                .param("testValue", "Updated Value"))
                .andExpect(status().isBadRequest());

        verify(testService, never()).updateTestEntry(anyLong(), anyString(), anyInt());
    }

    // ==================== TOGGLE TEST STATUS ENDPOINT ====================

    @Test
    void toggleTestStatus_WithValidId_ShouldReturnUpdatedTest() throws Exception {
        // Arrange
        ConnectionTest toggledTest = new ConnectionTest();
        toggledTest.setId(1L);
        toggledTest.setIsActive(false);
        
        when(testService.toggleTestStatus(1L)).thenReturn(toggledTest);

        // Act & Assert
        mockMvc.perform(patch("/api/test/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.isActive").value(false));

        verify(testService).toggleTestStatus(1L);
    }

    @Test
    void toggleTestStatus_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(testService.toggleTestStatus(999L)).thenThrow(new RuntimeException("Test not found"));

        // Act & Assert
        mockMvc.perform(patch("/api/test/999/toggle"))
                .andExpect(status().isNotFound());

        verify(testService).toggleTestStatus(999L);
    }

    // ==================== DELETE TEST ENDPOINT ====================

    @Test
    void deleteTest_WithExistingId_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        when(testService.deleteTestById(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/test/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test deleted successfully"));

        verify(testService).deleteTestById(1L);
    }

    @Test
    void deleteTest_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(testService.deleteTestById(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/test/999"))
                .andExpect(status().isNotFound());

        verify(testService).deleteTestById(999L);
    }

    // ==================== DELETE ALL TESTS ENDPOINT ====================

    @Test
    void deleteAllTests_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(testService).deleteAllTests();

        // Act & Assert
        mockMvc.perform(delete("/api/test/all"))
                .andExpect(status().isOk())
                .andExpect(content().string("All tests deleted successfully"));

        verify(testService).deleteAllTests();
    }

    // ==================== COUNT ACTIVE TESTS ENDPOINT ====================

    @Test
    void countActiveTests_ShouldReturnCount() throws Exception {
        // Arrange
        when(testService.countActiveTests()).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/api/test/count/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(testService).countActiveTests();
    }

    @Test
    void countActiveTests_WithZeroCount_ShouldReturnZero() throws Exception {
        // Arrange
        when(testService.countActiveTests()).thenReturn(0L);

        // Act & Assert
        mockMvc.perform(get("/api/test/count/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        verify(testService).countActiveTests();
    }

    // ==================== GET TESTS IN RANGE ENDPOINT ====================

    @Test
    void getTestsInRange_WithValidRange_ShouldReturnTests() throws Exception {
        // Arrange
        when(testService.getTestsInNumberRange(1, 200)).thenReturn(testList);

        // Act & Assert
        mockMvc.perform(get("/api/test/range")
                .param("start", "1")
                .param("end", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(testService).getTestsInNumberRange(1, 200);
    }

    @Test
    void getTestsInRange_WithMissingParameters_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/range")
                .param("start", "1"))
                .andExpect(status().isBadRequest());

        verify(testService, never()).getTestsInNumberRange(anyInt(), anyInt());
    }

    @Test
    void getTestsInRange_WithInvalidParameters_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/range")
                .param("start", "invalid")
                .param("end", "200"))
                .andExpect(status().isBadRequest());

        verify(testService, never()).getTestsInNumberRange(anyInt(), anyInt());
    }

    @Test
    void getTestsInRange_WithEmptyResult_ShouldReturnEmptyArray() throws Exception {
        // Arrange
        when(testService.getTestsInNumberRange(500, 600)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/test/range")
                .param("start", "500")
                .param("end", "600"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(testService).getTestsInNumberRange(500, 600);
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void createTest_WithServiceException_ShouldPropagateException() throws Exception {
        // Arrange
        when(testService.createTestEntry("Test Name", "Test Value", 100, true))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/test/create")
                .param("testName", "Test Name")
                .param("testValue", "Test Value")
                .param("testNumber", "100")
                .param("isActive", "true"))
                .andExpect(status().isInternalServerError());

        verify(testService).createTestEntry("Test Name", "Test Value", 100, true);
    }

    @Test
    void getAllTests_WithServiceException_ShouldPropagateException() throws Exception {
        // Arrange
        when(testService.getAllTests()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isInternalServerError());

        verify(testService).getAllTests();
    }
}
