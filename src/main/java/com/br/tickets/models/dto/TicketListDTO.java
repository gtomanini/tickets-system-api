package com.br.tickets.models.dto;

import java.util.List;
import java.util.UUID;

public record TicketListDTO(
        UUID id,
        String name,
        String obs,
        Integer totalCapacity,
        Long sectionId,
        String sectionName,
        List<TicketVariantListDTO> variants
) {}
