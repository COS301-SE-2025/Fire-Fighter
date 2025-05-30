package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessRequest;
import com.apex.firefighter.model.AccessSession;
import com.apex.firefighter.model.User;
import com.apex.firefighter.model.AccessRequest.RequestStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccessSessionRepositoryTest {

    @Autowired
    private AccessSessionRepository sessionRepository;

    @Autowired
    private AccessRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByRequestId() {
        User user = new User();
        user.setUsername("bob");
        user.setPassword("pass");
        user = userRepository.save(user);

        AccessRequest request = new AccessRequest();
        request.setStatus(RequestStatus.APPROVED);
        request.setTicketId("TCK-002");
        request.setUser(user);
        request.setRequestTime(LocalDateTime.now());
        request = requestRepository.save(request);

        AccessSession session = new AccessSession();
        session.setUser(user);
        session.setAccessRequest(request);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(LocalDateTime.now().plusHours(1));
        session.setActive(true);
        sessionRepository.save(session);

        Optional<AccessSession> found = sessionRepository.findByRequestId(request.getId());
        assertThat(found).isPresent();
        assertThat(found.get().isActive()).isTrue();
    }
}