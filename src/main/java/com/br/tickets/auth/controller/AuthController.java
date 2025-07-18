package com.br.tickets.auth.controller;

import com.br.tickets.auth.requests.RegisterRequest;
import com.br.tickets.auth.services.AuthService;
import com.br.tickets.auth.services.CustomUserDetailsService;
import com.br.tickets.auth.services.JwtService;
import com.br.tickets.models.dto.AuthRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok("Bearer " + token);
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }


}

