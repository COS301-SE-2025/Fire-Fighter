package com.apex.firefighter.service.access;

import com.apex.firefighter.model.AccessSession;
import com.apex.firefighter.model.AccessRequest;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.AccessSessionRepository;
import com.apex.firefighter.repository.AccessRequestRepository;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AccessSessionService handles access session management.
 * This service is responsible for:
 * - Creating and managing access sessions
 * - Tracking active sessions
 * - Session timeout management
 * - Querying sessions by various criteria
 */
@Service
@Transactional
public class AccessSessionService {

    private final AccessSessionRepository accessSessionRepository;
    private final AccessRequestRepository accessRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccessSessionService(AccessSessionRepository accessSessionRepository, 
                               AccessRequestRepository accessRequestRepository,
                               UserRepository userRepository) {
        this.accessSessionRepository = accessSessionRepository;
        this.accessRequestRepository = accessRequestRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new access session from an approved access request
     */
    public AccessSession createAccessSession(Long accessRequestId) {
        System.out.println("🔵 CREATE ACCESS SESSION: Creating session for request ID - " + accessRequestId);
        
        Optional<AccessRequest> requestOpt = accessRequestRepository.findById(accessRequestId);
        if (requestOpt.isPresent()) {
            AccessRequest request = requestOpt.get();
            User user = request.getUser();
            
            AccessSession session = new AccessSession(user, request, LocalDateTime.now(), null, true);
            AccessSession savedSession = accessSessionRepository.save(session);
            System.out.println("✅ ACCESS SESSION CREATED: " + savedSession);
            return savedSession;
        } else {
            System.out.println("❌ CREATE FAILED: Access request not found with ID - " + accessRequestId);
            throw new RuntimeException("Access request not found with ID: " + accessRequestId);
        }
    }

    /**
     * End an access session
     */
    public AccessSession endAccessSession(Long sessionId) {
        System.out.println("🔵 END SESSION: Ending session ID - " + sessionId);
        
        Optional<AccessSession> sessionOpt = accessSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            AccessSession session = sessionOpt.get();
            session.setEndTime(LocalDateTime.now());
            session.setActive(false);
            AccessSession updatedSession = accessSessionRepository.save(session);
            System.out.println("✅ SESSION ENDED: " + updatedSession);
            return updatedSession;
        } else {
            System.out.println("❌ END FAILED: Session not found with ID - " + sessionId);
            throw new RuntimeException("Access session not found with ID: " + sessionId);
        }
    }

    /**
     * Get active session for user
     */
    public Optional<AccessSession> getActiveSessionForUser(String firebaseUid) {
        List<AccessSession> activeSessions = accessSessionRepository.findActiveByUserId(firebaseUid);
        return activeSessions.isEmpty() ? Optional.empty() : Optional.of(activeSessions.get(0));
    }

    /**
     * Check if user has active session
     */
    public boolean hasActiveSession(String firebaseUid) {
        return !accessSessionRepository.findActiveByUserId(firebaseUid).isEmpty();
    }

    /**
     * QUERY OPERATIONS
     */

    /**
     * Get all access sessions
     */
    public List<AccessSession> getAllAccessSessions() {
        return accessSessionRepository.findAll();
    }

    /**
     * Get access session by ID
     */
    public Optional<AccessSession> getAccessSessionById(Long id) {
        return accessSessionRepository.findById(id);
    }

    /**
     * Get access sessions by user
     */
    public List<AccessSession> getAccessSessionsByUser(String firebaseUid) {
        return accessSessionRepository.findByUserId(firebaseUid);
    }

    /**
     * Get active access sessions
     */
    public List<AccessSession> getActiveAccessSessions() {
        return accessSessionRepository.findByActiveTrue();
    }

    /**
     * Get access sessions by ticket
     */
    public List<AccessSession> getAccessSessionsByTicket(String ticketId) {
        return accessSessionRepository.findByTicketId(ticketId);
    }

    /**
     * Get session by token
     */
    public Optional<AccessSession> getSessionByToken(String sessionToken) {
        return accessSessionRepository.findBySessionToken(sessionToken);
    }

    /**
     * End all active sessions for user (useful for logout)
     */
    public void endAllActiveSessionsForUser(String firebaseUid) {
        List<AccessSession> activeSessions = accessSessionRepository.findActiveByUserId(firebaseUid);
        for (AccessSession session : activeSessions) {
            session.setEndTime(LocalDateTime.now());
            session.setActive(false);
            accessSessionRepository.save(session);
        }
        System.out.println("✅ ALL ACTIVE SESSIONS ENDED for UID: " + firebaseUid);
    }

    /**
     * Get session count for user
     */
    public long getSessionCountForUser(String firebaseUid) {
        return accessSessionRepository.findByUserId(firebaseUid).size();
    }

    /**
     * Get active session count
     */
    public long getActiveSessionCount() {
        return accessSessionRepository.findByActiveTrue().size();
    }

    /**
     * Delete access session (admin only)
     */
    public boolean deleteAccessSession(Long sessionId) {
        if (accessSessionRepository.existsById(sessionId)) {
            accessSessionRepository.deleteById(sessionId);
            System.out.println("✅ ACCESS SESSION DELETED: ID - " + sessionId);
            return true;
        }
        System.out.println("❌ DELETE FAILED: Session not found with ID - " + sessionId);
        return false;
    }
} 