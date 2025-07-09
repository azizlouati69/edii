package com.example.EdiASN.repository;

import com.example.EdiASN.entity.ArticleClientCardboard;
import com.example.EdiASN.entity.Cardboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleClientCardboardRepository extends JpaRepository<ArticleClientCardboard, Long> {
    // You can add custom queries here if needed
    List<ArticleClientCardboard> findByUserId(Long userId);
    Optional<ArticleClientCardboard> findByIdAndUserId(Long id, Long userId);

}
