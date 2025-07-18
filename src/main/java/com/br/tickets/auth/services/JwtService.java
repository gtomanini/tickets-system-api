package com.br.tickets.auth.services;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "minha-chave-secreta-muito-forte-com-no-minimo-32-bytes";

    private SecretKey key;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    public String generateToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .orElse("ROLE_USER");

        return Jwts.builder()
                .claim("sub", userDetails.getUsername())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(Date.from(LocalDateTime.now().plusHours(3).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {
        JwtParser parser = Jwts.parser().verifyWith(getKey()).build();

        return parser.parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername());
    }

    private SecretKey getKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
