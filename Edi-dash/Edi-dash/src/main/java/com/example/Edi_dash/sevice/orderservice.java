package com.example.Edi_dash.sevice;
import com.example.Edi_dash.DTO.orderdto;
import entities.model.order;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import  repository.orderrepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class orderservice {
    private final orderrepository orderRepository;

    public orderservice(orderrepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public  order  getOrderByDocumentId(String  docId) {
        return orderRepository.findByDocumentId(docId).get(0);

    }
    public Long getOrdersThisMonth() {
        List<order> allOrders = orderRepository.findAll();

        return allOrders.stream()
                .filter(o -> {
                    try {
                        String rawDate = o.getIssueDate();
                        // Parse from custom string
                        LocalDate date = LocalDate.parse(rawDate.substring(0, 10)); // yyyy-MM-dd

                        YearMonth orderMonth = YearMonth.from(date);
                        YearMonth currentMonth = YearMonth.now();

                        return orderMonth.equals(currentMonth);
                    } catch (Exception e) {
                        return false; // skip malformed dates
                    }
                })
                .count();
    }
    public List<Map<String, Object>> getTop3BuyerArticles() {
        Pageable topThree = PageRequest.of(0, 3);
        List<Object[]> results = orderRepository.findTop3MostFrequentBuyerArticles(topThree);

        List<Map<String, Object>> topArticles = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("article", row[0]);
            entry.put("count", row[1]);
            topArticles.add(entry);
        }
        return topArticles;
    }
    public List<Map<String, Object>> getTop3BuyerArticlesThisMonth() {
        Pageable topThree = PageRequest.of(0, 3);

        // Extract "yyyy-MM" from current date
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // Fetch top 3 for current month
        List<Object[]> results = orderRepository.findTop3MostFrequentBuyerArticles(currentMonth, topThree);

        List<Map<String, Object>> topArticles = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("article", row[0]);
            entry.put("count", row[1]);
            topArticles.add(entry);
        }
        return topArticles;
    }
    public List<Map<String, Object>> getTop3BuyerArticlesThisYear() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        Pageable topThree = PageRequest.of(0, 3);
        List<Object[]> results = orderRepository.findTop3MostFrequentBuyerArticlesThisYear(year, topThree);
        return mapResults(results);
    }

    public List<Map<String, Object>> getTop3BuyerArticlesToday() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Pageable topThree = PageRequest.of(0, 3);
        List<Object[]> results = orderRepository.findTop3MostFrequentBuyerArticlesToday(today, topThree);
        return mapResults(results);
    }

    public List<Map<String, Object>> getTop3BuyerArticlesThisWeek() {
        LocalDate today = LocalDate.now();
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));

        List<String> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(startOfWeek.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
        List<order> allOrders = orderRepository.findAll();

        LocalDate today = LocalDate.now();
        // Monday as the start of the week
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);

        return allOrders.stream()
                .filter(o -> {
                    try {
                        String rawDate = o.getIssueDate();
                        LocalDate orderDate = LocalDate.parse(rawDate.substring(0, 10)); // "yyyy-MM-dd"
                        return !orderDate.isBefore(startOfWeek) && !orderDate.isAfter(today);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();
    }
    public List<order> getOrdersForToday() {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = todayStart.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHHmm");
        String startDate = todayStart.format(formatter);
        String endDate = todayEnd.format(formatter);

        return orderRepository.findOrdersBetween(startDate, endDate);
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
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY)).atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHHmm");
        String startDate = startOfWeek.format(formatter);
        String endDate = endOfWeek.format(formatter);

        return orderRepository.findOrdersBetween(startDate, endDate);
    }

    // For This Month
    public List<order> getOrdersForThisMonth() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHHmm");
        String startDate = startOfMonth.format(formatter);
        String endDate = endOfMonth.format(formatter);

        return orderRepository.findOrdersBetween(startDate, endDate);
    }
    public List<orderdto> getTop5LatestOrders() {
        Pageable top5 = PageRequest.of(0, 5);  // Page 0, with size of 3
        List<Object[]> orders = orderRepository.findTop3Orders(top5);

        // Map each Object[] to an OrderDTO
        return orders.stream()
                .map(order -> new orderdto(
                        (Long) order[0],                     // o.id
                        (String) order[1],                   // o.documentId
                        (String) order[2],                   // o.description
                        (String) order[3],                   // o.buyerArticleNumber
                        (String) order[4],                   // o.documentNumber
                        (String) order[5]             // o.issueDate
                ))
                .collect(Collectors.toList());
    }
    // For This Year
    public List<order> getOrdersForThisYear() {
        LocalDateTime startOfYear = LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime endOfYear = startOfYear.plusYears(1).minusSeconds(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHHmm");
        String startDate = startOfYear.format(formatter);
        String endDate = endOfYear.format(formatter);

        return orderRepository.findOrdersBetween(startDate, endDate);
    }

    public Long getOrdersToday() {
        List<order> allOrders = orderRepository.findAll();

        LocalDate today = LocalDate.now();

        return allOrders.stream()
                .filter(o -> {
                    try {
                        String rawDate = o.getIssueDate();
                        LocalDate orderDate = LocalDate.parse(rawDate.substring(0, 10)); // yyyy-MM-dd
                        return orderDate.equals(today);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();
    }
    public Long getTotalOrdersThisYear() {
        String currentYear = String.valueOf(java.time.Year.now().getValue());
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
    public List<order> searchByIssueDate(String IssueDate) {
        return orderRepository.findByIssueDateContainingIgnoreCase(IssueDate);
    }
    public List<order> searchByCalculationDate(String CalculationDate) {
        return orderRepository.findByCalculationDateContainingIgnoreCase(CalculationDate);
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
    public Optional<order> getOrderById(Long  Id) {
        return orderRepository.findById(Id);

    }
    public List<order> getAllOrders() {
        return orderRepository.findAll();
    }
}
