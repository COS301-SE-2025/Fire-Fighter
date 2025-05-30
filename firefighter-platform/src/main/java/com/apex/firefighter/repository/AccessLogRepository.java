package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    // You can add filtering methods later for e.g. time range or user
}
