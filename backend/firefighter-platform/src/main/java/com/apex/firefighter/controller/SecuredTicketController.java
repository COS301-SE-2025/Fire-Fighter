package com.apex.firefighter.controller;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/endpoints/tickets")
public class SecuredTicketController {

    private final TicketService ticketService;

    @Autowired
    public SecuredTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    
} 