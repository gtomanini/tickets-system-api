package com.br.tickets.models.dto;

import java.util.UUID;

public record TicketListDTO(
    UUID id,
    String name,
    Integer amount,
    String obs
){}
