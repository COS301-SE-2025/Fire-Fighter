package com.apex.firefighter.unit.services;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.AnomalyNotificationService;
import com.apex.firefighter.service.GmailEmailService;
import com.apex.firefighter.service.anomaly.AnomalyDetectionService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnomalyNotificationServiceTest {

    @Mock
    private GmailEmailService emailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnomalyDetectionService anomalyDetectionService;

    @InjectMocks
    private AnomalyNotificationService anomalyNotificationService;

    private User testUser;
    private Ticket testTicket;
    private List<User> adminUsers;
    private final String TEST_USER_ID = "test-user-123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(TEST_USER_ID);
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setDepartment("IT");

        testTicket = new Ticket();
        testTicket.setTicketId("TICKET-001");
        testTicket.setDescription("Emergency access request");
        testTicket.setStatus("Active");
        testTicket.setDateCreated(LocalDateTime.now().minusHours(1));
        testTicket.setUserId(TEST_USER_ID);
        testTicket.setEmergencyType("fire");
        testTicket.setEmergencyContact("911");
        testTicket.setDuration(60);

        // Create admin users
        User admin1 = new User();
        admin1.setUserId("admin-1");
        admin1.setUsername("admin1");
        admin1.setEmail("admin1@example.com");
        admin1.setIsAdmin(true);

        User admin2 = new User();
        admin2.setUserId("admin-2");
        admin2.setUsername("admin2");
        admin2.setEmail("admin2@example.com");
        admin2.setIsAdmin(true);

        adminUsers = Arrays.asList(admin1, admin2);
    }

    // ==================== NOTIFY ADMINS OF ANOMALY TESTS ====================

    @Test
    void notifyAdminsOfAnomaly_WithValidParameters_ShouldSendEmailsToAllAdmins() throws MessagingException {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "User has made 10 requests in the last hour (threshold: 5)";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());

        // Act
        anomalyNotificationService.notifyAdminsOfAnomaly(testUser, testTicket, anomalyType, anomalyDetails);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, times(2)).sendAnomalyDetectionNotificationEmail(
            anyString(), eq(testUser), eq(testTicket), eq(anomalyType), eq(anomalyDetails), eq("MEDIUM"));
        verify(emailService).sendAnomalyDetectionNotificationEmail(
            "admin1@example.com", testUser, testTicket, anomalyType, anomalyDetails, "MEDIUM");
        verify(emailService).sendAnomalyDetectionNotificationEmail(
            "admin2@example.com", testUser, testTicket, anomalyType, anomalyDetails, "MEDIUM");
    }

    @Test
    void notifyAdminsOfAnomaly_WithHighRiskDormantUser_ShouldSendHighRiskEmails() throws MessagingException {
        // Arrange
        String anomalyType = "DORMANT_USER_ACTIVITY";
        String anomalyDetails = "User was dormant for 30+ days, logged in and made 5 actions within 15 minutes";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());

        // Act
        anomalyNotificationService.notifyAdminsOfAnomaly(testUser, testTicket, anomalyType, anomalyDetails);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, times(2)).sendAnomalyDetectionNotificationEmail(
            anyString(), eq(testUser), eq(testTicket), eq(anomalyType), eq(anomalyDetails), eq("HIGH"));
    }

    @Test
    void notifyAdminsOfAnomaly_WithLowRiskOffHours_ShouldSendLowRiskEmails() throws MessagingException {
        // Arrange
        String anomalyType = "OFF_HOURS_ACTIVITY";
        String anomalyDetails = "User made a request at 22:00 which is outside of regular work hours";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());

        // Act
        anomalyNotificationService.notifyAdminsOfAnomaly(testUser, testTicket, anomalyType, anomalyDetails);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, times(2)).sendAnomalyDetectionNotificationEmail(
            anyString(), eq(testUser), eq(testTicket), eq(anomalyType), eq(anomalyDetails), eq("LOW"));
    }

    @Test
    void notifyAdminsOfAnomaly_WithNoAdminUsers_ShouldLogWarning() throws MessagingException {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "Test anomaly details";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(Collections.emptyList());

        // Act
        anomalyNotificationService.notifyAdminsOfAnomaly(testUser, testTicket, anomalyType, anomalyDetails);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, never()).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());
    }

    @Test
    void notifyAdminsOfAnomaly_WithEmailException_ShouldContinueWithOtherAdmins() throws MessagingException {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "Test anomaly details";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doThrow(new MessagingException("Email failed")).when(emailService)
            .sendAnomalyDetectionNotificationEmail(eq("admin1@example.com"), any(), any(), any(), any(), any());
        doNothing().when(emailService)
            .sendAnomalyDetectionNotificationEmail(eq("admin2@example.com"), any(), any(), any(), any(), any());

        // Act
        anomalyNotificationService.notifyAdminsOfAnomaly(testUser, testTicket, anomalyType, anomalyDetails);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, times(2)).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());
    }

    @Test
    void notifyAdminsOfAnomaly_WithRepositoryException_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "Test anomaly details";
        
        when(userRepository.findByIsAdminTrue()).thenThrow(new RuntimeException("Database error"));

        // Act
        anomalyNotificationService.notifyAdminsOfAnomaly(testUser, testTicket, anomalyType, anomalyDetails);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, never()).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());
    }

    // ==================== CHECK AND NOTIFY ANOMALIES TESTS ====================

    @Test
    void checkAndNotifyAnomalies_WithFrequentRequestAnomaly_ShouldNotifyAdmins() throws MessagingException {
        // Arrange
        String frequencyDetails = "User has made 10 requests in the last hour (threshold: 5)";
        
        when(anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID)).thenReturn(frequencyDetails);
        when(anomalyDetectionService.getDormantUserAnomalyDetails(TEST_USER_ID)).thenReturn(null);
        when(anomalyDetectionService.getOffHoursAnomalyDetails(TEST_USER_ID)).thenReturn(null);
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());

        // Act
        anomalyNotificationService.checkAndNotifyAnomalies(testUser, testTicket);

        // Assert
        verify(anomalyDetectionService).getRequestFrequencyDetails(TEST_USER_ID);
        verify(anomalyDetectionService).getDormantUserAnomalyDetails(TEST_USER_ID);
        verify(anomalyDetectionService).getOffHoursAnomalyDetails(TEST_USER_ID);
        verify(emailService, times(2)).sendAnomalyDetectionNotificationEmail(
            anyString(), eq(testUser), eq(testTicket), eq("FREQUENT_REQUESTS"), eq(frequencyDetails), eq("MEDIUM"));
    }

    @Test
    void checkAndNotifyAnomalies_WithDormantUserAnomaly_ShouldNotifyAdmins() throws MessagingException {
        // Arrange
        String dormantDetails = "User was dormant for 30+ days, logged in and made 5 actions within 15 minutes";
        
        when(anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID)).thenReturn(null);
        when(anomalyDetectionService.getDormantUserAnomalyDetails(TEST_USER_ID)).thenReturn(dormantDetails);
        when(anomalyDetectionService.getOffHoursAnomalyDetails(TEST_USER_ID)).thenReturn(null);
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());

        // Act
        anomalyNotificationService.checkAndNotifyAnomalies(testUser, testTicket);

        // Assert
        verify(emailService, times(2)).sendAnomalyDetectionNotificationEmail(
            anyString(), eq(testUser), eq(testTicket), eq("DORMANT_USER_ACTIVITY"), eq(dormantDetails), eq("HIGH"));
    }

    @Test
    void checkAndNotifyAnomalies_WithOffHoursAnomaly_ShouldNotifyAdmins() throws MessagingException {
        // Arrange
        String offHoursDetails = "User made a request at 22:00 which is outside of regular work hours";
        
        when(anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID)).thenReturn(null);
        when(anomalyDetectionService.getDormantUserAnomalyDetails(TEST_USER_ID)).thenReturn(null);
        when(anomalyDetectionService.getOffHoursAnomalyDetails(TEST_USER_ID)).thenReturn(offHoursDetails);
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());

        // Act
        anomalyNotificationService.checkAndNotifyAnomalies(testUser, testTicket);

        // Assert
        verify(emailService, times(2)).sendAnomalyDetectionNotificationEmail(
            anyString(), eq(testUser), eq(testTicket), eq("OFF_HOURS_ACTIVITY"), eq(offHoursDetails), eq("LOW"));
    }

    @Test
    void checkAndNotifyAnomalies_WithMultipleAnomalies_ShouldNotifyForAll() throws MessagingException {
        // Arrange
        String frequencyDetails = "User has made 10 requests in the last hour (threshold: 5)";
        String dormantDetails = "User was dormant for 30+ days, logged in and made 5 actions within 15 minutes";
        String offHoursDetails = "User made a request at 22:00 which is outside of regular work hours";
        
        when(anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID)).thenReturn(frequencyDetails);
        when(anomalyDetectionService.getDormantUserAnomalyDetails(TEST_USER_ID)).thenReturn(dormantDetails);
        when(anomalyDetectionService.getOffHoursAnomalyDetails(TEST_USER_ID)).thenReturn(offHoursDetails);
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());

        // Act
        anomalyNotificationService.checkAndNotifyAnomalies(testUser, testTicket);

        // Assert
        verify(emailService, times(6)).sendAnomalyDetectionNotificationEmail(
            anyString(), eq(testUser), eq(testTicket), anyString(), anyString(), anyString());
        
        // Verify each anomaly type was sent
        verify(emailService, times(2)).sendAnomalyDetectionNotificationEmail(
            anyString(), eq(testUser), eq(testTicket), eq("FREQUENT_REQUESTS"), eq(frequencyDetails), eq("MEDIUM"));
        verify(emailService, times(2)).sendAnomalyDetectionNotificationEmail(
            anyString(), eq(testUser), eq(testTicket), eq("DORMANT_USER_ACTIVITY"), eq(dormantDetails), eq("HIGH"));
        verify(emailService, times(2)).sendAnomalyDetectionNotificationEmail(
            anyString(), eq(testUser), eq(testTicket), eq("OFF_HOURS_ACTIVITY"), eq(offHoursDetails), eq("LOW"));
    }

    @Test
    void checkAndNotifyAnomalies_WithNoAnomalies_ShouldNotSendEmails() throws MessagingException {
        // Arrange
        when(anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID)).thenReturn(null);
        when(anomalyDetectionService.getDormantUserAnomalyDetails(TEST_USER_ID)).thenReturn(null);
        when(anomalyDetectionService.getOffHoursAnomalyDetails(TEST_USER_ID)).thenReturn(null);

        // Act
        anomalyNotificationService.checkAndNotifyAnomalies(testUser, testTicket);

        // Assert
        verify(anomalyDetectionService).getRequestFrequencyDetails(TEST_USER_ID);
        verify(anomalyDetectionService).getDormantUserAnomalyDetails(TEST_USER_ID);
        verify(anomalyDetectionService).getOffHoursAnomalyDetails(TEST_USER_ID);
        verify(emailService, never()).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());
    }

    @Test
    void checkAndNotifyAnomalies_WithAnomalyDetectionException_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        when(anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID))
            .thenThrow(new RuntimeException("Anomaly detection error"));

        // Act
        anomalyNotificationService.checkAndNotifyAnomalies(testUser, testTicket);

        // Assert
        verify(anomalyDetectionService).getRequestFrequencyDetails(TEST_USER_ID);
        verify(emailService, never()).sendAnomalyDetectionNotificationEmail(
            anyString(), any(User.class), any(Ticket.class), anyString(), anyString(), anyString());
    }

    // ==================== RISK LEVEL DETERMINATION TESTS ====================

    @Test
    void getAnomalyTypeDescription_WithFrequentRequests_ShouldReturnCorrectDescription() {
        // Act
        String description = anomalyNotificationService.getAnomalyTypeDescription("FREQUENT_REQUESTS");

        // Assert
        assertThat(description).isEqualTo("Excessive Request Frequency");
    }

    @Test
    void getAnomalyTypeDescription_WithDormantUserActivity_ShouldReturnCorrectDescription() {
        // Act
        String description = anomalyNotificationService.getAnomalyTypeDescription("DORMANT_USER_ACTIVITY");

        // Assert
        assertThat(description).isEqualTo("Dormant Account Sudden Activity");
    }

    @Test
    void getAnomalyTypeDescription_WithOffHoursActivity_ShouldReturnCorrectDescription() {
        // Act
        String description = anomalyNotificationService.getAnomalyTypeDescription("OFF_HOURS_ACTIVITY");

        // Assert
        assertThat(description).isEqualTo("Off-Hours System Access");
    }

    @Test
    void getAnomalyTypeDescription_WithUnknownType_ShouldReturnDefaultDescription() {
        // Act
        String description = anomalyNotificationService.getAnomalyTypeDescription("UNKNOWN_TYPE");

        // Assert
        assertThat(description).isEqualTo("Unknown Anomaly Type");
    }

    @Test
    void getAnomalyTypeDescription_WithNullType_ShouldReturnDefaultDescription() {
        // Act
        String description = anomalyNotificationService.getAnomalyTypeDescription(null);

        // Assert
        assertThat(description).isEqualTo("Unknown Anomaly Type");
    }

    // ==================== ADMIN NOTIFICATION COUNT TESTS ====================

    @Test
    void getAdminNotificationCount_WithMultipleAdmins_ShouldReturnCorrectCount() {
        // Arrange
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);

        // Act
        long count = anomalyNotificationService.getAdminNotificationCount();

        // Assert
        assertThat(count).isEqualTo(2);
        verify(userRepository).findByIsAdminTrue();
    }

    @Test
    void getAdminNotificationCount_WithNoAdmins_ShouldReturnZero() {
        // Arrange
        when(userRepository.findByIsAdminTrue()).thenReturn(Collections.emptyList());

        // Act
        long count = anomalyNotificationService.getAdminNotificationCount();

        // Assert
        assertThat(count).isEqualTo(0);
        verify(userRepository).findByIsAdminTrue();
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void notifyAdminsOfAnomaly_WithNullUser_ShouldHandleGracefully() {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "Test anomaly details";

        // Act
        anomalyNotificationService.notifyAdminsOfAnomaly(null, testTicket, anomalyType, anomalyDetails);

        // Assert - Should not crash, but may not send emails
        verify(userRepository, atMost(1)).findByIsAdminTrue();
    }

    @Test
    void notifyAdminsOfAnomaly_WithNullTicket_ShouldHandleGracefully() {
        // Arrange
        String anomalyType = "FREQUENT_REQUESTS";
        String anomalyDetails = "Test anomaly details";

        // Act
        anomalyNotificationService.notifyAdminsOfAnomaly(testUser, null, anomalyType, anomalyDetails);

        // Assert - Should not crash, but may not send emails
        verify(userRepository, atMost(1)).findByIsAdminTrue();
    }

    @Test
    void checkAndNotifyAnomalies_WithNullUser_ShouldHandleGracefully() {
        // Act
        anomalyNotificationService.checkAndNotifyAnomalies(null, testTicket);

        // Assert - Should not crash
        verify(anomalyDetectionService, never()).getRequestFrequencyDetails(anyString());
    }

    @Test
    void checkAndNotifyAnomalies_WithUserHavingNullUserId_ShouldHandleGracefully() {
        // Arrange
        testUser.setUserId(null);

        // Act
        anomalyNotificationService.checkAndNotifyAnomalies(testUser, testTicket);

        // Assert - Should not crash, but may not check anomalies
        verify(anomalyDetectionService, atMost(1)).getRequestFrequencyDetails(any());
    }
}
