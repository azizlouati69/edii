package com.example.Edi_dash.test;

import com.example.Edi_dash.DTO.orderdto;
import com.example.Edi_dash.sevice.orderservice;
import entities.model.client;
import entities.model.firmitem;
import entities.model.forecastitem;
import entities.model.order;
import entities.model.quantity;
import entities.model.seller;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import repository.clientrepository;
import repository.firmrepository;
import repository.forecastitemrepository;
import repository.orderrepository;
import repository.quantityrepository;
import repository.sellerrepository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class orderserviceTest {

    @Mock
    private orderrepository orderRepository;

    @Mock
    private quantityrepository quantityRepository;

    @Mock
    private forecastitemrepository forecastRepository;

    @Mock
    private firmrepository firmRepository;

    @Mock
    private sellerrepository sellerRepository;

    @Mock
    private clientrepository clientRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private orderservice orderService;

    private order testOrder;
    private client testClient;
    private seller testSeller;
    private quantity testQuantity;
    private firmitem testFirmItem;
    private forecastitem testForecastItem;

    @BeforeEach
    void setUp() {
        testClient = new client();
        testClient.setId(1L);
        testClient.setOrders(new ArrayList<>());

        testSeller = new seller();
        testSeller.setId(1L);
        testSeller.setOrders(new ArrayList<>());
        testSeller.setClients(new HashSet<>());

        testQuantity = new quantity();
        testQuantity.setId(1L);

        testFirmItem = new firmitem();
        testFirmItem.setId(1L);

        testForecastItem = new forecastitem();
        testForecastItem.setId(1L);

        testOrder = new order();
        testOrder.setId(1L);
        testOrder.setDocumentId("DOC123");
        testOrder.setDocumentNumber("NUM123");
        testOrder.setDescription("Test Order");
        testOrder.setBuyerArticleNumber("ART123");
        testOrder.setIssueDate(LocalDate.now());
        testOrder.setClient(testClient);
        testOrder.setSeller(testSeller);
        testOrder.setQuantity(testQuantity);
        testOrder.setFirmItems(new ArrayList<>(List.of(testFirmItem)));
        testOrder.setForecastItems(new ArrayList<>(List.of(testForecastItem)));
    }

    @Test
    void getOrderByDocumentId_ShouldReturnOrder_WhenFound() {
        when(orderRepository.findByDocumentId("DOC123")).thenReturn(List.of(testOrder));

        order result = orderService.getOrderByDocumentId("DOC123");

        assertNotNull(result);
        assertEquals("DOC123", result.getDocumentId());
        verify(orderRepository).findByDocumentId("DOC123");
    }

    @Test
    void getqById_ShouldReturnQuantity_WhenFound() {
        when(quantityRepository.findById(1L)).thenReturn(Optional.of(testQuantity));

        Optional<quantity> result = orderService.getqById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(quantityRepository).findById(1L);
    }

    @Test
    void deleteOrder_ShouldDeleteOrderAndRelatedEntities() {
        testClient.getOrders().add(testOrder);
        testSeller.getOrders().add(testOrder);
        testSeller.getClients().add(testClient);
        testClient.getSellers().add(testSeller);
        testFirmItem.setOrder(testOrder);
        testForecastItem.setOrder(testOrder);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        lenient().when(sellerRepository.save(any(seller.class))).thenReturn(testSeller);
        lenient().when(clientRepository.save(any(client.class))).thenReturn(testClient);
        doNothing().when(orderRepository).delete(any(order.class));
        doNothing().when(clientRepository).delete(any(client.class));
        doNothing().when(sellerRepository).delete(any(seller.class));

        orderService.deleteOrder(1L);

        verify(orderRepository).findById(1L);
        verify(orderRepository).delete(testOrder);
        verify(clientRepository).save(testClient);
        verify(sellerRepository).save(testSeller);
        verify(clientRepository).delete(testClient);
        verify(sellerRepository).delete(testSeller);
        verify(firmRepository, never()).delete(any(firmitem.class));
        verify(forecastRepository, never()).delete(any(forecastitem.class));

        assertTrue(testClient.getOrders().isEmpty());
        assertTrue(testSeller.getOrders().isEmpty());
        assertTrue(testSeller.getClients().isEmpty());
        assertTrue(testClient.getSellers().isEmpty());
        assertTrue(testOrder.getFirmItems().isEmpty());
        assertTrue(testOrder.getForecastItems().isEmpty());
        assertNull(testOrder.getQuantity());
    }

    @Test
    void deleteOrder_ShouldNotDeleteClientOrSeller_WhenTheyHaveOtherOrders() {
        order otherOrder = new order();
        otherOrder.setId(2L);
        testClient.getOrders().add(testOrder);
        testClient.getOrders().add(otherOrder);
        testSeller.getOrders().add(testOrder);
        testSeller.getOrders().add(otherOrder);
        testSeller.getClients().add(testClient);
        testClient.getSellers().add(testSeller);
        testFirmItem.setOrder(testOrder);
        testForecastItem.setOrder(testOrder);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).delete(any(order.class));

        orderService.deleteOrder(1L);

        verify(orderRepository).findById(1L);
        verify(orderRepository).delete(testOrder);
        verify(clientRepository, never()).save(any(client.class));
        verify(sellerRepository, never()).save(any(seller.class));
        verify(clientRepository, never()).delete(any(client.class));
        verify(sellerRepository, never()).delete(any(seller.class));
        verify(firmRepository, never()).delete(any(firmitem.class));
        verify(forecastRepository, never()).delete(any(forecastitem.class));

        assertEquals(1, testClient.getOrders().size());
        assertEquals(1, testSeller.getOrders().size());
        assertTrue(testOrder.getFirmItems().isEmpty());
        assertTrue(testOrder.getForecastItems().isEmpty());
        assertNull(testOrder.getQuantity());
    }

    @Test
    void deleteOrder_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.deleteOrder(1L));
        verify(orderRepository).findById(1L);
        verifyNoInteractions(clientRepository, sellerRepository, firmRepository, forecastRepository);
    }

    @Test
    void getOrdersThisMonth_ShouldReturnCount() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        when(orderRepository.findOrdersBetween(startOfMonth, endOfMonth)).thenReturn(List.of(testOrder));

        Long result = orderService.getOrdersThisMonth();

        assertEquals(1L, result);
        verify(orderRepository).findOrdersBetween(startOfMonth, endOfMonth);
    }

    @Test
    void getTop3BuyerArticles_ShouldReturnTopArticles() {
        Pageable topThree = PageRequest.of(0, 3);
        Object[] row = new Object[]{"ART123", 10L};
        List<Object[]> results = List.of(new Object[][]{row});
        when(orderRepository.findTop3MostFrequentBuyerArticles(topThree)).thenReturn(results);

        List<Map<String, Object>> topArticles = orderService.getTop3BuyerArticles();

        assertEquals(1, topArticles.size());
        assertEquals("ART123", topArticles.get(0).get("article"));
        assertEquals(10L, topArticles.get(0).get("count"));
        verify(orderRepository).findTop3MostFrequentBuyerArticles(topThree);
    }

    @Test
    void getTop3BuyerArticlesThisMonth_ShouldReturnTopArticles() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        Pageable topThree = PageRequest.of(0, 3);
        Object[] row = new Object[]{"ART123", 5L};
        List<Object[]> results = List.of(new Object[][]{row});
        when(orderRepository.findTop3MostFrequentBuyerArticlesThisMonth(startOfMonth, endOfMonth, topThree)).thenReturn(results);

        List<Map<String, Object>> topArticles = orderService.getTop3BuyerArticlesThisMonth();

        assertEquals(1, topArticles.size());
        assertEquals("ART123", topArticles.get(0).get("article"));
        assertEquals(5L, topArticles.get(0).get("count"));
        verify(orderRepository).findTop3MostFrequentBuyerArticlesThisMonth(startOfMonth, endOfMonth, topThree);
    }

    @Test
    void getTop3BuyerArticlesThisYear_ShouldReturnTopArticles() {
        int year = LocalDate.now().getYear();
        Pageable topThree = PageRequest.of(0, 3);
        Object[] row = new Object[]{"ART123", 15L};
        List<Object[]> results = List.of(new Object[][]{row});
        when(orderRepository.findTop3MostFrequentBuyerArticlesThisYear(year, topThree)).thenReturn(results);

        List<Map<String, Object>> topArticles = orderService.getTop3BuyerArticlesThisYear();

        assertEquals(1, topArticles.size());
        assertEquals("ART123", topArticles.get(0).get("article"));
        assertEquals(15L, topArticles.get(0).get("count"));
        verify(orderRepository).findTop3MostFrequentBuyerArticlesThisYear(year, topThree);
    }

    @Test
    void getTop3BuyerArticlesToday_ShouldReturnTopArticles() {
        LocalDate today = LocalDate.now();
        Pageable topThree = PageRequest.of(0, 3);
        Object[] row = new Object[]{"ART123", 3L};
        List<Object[]> results = List.of(new Object[][]{row});
        when(orderRepository.findTop3MostFrequentBuyerArticlesToday(today, topThree)).thenReturn(results);

        List<Map<String, Object>> topArticles = orderService.getTop3BuyerArticlesToday();

        assertEquals(1, topArticles.size());
        assertEquals("ART123", topArticles.get(0).get("article"));
        assertEquals(3L, topArticles.get(0).get("count"));
        verify(orderRepository).findTop3MostFrequentBuyerArticlesToday(today, topThree);
    }

    @Test
    void getTop3BuyerArticlesThisWeek_ShouldReturnTopArticles() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(startOfWeek.plusDays(i));
        }
        Pageable topThree = PageRequest.of(0, 3);
        Object[] row = new Object[]{"ART123", 7L};
        List<Object[]> results = List.of(new Object[][]{row});
        when(orderRepository.findTop3MostFrequentBuyerArticlesThisWeek(weekDates, topThree)).thenReturn(results);

        List<Map<String, Object>> topArticles = orderService.getTop3BuyerArticlesThisWeek();

        assertEquals(1, topArticles.size());
        assertEquals("ART123", topArticles.get(0).get("article"));
        assertEquals(7L, topArticles.get(0).get("count"));
        verify(orderRepository).findTop3MostFrequentBuyerArticlesThisWeek(weekDates, topThree);
    }

    @Test
    void getOrdersThisWeek_ShouldReturnCount() {
        LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = LocalDate.now().with(java.time.DayOfWeek.SUNDAY);
        when(orderRepository.findOrdersBetween(startOfWeek, endOfWeek)).thenReturn(List.of(testOrder));

        Long result = orderService.getOrdersThisWeek();

        assertEquals(1L, result);
        verify(orderRepository).findOrdersBetween(startOfWeek, endOfWeek);
    }

    @Test
    void getOrdersForToday_ShouldReturnOrders() {
        LocalDate today = LocalDate.now();
        when(orderRepository.findByIssueDate(today)).thenReturn(List.of(testOrder));

        List<order> result = orderService.getOrdersForToday();

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByIssueDate(today);
    }

    @Test
    void getMonthlyOrderCounts_ShouldReturnTrend() {
        Object[] row = new Object[]{"2025-01", 10L};
        List<Object[]> results = List.of(new Object[][]{row});
        when(orderRepository.getMonthlyOrderCounts()).thenReturn(results);

        Map<String, Long> trend = orderService.getMonthlyOrderCounts();

        assertEquals(1, trend.size());
        assertEquals(10L, trend.get("2025-01"));
        verify(orderRepository).getMonthlyOrderCounts();
    }

    @Test
    void getOrdersForThisWeek_ShouldReturnOrders() {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        when(orderRepository.findOrdersBetween(startOfWeek, endOfWeek)).thenReturn(List.of(testOrder));

        List<order> result = orderService.getOrdersForThisWeek();

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findOrdersBetween(startOfWeek, endOfWeek);
    }

    @Test
    void getOrdersForThisMonth_ShouldReturnOrders() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        when(orderRepository.findOrdersBetween(startOfMonth, endOfMonth)).thenReturn(List.of(testOrder));

        List<order> result = orderService.getOrdersForThisMonth();

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findOrdersBetween(startOfMonth, endOfMonth);
    }

    @Test
    void getTop5LatestOrders_ShouldReturnOrders() {
        Pageable top5 = PageRequest.of(0, 5);
        Object[] row = new Object[]{1L, "DOC123", "Test Order", "ART123", "NUM123", LocalDate.now()};
        List<Object[]> results = List.of(new Object[][]{row});
        when(orderRepository.findTop3Orders(top5)).thenReturn(results);

        List<orderdto> dtos = orderService.getTop5LatestOrders();

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("DOC123", dtos.get(0).getDocumentId());
        assertEquals("Test Order", dtos.get(0).getDescription());
        assertEquals("ART123", dtos.get(0).getBuyerArticleNumber());
        assertEquals("NUM123", dtos.get(0).getDocumentNumber());
        verify(orderRepository).findTop3Orders(top5);
    }

    @Test
    void getOrdersForThisYear_ShouldReturnOrders() {
        LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
        LocalDate endOfYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        when(orderRepository.findOrdersBetween(startOfYear, endOfYear)).thenReturn(List.of(testOrder));

        List<order> result = orderService.getOrdersForThisYear();

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findOrdersBetween(startOfYear, endOfYear);
    }

    @Test
    void getOrdersToday_ShouldReturnCount() {
        LocalDate today = LocalDate.now();
        when(orderRepository.findByIssueDate(today)).thenReturn(List.of(testOrder));

        Long result = orderService.getOrdersToday();

        assertEquals(1L, result);
        verify(orderRepository).findByIssueDate(today);
    }

    @Test
    void getTotalOrdersThisYear_ShouldReturnCount() {
        int currentYear = LocalDate.now().getYear();
        when(orderRepository.getTotalOrdersThisYear(currentYear)).thenReturn(100L);

        Long result = orderService.getTotalOrdersThisYear();

        assertEquals(100L, result);
        verify(orderRepository).getTotalOrdersThisYear(currentYear);
    }

    @Test
    void getTotalOrders_ShouldReturnCount() {
        when(orderRepository.getTotalOrders()).thenReturn(1000L);

        Long result = orderService.getTotalOrders();

        assertEquals(1000L, result);
        verify(orderRepository).getTotalOrders();
    }

    @Test
    void searchByDocumentId_ShouldReturnMatchingOrders() {
        when(orderRepository.findByDocumentIdContainingIgnoreCase("doc")).thenReturn(List.of(testOrder));

        List<order> result = orderService.searchByDocumentId("doc");

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByDocumentIdContainingIgnoreCase("doc");
    }

    @Test
    void searchByDocumentNumber_ShouldReturnMatchingOrders() {
        when(orderRepository.findByDocumentNumberContainingIgnoreCase("num")).thenReturn(List.of(testOrder));

        List<order> result = orderService.searchByDocumentNumber("num");

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByDocumentNumberContainingIgnoreCase("num");
    }

    @Test
    void searchByDescription_ShouldReturnMatchingOrders() {
        when(orderRepository.findByDescriptionContainingIgnoreCase("test")).thenReturn(List.of(testOrder));

        List<order> result = orderService.searchByDescription("test");

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByDescriptionContainingIgnoreCase("test");
    }

    @Test
    void searchByBuyerArticleNumber_ShouldReturnMatchingOrders() {
        when(orderRepository.findByBuyerArticleNumberContainingIgnoreCase("art")).thenReturn(List.of(testOrder));

        List<order> result = orderService.searchByBuyerArticleNumber("art");

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByBuyerArticleNumberContainingIgnoreCase("art");
    }

    @Test
    void searchByIssueDate_ShouldReturnMatchingOrders() {
        when(orderRepository.findByIssueDateContainingStringMySQL("2025")).thenReturn(List.of(testOrder));

        List<order> result = orderService.searchByIssueDate("2025");

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByIssueDateContainingStringMySQL("2025");
    }

    @Test
    void searchByIssueDate_ShouldReturnEmptyList_WhenInputEmpty() {
        List<order> result = orderService.searchByIssueDate("");

        assertTrue(result.isEmpty());
        verifyNoInteractions(orderRepository);
    }

    @Test
    void searchByCalculationDate_ShouldReturnMatchingOrders() {
        String dateStr = "2025-06-03";
        LocalDate date = LocalDate.parse(dateStr);
        when(orderRepository.findByCalculationDate(date)).thenReturn(List.of(testOrder));

        List<order> result = orderService.searchByCalculationDate(dateStr);

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByCalculationDate(date);
    }

    @Test
    void searchByCalculationDate_ShouldReturnEmptyList_WhenInvalidDate() {
        List<order> result = orderService.searchByCalculationDate("invalid");

        assertTrue(result.isEmpty());
        verifyNoInteractions(orderRepository);
    }

    @Test
    void searchByShipto_ShouldReturnMatchingOrders() {
        when(orderRepository.findByShiptoContainingIgnoreCase("ship")).thenReturn(List.of(testOrder));

        List<order> result = orderService.searchByShipto("ship");

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByShiptoContainingIgnoreCase("ship");
    }

    @Test
    void searchByInternaldestination_ShouldReturnMatchingOrders() {
        when(orderRepository.findByInternaldestinationContainingIgnoreCase("internal")).thenReturn(List.of(testOrder));

        List<order> result = orderService.searchByInternaldestination("internal");

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByInternaldestinationContainingIgnoreCase("internal");
    }

    @Test
    void searchByPlaceofdischarge_ShouldReturnMatchingOrders() {
        when(orderRepository.findByPlaceofdischargeContainingIgnoreCase("discharge")).thenReturn(List.of(testOrder));

        List<order> result = orderService.searchByPlaceofdischarge("discharge");

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByPlaceofdischargeContainingIgnoreCase("discharge");
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Optional<order> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(testOrder, result.get());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));

        List<order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findAll();
    }
}