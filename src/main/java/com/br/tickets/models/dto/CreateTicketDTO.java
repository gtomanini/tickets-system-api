package com.br.tickets.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketDTO(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Section is required") Long sectionId,
        @NotNull(message = "Total capacity is required") @Min(value = 1, message = "Total capacity must be at least 1") Integer totalCapacity,
        String obs
) {}
