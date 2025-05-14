package com.example.AuthService.controller;

import com.example.AuthService.DTO.AuthResponse;
import com.example.AuthService.DTO.RegisterRequest;
import com.example.AuthService.DTO.AuthRequest;
import com.example.AuthService.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
    @GetMapping("/loginn")
    public ResponseEntity<AuthResponse> login() {
        System.out.println("hello");
        return null;
    }
}