package com.br.tickets.controllers;

import com.br.tickets.models.dto.CreateEventDTO;
import com.br.tickets.models.dto.EventListDTO;
import com.br.tickets.models.dto.EventSearchCriteria;
import com.br.tickets.services.EventsService;
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

    @GetMapping("/events")
    public ResponseEntity<Page<EventListDTO>> list(
            @ModelAttribute EventSearchCriteria criteria,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(eventsService.searchEvents(criteria, pageable));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventListDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventsService.findById(id));
    }

    @PostMapping("/events")
    public ResponseEntity<EventListDTO> create(@RequestBody @Valid CreateEventDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventsService.createEvent(dto));
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<EventListDTO> update(@PathVariable Long id, @RequestBody @Valid CreateEventDTO dto) {
        return ResponseEntity.ok(eventsService.updateEvent(id, dto));
    }
}
