package com.apex.firefighter.unit.services;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.GmailEmailService;
import com.apex.firefighter.service.GroupChangeNotificationService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GroupChangeNotificationServiceTest {

    @Mock
    private GmailEmailService emailService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupChangeNotificationService groupChangeNotificationService;

    private User testUser;
    private List<User> adminUsers;
    private final String TEST_TICKET_ID = "TICKET-001";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("test-user-123");
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setDepartment("IT");
        testUser.setDolibarrId("DOL-123");

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

    // ==================== SUSPICIOUS GROUP CHANGE DETECTION TESTS ====================

    @Test
    void notifyAdminsOfGroupChange_WithHighRiskFinancialGroup_ShouldSendNotifications() throws MessagingException {
        // Arrange
        String oldGroup = "HR Emergency Group";
        String newGroup = "Financial Emergency Group";
        String reason = "Emergency ticket creation";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), eq(testUser), eq(TEST_TICKET_ID), eq(oldGroup), eq(newGroup), eq(reason), eq("HIGH"));
    }

    @Test
    void notifyAdminsOfGroupChange_WithHighRiskManagementGroup_ShouldSendNotifications() throws MessagingException {
        // Arrange
        String oldGroup = "Logistics Emergency Group";
        String newGroup = "Management Emergency Group";
        String reason = "Role change";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), eq(testUser), eq(TEST_TICKET_ID), eq(oldGroup), eq(newGroup), eq(reason), eq("HIGH"));
    }

    @Test
    void notifyAdminsOfGroupChange_WithMediumRiskHRGroup_ShouldSendNotifications() throws MessagingException {
        // Arrange
        String oldGroup = "Logistics Emergency Group";
        String newGroup = "HR Emergency Group";
        String reason = "Department transfer";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), eq(testUser), eq(TEST_TICKET_ID), eq(oldGroup), eq(newGroup), eq(reason), eq("MEDIUM"));
    }

    @Test
    void notifyAdminsOfGroupChange_WithNonSuspiciousLogisticsOnly_ShouldNotSendNotifications() throws MessagingException {
        // Arrange
        String oldGroup = null;
        String newGroup = "Logistics Emergency Group";
        String reason = "New assignment";

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(userRepository, never()).findByIsAdminTrue();
        verify(emailService, never()).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void notifyAdminsOfGroupChange_WithNoChange_ShouldNotSendNotifications() throws MessagingException {
        // Arrange
        String oldGroup = "HR Emergency Group";
        String newGroup = "HR Emergency Group";
        String reason = "No change";

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(userRepository, never()).findByIsAdminTrue();
        verify(emailService, never()).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void notifyAdminsOfGroupChange_WithNoAdminUsers_ShouldNotSendEmails() throws MessagingException {
        // Arrange
        String oldGroup = "HR Emergency Group";
        String newGroup = "Financial Emergency Group";
        String reason = "Emergency ticket creation";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(Collections.emptyList());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, never()).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void notifyAdminsOfGroupChange_WithEmailException_ShouldContinueWithOtherAdmins() throws MessagingException {
        // Arrange
        String oldGroup = "HR Emergency Group";
        String newGroup = "Financial Emergency Group";
        String reason = "Emergency ticket creation";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doThrow(new MessagingException("Email failed")).when(emailService)
            .sendSuspiciousGroupChangeNotificationEmail(eq("admin1@example.com"), any(), any(), any(), any(), any(), any());
        doNothing().when(emailService)
            .sendSuspiciousGroupChangeNotificationEmail(eq("admin2@example.com"), any(), any(), any(), any(), any(), any());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // ==================== GROUP CHANGE BY ID TESTS ====================

    @Test
    void notifyAdminsOfGroupChangeById_WithValidGroupIds_ShouldResolveNamesAndNotify() throws MessagingException {
        // Arrange
        Integer oldGroupId = 1; // HR Emergency Group
        Integer newGroupId = 2; // Financial Emergency Group
        String reason = "Emergency escalation";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChangeById(testUser, TEST_TICKET_ID, oldGroupId, newGroupId, reason);

        // Assert
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), eq(testUser), eq(TEST_TICKET_ID), eq("HR Emergency Group"), eq("Financial Emergency Group"), eq(reason), eq("HIGH"));
    }

    @Test
    void notifyAdminsOfGroupChangeById_WithNullOldGroupId_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        Integer oldGroupId = null;
        Integer newGroupId = 3; // Management Emergency Group
        String reason = "New user assignment";
        
        // Reset and setup mocks explicitly
        reset(userRepository, emailService);
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        lenient().doNothing().when(emailService).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChangeById(testUser, TEST_TICKET_ID, oldGroupId, newGroupId, reason);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), eq(testUser), eq(TEST_TICKET_ID), eq(null), eq("Management Emergency Group"), eq(reason), eq("HIGH"));
    }

    @Test
    void notifyAdminsOfGroupChangeById_WithUnknownGroupId_ShouldUseGroupIdFormat() throws MessagingException {
        // Arrange
        Integer oldGroupId = 4; // Logistics Emergency Group
        Integer newGroupId = 99; // Unknown group
        String reason = "System migration";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChangeById(testUser, TEST_TICKET_ID, oldGroupId, newGroupId, reason);

        // Assert
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), eq(testUser), eq(TEST_TICKET_ID), eq("Logistics Emergency Group"), eq("Group ID: 99"), eq(reason), eq("LOW"));
    }

    // ==================== ADMIN NOTIFICATION COUNT TESTS ====================

    @Test
    void getAdminNotificationCount_WithMultipleAdmins_ShouldReturnCorrectCount() {
        // Arrange
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);

        // Act
        long count = groupChangeNotificationService.getAdminNotificationCount();

        // Assert
        assertThat(count).isEqualTo(2);
        verify(userRepository).findByIsAdminTrue();
    }

    @Test
    void getAdminNotificationCount_WithNoAdmins_ShouldReturnZero() {
        // Arrange
        when(userRepository.findByIsAdminTrue()).thenReturn(Collections.emptyList());

        // Act
        long count = groupChangeNotificationService.getAdminNotificationCount();

        // Assert
        assertThat(count).isEqualTo(0);
        verify(userRepository).findByIsAdminTrue();
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void notifyAdminsOfGroupChange_WithRepositoryException_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        String oldGroup = "HR Emergency Group";
        String newGroup = "Financial Emergency Group";
        String reason = "Emergency ticket creation";
        
        when(userRepository.findByIsAdminTrue()).thenThrow(new RuntimeException("Database error"));

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, never()).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void notifyAdminsOfGroupChange_WithNullUser_ShouldHandleGracefully() {
        // Arrange
        String oldGroup = "HR Emergency Group";
        String newGroup = "Financial Emergency Group";
        String reason = "Emergency ticket creation";

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(null, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert - Should not crash, but may not send emails
        verify(userRepository, atMost(1)).findByIsAdminTrue();
    }

    @Test
    void notifyAdminsOfGroupChange_WithNullTicketId_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        String oldGroup = "HR Emergency Group";
        String newGroup = "Financial Emergency Group";
        String reason = "Emergency ticket creation";
        
        // Reset and setup mocks explicitly
        reset(userRepository, emailService);
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        lenient().doNothing().when(emailService).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, null, oldGroup, newGroup, reason);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), eq(testUser), eq(null), eq(oldGroup), eq(newGroup), eq(reason), eq("HIGH"));
    }

    @Test
    void notifyAdminsOfGroupChange_WithNullReason_ShouldHandleGracefully() throws MessagingException {
        // Arrange
        String oldGroup = "HR Emergency Group";
        String newGroup = "Financial Emergency Group";
        
        // Reset and setup mocks explicitly
        reset(userRepository, emailService);
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, null);

        // Assert
        verify(userRepository).findByIsAdminTrue();
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), eq(testUser), eq(TEST_TICKET_ID), eq(oldGroup), eq(newGroup), eq(null), eq("HIGH"));
    }

    // ==================== SUSPICIOUS GROUP CHANGE LOGIC TESTS ====================

    @Test
    void notifyAdminsOfGroupChange_FromHRToLogistics_ShouldBeSuspicious() throws MessagingException {
        // Arrange
        String oldGroup = "HR Emergency Group";
        String newGroup = "Logistics Emergency Group";
        String reason = "Department transfer";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), eq(testUser), eq(TEST_TICKET_ID), eq(oldGroup), eq(newGroup), eq(reason), eq("MEDIUM"));
    }

    @Test
    void notifyAdminsOfGroupChange_FromLogisticsToHR_ShouldBeSuspicious() throws MessagingException {
        // Arrange
        String oldGroup = "Logistics Emergency Group";
        String newGroup = "HR Emergency Group";
        String reason = "Role change";
        
        when(userRepository.findByIsAdminTrue()).thenReturn(adminUsers);
        doNothing().when(emailService).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(emailService, times(2)).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), eq(testUser), eq(TEST_TICKET_ID), eq(oldGroup), eq(newGroup), eq(reason), eq("MEDIUM"));
    }

    @Test
    void notifyAdminsOfGroupChange_RemovalFromLogistics_ShouldNotBeSuspicious() throws MessagingException {
        // Arrange
        String oldGroup = "Logistics Emergency Group";
        String newGroup = null;
        String reason = "User deactivation";

        // Act
        groupChangeNotificationService.notifyAdminsOfGroupChange(testUser, TEST_TICKET_ID, oldGroup, newGroup, reason);

        // Assert
        verify(userRepository, never()).findByIsAdminTrue();
        verify(emailService, never()).sendSuspiciousGroupChangeNotificationEmail(
            anyString(), any(User.class), anyString(), anyString(), anyString(), anyString(), anyString());
    }
}
