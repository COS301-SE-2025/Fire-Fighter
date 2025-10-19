package com.apex.firefighter.repository.accessgroup;

import com.apex.firefighter.model.accessgroup.AccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for AccessGroup entity
 */
@Repository
public interface AccessGroupRepository extends JpaRepository<AccessGroup, Long> {

    /**
     * Find access group by group ID
     */
    Optional<AccessGroup> findByGroupId(String groupId);

    /**
     * Check if access group exists by group ID
     */
    boolean existsByGroupId(String groupId);

    /**
     * Delete by group ID
     */
    void deleteByGroupId(String groupId);
}
