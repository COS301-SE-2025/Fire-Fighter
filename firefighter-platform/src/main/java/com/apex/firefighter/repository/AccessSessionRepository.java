package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccessSessionRepository extends JpaRepository<AccessSession, Long> {
    List<AccessSession> findByUserId(Long userId);
    Optional<AccessSession> findByRequestId(Long requestId);
}
