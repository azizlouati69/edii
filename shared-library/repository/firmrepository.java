package  repository;
import entities.model.firmitem;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface firmrepository extends JpaRepository<firmitem, Long> {

}
