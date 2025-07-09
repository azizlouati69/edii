package com.example.AuthService.controller;
import com.example.AuthService.DTO.*;
import com.example.AuthService.entity.PasswordResetToken;
import com.example.AuthService.entity.User;
import com.example.AuthService.entity.VerificationToken;
import com.example.AuthService.repository.PasswordResetTokenRepository;
import com.example.AuthService.repository.RefreshTokenRepository;
import com.example.AuthService.repository.VerificationTokenRepository;
import com.example.AuthService.service.AuthService;
import com.example.AuthService.service.EmailService;
import com.example.AuthService.service.JwtService;
import com.example.AuthService.repository.userrepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/auth")

public class AuthController {
    private final JwtService jwtService;
    private final userrepository userRepository;
    private final  PasswordResetTokenRepository PasswordResetTokenRepository;
    private final TokenStore refreshTokenStore;
    private final AuthService authService;
    private final com.example.AuthService.repository.RefreshTokenRepository RefreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenRepository VerificationTokenRepository;


    public AuthController(AuthService authService,VerificationTokenRepository VerificationTokenRepository  ,JwtService jwtservice, userrepository userRepository, com.example.AuthService.repository.PasswordResetTokenRepository passwordResetTokenRepository, RefreshTokenRepository RefreshTokenRepository, TokenStore refreshTokenStore, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.authService = authService;
        this.jwtService= jwtservice;
        this.userRepository = userRepository;
        PasswordResetTokenRepository = passwordResetTokenRepository;
        this.refreshTokenStore = refreshTokenStore;
        this.RefreshTokenRepository= RefreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.VerificationTokenRepository= VerificationTokenRepository;
    }
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal principal) {

        authService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) LocalDate birthday,
            @RequestPart(required = false) MultipartFile picture) throws IOException {

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setPhone(phone);
        request.setFirstname(firstname);
        request.setLastname(lastname);
        request.setBio(bio);
        request.setSex(sex);
        request.setBirthday(birthday);
        if (picture != null && !picture.isEmpty()) {
            request.setPicture(picture.getBytes());
        }

        User updatedUser = authService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/profile/picture")
    public ResponseEntity<byte[]> getProfilePicture(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        byte[] picture = user.getPicture();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Or IMAGE_PNG
                .body(picture);
    }
    @GetMapping("/profile/bio")
    public ResponseEntity<String> getBio(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String bio = user.getBio();
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(bio);
    }
    @GetMapping("/profile/firstname")
    public ResponseEntity<String> getFirstname(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String firstname = user.getFirstname();
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(firstname);
    }
    @GetMapping("/profile/lastname")
    public ResponseEntity<String> getLastname(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String lastname = user.getLastname();
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(lastname);
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        Logger logger = LoggerFactory.getLogger(AuthController.class);
        String token = request.getToken();

        logger.info("Password reset attempt received at {} with token: {}", LocalDateTime.now(), token.substring(0, Math.min(8, token.length())) + "...");

        Optional<PasswordResetToken> tokenOptional = PasswordResetTokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            logger.warn("Password reset failed - token not found: {}", token);
            return ResponseEntity.badRequest().body(Map.of("message", "Le lien de réinitialisation est invalide ou a expiré."));
        }

        PasswordResetToken resetToken = tokenOptional.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            logger.warn("Password reset failed - token expired for user: {}", resetToken.getUser().getEmail());
            return ResponseEntity.badRequest().body(Map.of("message", "Le lien de réinitialisation a expiré."));
        }

        try {
            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            PasswordResetTokenRepository.delete(resetToken); // Delete token after use
            logger.info("Password successfully reset for user: {}", user.getEmail());

            return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès."));

        } catch (Exception e) {
            logger.error("Erreur lors de la réinitialisation du mot de passe: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "Une erreur interne est survenue lors de la réinitialisation du mot de passe."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.authenticate(request);

        // Create secure HttpOnly cookie for the refresh token
        Cookie refreshCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // Set to true in production (HTTPS required)
        refreshCookie.setPath("/"); // Cookie valid for entire domain
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

        response.addCookie(refreshCookie);

        // Return only access token to frontend
        return ResponseEntity.ok(Map.of("accessToken", authResponse.getAccessToken()));
    }
    @GetMapping("/role")
    public ResponseEntity<Map<String, String>> getRole(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String role = jwtService.extractRole(token);
        Map<String, String> response = new HashMap<>();
        response.put("role", role);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/userid")
    public ResponseEntity<Map<String, Long>> getUserid(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
         Long userid = jwtService.extractUserId(token);
        Map<String, Long> response = new HashMap<>();
        response.put("userid", userid);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/profile/picture")
    public ResponseEntity<Map<String, String>> deleteProfilePicture(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPicture(null);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile picture deleted successfully"));
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. Extract refresh token from cookie
        Cookie[] cookies = request.getCookies();
        String oldRefreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    oldRefreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (oldRefreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
        }

        try {
            // 2. Validate old refresh token and generate new tokens
            String username = jwtService.extractUsername(oldRefreshToken);
            if (!jwtService.validateToken(oldRefreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }

            User userDetails = authService.loadUserByUsername(username);

            // Generate new tokens
            String newAccessToken = jwtService.generateAccessToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            // 3. Update refresh token cookie (httpOnly, secure, path, maxAge, etc.)
            Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // set to true if using HTTPS
            refreshTokenCookie.setPath("/"); // adjust path if needed
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // e.g. 7 days
            response.addCookie(refreshTokenCookie);

            // 4. Return access token and optionally the refresh token in response body
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);
            // optional, since refresh token is in cookie
refreshTokenStore.put(username, newRefreshToken);
            return ResponseEntity.ok(tokens);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }
    }
    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyAccount(@RequestParam("token") String token) {
        Map<String, String> response = new HashMap<>();

        VerificationToken verificationToken = VerificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            response.put("message", "lien invalide.");
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            response.put("message", "Le lien a expiré.");
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        VerificationTokenRepository.delete(verificationToken);

        response.put("message", "Compte activé avec succès. Vous pouvez maintenant vous connecter.");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not found");
        }

        User managedUser = userRepository.findById(userOptional.get().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        // Find existing token for user
        Optional<PasswordResetToken> existingTokenOpt = PasswordResetTokenRepository.findByUser(managedUser);

        PasswordResetToken resetToken;
        if (existingTokenOpt.isPresent()) {
            // Update existing token
            resetToken = existingTokenOpt.get();
            resetToken.setToken(token);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        } else {
            // Create new token entity
            resetToken = new PasswordResetToken();
            resetToken.setUser(managedUser);
            resetToken.setToken(token);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        }

        PasswordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:4200//reset-password?token=" + token;
        String subject = "Réinitialisation du mot de passe";
        String text = "Bonjour,\n\nCliquez sur le lien suivant pour réinitialiser votre mot de passe :\n" + resetLink + "\n\nCe lien expirera dans 30 minutes.";

        try {
            emailService.sendSimpleMessage(email, subject, text);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'envoi de l'email");
        }

        return ResponseEntity.ok("Lien de réinitialisation envoyé par email.");
    }



    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint() {
        return ResponseEntity.ok("You are authenticated!");
    }

    @Transactional
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Extract refreshToken from cookie
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // Clear cookie
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // for dev
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        if (refreshToken != null) {
            RefreshTokenRepository.deleteByToken(refreshToken);
        }

        return ResponseEntity.ok("Logged out successfully");
    }


}