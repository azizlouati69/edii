package  entities.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.micrometer.observation.transport.Propagator;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
 @AllArgsConstructor
@Table(name = "client")
public class client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderId;
    private String buyerIdentifier;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<order> orders = new ArrayList<>();

    @ManyToMany(mappedBy = "clients")
    @JsonIgnore
    private Set<seller> sellers = new HashSet<>();

    public Set<seller> getSellers() {
        return sellers;
    }

    public void setSellers(Set<seller> sellers) {
        this.sellers = sellers;
    }

    public client() {

    }

    @Override
    public String toString() {
        return "client{" +
                "id=" + id +
                ", senderId='" + senderId + '\'' +
                ", buyerIdentifier='" + buyerIdentifier +


                '}';
    }



    public List<order> getOrders() {
        return orders;
    }

    public void setOrders(List<order> orders) {
        this.orders = orders;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
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
}

