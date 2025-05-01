package  repository;

import entities.model.quantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface quantityrepository extends JpaRepository<quantity, Long> {
}
