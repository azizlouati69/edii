package com.example.Edi_dash.DTO;

import java.time.LocalDate;

public class recentclientdto {
    private Long id;
    private String buyerIdentifier;
    private String senderId;
        private LocalDate recentOrderDate;

    public recentclientdto(Long id, String buyerIdentifier, String senderId, LocalDate recentOrderDate) {
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

    public LocalDate getRecentOrderDate() {
        return recentOrderDate;
    }

    public void setRecentOrderDate(LocalDate recentOrderDate) {
        this.recentOrderDate = recentOrderDate;
    }
// getters & setters
}
