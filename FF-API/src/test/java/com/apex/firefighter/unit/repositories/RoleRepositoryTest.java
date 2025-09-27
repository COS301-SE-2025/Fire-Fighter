package com.apex.firefighter.unit.repositories;

import com.apex.firefighter.model.Role;
import com.apex.firefighter.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveAndFindByRoleName() {
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByName("ADMIN");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ADMIN");

        Role role2 = new Role();
        role2.setName("USER");
        roleRepository.save(role2);
        Optional<Role> found2 = roleRepository.findByName("USER");
        assertThat(found2).isPresent();
        assertThat(found2.get().getName()).isEqualTo("USER");

    }
}