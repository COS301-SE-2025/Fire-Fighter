package com.apex.firefighter.repository;

import com.apex.firefighter.model.AnomalyNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnomalyNotificationRepository extends JpaRepository<AnomalyNotification, Long> {
    
    /**
     * Find all unread notifications for a specific user
     */
    List<AnomalyNotification> findByUsernameAndIsReadFalseOrderByCreatedAtDesc(String username);
    
    /**
     * Find all notifications for a specific user
     */
    List<AnomalyNotification> findByUsernameOrderByCreatedAtDesc(String username);
    
    /**
     * Find notification by id and username (for security)
     */
    AnomalyNotification findByIdAndUsername(Long id, String username);
    
    /**
     * Count unread notifications for a specific user
     */
    long countByUsernameAndIsReadFalse(String username);
}