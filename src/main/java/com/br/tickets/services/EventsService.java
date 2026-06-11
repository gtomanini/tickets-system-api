package com.br.tickets.services;

import com.br.tickets.models.Event;
import com.br.tickets.models.EventCategory;
import com.br.tickets.models.Venue;
import com.br.tickets.models.dto.*;
import com.br.tickets.repositories.EventCategoryRepository;
import com.br.tickets.repositories.EventsRepository;
import com.br.tickets.repositories.VenuesRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class EventsService {

    private final EventsRepository eventsRepository;
    private final VenuesRepository venuesRepository;
    private final EventCategoryRepository eventCategoryRepository;

    public Page<EventListDTO> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addNamePredicate(cb, root, predicates, criteria);
            addStatusPredicate(cb, root, predicates, criteria);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return eventsRepository.findAll(spec, pageable).map(this::toDTO);
    }

    public EventListDTO findById(Long id) {
        return toDTO(eventsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id)));
    }

    @Transactional
    public EventListDTO createEvent(CreateEventDTO dto) {
        Event event = Event.builder()
                .name(dto.name())
                .description(dto.description())
                .status(dto.status())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .featured(dto.featured() != null ? dto.featured() : false)
                .closed(false)
                .ageRating(dto.ageRating())
                .tags(dto.tags() != null ? dto.tags() : List.of())
                .venue(resolveVenue(dto.venueId()))
                .categories(resolveCategories(dto.categoryIds()))
                .build();
        return toDTO(eventsRepository.save(event));
    }

    @Transactional
    public EventListDTO updateEvent(Long id, CreateEventDTO dto) {
        Event event = eventsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        event.setName(dto.name());
        event.setDescription(dto.description());
        event.setStatus(dto.status());
        event.setStartDate(dto.startDate());
        event.setEndDate(dto.endDate());
        event.setFeatured(dto.featured() != null ? dto.featured() : false);
        event.setAgeRating(dto.ageRating());
        event.setTags(dto.tags() != null ? dto.tags() : List.of());
        event.setVenue(resolveVenue(dto.venueId()));
        event.setCategories(resolveCategories(dto.categoryIds()));

        return toDTO(eventsRepository.save(event));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Venue resolveVenue(Long venueId) {
        return venuesRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + venueId));
    }

    private List<EventCategory> resolveCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return List.of();
        return eventCategoryRepository.findAllById(categoryIds);
    }

    private EventListDTO toDTO(Event event) {
        return new EventListDTO(
                event.getId(),
                event.getName(),
                event.getStatus(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                toVenueDTO(event.getVenue()),
                event.getFeatured(),
                event.getClosed(),
                AgeRatingDTO.from(event.getAgeRating()),
                event.getCategories() != null
                        ? event.getCategories().stream()
                                .map(c -> new EventCategoryListDTO(c.getId(), c.getName()))
                                .toList()
                        : List.of(),
                event.getTags() != null ? event.getTags() : List.of()
        );
    }

    private VenueListDTO toVenueDTO(Venue venue) {
        if (venue == null) return null;
        Long cityId = venue.getCity() != null ? venue.getCity().getId() : null;
        String cityName = venue.getCity() != null ? venue.getCity().getName() : null;
        Long stateId = venue.getCity() != null ? venue.getCity().getState().getId() : null;
        List<SectionListDTO> sections = venue.getSections() != null
                ? venue.getSections().stream()
                        .map(s -> new SectionListDTO(s.getId(), s.getName(), s.getCapacity(), s.getNumbered()))
                        .toList()
                : List.of();
        return new VenueListDTO(
                venue.getId(), venue.getName(), venue.getDescription(), venue.getAddress(),
                venue.getLongitude(), venue.getLatitude(), venue.getPlant_url(), venue.getPhotos(),
                cityId, cityName, stateId, sections
        );
    }

    private void addNamePredicate(jakarta.persistence.criteria.CriteriaBuilder cb,
                                   jakarta.persistence.criteria.Root<Event> root,
                                   List<Predicate> predicates, EventSearchCriteria criteria) {
        if (criteria.name() != null && !criteria.name().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
        }
    }

    private void addStatusPredicate(jakarta.persistence.criteria.CriteriaBuilder cb,
                                     jakarta.persistence.criteria.Root<Event> root,
                                     List<Predicate> predicates, EventSearchCriteria criteria) {
        if (criteria.status() != null && !criteria.status().isBlank()) {
            predicates.add(cb.equal(root.get("status"), criteria.status()));
        }
    }
}
