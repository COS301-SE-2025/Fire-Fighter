package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    List<AccessRequest> findByStatus(String status);
    List<AccessRequest> findByUserId(Long userId);
}
