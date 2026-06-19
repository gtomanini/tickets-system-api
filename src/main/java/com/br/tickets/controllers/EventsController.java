package com.br.tickets.controllers;

import com.br.tickets.models.dto.CreateEventDTO;
import com.br.tickets.models.dto.EventListDTO;
import com.br.tickets.models.dto.EventSearchCriteria;
import com.br.tickets.services.EventsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Events")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class EventsController {

    private final EventsService eventsService;

    @Operation(summary = "List events", description = "Returns a paginated list of events with optional filters by name and status")
    @ApiResponse(responseCode = "200", description = "List of events")
    @GetMapping("/events")
    public ResponseEntity<Page<EventListDTO>> list(
            @ModelAttribute EventSearchCriteria criteria,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(eventsService.searchEvents(criteria, pageable));
    }

    @Operation(summary = "Get event by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/events/{id}")
    public ResponseEntity<EventListDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventsService.findById(id));
    }

    @Operation(summary = "Create event", description = "Creates a new event linked to a venue with optional categories and tags")
    @ApiResponse(responseCode = "201", description = "Event created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventListDTO.class)))
    @PostMapping("/events")
    public ResponseEntity<EventListDTO> create(@RequestBody @Valid CreateEventDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventsService.createEvent(dto));
    }

    @Operation(summary = "Update event", description = "Updates all fields of an existing event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PutMapping("/events/{id}")
    public ResponseEntity<EventListDTO> update(@PathVariable Long id, @RequestBody @Valid CreateEventDTO dto) {
        return ResponseEntity.ok(eventsService.updateEvent(id, dto));
    }

    @Operation(summary = "Delete event", description = "Soft-deletes an event and all associated orders/tickets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventsService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
