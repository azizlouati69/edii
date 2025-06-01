package com.example.EdiASN.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cardboard {
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
    @OneToMany(mappedBy = "defaultcardboard", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Article> default_for_articles = new ArrayList<>();
    @OneToMany(mappedBy = "cardboard", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ArticleCardboard> articleCardboards = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity_pallet() {
        return quantity_pallet;
    }

    public void setQuantity_pallet(Integer quantity_pallet) {
        this.quantity_pallet = quantity_pallet;
    }

    public String getRef_cardboard() {
        return ref_cardboard;
    }

    public void setRef_cardboard(String ref_cardboard) {
        this.ref_cardboard = ref_cardboard;
    }

    public String getRef_pallet() {
        return ref_pallet;
    }

    public void setRef_pallet(String ref_pallet) {
        this.ref_pallet = ref_pallet;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public List<ArticleCardboard> getArticleCardboards() {
        return articleCardboards;
    }

    public void setArticleCardboards(List<ArticleCardboard> articleCardboards) {
        this.articleCardboards = articleCardboards;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
