package com.apex.firefighter.repository.accessgroup;

import com.apex.firefighter.model.accessgroup.UserAccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserAccessGroup entity
 */
@Repository
public interface UserAccessGroupRepository extends JpaRepository<UserAccessGroup, Long> {

    /**
     * Find all access groups for a user
     */
    List<UserAccessGroup> findByUserId(String userId);

    /**
     * Find specific user-group assignment
     */
    Optional<UserAccessGroup> findByUserIdAndGroupId(String userId, String groupId);

    /**
     * Find all users in a specific group
     */
    List<UserAccessGroup> findByGroupId(String groupId);

    /**
     * Check if user has access to a group
     */
    boolean existsByUserIdAndGroupId(String userId, String groupId);

    /**
     * Delete user from group
     */
    void deleteByUserIdAndGroupId(String userId, String groupId);

    /**
     * Delete all groups for a user
     */
    void deleteByUserId(String userId);

    /**
     * Delete all users from a group
     */
    void deleteByGroupId(String groupId);
}
