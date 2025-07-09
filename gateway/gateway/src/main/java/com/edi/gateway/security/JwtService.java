package com.edi.gateway.security;

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

    public boolean validateToken(String token) throws JwtException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw e; // Propagate expired token exception
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Other invalid tokens
        }
    }

    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            throw e; // Propagate expired token exception
        }
    }

    public Long extractUserId(String token) {
        try {
            return extractAllClaims(token).get("userId", Long.class);
        } catch (ExpiredJwtException e) {
            throw e; // Propagate expired token exception
        }
    }

    public String extractType(String token) {
        try {
            return extractAllClaims(token).get("type", String.class);
        } catch (ExpiredJwtException e) {
            throw e; // Propagate expired token exception
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // Token is expired
        }
    }

    public Date extractExpiration(String token) {
        try {
            return extractAllClaims(token).getExpiration();
        } catch (ExpiredJwtException e) {
            throw e; // Propagate expired token exception
        }
    }

    public UserDetails loadUserFromToken(String token) {
        try {
            return new org.springframework.security.core.userdetails.User(
                    extractUsername(token),
                    "",
                    Collections.emptyList()
            );
        } catch (ExpiredJwtException e) {
            throw e; // Propagate expired token exception
        }
    }

    public Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}