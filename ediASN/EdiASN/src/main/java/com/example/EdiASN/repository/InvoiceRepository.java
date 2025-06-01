package com.example.EdiASN.repository;

import com.example.EdiASN.entity.Article;
import com.example.EdiASN.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // No extra methods needed for basic save
    List<Invoice> findByUserId(Long userId);
    Optional<Invoice> findByIdAndUserId(Long id, Long userId);

}
