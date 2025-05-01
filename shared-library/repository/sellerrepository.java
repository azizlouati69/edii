package  repository;
import entities.model.seller;

  import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface sellerrepository extends JpaRepository<seller, Long> {
    List<seller> findByReceiverId(String receiverId);
    List<seller> findByReceiverIdContainingIgnoreCase(String receiverId);// This returns a list of clients
    Optional<seller> findById(Long  Id);

}
