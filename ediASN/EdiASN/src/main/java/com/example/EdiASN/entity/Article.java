package com.example.EdiASN.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    private String nameint;
    private String nameext;
    private String designation;
    private Float gross_weight;
    private Float net_weight;
    private String lot_number;
    private String order_number;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    @JoinColumn(name = "client_id") // foreign key in the Article table
    private Client client;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    @JoinColumn(name = "default_cardboard_id") // foreign key in the Article table
    private Cardboard defaultcardboard;
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ArticleCardboard> articleCardboards = new ArrayList<>();
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference

    private List<InvoiceArticle> invoiceArticles = new ArrayList<>();

}
