package com.br.tickets.auth.services;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-hours:3}")
    private int expirationHours;

    private SecretKey key;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    public String generateToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

        return Jwts.builder()
                .claim("sub", userDetails.getUsername())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(Date.from(
                        LocalDateTime.now()
                                .plusHours(expirationHours)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return jwtParser.parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername());
    }
}
