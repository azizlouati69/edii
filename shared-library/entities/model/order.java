package  entities.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordar")
public class order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buyerArticleNumber;
    private String description;
    @Column(name = "document_id")  // Ensure the column name is correct
    private String documentId;
    private String documentNumber;
    private String issueDate;
    private String calculationDate;
    private String shipto;
    private String placeofdischarge;
    private String internaldestination;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quantity_id", referencedColumnName = "id")
    @JsonManagedReference
    private quantity quantity;
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    @JsonIgnoreProperties({ "orders", "clients" })
    private seller seller;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties("orders")
    private client client;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<firmitem> firmItems;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<forecastitem> forecastItems;
    // Getters and Setters

    public List<firmitem> getFirmItems() {
        return firmItems;
    }

    public order(   ) {
        this.firmItems = new ArrayList<>();
        this.forecastItems = new ArrayList<>();}

    public void setFirmItems(List<firmitem> firmItems) {
        this.firmItems = firmItems;
    }

    public List<forecastitem> getForecastItems() {
        return forecastItems;
    }

    public void setForecastItems(List<forecastitem> forecastItems) {
        this.forecastItems = forecastItems;
    }

    public seller getSeller() {
        return seller;
    }

    public   void setSeller(seller seller) {
        this.seller = seller;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuyerArticleNumber() {
        return buyerArticleNumber;
    }

    public String getShipto() {
        return shipto;
    }

    public void setShipto(String shipto) {
        this.shipto = shipto;
    }

    public String getPlaceofdischarge() {
        return placeofdischarge;
    }

    public void setPlaceofdischarge(String placeofdischarge) {
        this.placeofdischarge = placeofdischarge;
    }

    public String getInternaldestination() {
        return internaldestination;
    }

    public void setInternaldestination(String internaldestination) {
        this.internaldestination = internaldestination;
    }

    public void setBuyerArticleNumber(String buyerArticleNumber) {
        this.buyerArticleNumber = buyerArticleNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(String calculationDate) {
        this.calculationDate = calculationDate;
    }

    public quantity getQuantity() {
        return quantity;
    }

    public client getClient() {
        return client;
    }

    public   void setClient(client client) {
        this.client = client;
    }

    public void setQuantity(quantity quantity) {
        this.quantity = quantity;
    }
}
