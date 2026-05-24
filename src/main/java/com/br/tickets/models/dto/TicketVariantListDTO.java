package com.br.tickets.models.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketVariantListDTO(
    UUID id,
    String name,
    BigDecimal amount,
    Integer quantity,
    String obs,
    LocalDateTime saleEndsAt,
    Boolean requiresDocument
) {}
