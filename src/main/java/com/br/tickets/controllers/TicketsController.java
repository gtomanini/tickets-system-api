package com.br.tickets.controllers;

import com.br.tickets.models.dto.CreateTicketDTO;
import com.br.tickets.models.dto.TicketListDTO;
import com.br.tickets.services.TicketsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tickets")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TicketsController {

    private final TicketsService ticketsService;

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketListDTO>> listByEvent(@RequestParam("eventId") Long eventId) {
        return ResponseEntity.ok(ticketsService.getTicketsByEventId(eventId));
    }

    @PostMapping("/events/{eventId}/tickets")
    public ResponseEntity<TicketListDTO> create(
            @PathVariable Long eventId,
            @RequestBody @Valid CreateTicketDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketsService.createTicket(eventId, dto));
    }
}
