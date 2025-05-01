package com.example.Edi_dash.controller;


import com.example.Edi_dash.DTO.clientdto;
import com.example.Edi_dash.DTO.recentclientdto;
import entities.model.client;
import com.example.Edi_dash.sevice.clientservice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/clients/")
public class clientcontroller {

    private final clientservice clientService;

    public clientcontroller(clientservice clientService) {
        this.clientService = clientService;
    }
    @GetMapping("/top-this-month")
    public List<clientdto> getTop3ClientsThisMonth() {
        return clientService.getTop3ClientsThisMonth();
    }
    @GetMapping("/top-this-year")
    public ResponseEntity<List<clientdto>> getTopClientsThisYear() {
        List<clientdto> topClients = clientService.getTop3ClientsThisYear();
        return ResponseEntity.ok(topClients);
    }
    @GetMapping("/top-this-week")
    public ResponseEntity<List<clientdto>> getTopClientsThisWeek() {
        List<clientdto> topClients = clientService.getTop3ClientsThisWeek();
        return ResponseEntity.ok(topClients);
    }
    @GetMapping("/top-today")
    public ResponseEntity<List<clientdto>> getTopClientsToday() {
        List<clientdto> topClients = clientService.getTop3ClientsByOrderCountToday();
        return ResponseEntity.ok(topClients);
    }
    @GetMapping
    public List<client> getAllClients() {
        return clientService.getAllClients();
    }
    @GetMapping("/total")
    public Long getTotalClients() {
        return clientService.getTotalClients();
    }

    // Endpoint for Active Clients
    @GetMapping("/active")
    public Long getActiveClients() {
        return clientService.getActiveClients();
    }

    // Endpoint for Inactive Clients
    @GetMapping("/inactive")
    public Long getInactiveClients() {
        return clientService.getInactiveClients();
    }

    // Endpoint for Average Orders per Client
    @GetMapping("/avg-orders")
    public Double getAverageOrdersPerClient() {
        return clientService.getAverageOrdersPerClient();
    }
    @GetMapping("/top-recent-orders")
    public ResponseEntity<List<recentclientdto>> getTopClientsByRecentOrders() {
        List<recentclientdto> topClients = clientService.getTopClientsByRecentOrders();
        return ResponseEntity.ok(topClients);
    }
    // Endpoint for Top Clients
    @GetMapping("/top")
    public List<clientdto> getTopClients() {
        return clientService.getTopClients();
    }
    @GetMapping("/SenderId/{senderId}")
    public client getClientBySenderId(@PathVariable String senderId) {
        return clientService.getClientBySenderId(senderId);
    }
    @GetMapping("/BuyerIdentifier/{buyerIdentifier}")
    public client getClientByBuyerIdentifier(@PathVariable String buyerIdentifier) {
        return clientService.getClientByBuyerIdentifier(buyerIdentifier);
    }
    @GetMapping("/Id/{Id}")
    public Optional<client> getClientById(@PathVariable Long Id) {
        return clientService.getClientById(Id);
    }

    @GetMapping("/search/senderId")
    public List<client> searchBySenderId(@RequestParam String senderId) {
        return clientService.searchBySenderId(senderId);
    }

    @GetMapping("/search/buyerIdentifier")
    public List<client> searchByBuyerIdentifier(@RequestParam String buyerIdentifier) {
        return clientService.searchByBuyerIdentifier(buyerIdentifier);
    }
}