package  entities.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
public class forecastitem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String forecastQuantity;
    private String forecastDateAfter;
    private String forecastDateBefore;

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

    public String getForecastDateAfter() {
        return forecastDateAfter;
    }

    public void setForecastDateAfter(String forecastDateAfter) {
        this.forecastDateAfter = forecastDateAfter;
    }

    public String getForecastDateBefore() {
        return forecastDateBefore;
    }

    public void setForecastDateBefore(String forecastDateBefore) {
        this.forecastDateBefore = forecastDateBefore;
    }

    public order getOrder() {
        return order;
    }

    public void setOrder(order order) {
        this.order = order;
    }
}
