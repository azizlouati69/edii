package com.example.EdiASN.repository;

import com.example.EdiASN.entity.ArticleClient;
import com.example.EdiASN.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleClientRepository extends JpaRepository<ArticleClient, Long> {
    // You can add custom queries here if needed
    List<ArticleClient> findByUserId(Long userId);
    Optional<ArticleClient> findByIdAndUserId(Long id, Long userId);

}