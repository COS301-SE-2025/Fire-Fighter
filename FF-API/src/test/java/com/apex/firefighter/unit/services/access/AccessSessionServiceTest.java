package com.apex.firefighter.unit.services.access;

import com.apex.firefighter.model.AccessRequest;
import com.apex.firefighter.model.AccessSession;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.AccessRequestRepository;
import com.apex.firefighter.repository.AccessSessionRepository;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.access.AccessSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessSessionServiceTest {

    @Mock
    private AccessSessionRepository accessSessionRepository;

    @Mock
    private AccessRequestRepository accessRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccessSession mockAccessSession;

    @InjectMocks
    private AccessSessionService accessSessionService;

    private User testUser;
    private AccessRequest testAccessRequest;
    private AccessSession testAccessSession;
    private final String FIREBASE_UID = "test-firebase-uid";
    private final String TICKET_ID = "TICKET-001";
    private final Long REQUEST_ID = 1L;
    private final Long SESSION_ID = 1L;
    private final String SESSION_TOKEN = "session-token-123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(FIREBASE_UID);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDepartment("IT");
        testUser.setIsAuthorized(true);

        testAccessRequest = new AccessRequest();
        testAccessRequest.setId(REQUEST_ID);
        testAccessRequest.setTicketId(TICKET_ID);
        testAccessRequest.setUser(testUser);
        testAccessRequest.setStatus(AccessRequest.RequestStatus.APPROVED);
        testAccessRequest.setRequestTime(ZonedDateTime.now());

        testAccessSession = new AccessSession();
        // Note: ID is auto-generated, cannot be set manually
        testAccessSession.setUser(testUser);
        testAccessSession.setAccessRequest(testAccessRequest);
        testAccessSession.setStartTime(LocalDateTime.now());
        testAccessSession.setEndTime(null);
        testAccessSession.setActive(true);
        testAccessSession.setSessionToken(SESSION_TOKEN);
    }

    // ==================== CREATE ACCESS SESSION TESTS ====================

    @Test
    void createAccessSession_WithValidAccessRequest_ShouldCreateAndReturnSession() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(testAccessRequest));
        when(accessSessionRepository.save(any(AccessSession.class))).thenReturn(testAccessSession);

        // Act
        AccessSession result = accessSessionService.createAccessSession(REQUEST_ID);

        // Assert
        assertThat(result).isEqualTo(testAccessSession);
        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessSessionRepository).save(any(AccessSession.class));
    }

    @Test
    void createAccessSession_WithNonExistentAccessRequest_ShouldThrowException() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accessSessionService.createAccessSession(REQUEST_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Access request not found with ID: " + REQUEST_ID);

        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessSessionRepository, never()).save(any(AccessSession.class));
    }

    @Test
    void createAccessSession_WithNullRequestId_ShouldHandleGracefully() {
        // Arrange
        when(accessRequestRepository.findById(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accessSessionService.createAccessSession(null))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Access request not found with ID: null");

        verify(accessRequestRepository).findById(null);
        verify(accessSessionRepository, never()).save(any(AccessSession.class));
    }

    @Test
    void createAccessSession_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(testAccessRequest));
        when(accessSessionRepository.save(any(AccessSession.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> accessSessionService.createAccessSession(REQUEST_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessSessionRepository).save(any(AccessSession.class));
    }

    // ==================== END ACCESS SESSION TESTS ====================

    @Test
    void endAccessSession_WithValidSession_ShouldEndAndReturnSession() {
        // Arrange
        when(accessSessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(mockAccessSession));
        when(accessSessionRepository.save(any(AccessSession.class))).thenReturn(mockAccessSession);

        // Act
        AccessSession result = accessSessionService.endAccessSession(SESSION_ID);

        // Assert
        assertThat(result).isEqualTo(mockAccessSession);
        verify(accessSessionRepository).findById(SESSION_ID);
        verify(accessSessionRepository).save(mockAccessSession);
        verify(mockAccessSession).setEndTime(any(LocalDateTime.class));
        verify(mockAccessSession).setActive(false);
    }

    @Test
    void endAccessSession_WithNonExistentSession_ShouldThrowException() {
        // Arrange
        when(accessSessionRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accessSessionService.endAccessSession(SESSION_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Access session not found with ID: " + SESSION_ID);

        verify(accessSessionRepository).findById(SESSION_ID);
        verify(accessSessionRepository, never()).save(any(AccessSession.class));
    }

    @Test
    void endAccessSession_WithNullSessionId_ShouldHandleGracefully() {
        // Arrange
        when(accessSessionRepository.findById(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accessSessionService.endAccessSession(null))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Access session not found with ID: null");

        verify(accessSessionRepository).findById(null);
        verify(accessSessionRepository, never()).save(any(AccessSession.class));
    }

    @Test
    void endAccessSession_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(accessSessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(testAccessSession));
        when(accessSessionRepository.save(any(AccessSession.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> accessSessionService.endAccessSession(SESSION_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(accessSessionRepository).findById(SESSION_ID);
        verify(accessSessionRepository).save(testAccessSession);
    }

    // ==================== ACTIVE SESSION MANAGEMENT TESTS ====================

    @Test
    void getActiveSessionForUser_WithActiveSession_ShouldReturnSession() {
        // Arrange
        List<AccessSession> activeSessions = Arrays.asList(testAccessSession);
        when(accessSessionRepository.findActiveByUserId(FIREBASE_UID)).thenReturn(activeSessions);

        // Act
        Optional<AccessSession> result = accessSessionService.getActiveSessionForUser(FIREBASE_UID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testAccessSession);
        verify(accessSessionRepository).findActiveByUserId(FIREBASE_UID);
    }

    @Test
    void getActiveSessionForUser_WithNoActiveSession_ShouldReturnEmpty() {
        // Arrange
        when(accessSessionRepository.findActiveByUserId(FIREBASE_UID)).thenReturn(Collections.emptyList());

        // Act
        Optional<AccessSession> result = accessSessionService.getActiveSessionForUser(FIREBASE_UID);

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findActiveByUserId(FIREBASE_UID);
    }

    @Test
    void getActiveSessionForUser_WithMultipleActiveSessions_ShouldReturnFirst() {
        // Arrange
        AccessSession secondSession = new AccessSession();
        // Note: ID is auto-generated, cannot be set manually
        List<AccessSession> activeSessions = Arrays.asList(testAccessSession, secondSession);
        when(accessSessionRepository.findActiveByUserId(FIREBASE_UID)).thenReturn(activeSessions);

        // Act
        Optional<AccessSession> result = accessSessionService.getActiveSessionForUser(FIREBASE_UID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testAccessSession);
        verify(accessSessionRepository).findActiveByUserId(FIREBASE_UID);
    }

    @Test
    void hasActiveSession_WithActiveSession_ShouldReturnTrue() {
        // Arrange
        List<AccessSession> activeSessions = Arrays.asList(testAccessSession);
        when(accessSessionRepository.findActiveByUserId(FIREBASE_UID)).thenReturn(activeSessions);

        // Act
        boolean result = accessSessionService.hasActiveSession(FIREBASE_UID);

        // Assert
        assertThat(result).isTrue();
        verify(accessSessionRepository).findActiveByUserId(FIREBASE_UID);
    }

    @Test
    void hasActiveSession_WithNoActiveSession_ShouldReturnFalse() {
        // Arrange
        when(accessSessionRepository.findActiveByUserId(FIREBASE_UID)).thenReturn(Collections.emptyList());

        // Act
        boolean result = accessSessionService.hasActiveSession(FIREBASE_UID);

        // Assert
        assertThat(result).isFalse();
        verify(accessSessionRepository).findActiveByUserId(FIREBASE_UID);
    }

    @Test
    void hasActiveSession_WithNullFirebaseUid_ShouldHandleGracefully() {
        // Arrange
        when(accessSessionRepository.findActiveByUserId(null)).thenReturn(Collections.emptyList());

        // Act
        boolean result = accessSessionService.hasActiveSession(null);

        // Assert
        assertThat(result).isFalse();
        verify(accessSessionRepository).findActiveByUserId(null);
    }

    // ==================== QUERY OPERATIONS TESTS ====================

    @Test
    void getAllAccessSessions_ShouldReturnAllSessions() {
        // Arrange
        List<AccessSession> sessions = Arrays.asList(testAccessSession);
        when(accessSessionRepository.findAll()).thenReturn(sessions);

        // Act
        List<AccessSession> result = accessSessionService.getAllAccessSessions();

        // Assert
        assertThat(result).isEqualTo(sessions);
        assertThat(result).hasSize(1);
        verify(accessSessionRepository).findAll();
    }

    @Test
    void getAllAccessSessions_WithEmptyRepository_ShouldReturnEmptyList() {
        // Arrange
        when(accessSessionRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<AccessSession> result = accessSessionService.getAllAccessSessions();

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findAll();
    }

    @Test
    void getAccessSessionById_WithExistingId_ShouldReturnSession() {
        // Arrange
        when(accessSessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(testAccessSession));

        // Act
        Optional<AccessSession> result = accessSessionService.getAccessSessionById(SESSION_ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testAccessSession);
        verify(accessSessionRepository).findById(SESSION_ID);
    }

    @Test
    void getAccessSessionById_WithNonExistentId_ShouldReturnEmpty() {
        // Arrange
        when(accessSessionRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

        // Act
        Optional<AccessSession> result = accessSessionService.getAccessSessionById(SESSION_ID);

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findById(SESSION_ID);
    }

    @Test
    void getAccessSessionById_WithNullId_ShouldReturnEmpty() {
        // Arrange
        when(accessSessionRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<AccessSession> result = accessSessionService.getAccessSessionById(null);

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findById(null);
    }

    @Test
    void getAccessSessionsByUser_ShouldReturnUserSessions() {
        // Arrange
        List<AccessSession> userSessions = Arrays.asList(testAccessSession);
        when(accessSessionRepository.findByUserId(FIREBASE_UID)).thenReturn(userSessions);

        // Act
        List<AccessSession> result = accessSessionService.getAccessSessionsByUser(FIREBASE_UID);

        // Assert
        assertThat(result).isEqualTo(userSessions);
        assertThat(result).hasSize(1);
        verify(accessSessionRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getAccessSessionsByUser_WithNoSessions_ShouldReturnEmptyList() {
        // Arrange
        when(accessSessionRepository.findByUserId(FIREBASE_UID)).thenReturn(Collections.emptyList());

        // Act
        List<AccessSession> result = accessSessionService.getAccessSessionsByUser(FIREBASE_UID);

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getAccessSessionsByUser_WithNullUserId_ShouldReturnEmptyList() {
        // Arrange
        when(accessSessionRepository.findByUserId(null)).thenReturn(Collections.emptyList());

        // Act
        List<AccessSession> result = accessSessionService.getAccessSessionsByUser(null);

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findByUserId(null);
    }

    @Test
    void getActiveAccessSessions_ShouldReturnActiveSessions() {
        // Arrange
        List<AccessSession> activeSessions = Arrays.asList(testAccessSession);
        when(accessSessionRepository.findByActiveTrue()).thenReturn(activeSessions);

        // Act
        List<AccessSession> result = accessSessionService.getActiveAccessSessions();

        // Assert
        assertThat(result).isEqualTo(activeSessions);
        assertThat(result).hasSize(1);
        verify(accessSessionRepository).findByActiveTrue();
    }

    @Test
    void getActiveAccessSessions_WithNoActiveSessions_ShouldReturnEmptyList() {
        // Arrange
        when(accessSessionRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        // Act
        List<AccessSession> result = accessSessionService.getActiveAccessSessions();

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findByActiveTrue();
    }

    @Test
    void getAccessSessionsByTicket_ShouldReturnTicketSessions() {
        // Arrange
        List<AccessSession> ticketSessions = Arrays.asList(testAccessSession);
        when(accessSessionRepository.findByTicketId(TICKET_ID)).thenReturn(ticketSessions);

        // Act
        List<AccessSession> result = accessSessionService.getAccessSessionsByTicket(TICKET_ID);

        // Assert
        assertThat(result).isEqualTo(ticketSessions);
        assertThat(result).hasSize(1);
        verify(accessSessionRepository).findByTicketId(TICKET_ID);
    }

    @Test
    void getAccessSessionsByTicket_WithNoSessions_ShouldReturnEmptyList() {
        // Arrange
        when(accessSessionRepository.findByTicketId(TICKET_ID)).thenReturn(Collections.emptyList());

        // Act
        List<AccessSession> result = accessSessionService.getAccessSessionsByTicket(TICKET_ID);

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findByTicketId(TICKET_ID);
    }

    @Test
    void getAccessSessionsByTicket_WithNullTicketId_ShouldReturnEmptyList() {
        // Arrange
        when(accessSessionRepository.findByTicketId(null)).thenReturn(Collections.emptyList());

        // Act
        List<AccessSession> result = accessSessionService.getAccessSessionsByTicket(null);

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findByTicketId(null);
    }

    @Test
    void getSessionByToken_WithExistingToken_ShouldReturnSession() {
        // Arrange
        when(accessSessionRepository.findBySessionToken(SESSION_TOKEN)).thenReturn(Optional.of(testAccessSession));

        // Act
        Optional<AccessSession> result = accessSessionService.getSessionByToken(SESSION_TOKEN);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testAccessSession);
        verify(accessSessionRepository).findBySessionToken(SESSION_TOKEN);
    }

    @Test
    void getSessionByToken_WithNonExistentToken_ShouldReturnEmpty() {
        // Arrange
        when(accessSessionRepository.findBySessionToken(SESSION_TOKEN)).thenReturn(Optional.empty());

        // Act
        Optional<AccessSession> result = accessSessionService.getSessionByToken(SESSION_TOKEN);

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findBySessionToken(SESSION_TOKEN);
    }

    @Test
    void getSessionByToken_WithNullToken_ShouldHandleGracefully() {
        // Arrange
        when(accessSessionRepository.findBySessionToken(null)).thenReturn(Optional.empty());

        // Act
        Optional<AccessSession> result = accessSessionService.getSessionByToken(null);

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findBySessionToken(null);
    }

    @Test
    void getActiveSessionForUser_WithNullUserId_ShouldReturnEmpty() {
        // Arrange
        when(accessSessionRepository.findActiveByUserId(null)).thenReturn(Collections.emptyList());

        // Act
        Optional<AccessSession> result = accessSessionService.getActiveSessionForUser(null);

        // Assert
        assertThat(result).isEmpty();
        verify(accessSessionRepository).findActiveByUserId(null);
    }

    // ==================== BULK OPERATIONS TESTS ====================

    @Test
    void endAllActiveSessionsForUser_WithActiveSessions_ShouldEndAllSessions() {
        // Arrange
        AccessSession secondSession = mock(AccessSession.class);
        List<AccessSession> activeSessions = Arrays.asList(mockAccessSession, secondSession);
        when(accessSessionRepository.findActiveByUserId(FIREBASE_UID)).thenReturn(activeSessions);
        when(accessSessionRepository.save(any(AccessSession.class))).thenReturn(mockAccessSession);

        // Act
        accessSessionService.endAllActiveSessionsForUser(FIREBASE_UID);

        // Assert
        verify(accessSessionRepository).findActiveByUserId(FIREBASE_UID);
        verify(accessSessionRepository, times(2)).save(any(AccessSession.class));
        verify(mockAccessSession).setEndTime(any(LocalDateTime.class));
        verify(mockAccessSession).setActive(false);
        verify(secondSession).setEndTime(any(LocalDateTime.class));
        verify(secondSession).setActive(false);
        verify(secondSession).setEndTime(any(LocalDateTime.class));
        verify(secondSession).setActive(false);
    }

    @Test
    void endAllActiveSessionsForUser_WithNoActiveSessions_ShouldDoNothing() {
        // Arrange
        when(accessSessionRepository.findActiveByUserId(FIREBASE_UID)).thenReturn(Collections.emptyList());

        // Act
        accessSessionService.endAllActiveSessionsForUser(FIREBASE_UID);

        // Assert
        verify(accessSessionRepository).findActiveByUserId(FIREBASE_UID);
        verify(accessSessionRepository, never()).save(any(AccessSession.class));
    }

    @Test
    void endAllActiveSessionsForUser_WithNullFirebaseUid_ShouldHandleGracefully() {
        // Arrange
        when(accessSessionRepository.findActiveByUserId(null)).thenReturn(Collections.emptyList());

        // Act
        accessSessionService.endAllActiveSessionsForUser(null);

        // Assert
        verify(accessSessionRepository).findActiveByUserId(null);
        verify(accessSessionRepository, never()).save(any(AccessSession.class));
    }

    // ==================== COUNT OPERATIONS TESTS ====================

    @Test
    void getSessionCountForUser_ShouldReturnCorrectCount() {
        // Arrange
        List<AccessSession> userSessions = Arrays.asList(testAccessSession, new AccessSession());
        when(accessSessionRepository.findByUserId(FIREBASE_UID)).thenReturn(userSessions);

        // Act
        long result = accessSessionService.getSessionCountForUser(FIREBASE_UID);

        // Assert
        assertThat(result).isEqualTo(2L);
        verify(accessSessionRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getSessionCountForUser_WithNoSessions_ShouldReturnZero() {
        // Arrange
        when(accessSessionRepository.findByUserId(FIREBASE_UID)).thenReturn(Collections.emptyList());

        // Act
        long result = accessSessionService.getSessionCountForUser(FIREBASE_UID);

        // Assert
        assertThat(result).isEqualTo(0L);
        verify(accessSessionRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getActiveSessionCount_ShouldReturnCorrectCount() {
        // Arrange
        List<AccessSession> activeSessions = Arrays.asList(testAccessSession, new AccessSession(), new AccessSession());
        when(accessSessionRepository.findByActiveTrue()).thenReturn(activeSessions);

        // Act
        long result = accessSessionService.getActiveSessionCount();

        // Assert
        assertThat(result).isEqualTo(3L);
        verify(accessSessionRepository).findByActiveTrue();
    }

    @Test
    void getActiveSessionCount_WithNoActiveSessions_ShouldReturnZero() {
        // Arrange
        when(accessSessionRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        // Act
        long result = accessSessionService.getActiveSessionCount();

        // Assert
        assertThat(result).isEqualTo(0L);
        verify(accessSessionRepository).findByActiveTrue();
    }

    // ==================== DELETE OPERATIONS TESTS ====================

    @Test
    void deleteAccessSession_WithExistingSession_ShouldReturnTrue() {
        // Arrange
        when(accessSessionRepository.existsById(SESSION_ID)).thenReturn(true);
        doNothing().when(accessSessionRepository).deleteById(SESSION_ID);

        // Act
        boolean result = accessSessionService.deleteAccessSession(SESSION_ID);

        // Assert
        assertThat(result).isTrue();
        verify(accessSessionRepository).existsById(SESSION_ID);
        verify(accessSessionRepository).deleteById(SESSION_ID);
    }

    @Test
    void deleteAccessSession_WithNonExistentSession_ShouldReturnFalse() {
        // Arrange
        when(accessSessionRepository.existsById(SESSION_ID)).thenReturn(false);

        // Act
        boolean result = accessSessionService.deleteAccessSession(SESSION_ID);

        // Assert
        assertThat(result).isFalse();
        verify(accessSessionRepository).existsById(SESSION_ID);
        verify(accessSessionRepository, never()).deleteById(SESSION_ID);
    }

    @Test
    void deleteAccessSession_WhenDeleteThrowsException_ShouldPropagateException() {
        // Arrange
        when(accessSessionRepository.existsById(SESSION_ID)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(accessSessionRepository).deleteById(SESSION_ID);

        // Act & Assert
        assertThatThrownBy(() -> accessSessionService.deleteAccessSession(SESSION_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(accessSessionRepository).existsById(SESSION_ID);
        verify(accessSessionRepository).deleteById(SESSION_ID);
    }

    @Test
    void deleteAccessSession_WithNullId_ShouldHandleGracefully() {
        // Arrange
        when(accessSessionRepository.existsById(null)).thenReturn(false);

        // Act
        boolean result = accessSessionService.deleteAccessSession(null);

        // Assert
        assertThat(result).isFalse();
        verify(accessSessionRepository).existsById(null);
        verify(accessSessionRepository, never()).deleteById(null);
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void getAllAccessSessions_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(accessSessionRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> accessSessionService.getAllAccessSessions())
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(accessSessionRepository).findAll();
    }

    @Test
    void getAccessSessionsByUser_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(accessSessionRepository.findByUserId(FIREBASE_UID)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> accessSessionService.getAccessSessionsByUser(FIREBASE_UID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(accessSessionRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void endAllActiveSessionsForUser_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        List<AccessSession> activeSessions = Arrays.asList(testAccessSession);
        when(accessSessionRepository.findActiveByUserId(FIREBASE_UID)).thenReturn(activeSessions);
        when(accessSessionRepository.save(any(AccessSession.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> accessSessionService.endAllActiveSessionsForUser(FIREBASE_UID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(accessSessionRepository).findActiveByUserId(FIREBASE_UID);
        verify(accessSessionRepository).save(any(AccessSession.class));
    }

    // ==================== COMPREHENSIVE INTEGRATION TESTS ====================

    @Test
    void fullWorkflow_CreateEndAndQuery_ShouldWorkCorrectly() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(testAccessRequest));
        when(accessSessionRepository.save(any(AccessSession.class))).thenReturn(testAccessSession);
        when(accessSessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(testAccessSession));
        when(accessSessionRepository.findByUserId(FIREBASE_UID)).thenReturn(Arrays.asList(testAccessSession));
        when(accessSessionRepository.findActiveByUserId(FIREBASE_UID)).thenReturn(Arrays.asList(testAccessSession));

        // Act
        AccessSession created = accessSessionService.createAccessSession(REQUEST_ID);
        boolean hasActive = accessSessionService.hasActiveSession(FIREBASE_UID);
        Optional<AccessSession> activeSession = accessSessionService.getActiveSessionForUser(FIREBASE_UID);
        AccessSession ended = accessSessionService.endAccessSession(SESSION_ID);
        List<AccessSession> userSessions = accessSessionService.getAccessSessionsByUser(FIREBASE_UID);

        // Assert
        assertThat(created).isEqualTo(testAccessSession);
        assertThat(hasActive).isTrue();
        assertThat(activeSession).isPresent();
        assertThat(activeSession.get()).isEqualTo(testAccessSession);
        assertThat(ended).isEqualTo(testAccessSession);
        assertThat(userSessions).hasSize(1);
        assertThat(userSessions.get(0)).isEqualTo(testAccessSession);

        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessSessionRepository, times(2)).save(any(AccessSession.class));
        verify(accessSessionRepository).findById(SESSION_ID);
        verify(accessSessionRepository).findByUserId(FIREBASE_UID);
        verify(accessSessionRepository, times(2)).findActiveByUserId(FIREBASE_UID); // Called by hasActiveSession and getActiveSessionForUser
    }

    @Test
    void sessionManagement_MultipleOperations_ShouldWorkCorrectly() {
        // Arrange
        AccessSession secondSession = new AccessSession();
        // Note: ID is auto-generated, cannot be set manually
        secondSession.setActive(true);

        when(accessSessionRepository.findActiveByUserId(FIREBASE_UID)).thenReturn(Arrays.asList(testAccessSession, secondSession));
        when(accessSessionRepository.findByUserId(FIREBASE_UID)).thenReturn(Arrays.asList(testAccessSession, secondSession));
        when(accessSessionRepository.findByActiveTrue()).thenReturn(Arrays.asList(testAccessSession, secondSession));
        when(accessSessionRepository.save(any(AccessSession.class))).thenReturn(testAccessSession);

        // Act
        boolean hasActive = accessSessionService.hasActiveSession(FIREBASE_UID);
        long userSessionCount = accessSessionService.getSessionCountForUser(FIREBASE_UID);
        long activeSessionCount = accessSessionService.getActiveSessionCount();
        accessSessionService.endAllActiveSessionsForUser(FIREBASE_UID);

        // Assert
        assertThat(hasActive).isTrue();
        assertThat(userSessionCount).isEqualTo(2L);
        assertThat(activeSessionCount).isEqualTo(2L);

        verify(accessSessionRepository, times(2)).findActiveByUserId(FIREBASE_UID); // Called by hasActiveSession and endAllActiveSessionsForUser
        verify(accessSessionRepository).findByUserId(FIREBASE_UID);
        verify(accessSessionRepository).findByActiveTrue();
        verify(accessSessionRepository, times(2)).save(any(AccessSession.class)); // Called twice in endAllActiveSessionsForUser
    }

    @Test
    void queryOperations_AllMethods_ShouldWorkCorrectly() {
        // Arrange
        List<AccessSession> allSessions = Arrays.asList(testAccessSession);
        when(accessSessionRepository.findAll()).thenReturn(allSessions);
        when(accessSessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(testAccessSession));
        when(accessSessionRepository.findByUserId(FIREBASE_UID)).thenReturn(allSessions);
        when(accessSessionRepository.findByActiveTrue()).thenReturn(allSessions);
        when(accessSessionRepository.findByTicketId(TICKET_ID)).thenReturn(allSessions);
        when(accessSessionRepository.findBySessionToken(SESSION_TOKEN)).thenReturn(Optional.of(testAccessSession));

        // Act & Assert - All query methods should work
        List<AccessSession> all = accessSessionService.getAllAccessSessions();
        Optional<AccessSession> byId = accessSessionService.getAccessSessionById(SESSION_ID);
        List<AccessSession> byUser = accessSessionService.getAccessSessionsByUser(FIREBASE_UID);
        List<AccessSession> active = accessSessionService.getActiveAccessSessions();
        List<AccessSession> byTicket = accessSessionService.getAccessSessionsByTicket(TICKET_ID);
        Optional<AccessSession> byToken = accessSessionService.getSessionByToken(SESSION_TOKEN);

        assertThat(all).hasSize(1);
        assertThat(byId).isPresent();
        assertThat(byUser).hasSize(1);
        assertThat(active).hasSize(1);
        assertThat(byTicket).hasSize(1);
        assertThat(byToken).isPresent();

        verify(accessSessionRepository).findAll();
        verify(accessSessionRepository).findById(SESSION_ID);
        verify(accessSessionRepository).findByUserId(FIREBASE_UID);
        verify(accessSessionRepository).findByActiveTrue();
        verify(accessSessionRepository).findByTicketId(TICKET_ID);
        verify(accessSessionRepository).findBySessionToken(SESSION_TOKEN);
    }

    @Test
    void deleteAndCountOperations_ShouldWorkCorrectly() {
        // Arrange
        when(accessSessionRepository.existsById(SESSION_ID)).thenReturn(true);
        when(accessSessionRepository.findByUserId(FIREBASE_UID)).thenReturn(Arrays.asList(testAccessSession));
        when(accessSessionRepository.findByActiveTrue()).thenReturn(Arrays.asList(testAccessSession));
        doNothing().when(accessSessionRepository).deleteById(SESSION_ID);

        // Act
        long userCount = accessSessionService.getSessionCountForUser(FIREBASE_UID);
        long activeCount = accessSessionService.getActiveSessionCount();
        boolean deleted = accessSessionService.deleteAccessSession(SESSION_ID);

        // Assert
        assertThat(userCount).isEqualTo(1L);
        assertThat(activeCount).isEqualTo(1L);
        assertThat(deleted).isTrue();

        verify(accessSessionRepository).findByUserId(FIREBASE_UID);
        verify(accessSessionRepository).findByActiveTrue();
        verify(accessSessionRepository).existsById(SESSION_ID);
        verify(accessSessionRepository).deleteById(SESSION_ID);
    }
}
