package com.br.tickets.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateVenueDTO(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "City is required") Long cityId,
        String description,
        String address,
        Double longitude,
        Double latitude,
        String plantUrl,
        String photos
) {}
