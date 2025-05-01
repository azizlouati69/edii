package com.example.Edi_dash.DTO;

public class recentclientdto {
    private Long id;
    private String buyerIdentifier;
    private String senderId;
    private String recentOrderDate;

    public recentclientdto(Long id, String buyerIdentifier, String senderId, String recentOrderDate) {
        this.id = id;
        this.buyerIdentifier = buyerIdentifier;
        this.senderId = senderId;
        this.recentOrderDate = recentOrderDate;
    }

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

    public String getRecentOrderDate() {
        return recentOrderDate;
    }

    public void setRecentOrderDate(String recentOrderDate) {
        this.recentOrderDate = recentOrderDate;
    }
// getters & setters
}
