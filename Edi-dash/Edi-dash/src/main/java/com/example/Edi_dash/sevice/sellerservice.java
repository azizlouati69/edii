package com.example.Edi_dash.sevice;

import entities.model.client;
import entities.model.order;
import entities.model.seller;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.clientrepository;
import repository.orderrepository;
import repository.sellerrepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class sellerservice {
    private final sellerrepository sellerRepository;
    private final orderrepository orderRepository;
    private final clientrepository clientRepository;


    @PersistenceContext
    private EntityManager entityManager;

    public sellerservice(sellerrepository sellerRepository, clientrepository clientRepository,orderrepository orderRepository) {
        this.sellerRepository = sellerRepository;
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
    }

    public List<seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public List<seller> searchSellerByReceiverId(String receiverId) {
        System.out.println("Repository call with receiverId: [" + receiverId + "]");
        return sellerRepository.findByReceiverIdContainingIgnoreCase(receiverId);
    }
    public Optional<seller> getSellerById(Long  Id) {
        return sellerRepository.findById(Id);

    }
    public seller getSellerByReceiverId(String ReceiverId) {
        return   sellerRepository.findByReceiverId(ReceiverId).get(0);

    }
    @Transactional
    public void deleteSeller(Long sellerId) {
        // Step 1: Find the seller or throw an exception
        seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        // Step 2: Prepare a list of clients to potentially delete
        List<client> associatedClients = new ArrayList<>(seller.getClients());

        // Step 3: Remove seller from clients
        for (client client : associatedClients) {
            client.getSellers().remove(seller);
        }
        seller.getClients().clear();

        // Step 4: Disassociate orders from seller
        for (order order : new ArrayList<>(seller.getOrders())) {
            order.setSeller(null);
        }
        seller.getOrders().clear();

        // Step 5: Delete the seller
        sellerRepository.delete(seller);

        // Step 6: Delete clients that no longer have sellers
        for (client client : associatedClients) {
            if (client.getSellers().isEmpty()) {
                // Disassociate and delete client's orders
                for (order order : new ArrayList<>(client.getOrders())) {
                    orderRepository.delete(order); // This will also cascade firmItems, forecastItems
                }
                client.getOrders().clear();
                clientRepository.delete(client);
            }
        }
    }




}
