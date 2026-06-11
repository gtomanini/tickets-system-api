package com.br.tickets.models.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryDTO(
        @NotBlank(message = "Name is required") String name,
        String description
) {}
