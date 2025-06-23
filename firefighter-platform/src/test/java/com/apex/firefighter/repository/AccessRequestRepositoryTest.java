package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessRequest;
import com.apex.firefighter.model.User;
import com.apex.firefighter.model.AccessRequest.RequestStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccessRequestRepositoryTest {

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByStatus() {
        User user = new User("alice123", "alice", "alice@example.com", "Operations");
        user = userRepository.save(user);

        AccessRequest request = new AccessRequest();
        request.setTicketId("TCK-001");
        request.setRequestTime(ZonedDateTime.now());
        request.setStatus(RequestStatus.PENDING);
        request.setUser(user);
        accessRequestRepository.save(request);

        List<AccessRequest> results = accessRequestRepository.findByStatus(RequestStatus.PENDING);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getTicketId()).isEqualTo("TCK-001");

        AccessRequest request2 = new AccessRequest();
        request2.setTicketId("TCK-002");
        request2.setStatus(RequestStatus.APPROVED);
        request2.setRequestTime(ZonedDateTime.now());
        request2.setUser(user);
        accessRequestRepository.save(request2);
        List<AccessRequest> approvedResults = accessRequestRepository.findByStatus(RequestStatus.APPROVED);
        assertThat(approvedResults).isNotEmpty();
        assertThat(approvedResults.get(0).getTicketId()).isEqualTo("TCK-002");
    }
}