package com.apex.firefighter.repository;

import com.apex.firefighter.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveAndFindByRoleName() {
        Role role = new Role();
        role.setRoleName("ADMIN");
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByRoleName("ADMIN");
        assertThat(found).isPresent();
        assertThat(found.get().getRoleName()).isEqualTo("ADMIN");
    }
}