package com.example.AuthService.DTO;



import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String username;

    private Instant createdAt;

    public RefreshToken() {
        this.createdAt = Instant.now();
    }

    public RefreshToken(String token, String username) {
        this.token = token;
        this.username = username;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
// Getters and Setters
}
