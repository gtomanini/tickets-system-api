package com.br.tickets.models.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateVenueDTO(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "City is required") Long cityId,
        @NotNull(message = "At least one section is required")
        @Size(min = 1, message = "At least one section is required")
        List<@Valid CreateSectionDTO> sections,
        String description,
        String address,
        Double longitude,
        Double latitude,
        String plantUrl,
        String photos
) {}
