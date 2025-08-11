package com.apex.firefighter.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.apex.firefighter.model.DolibarrUserGroupMembership;
import com.apex.firefighter.repository.DolibarrUserGroupMembershipRepository;

@DataJpaTest
@Import(DolibarrUserGroupService.class)
public class DolibarrUserGroupServiceTest {
    @Autowired
    private DolibarrUserGroupMembershipRepository membershipRepo;

    @Autowired
    private DolibarrUserGroupService service;

    @BeforeEach
    void setUp() {
        membershipRepo.deleteAll();
    }

    @Test
    void addUserToGroup_shouldInsertRecord() {
        Long userId = 1L;
        Long groupId = 10L;

        DolibarrUserGroupMembership membership = service.addUserToGroup(userId, groupId);

        assertThat(membership.getId()).isNotNull();
        assertThat(membershipRepo.existsByUserIdAndGroupId(userId, groupId)).isTrue();
    }

    @Test
    void addUserToGroup_shouldThrowWhenDuplicate() {
        Long userId = 1L;
        Long groupId = 10L;

        service.addUserToGroup(userId, groupId);

        assertThatThrownBy(() -> service.addUserToGroup(userId, groupId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already in group");
    }

    @Test
    void removeUserFromGroup_shouldDeleteRecord() {
        Long userId = 2L;
        Long groupId = 20L;
        service.addUserToGroup(userId, groupId);

        service.removeUserFromGroup(userId, groupId);

        assertThat(membershipRepo.existsByUserIdAndGroupId(userId, groupId)).isFalse();
    }
}
