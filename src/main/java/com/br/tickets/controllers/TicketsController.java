package com.br.tickets.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.br.tickets.models.dto.TicketListDTO;
import com.br.tickets.services.TicketsService;


@RestController
@RequestMapping("/api")
public class TicketsController {

    private final TicketsService ticketsService;

    public TicketsController(TicketsService ticketsService) {
        this.ticketsService = ticketsService;
    }
    
    @GetMapping("/tickets")
    public List<TicketListDTO> getTickets(@RequestParam("eventId") Long eventId) {
        return ticketsService.getTicketsByEventId(eventId);
    }

}
