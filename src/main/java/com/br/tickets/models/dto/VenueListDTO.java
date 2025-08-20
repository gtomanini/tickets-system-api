package com.br.tickets.models.dto;

import com.br.tickets.models.City;

public record VenueListDTO(
    Long id,
    String name,
    String description,
    String address,
    Double longitude,
    Double latitude,
    String plant_url,
    String photos,
    City city
){}

