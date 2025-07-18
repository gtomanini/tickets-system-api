package com.br.tickets.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "minha-chave-secreta-muito-forte-com-no-minimo-32-bytes";

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusHours(4)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

//    public String extractEmail(String token) {
//        return Jwts.builder()
////                .setSigningKey(SECRET.getBytes())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }

//    public boolean isTokenValid(String token) {
//        try {
//            extractEmail(token); // já falha se inválido
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
}

