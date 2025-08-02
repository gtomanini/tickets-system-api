package com.br.tickets.models.dto;

import java.time.LocalDateTime;

public record EventListDTO(
    Long id,
    String name,
    String status,
    String description,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean featured,
    Boolean closed
){}
