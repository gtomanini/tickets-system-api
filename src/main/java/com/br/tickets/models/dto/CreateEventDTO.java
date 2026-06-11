package com.br.tickets.models.dto;

import com.br.tickets.enums.AgeRating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateEventDTO(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Venue is required") Long venueId,
        @NotNull(message = "Start date is required") LocalDateTime startDate,
        @NotNull(message = "End date is required") LocalDateTime endDate,
        @NotBlank(message = "Status is required") String status,
        String description,
        Boolean featured,
        AgeRating ageRating,
        List<Long> categoryIds,
        List<String> tags
) {}
