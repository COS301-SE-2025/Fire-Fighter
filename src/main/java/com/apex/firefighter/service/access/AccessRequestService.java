package com.apex.firefighter.service.access;

import com.apex.firefighter.model.AccessRequest;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.AccessRequestRepository;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * AccessRequestService handles access request management.
 * This service is responsible for:
 * - Creating access requests
 * - Approving/denying requests
 * - Managing request status
 * - Querying requests by various criteria
 */
@Service
@Transactional
public class AccessRequestService {

    private final AccessRequestRepository accessRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccessRequestService(AccessRequestRepository accessRequestRepository, UserRepository userRepository) {
        this.accessRequestRepository = accessRequestRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new access request
     */
    public AccessRequest createAccessRequest(String firebaseUid, String ticketId) {
        System.out.println("CREATE ACCESS REQUEST: Creating request for UID - " + firebaseUid + " ticket - " + ticketId);
        
        Optional<User> userOpt = userRepository.findByUserId(firebaseUid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            AccessRequest request = new AccessRequest(ticketId, user);
            AccessRequest savedRequest = accessRequestRepository.save(request);
            System.out.println("ACCESS REQUEST CREATED: " + savedRequest);
            return savedRequest;
        } else {
            System.out.println("CREATE FAILED: User not found for UID - " + firebaseUid);
            throw new RuntimeException("User not found with Firebase UID: " + firebaseUid);
        }
    }

    /**
     * Approve an access request
     */
    public AccessRequest approveAccessRequest(Long requestId, String approvedBy) {
        System.out.println("APPROVE REQUEST: Approving request ID - " + requestId);
        
        Optional<AccessRequest> requestOpt = accessRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            AccessRequest request = requestOpt.get();
            request.approve(approvedBy);
            AccessRequest updatedRequest = accessRequestRepository.save(request);
            System.out.println("REQUEST APPROVED: " + updatedRequest);
            return updatedRequest;
        } else {
            System.out.println("APPROVE FAILED: Request not found with ID - " + requestId);
            throw new RuntimeException("Access request not found with ID: " + requestId);
        }
    }

    /**
     * Deny an access request
     */
    public AccessRequest denyAccessRequest(Long requestId, String deniedBy) {
        System.out.println("DENY REQUEST: Denying request ID - " + requestId);
        
        Optional<AccessRequest> requestOpt = accessRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            AccessRequest request = requestOpt.get();
            request.deny(deniedBy);
            AccessRequest updatedRequest = accessRequestRepository.save(request);
            System.out.println("REQUEST DENIED: " + updatedRequest);
            return updatedRequest;
        } else {
            System.out.println("DENY FAILED: Request not found with ID - " + requestId);
            throw new RuntimeException("Access request not found with ID: " + requestId);
        }
    }

    /**
     * Revoke an access request
     */
    public AccessRequest revokeAccessRequest(Long requestId, String revokedBy) {
        System.out.println("REVOKE REQUEST: Revoking request ID - " + requestId);
        
        Optional<AccessRequest> requestOpt = accessRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            AccessRequest request = requestOpt.get();
            request.revoke(revokedBy);
            AccessRequest updatedRequest = accessRequestRepository.save(request);
            System.out.println("REQUEST REVOKED: " + updatedRequest);
            return updatedRequest;
        } else {
            System.out.println("REVOKE FAILED: Request not found with ID - " + requestId);
            throw new RuntimeException("Access request not found with ID: " + requestId);
        }
    }

    /**
     * QUERY OPERATIONS
     */

    /**
     * Get all access requests
     */
    public List<AccessRequest> getAllAccessRequests() {
        return accessRequestRepository.findAll();
    }

    /**
     * Get access request by ID
     */
    public Optional<AccessRequest> getAccessRequestById(Long id) {
        return accessRequestRepository.findById(id);
    }

    /**
     * Get access requests by user
     */
    public List<AccessRequest> getAccessRequestsByUser(String firebaseUid) {
        return accessRequestRepository.findByUserId(firebaseUid);
    }

    /**
     * Get access requests by status
     */
    public List<AccessRequest> getAccessRequestsByStatus(AccessRequest.RequestStatus status) {
        return accessRequestRepository.findByStatus(status);
    }

    /**
     * Get pending access requests
     */
    public List<AccessRequest> getPendingAccessRequests() {
        return accessRequestRepository.findByStatus(AccessRequest.RequestStatus.PENDING);
    }

    /**
     * Get approved access requests
     */
    public List<AccessRequest> getApprovedAccessRequests() {
        return accessRequestRepository.findByStatus(AccessRequest.RequestStatus.APPROVED);
    }

    /**
     * Delete access request
     */
    public boolean deleteAccessRequest(Long requestId) {
        if (accessRequestRepository.existsById(requestId)) {
            accessRequestRepository.deleteById(requestId);
            System.out.println("ACCESS REQUEST DELETED: ID - " + requestId);
            return true;
        }
        System.out.println("DELETE FAILED: Request not found with ID - " + requestId);
        return false;
    }
} 