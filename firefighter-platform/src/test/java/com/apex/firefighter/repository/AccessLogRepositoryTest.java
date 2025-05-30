package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessLog;
import com.apex.firefighter.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccessLogRepositoryTest {

    @Autowired
    private AccessLogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveLog() {
        User user = new User();
        user.setUsername("logger");
        user.setPassword("pw");
        user = userRepository.save(user);

        AccessLog log = new AccessLog();
        log.setUser(user);
        log.setAction("GRANTED_ACCESS");
        log.setTicketId("T-99");
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);

        assertThat(logRepository.findAll()).hasSize(1);
    }
}