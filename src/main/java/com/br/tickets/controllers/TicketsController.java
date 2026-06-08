package com.br.tickets.controllers;

import com.br.tickets.models.dto.TicketListDTO;
import com.br.tickets.services.TicketsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Tickets")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TicketsController {

    private final TicketsService ticketsService;

    @GetMapping("/tickets")
    public List<TicketListDTO> getTickets(@RequestParam("eventId") Long eventId) {
        return ticketsService.getTicketsByEventId(eventId);
    }
}
