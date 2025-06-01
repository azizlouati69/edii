package com.example.EdiASN.repository;

 import com.example.EdiASN.entity.Client;
 import com.example.EdiASN.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

 import java.util.List;
 import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    // No extra methods needed for basic save
    List<Client> findByUserId(Long userId);
 Optional<Client> findByIdAndUserId(Long id, Long userId);
}
