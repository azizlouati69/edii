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
public class ArticleClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "article_id")
    private Article article;
    @ManyToOne
    @JsonManagedReference

    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "articleClient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ArticleClientCardboard> articleClientCardboards = new ArrayList<>();
    private Long userId;


}
