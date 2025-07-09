package com.example.AuthService.service;

import com.example.AuthService.DTO.*;
import com.example.AuthService.entity.User;
import com.example.AuthService.entity.VerificationToken;
import com.example.AuthService.repository.userrepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.AuthService.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final userrepository userRepository;
    @Qualifier("securityPasswordEncoder")
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;

    private final TokenStore refreshTokenStore;
    private final  VerificationTokenRepository verificationTokenRepository;

    @Transactional
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


        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role("ROLE_USER")
                .enabled(false)
                .build();

        userRepository.save(newUser);
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                token,
                newUser,
                LocalDateTime.now().plusHours(24)
        );
        verificationTokenRepository.save(verificationToken);

        // ✅ Send verification email
        sendVerificationEmail(newUser.getEmail(), token);

        // ⛔ Don't generate tokens yet, wait for verification
        return new AuthResponse("Veuillez vérifier votre email pour activer votre compte.", null);
    }
    public void sendVerificationEmail(String recipientEmail, String token) {
        String verificationUrl = "http://localhost:4200//verify-account?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Vérification de votre adresse e-mail");
        message.setText("Bonjour,\n\nMerci de vous être inscrit. Veuillez cliquer sur le lien suivant pour activer votre compte :\n"
                + verificationUrl + "\n\nCe lien expirera dans 24 heures.");

        mailSender.send(message);
    }
    public User updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            // Check if phone is already used by another user
            Optional<User> userWithPhone = userRepository.findByPhone(request.getPhone());
            if (userWithPhone.isPresent() && !userWithPhone.get().getUsername().equals(username)) {
                throw new IllegalArgumentException("Numéro de téléphone déjà utilisé.");
            }
            user.setPhone(request.getPhone());
        }
        if (request.getFirstname() != null && !request.getFirstname().isBlank()) {
            user.setFirstname(request.getFirstname());
        }
        if (request.getLastname() != null && !request.getLastname().isBlank()) {
            user.setLastname(request.getLastname());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (request.getSex() != null) {
            user.setSex(request.getSex());
        }

        if (request.getPicture() != null && request.getPicture().length > 0) {
            user.setPicture(request.getPicture());
        }
        return userRepository.save(user);
    }
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Compte inexistant"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Veuillez d'abord vérifier votre adresse email.");
        }

        // Generate both tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        refreshTokenStore.put(user.getUsername(), refreshToken ); // ✅ Store it

        // Return both tokens
        return new AuthResponse(accessToken, refreshToken);
    }
}