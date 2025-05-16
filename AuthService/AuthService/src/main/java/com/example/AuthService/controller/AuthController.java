package com.example.AuthService.controller;
import com.example.AuthService.DTO.*;
import com.example.AuthService.entity.user;
import com.example.AuthService.service.AuthService;
import com.example.AuthService.service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")

public class AuthController {
    private final JwtService jwtService;
    private final TokenStore refreshTokenStore;
    private final AuthService authService;
    private final RefreshTokenRepository RefreshTokenRepository;
    public AuthController(AuthService authService, JwtService jwtservice, RefreshTokenRepository RefreshTokenRepository,TokenStore refreshTokenStore) {
        this.authService = authService;
        this.jwtService= jwtservice;
        this.refreshTokenStore = refreshTokenStore;
        this.RefreshTokenRepository= RefreshTokenRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
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

            user userDetails = authService.loadUserByUsername(username);

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