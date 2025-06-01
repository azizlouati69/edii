package com.example.EdiASN.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String invoice_number;
    private LocalDate deliveryDate;
    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonManagedReference
// Foreign key in invoice table
    private Client client;


    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference

    private List<InvoiceArticle> invoiceArticles = new ArrayList<>();
}
