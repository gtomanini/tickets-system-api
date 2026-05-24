package com.br.tickets.models.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTicketVariantDTO(
    @NotBlank String name,
    @NotNull @DecimalMin("0.00") BigDecimal amount,
    @NotNull @Min(1) Integer quantity,
    String obs,
    LocalDateTime saleEndsAt,
    Boolean requiresDocument
) {}
