package entities.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class firmitem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deliveryQuantity;
    private LocalDate deliveryDateAfter;
    private LocalDate deliveryDateBefore;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private order order;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryQuantity() {
        return deliveryQuantity;
    }

    public void setDeliveryQuantity(String deliveryQuantity) {
        this.deliveryQuantity = deliveryQuantity;
    }

    public LocalDate getDeliveryDateAfter() {
        return deliveryDateAfter;
    }

    public void setDeliveryDateAfter(LocalDate deliveryDateAfter) {
        this.deliveryDateAfter = deliveryDateAfter;
    }

    public LocalDate getDeliveryDateBefore() {
        return deliveryDateBefore;
    }

    public void setDeliveryDateBefore(LocalDate deliveryDateBefore) {
        this.deliveryDateBefore = deliveryDateBefore;
    }

    public order getOrder() {
        return order;
    }

    public void setOrder(order order) {
        this.order = order;
    }
}