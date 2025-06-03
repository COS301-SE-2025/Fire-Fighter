package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccessSessionRepository extends JpaRepository<AccessSession, Long> {
    List<AccessSession> findByUser_Id(Long userId);
    Optional<AccessSession> findByAccessRequest_Id(Long accessRequestId);
}
