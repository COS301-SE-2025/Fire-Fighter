package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessRequest;
import com.apex.firefighter.model.AccessRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    
    // Find by status
    List<AccessRequest> findByStatus(RequestStatus status);
    
    // Find by user Firebase UID
    @Query("SELECT ar FROM AccessRequest ar WHERE ar.user.userId = :userId")
    List<AccessRequest> findByUserId(@Param("userId") String userId);
    
    // Find by ticket ID
    List<AccessRequest> findByTicketId(String ticketId);
    
    // Find pending requests for a specific user
    @Query("SELECT ar FROM AccessRequest ar WHERE ar.user.userId = :userId AND ar.status = :status")
    List<AccessRequest> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") RequestStatus status);
    
    // Find all pending requests
    List<AccessRequest> findByStatusOrderByRequestTimeAsc(RequestStatus status);
    
    // Find requests by approver
    List<AccessRequest> findByApprovedBy(String approvedByUserId);
}
