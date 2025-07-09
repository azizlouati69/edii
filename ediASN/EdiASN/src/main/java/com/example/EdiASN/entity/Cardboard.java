package com.example.EdiASN.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
public class  Cardboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer quantity_pallet;
    private String ref_cardboard;
    private String ref_pallet;
    private Float length;
    private Float width;
    private Float thickness;
    private Long userId;
    @OneToMany(mappedBy = "default_cardboard", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ArticleClientCardboard> default_for_articles = new ArrayList<>();
    @OneToMany(mappedBy = "cardboard", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ArticleClientCardboard> articleCardboards = new ArrayList<>();


}
