package com.br.tickets.auth.requests;

public record RegisterRequest(
        String name,
        String email,
        String password
) {}
