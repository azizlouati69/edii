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
    private final TokenStore refreshTokenStore;
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Nom d'utilisateur déjà pris.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Adresse email déjà utilisée.");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Numéro de telephone déjà utilisé.");
        }


        user newUser = user.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role("ROLE_USER")
                .enabled(true)
                .build();

        userRepository.save(newUser);

        // Generate both tokens
        String accessToken = jwtService.generateAccessToken(newUser);
        String refreshToken = jwtService.generateRefreshToken(newUser);
        refreshTokenStore.put(newUser.getUsername(), refreshToken); // ✅ Store it

        // Return both in AuthResponse
        return new AuthResponse(accessToken, refreshToken);
    }

    public user loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
    public AuthResponse authenticate(AuthRequest request) {
        user user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Compte inexistant"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        // Generate both tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        refreshTokenStore.put(user.getUsername(), refreshToken); // ✅ Store it

        // Return both tokens
        return new AuthResponse(accessToken, refreshToken);
    }
}