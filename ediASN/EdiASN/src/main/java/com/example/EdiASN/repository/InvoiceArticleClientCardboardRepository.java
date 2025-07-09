package com.example.EdiASN.repository;

import com.example.EdiASN.entity.InvoiceArticleClientCardboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceArticleClientCardboardRepository extends JpaRepository<InvoiceArticleClientCardboard, Long> {
    // You can add custom queries here if needed
    List<InvoiceArticleClientCardboard> findByUserId(Long userId);
    Optional<InvoiceArticleClientCardboard> findByIdAndUserId(Long id, Long userId);

}