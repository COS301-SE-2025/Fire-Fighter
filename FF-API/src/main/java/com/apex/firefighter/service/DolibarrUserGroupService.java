package com.apex.firefighter.service;

import org.springframework.stereotype.Service;

import com.apex.firefighter.model.DolibarrUserGroupMembership;
import com.apex.firefighter.repository.DolibarrUserGroupMembershipRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DolibarrUserGroupService {

    private final DolibarrUserGroupMembershipRepository membershipRepo;

    public DolibarrUserGroupService(DolibarrUserGroupMembershipRepository membershipRepo) {
        this.membershipRepo = membershipRepo;
    }

    public DolibarrUserGroupMembership addUserToGroup(Long userId, Long groupId) {
        if (membershipRepo.existsByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalStateException("User already in group");
        }
        DolibarrUserGroupMembership membership = new DolibarrUserGroupMembership(userId, groupId);
        return membershipRepo.save(membership);
    }

    public void removeUserFromGroup(Long userId, Long groupId) {
        membershipRepo.deleteByUserIdAndGroupId(userId, groupId);
    }
}