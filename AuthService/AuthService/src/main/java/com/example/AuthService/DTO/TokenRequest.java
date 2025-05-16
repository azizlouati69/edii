package com.example.AuthService.DTO;

import lombok.Data;

@Data
public class TokenRequest {
    private String refreshToken;
}