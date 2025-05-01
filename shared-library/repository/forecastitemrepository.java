package  repository;

 ;import entities.model.forecastitem;

  import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface forecastitemrepository extends JpaRepository<forecastitem, Long> {
}
