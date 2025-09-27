package com.apex.firefighter.unit.services.anomaly;

import com.apex.firefighter.model.AccessLog;
import com.apex.firefighter.model.AccessSession;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.AccessLogRepository;
import com.apex.firefighter.repository.AccessSessionRepository;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.service.anomaly.AnomalyDetectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnomalyDetectionServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AccessLogRepository accessLogRepository;

    @Mock
    private AccessSessionRepository accessSessionRepository;

    @InjectMocks
    private AnomalyDetectionService anomalyDetectionService;

    private final String TEST_USER_ID = "test-user-123";
    private AccessLog testAccessLog;
    private AccessSession testAccessSession;

    @BeforeEach
    void setUp() {
        User testUser = new User(TEST_USER_ID, "testuser", "test@example.com", "IT");
        
        testAccessLog = new AccessLog();
        testAccessLog.setUser(testUser);
        testAccessLog.setTimestamp(LocalDateTime.now().minusMinutes(5));
        testAccessLog.setAction("LOGIN");

        testAccessSession = new AccessSession();
        testAccessSession.setUser(null); // We'll mock the user relationship
        testAccessSession.setStartTime(LocalDateTime.now().minusMinutes(10));
        testAccessSession.setActive(true);
    }

    // ==================== ANOMALOUS TICKET CREATION TESTS ====================

    @Test
    void checkForAnomalousTicketCreation_WithFrequentRequestAnomaly_ShouldReturnTrue() {
        // Arrange
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(6L) // Exceeds MAX_REQUESTS_PER_HOUR (5)
            .thenReturn(10L); // For daily check

        when(accessLogRepository.findLoginEventsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        // Act
        boolean result = anomalyDetectionService.checkForAnomalousTicketCreation(TEST_USER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(ticketRepository, times(2)).countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class));
    }

    @Test
    void checkForAnomalousTicketCreation_WithDormantUserAnomaly_ShouldReturnTrue() {
        // Arrange
        // No frequent request anomaly
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(2L) // Below threshold
            .thenReturn(5L); // Below threshold

        // Dormant user anomaly setup
        when(accessLogRepository.findLoginEventsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testAccessLog)) // Recent login
            .thenReturn(Collections.emptyList()); // No old logins

        when(accessSessionRepository.findByUserIdBeforeDate(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList()); // No old sessions

        when(accessLogRepository.countActionsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(3L); // Actions after login

        // Act
        boolean result = anomalyDetectionService.checkForAnomalousTicketCreation(TEST_USER_ID);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void checkForAnomalousTicketCreation_WithNoAnomalies_ShouldReturnFalse() {
        // Arrange
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(2L) // Below threshold
            .thenReturn(5L); // Below threshold

        when(accessLogRepository.findLoginEventsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList()); // No recent login

        // Act
        boolean result = anomalyDetectionService.checkForAnomalousTicketCreation(TEST_USER_ID);

        // Assert
        assertThat(result).isFalse();
    }

    // ==================== FREQUENT REQUEST ANOMALY TESTS ====================

    @Test
    void getRequestFrequencyDetails_WithHourlyThresholdExceeded_ShouldReturnDetails() {
        // Arrange
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(6L) // Exceeds hourly threshold (5)
            .thenReturn(10L); // Daily count

        // Act
        String result = anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains("6 requests in the last hour");
        assertThat(result).contains("threshold: 5");
    }

    @Test
    void getRequestFrequencyDetails_WithDailyThresholdExceeded_ShouldReturnDetails() {
        // Arrange
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(3L) // Below hourly threshold
            .thenReturn(25L); // Exceeds daily threshold (20)

        // Act
        String result = anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains("25 requests in the last 24 hours");
        assertThat(result).contains("threshold: 20");
    }

    @Test
    void getRequestFrequencyDetails_WithNoThresholdExceeded_ShouldReturnNull() {
        // Arrange
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(3L) // Below hourly threshold
            .thenReturn(15L); // Below daily threshold

        // Act
        String result = anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void getRequestFrequencyDetails_WithExactThresholds_ShouldReturnDetails() {
        // Arrange - Test boundary conditions
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(5L) // Exactly at hourly threshold
            .thenReturn(10L);

        // Act
        String result = anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains("5 requests in the last hour");
    }

    // ==================== DORMANT USER ANOMALY TESTS ====================

    @Test
    void getDormantUserAnomalyDetails_WithDormantUserAnomaly_ShouldReturnDetails() {
        // Arrange
        when(accessLogRepository.findLoginEventsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testAccessLog)) // Recent login
            .thenReturn(Collections.emptyList()); // No old logins

        when(accessSessionRepository.findByUserIdBeforeDate(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList()); // No old sessions

        when(accessLogRepository.countActionsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(3L); // Actions after login

        // Act
        String result = anomalyDetectionService.getDormantUserAnomalyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains("User was dormant for 5+ days");
        assertThat(result).contains("made 3 actions within 15 minutes");
    }

    @Test
    void getDormantUserAnomalyDetails_WithNoRecentLogin_ShouldReturnNull() {
        // Arrange
        when(accessLogRepository.findLoginEventsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList()); // No recent login

        // Act
        String result = anomalyDetectionService.getDormantUserAnomalyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void getDormantUserAnomalyDetails_WithActiveUser_ShouldReturnNull() {
        // Arrange
        when(accessLogRepository.findLoginEventsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testAccessLog)) // Recent login
            .thenReturn(Arrays.asList(testAccessLog)); // Has old logins (not dormant)

        when(accessSessionRepository.findByUserIdBeforeDate(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testAccessSession)); // Has old sessions (not dormant)

        // Act
        String result = anomalyDetectionService.getDormantUserAnomalyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void getDormantUserAnomalyDetails_WithNoActionsAfterLogin_ShouldReturnNull() {
        // Arrange
        when(accessLogRepository.findLoginEventsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testAccessLog)) // Recent login
            .thenReturn(Collections.emptyList()); // No old logins

        when(accessSessionRepository.findByUserIdBeforeDate(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList()); // No old sessions

        when(accessLogRepository.countActionsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(0L); // No actions after login

        // Act
        String result = anomalyDetectionService.getDormantUserAnomalyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNull();
    }

    // ==================== OFF-HOURS ANOMALY TESTS ====================

    @Test
    void isOffHoursAnomaly_WithEarlyMorningRequest_ShouldReturnTrue() {
        // Arrange - Mock current time to be 6 AM (before 7 AM working hours)
        LocalDateTime earlyMorning = LocalDateTime.of(2023, 6, 15, 6, 0); // Thursday 6 AM

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(earlyMorning);

            // Act
            boolean result = anomalyDetectionService.isOffHoursAnomaly(TEST_USER_ID);

            // Assert
            assertThat(result).isTrue();
        }
    }

    @Test
    void isOffHoursAnomaly_WithLateEveningRequest_ShouldReturnTrue() {
        // Arrange - Mock current time to be 6 PM (after 5 PM working hours)
        LocalDateTime lateEvening = LocalDateTime.of(2023, 6, 15, 18, 0); // Thursday 6 PM

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(lateEvening);

            // Act
            boolean result = anomalyDetectionService.isOffHoursAnomaly(TEST_USER_ID);

            // Assert
            assertThat(result).isTrue();
        }
    }

    @Test
    void isOffHoursAnomaly_WithWeekendRequest_ShouldReturnTrue() {
        // Arrange - Mock current time to be Saturday
        LocalDateTime saturday = LocalDateTime.of(2023, 6, 17, 10, 0); // Saturday 10 AM

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(saturday);

            // Act
            boolean result = anomalyDetectionService.isOffHoursAnomaly(TEST_USER_ID);

            // Assert
            assertThat(result).isTrue();
        }
    }

    @Test
    void isOffHoursAnomaly_WithSundayRequest_ShouldReturnTrue() {
        // Arrange - Mock current time to be Sunday
        LocalDateTime sunday = LocalDateTime.of(2023, 6, 18, 14, 0); // Sunday 2 PM

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(sunday);

            // Act
            boolean result = anomalyDetectionService.isOffHoursAnomaly(TEST_USER_ID);

            // Assert
            assertThat(result).isTrue();
        }
    }

    @Test
    void isOffHoursAnomaly_WithNormalWorkingHours_ShouldReturnFalse() {
        // Arrange - Mock current time to be within working hours
        LocalDateTime workingHours = LocalDateTime.of(2023, 6, 15, 10, 0); // Thursday 10 AM

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(workingHours);

            // Act
            boolean result = anomalyDetectionService.isOffHoursAnomaly(TEST_USER_ID);

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Test
    void isOffHoursAnomaly_WithBoundaryTimes_ShouldHandleCorrectly() {
        // Test 7 AM (start of working hours)
        LocalDateTime sevenAM = LocalDateTime.of(2023, 6, 15, 7, 0);
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(sevenAM);
            boolean result = anomalyDetectionService.isOffHoursAnomaly(TEST_USER_ID);
            assertThat(result).isFalse(); // 7 AM should be allowed
        }

        // Test 5 PM (end of working hours)
        LocalDateTime fivePM = LocalDateTime.of(2023, 6, 15, 17, 0);
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fivePM);
            boolean result = anomalyDetectionService.isOffHoursAnomaly(TEST_USER_ID);
            assertThat(result).isFalse(); // 5 PM should be allowed
        }

        // Test 6 PM (after working hours)
        LocalDateTime sixPM = LocalDateTime.of(2023, 6, 15, 18, 0);
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(sixPM);
            boolean result = anomalyDetectionService.isOffHoursAnomaly(TEST_USER_ID);
            assertThat(result).isTrue(); // 6 PM should not be allowed
        }
    }

    // ==================== OFF-HOURS ANOMALY DETAILS TESTS ====================

    @Test
    void getOffHoursAnomalyDetails_WithEarlyMorningRequest_ShouldReturnTimeDetails() {
        // Arrange
        LocalDateTime earlyMorning = LocalDateTime.of(2023, 6, 15, 5, 0); // Thursday 5 AM

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(earlyMorning);

            // Act
            String result = anomalyDetectionService.getOffHoursAnomalyDetails(TEST_USER_ID);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).contains("5:00 which is outside of regular work hours");
            assertThat(result).contains("allowed: 7:00 AM - 17:00 PM");
        }
    }

    @Test
    void getOffHoursAnomalyDetails_WithWeekendRequest_ShouldReturnDayDetails() {
        // Arrange
        LocalDateTime saturday = LocalDateTime.of(2023, 6, 17, 10, 0); // Saturday 10 AM

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(saturday);

            // Act
            String result = anomalyDetectionService.getOffHoursAnomalyDetails(TEST_USER_ID);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).contains("SATURDAY which is outside of regular work hours");
            assertThat(result).contains("allowed: weekdays only");
        }
    }

    @Test
    void getOffHoursAnomalyDetails_WithNormalWorkingHours_ShouldReturnNull() {
        // Arrange
        LocalDateTime workingHours = LocalDateTime.of(2023, 6, 15, 10, 0); // Thursday 10 AM

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(workingHours);

            // Act
            String result = anomalyDetectionService.getOffHoursAnomalyDetails(TEST_USER_ID);

            // Assert
            assertThat(result).isNull();
        }
    }

    // ==================== FREQUENCY ANOMALY DETAILS TESTS ====================

    @Test
    void getFrequencyAnomalyDetails_WithFrequentRequestAnomaly_ShouldReturnFrequencyDetails() {
        // Arrange
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(6L) // Exceeds threshold
            .thenReturn(10L);

        // Act
        String result = anomalyDetectionService.getFrequencyAnomalyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).startsWith("Frequent Request Anomaly:");
        assertThat(result).contains("6 requests in the last hour");
    }

    @Test
    void getFrequencyAnomalyDetails_WithDormantUserAnomaly_ShouldReturnDormantDetails() {
        // Arrange
        // No frequent request anomaly
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(2L) // Below threshold
            .thenReturn(5L);

        // Dormant user anomaly
        when(accessLogRepository.findLoginEventsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testAccessLog))
            .thenReturn(Collections.emptyList());

        when(accessSessionRepository.findByUserIdBeforeDate(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        when(accessLogRepository.countActionsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(3L);

        // Act
        String result = anomalyDetectionService.getFrequencyAnomalyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).startsWith("Dormant User Anomaly:");
        assertThat(result).contains("User was dormant for 5+ days");
    }

    @Test
    void getFrequencyAnomalyDetails_WithNoAnomalies_ShouldReturnNull() {
        // Arrange
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(2L) // Below threshold
            .thenReturn(5L);

        when(accessLogRepository.findLoginEventsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList()); // No recent login

        // Act
        String result = anomalyDetectionService.getFrequencyAnomalyDetails(TEST_USER_ID);

        // Assert
        assertThat(result).isNull();
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void checkForAnomalousTicketCreation_WithNullUserId_ShouldHandleGracefully() {
        // Arrange
        when(ticketRepository.countTicketsByUserSince(eq(null), any(LocalDateTime.class)))
            .thenReturn(0L);

        when(accessLogRepository.findLoginEventsByUserSince(eq(null), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        // Act
        boolean result = anomalyDetectionService.checkForAnomalousTicketCreation(null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void checkForAnomalousTicketCreation_WithEmptyUserId_ShouldHandleGracefully() {
        // Arrange
        String emptyUserId = "";
        when(ticketRepository.countTicketsByUserSince(eq(emptyUserId), any(LocalDateTime.class)))
            .thenReturn(0L);

        when(accessLogRepository.findLoginEventsByUserSince(eq(emptyUserId), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        // Act
        boolean result = anomalyDetectionService.checkForAnomalousTicketCreation(emptyUserId);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void getRequestFrequencyDetails_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> 
            anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
    }

    @Test
    void isOffHoursAnomaly_WithNullUserId_ShouldHandleGracefully() {
        // Arrange
        LocalDateTime workingHours = LocalDateTime.of(2023, 6, 15, 10, 0);

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(workingHours);

            // Act
            boolean result = anomalyDetectionService.isOffHoursAnomaly(null);

            // Assert
            assertThat(result).isFalse();
        }
    }

    // ==================== INTEGRATION WORKFLOW TESTS ====================

    @Test
    void fullAnomalyCheck_WithMultipleAnomalies_ShouldDetectAll() {
        // Arrange - Frequent request anomaly
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(6L) // Exceeds hourly threshold
            .thenReturn(25L); // Exceeds daily threshold

        // Act
        boolean hasAnomaly = anomalyDetectionService.checkForAnomalousTicketCreation(TEST_USER_ID);
        String frequencyDetails = anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID);

        // Assert
        assertThat(hasAnomaly).isTrue();
        assertThat(frequencyDetails).isNotNull();
        assertThat(frequencyDetails).contains("6 requests in the last hour");
    }

    @Test
    void allAnomalyMethods_WithNormalBehavior_ShouldReturnNoAnomalies() {
        // Arrange - Normal behavior
        when(ticketRepository.countTicketsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(2L) // Below thresholds
            .thenReturn(10L);

        when(accessLogRepository.findLoginEventsByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        LocalDateTime workingHours = LocalDateTime.of(2023, 6, 15, 10, 0); // Thursday 10 AM

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(workingHours);

            // Act
            boolean hasTicketAnomaly = anomalyDetectionService.checkForAnomalousTicketCreation(TEST_USER_ID);
            String frequencyDetails = anomalyDetectionService.getRequestFrequencyDetails(TEST_USER_ID);
            String dormantDetails = anomalyDetectionService.getDormantUserAnomalyDetails(TEST_USER_ID);
            boolean hasOffHoursAnomaly = anomalyDetectionService.isOffHoursAnomaly(TEST_USER_ID);
            String offHoursDetails = anomalyDetectionService.getOffHoursAnomalyDetails(TEST_USER_ID);
            String combinedDetails = anomalyDetectionService.getFrequencyAnomalyDetails(TEST_USER_ID);

            // Assert
            assertThat(hasTicketAnomaly).isFalse();
            assertThat(frequencyDetails).isNull();
            assertThat(dormantDetails).isNull();
            assertThat(hasOffHoursAnomaly).isFalse();
            assertThat(offHoursDetails).isNull();
            assertThat(combinedDetails).isNull();
        }
    }
}
