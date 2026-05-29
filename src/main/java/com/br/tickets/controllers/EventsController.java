package com.br.tickets.controllers;

import com.br.tickets.models.Event;
import com.br.tickets.models.dto.CreateEventDTO;
import com.br.tickets.models.dto.EventListDTO;
import com.br.tickets.models.dto.EventSearchCriteria;
import com.br.tickets.services.EventsService;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/events")
    public Event create(@RequestBody CreateEventDTO eventListDTO) {
        return eventsService.saveEvent(eventListDTO);
    }
}
