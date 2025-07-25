package com.br.tickets.models.dto;

import java.time.LocalDateTime;

public record CreateEventDTO(
        String name,
        String status,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean featured
) {}
