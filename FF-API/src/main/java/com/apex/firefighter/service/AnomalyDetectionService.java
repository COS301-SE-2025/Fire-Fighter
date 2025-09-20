package com.apex.firefighter.service;

import com.apex.firefighter.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnomalyDetectionService {

    private final TicketRepository ticketRepository;

    //configuration for frequent request detection
    private static final int MAX_REQUESTS_PER_HOUR = 5;
    private static final int MAX_REQUESTS_PER_DAY = 20;

    public AnomalyDetectionService(TicketRepository ticketRepository){

        this.ticketRepository = ticketRepository;

    }
    
}