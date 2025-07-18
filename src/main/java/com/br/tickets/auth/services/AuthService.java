package com.br.tickets.auth.services;

import com.br.tickets.auth.requests.RegisterRequest;
import com.br.tickets.models.User;
import com.br.tickets.models.dto.AuthRequest;
import com.br.tickets.models.dto.JwtResponse;
import com.br.tickets.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    public JwtResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Credenciais inválidas");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String jwtToken = jwtService.generateToken(user);

        return new JwtResponse(jwtToken);
    }

    public String login(AuthRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        authenticationManager.authenticate(authToken);

        var user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return jwtService.generateToken(user);
    }
}

