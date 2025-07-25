package com.br.tickets.controllers;

import com.br.tickets.models.Event;
import com.br.tickets.models.dto.CreateEventDTO;
import com.br.tickets.models.dto.EventListDTO;
import com.br.tickets.models.dto.EventSearchCriteria;
import com.br.tickets.services.EventsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventsController {

    private final EventsService eventsService;

    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping
    public Page<EventListDTO> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,desc") String[] sort
    ) {
        EventSearchCriteria criteria = new EventSearchCriteria();
        criteria.setName(name);
        criteria.setStatus(status);

        Sort sorting = Sort.by(
                Sort.Order.by(sort[0]).with(sort.length > 1 && sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC)
        );

        Pageable pageable = PageRequest.of(page, size, sorting);

        return eventsService.searchEvents(criteria, pageable);
    }

    @PostMapping
    public Event create(@RequestBody CreateEventDTO eventListDTO) {
        return eventsService.saveEvent(eventListDTO);
    }
}
