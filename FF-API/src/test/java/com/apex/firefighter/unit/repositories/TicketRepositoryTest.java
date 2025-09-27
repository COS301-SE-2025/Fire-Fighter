package com.apex.firefighter.unit.repositories;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void testFindByTicketId() {
        Ticket ticket = new Ticket();
        ticket.setTicketId("JIRA-456");
        ticket.setDescription("Test Ticket");
        ticket.setDuration(30);
        ticket.setUserId("user1");
        ticket.setEmergencyType("critical-system-failure");
        ticket.setEmergencyContact("12345");
        ticketRepository.save(ticket);

        Optional<Ticket> found = ticketRepository.findByTicketId("JIRA-456");
        assertThat(found).isPresent();
        assertThat(found.get().getTicketId()).isEqualTo("JIRA-456");
        assertThat(found.get().getDuration()).isEqualTo(30);
    }
}
