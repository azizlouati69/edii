package com.example.AuthService.service;

import com.example.AuthService.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(User user) {
        long accessTokenExpiration = 60 * 60 * 1000; // 60 minutes
        return generateToken(user.getUsername(), user.getId(), accessTokenExpiration, "access");
    }

    public String generateRefreshToken(User user) {
        long refreshTokenExpiration = 7 * 24 * 60 * 60 * 1000; // 7 days
        return generateToken(user.getUsername(), user.getId(), refreshTokenExpiration, "refresh");
    }

    private String generateToken(String username, Long userId, long expirationMillis, String type) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("type", type)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public String extractType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public UserDetails loadUserFromToken(String token) {
        return new org.springframework.security.core.userdetails.User(
                extractUsername(token),
                "",
                Collections.emptyList()
        );
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}