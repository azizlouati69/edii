package com.example.Edi_dash.controller;


 import entities.model.seller;

import com.example.Edi_dash.sevice.sellerservice;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.*;

 import java.util.Collections;
 import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("edi-dash/sellers/")
public class sellercontroller {

    private final sellerservice sellerservice;


    public sellercontroller(sellerservice sellerservice) {
        this.sellerservice = sellerservice;
    }

    @GetMapping
    public List<seller> getAllSellers() {
        return sellerservice.getAllSellers();
    }
    @GetMapping("/ReceiverId/{ReceiverId}")
    public seller getSellerByReceiverId(@PathVariable String ReceiverId) {
        return sellerservice.getSellerByReceiverId(ReceiverId);
    }

    @GetMapping("/search/receiverId")
    public List<seller> searchByReceiverId(@RequestParam String receiverId) {
        if (receiverId == null || receiverId.trim().isEmpty()) {
            System.out.println("Invalid receiverId: [" + receiverId + "]");
            return Collections.emptyList();
        }
        System.out.println("Searching for receiverId: [" + receiverId + "]");
        return sellerservice.searchSellerByReceiverId(receiverId);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {


            sellerservice.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/Id/{Id}")
    public Optional<seller> getSellerById(@PathVariable Long Id) {
        return sellerservice.getSellerById(Id);
    }
}
