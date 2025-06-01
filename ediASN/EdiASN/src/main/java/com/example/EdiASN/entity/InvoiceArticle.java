package com.example.EdiASN.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
public class InvoiceArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalQuantity;
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    @JsonManagedReference

    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "article_id")
    @JsonManagedReference

    private Article article;


}
