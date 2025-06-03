package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    List<AccessRequest> findByStatus(AccessRequest.RequestStatus status);
    List<AccessRequest> findByUser_Id(Long userId);
}
