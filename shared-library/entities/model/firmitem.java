package  entities.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class firmitem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deliveryQuantity;
    private String deliveryDateAfter;
    private String deliveryDateBefore;

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

    public String getDeliveryDateAfter() {
        return deliveryDateAfter;
    }

    public void setDeliveryDateAfter(String deliveryDateAfter) {
        this.deliveryDateAfter = deliveryDateAfter;
    }

    public String getDeliveryDateBefore() {
        return deliveryDateBefore;
    }

    public void setDeliveryDateBefore(String deliveryDateBefore) {
        this.deliveryDateBefore = deliveryDateBefore;
    }

    public order getOrder() {
        return order;
    }

    public void setOrder(order order) {
        this.order = order;
    }
}
