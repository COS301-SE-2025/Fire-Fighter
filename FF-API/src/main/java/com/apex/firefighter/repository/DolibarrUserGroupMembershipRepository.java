package com.apex.firefighter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apex.firefighter.model.DolibarrUserGroupMembership;

@Repository
public interface DolibarrUserGroupMembershipRepository
        extends JpaRepository<DolibarrUserGroupMembership, Long> {

    boolean existsByUserIdAndGroupId(Long userId, Long groupId);

    void deleteByUserIdAndGroupId(Long userId, Long groupId);
}