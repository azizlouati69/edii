package  entities.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
@Entity
public class quantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String latestReceivedQuantity;
    private String receivingDate;
    private String deliveryNoteDocNumber;
    private String cumulativeReceivedQuantity;
    @OneToOne(mappedBy = "quantity"   )
    @JsonBackReference
    private order order;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public order getOrder() {
        return order;
    }

    public void setOrder(order order) {
        this.order = order;
    }

    public String getCumulativeReceivedQuantity() {
        return cumulativeReceivedQuantity;
    }

    public void setCumulativeReceivedQuantity(String cumulativeReceivedQuantity) {
        this.cumulativeReceivedQuantity = cumulativeReceivedQuantity;
    }

    public String getReceivingDate() {
        return receivingDate;
    }

    public void setReceivingDate(String receivingDate) {
        this.receivingDate = receivingDate;
    }

    public String getLatestReceivedQuantity() {
        return latestReceivedQuantity;
    }

    public void setLatestReceivedQuantity(String latestReceivedQuantity) {
        this.latestReceivedQuantity = latestReceivedQuantity;
    }

    public String getDeliveryNoteDocNumber() {
        return deliveryNoteDocNumber;
    }

    public void setDeliveryNoteDocNumber(String deliveryNoteDocNumber) {
        this.deliveryNoteDocNumber = deliveryNoteDocNumber;
    }
// Getters and Setters
}
