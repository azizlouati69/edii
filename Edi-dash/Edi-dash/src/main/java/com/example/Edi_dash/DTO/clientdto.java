package com.example.Edi_dash.DTO;



public class clientdto {

    private Long id;
    private String buyerIdentifier;
    private String senderId;  // Added senderId field
    private int orderCount;

    // Constructor
    public clientdto(Long id, String buyerIdentifier, String senderId, int orderCount) {
        this.id = id;
        this.buyerIdentifier = buyerIdentifier;
        this.senderId = senderId;  // Set senderId
        this.orderCount = orderCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuyerIdentifier() {
        return buyerIdentifier;
    }

    public void setBuyerIdentifier(String buyerIdentifier) {
        this.buyerIdentifier = buyerIdentifier;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}
