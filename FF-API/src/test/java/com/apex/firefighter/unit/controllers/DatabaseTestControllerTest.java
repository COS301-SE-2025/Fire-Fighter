package com.apex.firefighter.controller;

import com.apex.firefighter.model.ConnectionTest;
import com.apex.firefighter.service.DatabaseConnectionTestService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatabaseTestController.class)
class DatabaseTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatabaseConnectionTestService testService;

    @Autowired
    private ObjectMapper objectMapper;

    private ConnectionTest testConnection;
    private final String BASE_URL = "/api/test";

    @BeforeEach
    void setUp() {
        testConnection = new ConnectionTest();
        testConnection.setId(1L);
        testConnection.setTestName("Test Connection");
        testConnection.setTestValue("Test Value");
        testConnection.setTestNumber(1);
        testConnection.setIsActive(true);
        testConnection.setCreatedAt(LocalDateTime.now());
        testConnection.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void corsTest_ShouldReturnSuccessMessage() throws Exception {
        
        mockMvc.perform(get(BASE_URL + "/cors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("CORS is working correctly! You can access this API from any device on the network."));

        verifyNoInteractions(testService);
    }

    @Test
    @WithMockUser
    void runComprehensiveTest_WhenSuccessful_ShouldReturnSuccessMessage() throws Exception {
        
        doNothing().when(testService).runComprehensiveTest();

        mockMvc.perform(get(BASE_URL + "/run"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Database connection test completed successfully! Check console for details."));

        verify(testService).runComprehensiveTest();
    }

    @Test
    @WithMockUser
    void runComprehensiveTest_WhenServiceThrowsException_ShouldReturnErrorMessage() throws Exception {
        
        doThrow(new RuntimeException("Database connection failed")).when(testService).runComprehensiveTest();

        mockMvc.perform(get(BASE_URL + "/run"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Test failed: Database connection failed"));

        verify(testService).runComprehensiveTest();
    }

    @Test
    @WithMockUser
    void createTest_WhenSuccessful_ShouldReturnCreatedTest() throws Exception {
        
        when(testService.createTestEntry("Test Connection", "Test Value", 1, true))
                .thenReturn(testConnection);

        mockMvc.perform(post(BASE_URL + "/create")
                .param("testName", "Test Connection")
                .param("testValue", "Test Value")
                .param("testNumber", "1")
                .param("isActive", "true")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.testName").value("Test Connection"))
                .andExpect(jsonPath("$.testValue").value("Test Value"))
                .andExpect(jsonPath("$.testNumber").value(1))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(testService).createTestEntry("Test Connection", "Test Value", 1, true);
    }

    @Test
    @WithMockUser
    void createTest_WithDefaultValues_ShouldReturnCreatedTest() throws Exception {
        
        when(testService.createTestEntry("Default Test", "Default Value", 0, true))
                .thenReturn(testConnection);

        mockMvc.perform(post(BASE_URL + "/create")
                .param("testName", "Default Test")
                .param("testValue", "Default Value")
                .param("testNumber", "0")
                .param("isActive", "true")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(testService).createTestEntry("Default Test", "Default Value", 0, true);
    }

    @Test
    @WithMockUser
    void getAllTests_ShouldReturnTestsList() throws Exception {
        
        List<ConnectionTest> tests = Arrays.asList(testConnection);
        when(testService.getAllTests()).thenReturn(tests);

        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].testName").value("Test Connection"));

        verify(testService).getAllTests();
    }

    @Test
    @WithMockUser
    void getTestById_WhenExists_ShouldReturnTest() throws Exception {
        
        when(testService.getTestById(1L)).thenReturn(Optional.of(testConnection));

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.testName").value("Test Connection"));

        verify(testService).getTestById(1L);
    }

    @Test
    @WithMockUser
    void getTestById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        
        when(testService.getTestById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isNotFound());

        verify(testService).getTestById(1L);
    }

    @Test
    @WithMockUser
    void deleteAllTests_ShouldReturnSuccessMessage() throws Exception {
        
        doNothing().when(testService).deleteAllTests();

        mockMvc.perform(delete(BASE_URL + "/all")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("All tests deleted successfully"));

        verify(testService).deleteAllTests();
    }

    @Test
    @WithMockUser
    void countActiveTests_ShouldReturnCount() throws Exception {
        
        when(testService.countActiveTests()).thenReturn(5L);

        mockMvc.perform(get(BASE_URL + "/count/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("5"));

        verify(testService).countActiveTests();
    }

    @Test
    @WithMockUser
    void getTestsInRange_ShouldReturnTestsInRange() throws Exception {
        
        List<ConnectionTest> testsInRange = Arrays.asList(testConnection);
        when(testService.getTestsInNumberRange(1, 100)).thenReturn(testsInRange);

        mockMvc.perform(get(BASE_URL + "/range")
                .param("start", "1")
                .param("end", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].testName").value("Test Connection"));

        verify(testService).getTestsInNumberRange(1, 100);
    }

    @Test
    @WithMockUser
    void getActiveTests_ShouldReturnActiveTestsList() throws Exception {
        
        List<ConnectionTest> activeTests = Arrays.asList(testConnection);
        when(testService.getActiveTests()).thenReturn(activeTests);

        mockMvc.perform(get(BASE_URL + "/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(testService).getActiveTests();
    }

    @Test
    @WithMockUser
    void searchTests_WhenTestsFound_ShouldReturnMatchingTests() throws Exception {
        
        ConnectionTest matchingTest1 = new ConnectionTest();
        matchingTest1.setId(1L);
        matchingTest1.setTestName("Connection Test");
        matchingTest1.setTestValue("Test Value");
        matchingTest1.setTestNumber(1);
        matchingTest1.setIsActive(true);
        matchingTest1.setCreatedAt(LocalDateTime.now());
        matchingTest1.setUpdatedAt(LocalDateTime.now());

        ConnectionTest matchingTest2 = new ConnectionTest();
        matchingTest2.setId(2L);
        matchingTest2.setTestName("Another Connection Test");
        matchingTest2.setTestValue("Another Value");
        matchingTest2.setTestNumber(2);
        matchingTest2.setIsActive(false);
        matchingTest2.setCreatedAt(LocalDateTime.now());
        matchingTest2.setUpdatedAt(LocalDateTime.now());

        List<ConnectionTest> searchResults = Arrays.asList(matchingTest1, matchingTest2);
        String searchTerm = "Connection";

        when(testService.searchTestsByName(searchTerm)).thenReturn(searchResults);

        mockMvc.perform(get(BASE_URL + "/search")
                .param("name", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].testName").value("Connection Test"))
                .andExpect(jsonPath("$[0].testValue").value("Test Value"))
                .andExpect(jsonPath("$[0].testNumber").value(1))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].testName").value("Another Connection Test"))
                .andExpect(jsonPath("$[1].isActive").value(false));

        verify(testService).searchTestsByName(searchTerm);
    }

    @Test
    @WithMockUser
    void searchTests_WhenNoTestsFound_ShouldReturnEmptyList() throws Exception {
    
        String searchTerm = "NonExistent";
        List<ConnectionTest> emptyResults = Arrays.asList();

        when(testService.searchTestsByName(searchTerm)).thenReturn(emptyResults);

        mockMvc.perform(get(BASE_URL + "/search")
                .param("name", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(testService).searchTestsByName(searchTerm);
    }

    @Test
    @WithMockUser
    void searchTests_WhenMissingNameParameter_ShouldReturnBadRequest() throws Exception {
        
        mockMvc.perform(get(BASE_URL + "/search"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    @WithMockUser
    void updateTest_ShouldReturnUpdatedTest() throws Exception {
        
        Long id = 1L;
        String newTestValue = "Updated Test Value";
        Integer newTestNumber = 100;

        ConnectionTest updatedTest = new ConnectionTest();
        updatedTest.setId(id);
        updatedTest.setTestName("Test Connection"); 
        updatedTest.setTestValue(newTestValue);
        updatedTest.setTestNumber(newTestNumber);
        updatedTest.setIsActive(true);
        updatedTest.setCreatedAt(LocalDateTime.now());
        updatedTest.setUpdatedAt(LocalDateTime.now());

        when(testService.updateTestEntry(id, newTestValue, newTestNumber)).thenReturn(updatedTest);

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                .param("testValue", newTestValue)
                .param("testNumber", newTestNumber.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.testName").value("Test Connection"))
                .andExpect(jsonPath("$.testValue").value(newTestValue))
                .andExpect(jsonPath("$.testNumber").value(newTestNumber))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(testService).updateTestEntry(id, newTestValue, newTestNumber);
    }

    @Test
    @WithMockUser
    void updateTest_WhenTestNotFound_ShouldReturnNotFound() throws Exception {
        
        Long id = 999L;
        String testValue = "Test Value";
        Integer testNumber = 1;

        when(testService.updateTestEntry(id, testValue, testNumber))
                .thenThrow(new RuntimeException("Test not found with ID: " + id));

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                .param("testValue", testValue)
                .param("testNumber", testNumber.toString())
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(testService).updateTestEntry(id, testValue, testNumber);
    }
    
    @Test
    @WithMockUser
    void toggleTestStatus_ShouldReturnTestStatus() throws Exception {
        
        Long id = 1L;

        ConnectionTest updatedTest = new ConnectionTest();
        updatedTest.setId(id);
        updatedTest.setTestName("Test Connection");
        updatedTest.setTestValue("Test Value");
        updatedTest.setTestNumber(1);
        updatedTest.setIsActive(false);
        updatedTest.setCreatedAt(LocalDateTime.now());
        updatedTest.setUpdatedAt(LocalDateTime.now());

        when(testService.toggleTestStatus(id)).thenReturn(updatedTest);

        mockMvc.perform(patch(BASE_URL + "/{id}/toggle", id)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.testName").value("Test Connection"))
                .andExpect(jsonPath("$.testValue").value("Test Value"))
                .andExpect(jsonPath("$.testNumber").value(1))
                .andExpect(jsonPath("$.isActive").value(false));

        verify(testService).toggleTestStatus(id);
    }

    @Test
    @WithMockUser
    void toggleTestStatus_WhenTestNotFound_ShouldReturnNotFound() throws Exception {
        
        Long id = 999L;

        when(testService.toggleTestStatus(id))
                .thenThrow(new RuntimeException("Test not found with ID: " + id));

        mockMvc.perform(patch(BASE_URL + "/{id}/toggle", id)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(testService).toggleTestStatus(id);
    }

    @Test
    @WithMockUser
    void deleteTest_ShouldReturnTrue() throws Exception {
        
        Long id = 1L;

        when(testService.deleteTestById(id)).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/{id}", id)
                .with(csrf()))
                .andExpect(status().isOk());

        verify(testService).deleteTestById(id);
    }

    @Test
    @WithMockUser
    void deleteTest_WhenTestNotFound_ShouldReturnNotFound() throws Exception {
        
        Long id = 1L;

        when(testService.deleteTestById(id)).thenReturn(false);

        mockMvc.perform(delete(BASE_URL + "/{id}", id)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(testService).deleteTestById(id);
    }

}
