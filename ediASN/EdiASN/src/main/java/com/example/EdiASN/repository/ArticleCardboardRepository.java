package com.example.EdiASN.repository;

import com.example.EdiASN.entity.ArticleCardboard;
import com.example.EdiASN.entity.Cardboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleCardboardRepository extends JpaRepository<ArticleCardboard, Long> {
    // You can add custom queries here if needed
    List<ArticleCardboard> findByUserId(Long userId);
    Optional<ArticleCardboard> findByIdAndUserId(Long id, Long userId);

}
