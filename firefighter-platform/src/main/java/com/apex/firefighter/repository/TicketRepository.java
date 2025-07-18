package com.apex.firefighter.repository;

import com.apex.firefighter.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketId(String ticketId);
    
    // Find all tickets with 'Active' status
    List<Ticket> findByStatusOrderByDateCreatedDesc(String status);
    
    // Find all active tickets
    @Query("SELECT t FROM Ticket t WHERE t.status = 'Active' ORDER BY t.dateCreated DESC")
    List<Ticket> findActiveTickets();
    
    // Find all tickets ordered by creation date descending (for history)
    List<Ticket> findAllByOrderByDateCreatedDesc();
    
    // Find tickets by status
    List<Ticket> findByStatus(String status);

    // Find tickets by user ID
    List<Ticket> findByUserId(String userId);

    // Find all active tickets that have a duration set (not null)
    @Query("SELECT t FROM Ticket t WHERE t.status = 'Active' AND t.duration IS NOT NULL")
    List<Ticket> findActiveTicketsWithDuration();

    // Find tickets within a date range (inclusive)
    @Query("SELECT t FROM Ticket t WHERE t.dateCreated >= :startDate AND t.dateCreated <= :endDate ORDER BY t.dateCreated DESC")
    List<Ticket> findByDateCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
