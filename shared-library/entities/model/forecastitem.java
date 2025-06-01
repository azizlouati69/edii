package entities.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class forecastitem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String forecastQuantity;
    private LocalDate forecastDateAfter;
    private LocalDate forecastDateBefore;

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

    public String getForecastQuantity() {
        return forecastQuantity;
    }

    public void setForecastQuantity(String forecastQuantity) {
        this.forecastQuantity = forecastQuantity;
    }

    public LocalDate getForecastDateAfter() {
        return forecastDateAfter;
    }

    public void setForecastDateAfter(LocalDate forecastDateAfter) {
        this.forecastDateAfter = forecastDateAfter;
    }

    public LocalDate getForecastDateBefore() {
        return forecastDateBefore;
    }

    public void setForecastDateBefore(LocalDate forecastDateBefore) {
        this.forecastDateBefore = forecastDateBefore;
    }

    public order getOrder() {
        return order;
    }

    public void setOrder(order order) {
        this.order = order;
    }
}