package com.apex.firefighter.integration.services;

import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import com.apex.firefighter.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User normalUser;
    private User adminUser;

    @BeforeEach
    @Transactional
    void setup() {
        userRepository.deleteAll();

        // Add test users directly to the repository
        adminUser = new User("adminUid", "Admin User", "admin1@example.com", "Fire Department");
        adminUser.setIsAdmin(true);
        userRepository.save(adminUser);

        normalUser = new User("uid1", "Normal User", "user1@example.com", "Medical Department");
        normalUser.setIsAdmin(false);
        userRepository.save(normalUser);
    }

    @Test
    void testVerifyOrCreateUser() {
        User created = userService.verifyOrCreateUser("uid2", "Another User", "user2@example.com", "Police");
        Assertions.assertNotNull(created);
        Assertions.assertEquals("user2@example.com", created.getEmail());
    }

    @Test
    void testGetUserByEmail() {
        Optional<User> found = userService.getUserByEmail("user1@example.com");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("uid1", found.get().getUserId());
    }

    @Test
    void testAuthorizeUserAndRevoke() {
        User authorized = userService.authorizeUser("uid1", "adminUid");
        Assertions.assertTrue(userService.isUserAuthorized("uid1"));

        User revoked = userService.revokeUserAuthorization("uid1", "adminUid");
        Assertions.assertFalse(userService.isUserAuthorized("uid1"));
    }

    @Test
    void testAssignRoleAndHasRole() {
        userService.assignRole("uid1", "RESPONDER", "adminUid");
        Assertions.assertTrue(userService.hasRole("uid1", "RESPONDER"));
    }

    @Test
    void testIsUserAuthorized() {
        userService.authorizeUser("uid1", "adminUid");
        Assertions.assertTrue(userService.isUserAuthorized("uid1"));
    }

    @Test
    void testGetAuthorizedUsers() {
        userService.authorizeUser("uid1", "adminUid");
        List<User> authorized = userService.getAuthorizedUsers();
        Assertions.assertTrue(authorized.stream().anyMatch(u -> u.getUserId().equals("uid1")));
    }

    @Test
    void testGetUsersByDepartment() {
        List<User> medicalUsers = userService.getUsersByDepartment("Medical Department");
        Assertions.assertTrue(medicalUsers.stream().anyMatch(u -> u.getUserId().equals("uid1")));
    }

    @Test
    void testGetUsersByRole() {
        userService.assignRole("uid1", "RESPONDER", "adminUid");
        List<User> responders = userService.getUsersByRole("RESPONDER");
        Assertions.assertTrue(responders.stream().anyMatch(u -> u.getUserId().equals("uid1")));
    }

    @Test
    void testGetAuthorizedUsersByRole() {
        userService.assignRole("uid1", "RESPONDER", "adminUid");
        userService.authorizeUser("uid1", "adminUid");
        List<User> authorizedResponders = userService.getAuthorizedUsersByRole("RESPONDER");
        Assertions.assertTrue(authorizedResponders.stream().anyMatch(u -> u.getUserId().equals("uid1")));
    }
}