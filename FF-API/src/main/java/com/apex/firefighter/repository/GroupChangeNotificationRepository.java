package com.apex.firefighter.repository;

import com.apex.firefighter.model.GroupChangeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChangeNotificationRepository extends JpaRepository<GroupChangeNotification, Long> {
    
    /**
     * Find all unread notifications for a specific user
     */
    List<GroupChangeNotification> findByUsernameAndIsReadFalseOrderByCreatedAtDesc(String username);
    
    /**
     * Find all notifications for a specific user
     */
    List<GroupChangeNotification> findByUsernameOrderByCreatedAtDesc(String username);
    
    /**
     * Find notification by id and username (for security)
     */
    GroupChangeNotification findByIdAndUsername(Long id, String username);
    
    /**
     * Count unread notifications for a specific user
     */
    long countByUsernameAndIsReadFalse(String username);
}