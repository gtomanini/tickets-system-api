package com.br.tickets.controllers;

import com.br.tickets.models.dto.CreateTicketDTO;
import com.br.tickets.models.dto.TicketListDTO;
import com.br.tickets.services.TicketsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Tickets")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TicketsController {

    private final TicketsService ticketsService;

    @Operation(summary = "List tickets by event", description = "Returns all ticket types for a given event with their variants")
    @ApiResponse(responseCode = "200", description = "List of tickets")
    @GetMapping("/tickets")
    public ResponseEntity<List<TicketListDTO>> listByEvent(@RequestParam("eventId") Long eventId) {
        return ResponseEntity.ok(ticketsService.getTicketsByEventId(eventId));
    }

    @Operation(summary = "Create ticket", description = "Creates a new ticket type for an event in a specific section")
    @ApiResponse(responseCode = "201", description = "Ticket created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketListDTO.class)))
    @PostMapping("/events/{eventId}/tickets")
    public ResponseEntity<TicketListDTO> create(
            @PathVariable Long eventId,
            @RequestBody @Valid CreateTicketDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketsService.createTicket(eventId, dto));
    }

    @Operation(summary = "Update ticket", description = "Updates ticket name, capacity, section, and notes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket updated"),
            @ApiResponse(responseCode = "404", description = "Event or ticket not found")
    })
    @PutMapping("/events/{eventId}/tickets/{ticketId}")
    public ResponseEntity<TicketListDTO> update(
            @PathVariable Long eventId,
            @PathVariable UUID ticketId,
            @RequestBody @Valid CreateTicketDTO dto) {
        return ResponseEntity.ok(ticketsService.updateTicket(eventId, ticketId, dto));
    }

    @Operation(summary = "Delete ticket", description = "Soft-deletes a ticket and all its variants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ticket deleted"),
            @ApiResponse(responseCode = "404", description = "Event or ticket not found")
    })
    @DeleteMapping("/events/{eventId}/tickets/{ticketId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long eventId,
            @PathVariable UUID ticketId) {
        ticketsService.deleteTicket(eventId, ticketId);
        return ResponseEntity.noContent().build();
    }
}
