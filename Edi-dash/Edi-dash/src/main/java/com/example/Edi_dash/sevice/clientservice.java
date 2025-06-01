package com.example.Edi_dash.sevice;

import com.example.Edi_dash.DTO.clientdto;
import com.example.Edi_dash.DTO.recentclientdto;
import entities.model.client;
import entities.model.order;
import entities.model.seller;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.clientrepository;
import repository.orderrepository;
import repository.sellerrepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class clientservice {

    private final clientrepository clientRepository;
    private final orderrepository orderRepository;
    private final sellerrepository sellerRepository;

    public clientservice(clientrepository clientRepository, orderrepository orderRepository, sellerrepository sellerRepository) {
        this.clientRepository = clientRepository;
        this.orderRepository = orderRepository;
        this.sellerRepository = sellerRepository;
    }

    public client getClientByBuyerIdentifier(String buyerIdentifier) {
        return clientRepository.findByBuyerIdentifier(buyerIdentifier).get(0);
    }

    public client getClientBySenderId(String senderId) {
        return clientRepository.findBySenderId(senderId).get(0);
    }

    public Optional<client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public List<client> getAllClients() {
        return clientRepository.findAll();
    }

    public List<client> searchBySenderId(String senderId) {
        return clientRepository.findBySenderIdContainingIgnoreCase(senderId);
    }

    public List<client> searchByBuyerIdentifier(String buyerIdentifier) {
        return clientRepository.findByBuyerIdentifierContainingIgnoreCase(buyerIdentifier);
    }

    public Long getTotalClients() {
        return clientRepository.countAllClients();
    }

    public Long getActiveClients() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        return clientRepository.countActiveClientsThisMonth(startOfMonth, endOfMonth);
    }

    public Long getInactiveClients() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        return clientRepository.countInactiveClientsThisMonth(startOfMonth, endOfMonth);
    }

    public Double getAverageOrdersPerClient() {
        Double avg = clientRepository.averageOrdersPerClient();
        return avg == null ? 0.0 : Math.round(avg * 100.0) / 100.0;
    }

    public List<clientdto> getTop3ClientsThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        List<Object[]> result = clientRepository.findTopClientsByOrderCountThisMonth(startOfMonth, endOfMonth);
        return mapToClientDTO(result, 3);
    }

    public List<clientdto> getTop3ClientsThisYear() {
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = now.withDayOfYear(1);
        LocalDate endOfYear = now.withMonth(12).withDayOfMonth(31);
        List<Object[]> result = clientRepository.findTopClientsByOrderCountThisYear(startOfYear, endOfYear);
        return mapToClientDTO(result, 3);
    }

    public List<clientdto> getTop3ClientsByOrderCountToday() {
        LocalDate today = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 3);
        List<Object[]> result = clientRepository.findTop3ClientsByOrderCountToday(today, pageable);
        return mapToClientDTO(result, result.size());
    }

    public List<clientdto> getTop3ClientsThisWeek() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
        List<Object[]> result = clientRepository.findTopClientsByOrderCountThisWeek(startOfWeek, endOfWeek);
        return mapToClientDTO(result, 3);
    }



    public List<clientdto> getTopClients() {
        Pageable topThree = PageRequest.of(0, 3);
        List<Object[]> result = clientRepository.findTopClientsByOrderCount(topThree);
        return mapToClientDTO(result, result.size());
    }

    public List<recentclientdto> getTopClientsByRecentOrders() {
        Pageable top5 = PageRequest.of(0, 5);
        List<Object[]> results = clientRepository.findTopClientsByRecentOrders(top5);
        return results.stream()
                .map(r -> new recentclientdto(
                        (Long) r[0],
                        (String) r[1],
                        (String) r[2],
                        (LocalDate) r[3]
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteClient(Long clientId) {
        client clientToDelete = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        List<seller> associatedSellers = new ArrayList<>(clientToDelete.getSellers());

        for (seller seller : associatedSellers) {
            seller.getClients().remove(clientToDelete);
            sellerRepository.save(seller);
        }
        clientToDelete.getSellers().clear();

        for (order order : new ArrayList<>(clientToDelete.getOrders())) {
            orderRepository.delete(order);
        }
        clientToDelete.getOrders().clear();

        clientRepository.delete(clientToDelete);

        for (seller seller : associatedSellers) {
            if (seller.getClients().isEmpty()) {
                for (order order : new ArrayList<>(seller.getOrders())) {
                    orderRepository.delete(order);
                }
                seller.getOrders().clear();
                sellerRepository.delete(seller);
            }
        }
    }

    private List<clientdto> mapToClientDTO(List<Object[]> result, int maxLimit) {
        List<clientdto> dtos = new ArrayList<>();
        int limit = Math.min(maxLimit, result.size());
        for (int i = 0; i < limit; i++) {
            Object[] row = result.get(i);
            dtos.add(new clientdto(
                    (Long) row[0],
                    (String) row[1],
                    (String) row[2],
                    ((Number) row[3]).intValue()
            ));
        }
        return dtos;
    }
}
