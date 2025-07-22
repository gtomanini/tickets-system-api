package com.br.tickets.auth.controller;

import com.br.tickets.auth.requests.RegisterRequest;
import com.br.tickets.auth.services.AuthService;
import com.br.tickets.auth.services.CustomUserDetailsService;
import com.br.tickets.auth.services.JwtService;
import com.br.tickets.models.dto.AuthRequest;
import com.br.tickets.models.dto.JwtResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
@Component
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
        JwtResponse response = new JwtResponse("Bearer " + token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }


}

