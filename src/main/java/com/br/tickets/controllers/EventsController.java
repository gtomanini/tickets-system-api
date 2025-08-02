package com.br.tickets.controllers;

import com.br.tickets.models.Event;
import com.br.tickets.models.dto.CreateEventDTO;
import com.br.tickets.models.dto.EventListDTO;
import com.br.tickets.models.dto.EventSearchCriteria;
import com.br.tickets.services.EventsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EventsController {

    @Autowired
    private EventsService eventsService;

    @GetMapping("/events")
    public ResponseEntity<Page<EventListDTO>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,desc") String sort) {
                
        EventSearchCriteria criteria = new EventSearchCriteria();
        criteria.setName(name);
        criteria.setStatus(status);

        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("DESC")) {
            direction = Sort.Direction.DESC;
        }

        Sort sorting = Sort.by(direction, property);
        Pageable pageable = PageRequest.of(page, size, sorting);
            return ResponseEntity.ok(this.eventsService.searchEvents(criteria, pageable));
    }

    @PostMapping("/events")
    public Event create(@RequestBody CreateEventDTO eventListDTO) {
        return eventsService.saveEvent(eventListDTO);
    }
}
