package com.example.Edi_dash.sevice;

import com.example.Edi_dash.DTO.orderdto;
import entities.model.firmitem;
import entities.model.forecastitem;
import entities.model.order;
import lombok.extern.slf4j.Slf4j;
import java.util.logging.Logger;
import entities.model.seller;
import entities.model.client;

import entities.model.quantity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import repository.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class orderservice {
    private static final Logger log = Logger.getLogger(orderservice.class.getName());
    @Autowired
    private PlatformTransactionManager transactionManager;
    private static orderrepository orderRepository;
    private static forecastitemrepository forecastRepository;
    private static firmrepository firmRepository;
    private static quantityrepository quantityRepository;
    private static sellerrepository sellerRepository;
    private static clientrepository clientRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public orderservice(orderrepository orderRepository, quantityrepository quantityRepository,
                        forecastitemrepository forecastRepository,
                        firmrepository firmRepository, sellerrepository sellerRepository, clientrepository clientRepository) {
        this.orderRepository = orderRepository;
        this.forecastRepository = forecastRepository;
        this.firmRepository = firmRepository;
        this.quantityRepository = quantityRepository;
        this.sellerRepository = sellerRepository;
        this.clientRepository = clientRepository;
    }

    public order getOrderByDocumentId(String docId) {
        return orderRepository.findByDocumentId(docId).get(0);
    }

    public Optional<quantity> getqById(Long id) {
        return quantityRepository.findById(id);
    }

    public void deleteOrder(Long orderId) {
        order orderToDelete = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        client associatedClient = orderToDelete.getClient();
        seller associatedSeller = orderToDelete.getSeller();

        // Step 1: Remove firmItems
        if (orderToDelete.getFirmItems() != null) {
            for (firmitem firmItem : new ArrayList<>(orderToDelete.getFirmItems())) {
                firmItem.setOrder(null);
            }
            orderToDelete.getFirmItems().clear();
        }

        // Step 2: Remove forecastItems
        if (orderToDelete.getForecastItems() != null) {
            for (forecastitem forecastItem : new ArrayList<>(orderToDelete.getForecastItems())) {
                forecastItem.setOrder(null);
            }
            orderToDelete.getForecastItems().clear();
        }

        // Step 3: Break quantity
        if (orderToDelete.getQuantity() != null) {
            orderToDelete.setQuantity(null);
        }

        // Step 4: Remove order from client and seller
        if (associatedClient != null) {
            associatedClient.getOrders().remove(orderToDelete);
        }
        if (associatedSeller != null) {
            associatedSeller.getOrders().remove(orderToDelete);
        }

        // Step 5: Delete the order
        orderRepository.delete(orderToDelete);

        // Step 6: If client has no more orders, delete client
        if (associatedClient != null && associatedClient.getOrders().isEmpty()) {
            for (seller seller : new ArrayList<>(associatedClient.getSellers())) {
                seller.getClients().remove(associatedClient);
                sellerRepository.save(seller);
            }
            associatedClient.getSellers().clear();
            clientRepository.delete(associatedClient);
        }

        // Step 7: If seller has no more orders, delete seller
        if (associatedSeller != null && associatedSeller.getOrders().isEmpty()) {
            for (client client : new ArrayList<>(associatedSeller.getClients())) {
                client.getSellers().remove(associatedSeller);
                clientRepository.save(client);
            }
            associatedSeller.getClients().clear();
            sellerRepository.delete(associatedSeller);
        }
    }

    public Long getOrdersThisMonth() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return orderRepository.findOrdersBetween(startOfMonth, endOfMonth).stream().count();
    }

    public List<Map<String, Object>> getTop3BuyerArticles() {
        Pageable topThree = PageRequest.of(0, 3);
        List<Object[]> results = orderRepository.findTop3MostFrequentBuyerArticles(topThree);
        return mapResults(results);
    }

    public List<Map<String, Object>> getTop3BuyerArticlesThisMonth() {
        Pageable topThree = PageRequest.of(0, 3);
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        List<Object[]> results = orderRepository.findTop3MostFrequentBuyerArticlesThisMonth(startOfMonth, endOfMonth, topThree);
        return mapResults(results);
    }

    public List<Map<String, Object>> getTop3BuyerArticlesThisYear() {
        int year = LocalDate.now().getYear();
        Pageable topThree = PageRequest.of(0, 3);
        List<Object[]> results = orderRepository.findTop3MostFrequentBuyerArticlesThisYear(year, topThree);
        return mapResults(results);
    }

    public List<Map<String, Object>> getTop3BuyerArticlesToday() {
        LocalDate today = LocalDate.now();
        Pageable topThree = PageRequest.of(0, 3);
        List<Object[]> results = orderRepository.findTop3MostFrequentBuyerArticlesToday(today, topThree);
        return mapResults(results);
    }

    public List<Map<String, Object>> getTop3BuyerArticlesThisWeek() {
        LocalDate today = LocalDate.now();
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));

        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(startOfWeek.plusDays(i));
        }

        Pageable topThree = PageRequest.of(0, 3);
        List<Object[]> results = orderRepository.findTop3MostFrequentBuyerArticlesThisWeek(weekDates, topThree);
        return mapResults(results);
    }

    private List<Map<String, Object>> mapResults(List<Object[]> results) {
        List<Map<String, Object>> topArticles = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("article", row[0]);
            entry.put("count", row[1]);
            topArticles.add(entry);
        }
        return topArticles;
    }

    // Orders This Week
    public Long getOrdersThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY); // Monday as the start of the week
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY); // Sunday as the end of the week, or today if it's earlier in the week

        return orderRepository.findOrdersBetween(startOfWeek, endOfWeek).stream().count();
    }

    public List<order> getOrdersForToday() {
        LocalDate today = LocalDate.now();
        return orderRepository.findByIssueDate(today);
    }

    public Map<String, Long> getMonthlyOrderCounts() {
        List<Object[]> result = orderRepository.getMonthlyOrderCounts();
        Map<String, Long> trend = new LinkedHashMap<>();
        for (Object[] row : result) {
            String month = (String) row[0];
            Long count = (Long) row[1];
            trend.put(month, count);
        }
        return trend;
    }

    public List<order> getOrdersForThisWeek() {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return orderRepository.findOrdersBetween(startOfWeek, endOfWeek);
    }

    // For This Month
    public List<order> getOrdersForThisMonth() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return orderRepository.findOrdersBetween(startOfMonth, endOfMonth);
    }

    public List<orderdto> getTop5LatestOrders() {
        Pageable top5 = PageRequest.of(0, 5);
        List<Object[]> orders = orderRepository.findTop3Orders(top5); // Note: Method name implies top 3, but pageable is 5

        // Map each Object[] to an OrderDTO
        return orders.stream()
                .map(order -> new orderdto(
                        (Long) order[0],         // o.id
                        (String) order[1],       // o.documentId
                        (String) order[2],       // o.description
                        (String) order[3],       // o.buyerArticleNumber
                        (String) order[4],       // o.documentNumber
                        (LocalDate) order[5]        // o.issueDate
                ))
                .collect(Collectors.toList());
    }

    // For This Year
    public List<order> getOrdersForThisYear() {
        LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
        LocalDate endOfYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        return orderRepository.findOrdersBetween(startOfYear, endOfYear);
    }

    public Long getOrdersToday() {
        LocalDate today = LocalDate.now();
        return orderRepository.findByIssueDate(today).stream().count();
    }

    public Long getTotalOrdersThisYear() {
        int currentYear = Year.now().getValue();
        return orderRepository.getTotalOrdersThisYear(currentYear);
    }

    public Long getTotalOrders() {
        return orderRepository.getTotalOrders();
    }

    public List<order> searchByDocumentId(String documentId) {
        return orderRepository.findByDocumentIdContainingIgnoreCase(documentId);
    }

    public List<order> searchByDocumentNumber(String documentnumber) {
        return orderRepository.findByDocumentNumberContainingIgnoreCase(documentnumber);
    }

    public List<order> searchByDescription(String description) {
        return orderRepository.findByDescriptionContainingIgnoreCase(description);
    }

    public List<order> searchByBuyerArticleNumber(String buyerArticleNumber) {
        return orderRepository.findByBuyerArticleNumberContainingIgnoreCase(buyerArticleNumber);
    }

    public List<order> searchByIssueDate(String partialDateString) {
        if (partialDateString == null || partialDateString.trim().isEmpty()) {
            log.warning("Partial date string for substring search cannot be empty.");
            return Collections.emptyList();
        }

        // IMPORTANT: UNCOMMENT THE LINE BELOW THAT CORRESPONDS TO YOUR DATABASE
        // AND COMMENT OUT THE OTHERS.




         return orderRepository.findByIssueDateContainingStringMySQL(partialDateString);

    }

    public List<order> searchByCalculationDate(String CalculationDate) {
        // Assuming CalculationDate string format is "yyyy-MM-dd"
        try {
            LocalDate date = LocalDate.parse(CalculationDate);
            return orderRepository.findByCalculationDate(date);
        } catch (Exception e) {
            log.warning("Invalid CalculationDate format: " + CalculationDate + ". Please use yyyy-MM-dd.");
            return Collections.emptyList();
        }
    }

    public List<order> searchByShipto(String Shipto) {
        return orderRepository.findByShiptoContainingIgnoreCase(Shipto);
    }

    public List<order> searchByInternaldestination(String Internaldestination) {
        return orderRepository.findByInternaldestinationContainingIgnoreCase(Internaldestination);
    }

    public List<order> searchByPlaceofdischarge(String Placeofdischarge) {
        return orderRepository.findByPlaceofdischargeContainingIgnoreCase(Placeofdischarge);
    }

    public Optional<order> getOrderById(Long Id) {
        return orderRepository.findById(Id);
    }

    public List<order> getAllOrders() {
        return orderRepository.findAll();
    }
}