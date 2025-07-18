//package com.br.tickets.controllers;
//
//import com.br.tickets.auth.services.AuthService;
//import com.br.tickets.models.dto.AuthRequest;
//import com.br.tickets.models.dto.JwtResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService authService;
//
//    @PostMapping("/login")
//    public ResponseEntity<JwtResponse> login(@RequestBody AuthRequest request) {
//        return ResponseEntity.ok(authService.authenticate(request));
//    }
//}
