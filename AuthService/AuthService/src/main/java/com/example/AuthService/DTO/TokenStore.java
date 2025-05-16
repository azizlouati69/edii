package com.example.AuthService.DTO;

import com.example.AuthService.DTO.RefreshToken;
import com.example.AuthService.DTO.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenStore {

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenStore(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    @Transactional
    public void put(String username, String token) {
        refreshTokenRepository.deleteByUsername(username); // Rotate old one
        refreshTokenRepository.save(new RefreshToken(token, username));
    }

    public String get(String username) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByUsername(username);
        return tokenOpt.map(RefreshToken::getToken).orElse(null);
    }
}
