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
public class ArticleClientCardboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "articleclient_id")
    private ArticleClient articleClient;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "default_cardboard_id")
    private Cardboard default_cardboard;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "cardboard_id")
    private Cardboard cardboard;

    private int quantityPerCardboard;
    private Long userId;

    @OneToMany(mappedBy = "articleClientCardboard", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<InvoiceArticleClientCardboard> invoiceArticleClientCardboards = new ArrayList<>();
}
