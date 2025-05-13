package com.example.Edi_dash.controller;


 import entities.model.seller;

import com.example.Edi_dash.sevice.sellerservice;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequestMapping("/sellers/")
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

    @GetMapping("/search/senderId")
    public List<seller> searchByReceiverId(@RequestParam String receiverId) {
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
