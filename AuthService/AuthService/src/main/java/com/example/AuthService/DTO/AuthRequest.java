package com.example.AuthService.DTO;

import lombok.*;

@Data
public class AuthRequest {
    private String email;
    private String password;

    public String getUsername() {
        return email;
    }

    public void setUsername(String username) {
        this.email = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}