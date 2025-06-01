package com.example.EdiASN.repository;

import com.example.EdiASN.entity.InvoiceArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceArticleRepository extends JpaRepository<InvoiceArticle, Long> {
    // You can add custom queries here if needed
    List<InvoiceArticle> findByUserId(Long userId);
    Optional<InvoiceArticle> findByIdAndUserId(Long id, Long userId);

}