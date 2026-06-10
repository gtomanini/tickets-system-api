package com.br.tickets.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSectionDTO(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Capacity is required") @Min(value = 1, message = "Capacity must be at least 1") Integer capacity,
        @NotNull(message = "Numbered is required") Boolean numbered
) {
}
