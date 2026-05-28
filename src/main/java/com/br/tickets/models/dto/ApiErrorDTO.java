package com.br.tickets.models.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiErrorDTO(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
    public static ApiErrorDTO of(HttpStatus status, String message) {
        return new ApiErrorDTO(status.value(), status.getReasonPhrase(), message, LocalDateTime.now());
    }
}
