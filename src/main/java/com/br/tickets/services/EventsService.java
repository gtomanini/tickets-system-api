package com.br.tickets.services;

import com.br.tickets.models.dto.CreateEventDTO;
import com.br.tickets.models.dto.EventListDTO;
import com.br.tickets.models.dto.EventSearchCriteria;
import com.br.tickets.repositories.EventsRepository;
import com.br.tickets.models.Event;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class EventsService {

    // Autowired repository to interact with the database
    @Autowired
    private EventsRepository eventsRepository;

    // public EventsService(EventsRepository eventsRepository) {
    //     this.eventsRepository = eventsRepository;
    // }

    public List<Event> getAllEvents(){
        return eventsRepository.findAll();
    }

    public Page<EventListDTO> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addNamePredicate(cb, root, predicates, criteria);
            addStatusPredicate(cb, root, predicates, criteria);

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Event> page = eventsRepository.findAll(spec, pageable);

        return page.map(event -> new EventListDTO(
                    event.getId(),
                    event.getName(),
                    event.getStatus(),
                    event.getDescription(),
                    event.getStartDate(),
                    event.getEndDate(),
                    event.getFeatured(),
                    event.getClosed()
            )
        );
    }

    public Event saveEvent(CreateEventDTO dto) {
        Event event = Event.builder()
                .name(dto.name())
                .status(dto.status())
                .description(dto.description())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .featured(dto.featured())
                .build();

        return eventsRepository.save(event);
    }

    private void addNamePredicate(CriteriaBuilder cb, Root<Event> root, List<Predicate> predicates, EventSearchCriteria criteria) {
    if (criteria.getName() != null && !criteria.getName().isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
        }
    }

    private void addStatusPredicate(CriteriaBuilder cb, Root<Event> root, List<Predicate> predicates, EventSearchCriteria criteria) {
        if (criteria.getStatus() != null && !criteria.getStatus().isBlank()) {
            predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
        }
    }

}
