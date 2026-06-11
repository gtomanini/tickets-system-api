package com.br.tickets.models.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EventListDTO(
        Long id,
        String name,
        String status,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        VenueListDTO venue,
        Boolean featured,
        Boolean closed,
        AgeRatingDTO ageRating,
        List<EventCategoryListDTO> categories,
        List<String> tags
) {}
