package com.br.tickets.models.dto;

public record EventSearchCriteria(String name, String status) {

    // Convenience no-arg equivalent — used when no filter params are sent
    public EventSearchCriteria() {
        this(null, null);
    }
}
