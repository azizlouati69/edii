package com.example.AuthService.DTO;

import com.example.AuthService.DTO.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUsername(String username);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUsername(String username);
    void deleteByToken(String token);


}
