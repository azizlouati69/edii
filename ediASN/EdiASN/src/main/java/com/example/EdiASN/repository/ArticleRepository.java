package com.example.EdiASN.repository;

import com.example.EdiASN.entity.Article;
import com.example.EdiASN.entity.Cardboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // No extra methods needed for basic save
    List<Article> findByUserId(Long userId);
    Optional<Article> findByIdAndUserId(Long id, Long userId);

}
