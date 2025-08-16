package com.apex.firefighter.integration.services;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.role.RoleService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RoleServiceIT {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User authorizedUser;

    @BeforeEach
    @Transactional
    void setup() {
        userRepository.deleteAll();
        user = new User("user1", "Normal User", "user1@example.com", "Medical Department");
        user.setIsAuthorized(false);
        userRepository.save(user);

        authorizedUser = new User("user2", "Authorized User", "user2@example.com", "Medical Department");
        authorizedUser.setIsAuthorized(true);
        userRepository.save(authorizedUser);
    }

    @Test
    void testAssignRole() {
        User updated = roleService.assignRole(user.getUserId(), "RESPONDER", "admin");
        Assertions.assertEquals("RESPONDER", updated.getRole());
    }

    @Test
    void testRemoveRole() {
        roleService.assignRole(user.getUserId(), "RESPONDER", "admin");
        User updated = roleService.removeRole(user.getUserId(), "RESPONDER");
        Assertions.assertNull(updated.getRole());
    }

    @Test
    void testGetUsersByRole() {
        roleService.assignRole(user.getUserId(), "RESPONDER", "admin");
        List<User> responders = roleService.getUsersByRole("RESPONDER");
        Assertions.assertTrue(responders.stream().anyMatch(u -> u.getUserId().equals(user.getUserId())));
    }

    @Test
    void testGetAuthorizedUsersByRole() {
        roleService.assignRole(authorizedUser.getUserId(), "RESPONDER", "admin");
        List<User> authorizedResponders = roleService.getAuthorizedUsersByRole("RESPONDER");
        Assertions.assertTrue(authorizedResponders.stream().anyMatch(u -> u.getUserId().equals(authorizedUser.getUserId())));
    }
}