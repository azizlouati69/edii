package  entities.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "seller")
public class seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String receiverId;
    private String  name = "Vernicolor";
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonIgnoreProperties({ "firmItems", "forecastItems" })
    private List<order> orders = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "seller_client",
            joinColumns = @JoinColumn(name = "seller_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    @JsonIgnoreProperties("orders")
    private Set<client> clients = new HashSet<>();

    public seller() {

    }
    // Getters and Setters


    public Set<client> getClients() {
        return clients;
    }

    public void setClients(Set<client> clients) {
        this.clients = clients;
    }

    public List<order> getOrders() {
        return orders;
    }

    public void setOrders(List<order> orders) {
        this.orders = orders;
    }

    public seller(String receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public String toString() {
        return "Seller{receiverId='" + receiverId + "'}";
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return  name;
    }
}
