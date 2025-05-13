package com.example.Edi_dash.sevice;
import entities.model.seller;

import com.example.Edi_dash.DTO.clientdto;
import com.example.Edi_dash.DTO.recentclientdto;
import entities.model.client;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.clientrepository;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import entities.model.order;
import repository.orderrepository;
import repository.sellerrepository;

@Service
public class clientservice {
    private final clientrepository clientRepository;
    private final orderrepository orderRepository;
    private final sellerrepository sellerRepository;



    public clientservice(clientrepository clientRepository , orderrepository orderRepository, sellerrepository sellerRepository) {
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
    public Optional<client> getClientById(Long  Id) {
        return clientRepository.findById(Id);

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

    // KPI: Active Clients (Clients with at least one order)
    public Long getActiveClients() {
        // Get current month in "yyyy-MM" format
        String currentMonthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return clientRepository.countActiveClientsThisMonth(currentMonthYear);
    }

    // KPI: Inactive Clients (Clients with no orders)
    public Long getInactiveClients() {
        String currentMonthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return clientRepository.countInactiveClientsThisMonth(currentMonthYear);
    }
    public List<clientdto> getTop3ClientsThisMonth() {
        // Get the current month in "yyyy-MM" format
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // Fetch the top clients for the current month (no pagination)
        List<Object[]> result = clientRepository.findTopClientsByOrderCountThisMonth(currentMonth);

        // Convert query result to DTO list
        List<clientdto> topClients = new ArrayList<>();

        // Ensure we only take the top 3 clients
        int limit = Math.min(3, result.size());  // Handle case where less than 3 results are returned

        for (int i = 0; i < limit; i++) {
            Object[] row = result.get(i);
            Long clientId = (Long) row[0];
            String buyerIdentifier = (String) row[1];
            String senderId = (String) row[2];
            Integer orderCount = ((Long) row[3]).intValue(); // Convert Long to Integer

            clientdto dto = new clientdto(clientId, buyerIdentifier, senderId, orderCount);
            topClients.add(dto);
        }

        return topClients;
    }
    // KPI: Average Orders per Client
    public Double getAverageOrdersPerClient() {
        Double avg = clientRepository.averageOrdersPerClient();
        if (avg == null) {
            return 0.0;
        }
        return Math.round(avg * 100.0) / 100.0;
    }
    public List<clientdto> getTop3ClientsThisYear() {
        // Get the current year in "yyyy" format
        String currentYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));

        // Fetch the top clients for the current year
        List<Object[]> result = clientRepository.findTopClientsByOrderCountThisYear(currentYear);

        // Convert query result to DTO list
        List<clientdto> topClients = new ArrayList<>();

        // Ensure we only take the top 3 clients
        int limit = Math.min(3, result.size());
        for (int i = 0; i < limit; i++) {
            Object[] row = result.get(i);
            Long clientId = (Long) row[0];
            String buyerIdentifier = (String) row[1];
            String senderId = (String) row[2];
            Integer orderCount = ((Long) row[3]).intValue();

            clientdto dto = new clientdto(clientId, buyerIdentifier, senderId, orderCount);
            topClients.add(dto);
        }

        return topClients;
    }
    public List<clientdto> getTop3ClientsByOrderCountToday() {
        // Get today's date in "yyyy-MM-dd" format
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Create Pageable object for top 3 (page number 0, page size 3)
        Pageable pageable = PageRequest.of(0, 3);

        // Call the repository method to fetch the top 3 clients
        List<Object[]> result = clientRepository.findTop3ClientsByOrderCountToday(today, pageable);

        // Convert result to DTO
        List<clientdto> topClients = new ArrayList<>();
        for (Object[] row : result) {
            Long clientId = (Long) row[0];
            String buyerIdentifier = (String) row[1];
            String senderId = (String) row[2];
            Integer orderCount = ((Long) row[3]).intValue();

            clientdto dto = new clientdto(clientId, buyerIdentifier, senderId, orderCount);
            topClients.add(dto);
        }

        return topClients;
    }



    public List<clientdto> getTop3ClientsThisWeek() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the start and end of the current week
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);  // Start of the week (Monday)
        LocalDate endOfWeek = currentDate.with(DayOfWeek.SUNDAY);    // End of the week (Sunday)

        // Format the dates as Strings in "yyyy-MM-dd" format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startOfWeekStr = startOfWeek.format(formatter);
        String endOfWeekStr = endOfWeek.format(formatter);

        // Fetch the top clients for this week
        List<Object[]> result = clientRepository.findTopClientsByOrderCountThisWeek(startOfWeekStr, endOfWeekStr);

        // Convert query result to DTO list
        List<clientdto> topClients = new ArrayList<>();
        for (Object[] row : result) {
            Long clientId = (Long) row[0];
            String buyerIdentifier = (String) row[1];
            String senderId = (String) row[2];
            Integer orderCount = ((Long) row[3]).intValue(); // Convert Long to Integer

            clientdto dto = new clientdto(clientId, buyerIdentifier, senderId, orderCount);
            topClients.add(dto);
        }

        return topClients;
    }


    public List<recentclientdto> getTopClientsByRecentOrders() {
        Pageable top5 = PageRequest.of(0, 5);
        List<Object[]> results = clientRepository.findTopClientsByRecentOrders(top5);

        return results.stream()
                .map(r -> new recentclientdto(
                        (Long) r[0],
                        (String) r[1],
                        (String) r[2],
                        (String) r[3]
                ))
                .collect(Collectors.toList());
    }
    // KPI: Top 5 Clients by Order Count
    public List<clientdto> getTopClients() {
        Pageable topThree = PageRequest.of(0, 3); // get only top 3
        List<Object[]> results = clientRepository.findTopClientsByOrderCount(topThree);

        return results.stream()
                .map(result -> new clientdto(
                        (Long) result[0],              // client ID
                        (String) result[1],            // buyer identifier
                        (String) result[2],            // senderId
                        ((Number) result[3]).intValue() // order count
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteClient(Long clientId) {
        // Step 1: Find the client or throw exception
        client clientToDelete = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Step 2: Save a copy of associated sellers (for later check)
        List<seller> associatedSellers = new ArrayList<>(clientToDelete.getSellers());

        // Step 3: Break the ManyToMany relationship (client <-> sellers)
        for (seller seller : associatedSellers) {
            seller.getClients().remove(clientToDelete);
            sellerRepository.save(seller);
        }
        clientToDelete.getSellers().clear();

        // Step 4: Delete related orders (with firmitems & forecastitems via cascade)
        for (order order : new ArrayList<>(clientToDelete.getOrders())) {
            orderRepository.delete(order);
        }
        clientToDelete.getOrders().clear();

        // Step 5: Delete the client
        clientRepository.delete(clientToDelete);

        // Step 6: Delete orphaned sellers
        for (seller seller : associatedSellers) {
            if (seller.getClients().isEmpty()) {
                // Disassociate and delete seller's orders (just in case)
                for (order order : new ArrayList<>(seller.getOrders())) {
                    orderRepository.delete(order);
                }
                seller.getOrders().clear();
                sellerRepository.delete(seller);
            }
        }
    }



}