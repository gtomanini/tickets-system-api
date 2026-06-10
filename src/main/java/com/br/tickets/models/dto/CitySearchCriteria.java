package com.br.tickets.models.dto;

public record CitySearchCriteria(Long stateId, String name) {

    public CitySearchCriteria() {
        this(null, null);
    }
}
