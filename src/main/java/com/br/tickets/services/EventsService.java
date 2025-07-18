package com.br.tickets.services;

import com.br.tickets.models.dto.EventListDTO;
import com.br.tickets.models.dto.EventSearchCriteria;
import com.br.tickets.repositories.EventsRepository;
import com.br.tickets.models.Event;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class EventsService {

    private final EventsRepository eventsRepository;

    public EventsService(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    public List<Event> getAllEvents(){
        return eventsRepository.findAll();
    }

    public Page<EventListDTO> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
            }

            if (criteria.getStatus() != null && !criteria.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Event> page = eventsRepository.findAll(spec, pageable);

        return page.map(event -> {
            EventListDTO dto = new EventListDTO();
            dto.setId(event.getId());
            dto.setName(event.getName());
//            dto.setDate(event.getDate());
//            dto.setStatus(event.getStatus());
            return dto;
        });
    }

}
