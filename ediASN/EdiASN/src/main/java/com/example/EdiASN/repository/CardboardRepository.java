package com.example.EdiASN.repository;

import com.example.EdiASN.entity.Cardboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardboardRepository extends JpaRepository<Cardboard, Long> {
    // Optional: add methods to find by userId, etc.
    List<Cardboard> findByUserId(Long userId);
    Optional<Cardboard> findByIdAndUserId(Long id, Long userId);
}

