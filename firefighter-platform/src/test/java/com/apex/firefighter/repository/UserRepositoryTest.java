package com.apex.firefighter.repository;

import com.apex.firefighter.model.Role;
import com.apex.firefighter.model.User;
import com.apex.firefighter.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Should save and retrieve user by username")
    void testFindByUsername() {
        Role role = new Role();
        role.setName("USER");
        role = roleRepository.save(role);

        User user = new User("user123", "testuser", "test@example.com", "IT");
        user = userRepository.save(user);
        
        // Create UserRole relationship
        UserRole userRole = new UserRole(user, role, "admin");
        user.addUserRole(userRole);
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsername("testuser");
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");

        User user2 = new User("testuser2", "test2@example.com", "password456", Set.of(role));
        userRepository.save(user2);
        Optional<User> result2 = userRepository.findByUsername("testuser2");
        assertThat(result2).isPresent();
        assertThat(result2.get().getUsername()).isEqualTo("testuser2");
        assertThat(result2.get().getRoles()).contains(role);
    }
}
