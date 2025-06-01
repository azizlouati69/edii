package com.example.EdiASN.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
public class ArticleCardboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "article_id")
    private Article article;
    @ManyToOne
    @JsonManagedReference

    @JoinColumn(name = "cardboard_id")
    private Cardboard cardboard;

    private int quantityPerCardboard;
    private Long userId;


}
