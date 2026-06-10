package com.br.tickets.models.dto;

public record VenueListDTO(
        Long id,
        String name,
        String description,
        String address,
        Double longitude,
        Double latitude,
        String plantUrl,
        String photos,
        Long cityId,
        String cityName
) {}
