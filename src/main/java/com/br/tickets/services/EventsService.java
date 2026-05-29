package com.br.tickets.services;

import com.br.tickets.models.dto.CreateEventDTO;
import com.br.tickets.models.dto.EventListDTO;
import com.br.tickets.models.dto.EventSearchCriteria;
import com.br.tickets.repositories.EventsRepository;
import com.br.tickets.models.Event;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class EventsService {

    private final EventsRepository eventsRepository;

    public Page<EventListDTO> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addNamePredicate(cb, root, predicates, criteria);
            addStatusPredicate(cb, root, predicates, criteria);

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return eventsRepository.findAll(spec, pageable).map(event -> new EventListDTO(
                event.getId(),
                event.getName(),
                event.getStatus(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getVenue(),
                event.getFeatured(),
                event.getClosed()
        ));
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
        if (criteria.name() != null && !criteria.name().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
        }
    }

    private void addStatusPredicate(CriteriaBuilder cb, Root<Event> root, List<Predicate> predicates, EventSearchCriteria criteria) {
        if (criteria.status() != null && !criteria.status().isBlank()) {
            predicates.add(cb.equal(root.get("status"), criteria.status()));
        }
    }
}
