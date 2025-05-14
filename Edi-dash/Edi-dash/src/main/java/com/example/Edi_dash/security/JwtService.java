package com.example.Edi_dash.security;



import com.example.AuthService.entity.user;
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

    private SecretKey getSignInKey() {
        String secretKey = "Y2oTGqRyIT8Yz3s/fYAz5j2R3tVnSjbLG2l9EXtYiAnAYs1U1xB9bb08a9YYIXXQ75qVXAxu6v1yixF0UuUuWA==";
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(user user) {
        // Make sure the expiration time is correct (in milliseconds)
        long jwtExpirationMs = 3600000;  // Example: 1 hour expiration time

        return Jwts.builder()
                .setSubject(user.getUsername())  // Set the subject to the username
                .setIssuedAt(new Date())  // Set the current timestamp
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))  // Set the expiration time
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)  // Sign the token with the secret key
                .compact();  // Build and return the token
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
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public UserDetails loadUserFromToken(String token) {
        return new org.springframework.security.core.userdetails.User(
                extractUsername(token),
                "", // password (not used here)
                Collections.emptyList() // <- never null!
        );
    }
}