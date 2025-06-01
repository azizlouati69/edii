package com.example.EdiASN.dto;

public class CardboardDTO {
    private String name;
    private Integer quantity_pallet;
    private String ref_cardboard;
    private String ref_pallet;
    private Float length;
    private Float width;
    private Float thickness;
    private Long userId;

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

    public Float getLength() {
        return length;
    }

    public void setLength(Float length) {
        this.length = length;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Float getThickness() {
        return thickness;
    }

    public void setThickness(Float thickness) {
        this.thickness = thickness;
    }
// getters and setters
}
