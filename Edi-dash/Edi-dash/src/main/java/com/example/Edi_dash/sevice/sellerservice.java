package com.example.Edi_dash.sevice;


import entities.model.seller;
import repository.sellerrepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class sellerservice {
    private final sellerrepository sellerRepository;

    public sellerservice(sellerrepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public List<seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public List<seller> searchSellerByReceiverId(String ReceiverId) {
        return   sellerRepository.findByReceiverIdContainingIgnoreCase(ReceiverId);

    }
    public Optional<seller> getSellerById(Long  Id) {
        return sellerRepository.findById(Id);

    }
    public seller getSellerByReceiverId(String ReceiverId) {
        return   sellerRepository.findByReceiverId(ReceiverId).get(0);

    }
}
