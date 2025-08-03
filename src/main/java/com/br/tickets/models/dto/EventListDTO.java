package com.br.tickets.models.dto;

import java.time.LocalDateTime;

import com.br.tickets.models.Venue;

public record EventListDTO(
    Long id,
    String name,
    String status,
    String description,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Venue venue,
    Boolean featured,
    Boolean closed
){}
