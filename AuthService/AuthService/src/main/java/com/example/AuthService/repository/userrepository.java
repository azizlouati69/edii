package com.example.AuthService.repository;



import com.example.AuthService.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface userrepository extends JpaRepository<user, Long> {
    Optional<user> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<user> findByEmail(String email);

}
