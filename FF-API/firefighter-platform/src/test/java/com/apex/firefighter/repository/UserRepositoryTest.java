package com.apex.firefighter.repository;

import com.apex.firefighter.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and retrieve user by username")
    void testFindByUsername() {
        User user = new User("user123", "testuser", "test@example.com", "IT");
        user.setRole("USER"); // Set simple role
        user = userRepository.save(user);

        Optional<User> result = userRepository.findByUsername("testuser");
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo("user123");
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should find users by role")
    void testFindByRole() {
        User user1 = new User("user123", "testuser1", "test1@example.com", "IT");
        user1.setRole("ADMIN");
        userRepository.save(user1);

        User user2 = new User("user456", "testuser2", "test2@example.com", "HR");
        user2.setRole("USER");
        userRepository.save(user2);

        User user3 = new User("user789", "testuser3", "test3@example.com", "IT");
        user3.setRole("ADMIN");
        userRepository.save(user3);

        List<User> adminUsers = userRepository.findByRole("ADMIN");
        assertThat(adminUsers).hasSize(2);
        assertThat(adminUsers).extracting(User::getRole).containsOnly("ADMIN");

        List<User> regularUsers = userRepository.findByRole("USER");
        assertThat(regularUsers).hasSize(1);
        assertThat(regularUsers.get(0).getUsername()).isEqualTo("testuser2");
    }

    @Test
    @DisplayName("Should find authorized users by role")
    void testFindByRoleAndIsAuthorizedTrue() {
        User user1 = new User("user123", "testuser1", "test1@example.com", "IT");
        user1.setRole("ADMIN");
        user1.setIsAuthorized(true);
        userRepository.save(user1);

        User user2 = new User("user456", "testuser2", "test2@example.com", "HR");
        user2.setRole("ADMIN");
        user2.setIsAuthorized(false);
        userRepository.save(user2);

        List<User> authorizedAdmins = userRepository.findByRoleAndIsAuthorizedTrue("ADMIN");
        assertThat(authorizedAdmins).hasSize(1);
        assertThat(authorizedAdmins.get(0).getUsername()).isEqualTo("testuser1");
        assertThat(authorizedAdmins.get(0).getIsAuthorized()).isTrue();
    }
}
