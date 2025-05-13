package com.example.AuthService.service;

import com.example.AuthService.DTO.*;
import com.example.AuthService.entity.user;
import com.example.AuthService.repository.userrepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final userrepository userRepository;
    @Qualifier("securityPasswordEncoder")
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Nom d'utilisateur déjà pris.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Adresse email déjà utilisée.");
        }

        user newUser = user.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role("ROLE_USER")
                .enabled(true)
                .build();

        userRepository.save(newUser);

        String token = jwtService.generateToken(newUser);
        return new AuthResponse(token);
    }


    public AuthResponse authenticate(AuthRequest request) {
        user user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Compte inexistant"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}