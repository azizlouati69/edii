package com.example.Edi_dash.test;

import com.example.Edi_dash.DTO.clientdto;
import com.example.Edi_dash.DTO.recentclientdto;
import com.example.Edi_dash.sevice.clientservice;
import entities.model.client;
import entities.model.order;
import entities.model.seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import repository.clientrepository;
import repository.orderrepository;
import repository.sellerrepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class clientserviceTest {

    @Mock
    private clientrepository clientRepository;

    @Mock
    private orderrepository orderRepository;

    @Mock
    private sellerrepository sellerRepository;

    @InjectMocks
    private clientservice clientService;

    private client testClient;
    private seller testSeller;
    private order testOrder;

    @BeforeEach
    void setUp() {
        testClient = new client();
        testClient.setId(1L);
        testClient.setSenderId("SENDER123");
        testClient.setBuyerIdentifier("BUYER123");

        testSeller = new seller();
        testSeller.setId(1L);
        testSeller.setClients(new HashSet<>());
        testSeller.setOrders(new ArrayList<>());

        testOrder = new order();
        testOrder.setId(1L);
        testOrder.setClient(testClient);
    }

    @Test
    void getClientByBuyerIdentifier_ShouldReturnClient_WhenFound() {
        when(clientRepository.findByBuyerIdentifier("BUYER123")).thenReturn(List.of(testClient));

        client result = clientService.getClientByBuyerIdentifier("BUYER123");

        assertNotNull(result);
        assertEquals("BUYER123", result.getBuyerIdentifier());
        verify(clientRepository).findByBuyerIdentifier("BUYER123");
    }

    @Test
    void getClientBySenderId_ShouldReturnClient_WhenFound() {
        when(clientRepository.findBySenderId("SENDER123")).thenReturn(List.of(testClient));

        client result = clientService.getClientBySenderId("SENDER123");

        assertNotNull(result);
        assertEquals("SENDER123", result.getSenderId());
        verify(clientRepository).findBySenderId("SENDER123");
    }

    @Test
    void getClientById_ShouldReturnOptionalClient_WhenFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        Optional<client> result = clientService.getClientById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(clientRepository).findById(1L);
    }

    @Test
    void getAllClients_ShouldReturnListOfClients() {
        List<client> clients = List.of(testClient);
        when(clientRepository.findAll()).thenReturn(clients);

        List<client> result = clientService.getAllClients();

        assertEquals(1, result.size());
        assertEquals(testClient, result.get(0));
        verify(clientRepository).findAll();
    }

    @Test
    void searchBySenderId_ShouldReturnMatchingClients() {
        List<client> clients = List.of(testClient);
        when(clientRepository.findBySenderIdContainingIgnoreCase("sender")).thenReturn(clients);

        List<client> result = clientService.searchBySenderId("sender");

        assertEquals(1, result.size());
        assertEquals(testClient, result.get(0));
        verify(clientRepository).findBySenderIdContainingIgnoreCase("sender");
    }

    @Test
    void searchByBuyerIdentifier_ShouldReturnMatchingClients() {
        List<client> clients = List.of(testClient);
        when(clientRepository.findByBuyerIdentifierContainingIgnoreCase("buyer")).thenReturn(clients);

        List<client> result = clientService.searchByBuyerIdentifier("buyer");

        assertEquals(1, result.size());
        assertEquals(testClient, result.get(0));
        verify(clientRepository).findByBuyerIdentifierContainingIgnoreCase("buyer");
    }

    @Test
    void getTotalClients_ShouldReturnCount() {
        when(clientRepository.countAllClients()).thenReturn(10L);

        Long result = clientService.getTotalClients();

        assertEquals(10L, result);
        verify(clientRepository).countAllClients();
    }

    @Test
    void getActiveClients_ShouldReturnCountForCurrentMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        when(clientRepository.countActiveClientsThisMonth(startOfMonth, endOfMonth)).thenReturn(5L);

        Long result = clientService.getActiveClients();

        assertEquals(5L, result);
        verify(clientRepository).countActiveClientsThisMonth(startOfMonth, endOfMonth);
    }

    @Test
    void getInactiveClients_ShouldReturnCountForCurrentMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        when(clientRepository.countInactiveClientsThisMonth(startOfMonth, endOfMonth)).thenReturn(3L);

        Long result = clientService.getInactiveClients();

        assertEquals(3L, result);
        verify(clientRepository).countInactiveClientsThisMonth(startOfMonth, endOfMonth);
    }

    @Test
    void getAverageOrdersPerClient_ShouldReturnRoundedAverage() {
        when(clientRepository.averageOrdersPerClient()).thenReturn(5.666);
        Double result = clientService.getAverageOrdersPerClient();
        assertEquals(5.67, result);
        verify(clientRepository).averageOrdersPerClient();
    }

    @Test
    void getAverageOrdersPerClient_ShouldReturnZero_WhenNull() {
        when(clientRepository.averageOrdersPerClient()).thenReturn(null);

        Double result = clientService.getAverageOrdersPerClient();

        assertEquals(0.0, result);
        verify(clientRepository).averageOrdersPerClient();
    }

    @Test
    void getTop3ClientsThisMonth_ShouldReturnTopClients() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        Object[] row = new Object[]{1L, "SENDER123", "BUYER123", 10};
        List<Object[]> result = List.of(new Object[][]{row});
        when(clientRepository.findTopClientsByOrderCountThisMonth(startOfMonth, endOfMonth)).thenReturn(result);

        List<clientdto> dtos = clientService.getTop3ClientsThisMonth();

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("SENDER123", dtos.get(0).getSenderId());
        assertEquals("BUYER123", dtos.get(0).getBuyerIdentifier());
        assertEquals(10, dtos.get(0).getOrderCount());
        verify(clientRepository).findTopClientsByOrderCountThisMonth(startOfMonth, endOfMonth);
    }

    @Test
    void getTop3ClientsThisYear_ShouldReturnTopClients() {
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = now.withDayOfYear(1);
        LocalDate endOfYear = now.withMonth(12).withDayOfMonth(31);
        Object[] row = new Object[]{1L, "SENDER123", "BUYER123", 15};
        List<Object[]> result = List.of(new Object[][]{row});
        when(clientRepository.findTopClientsByOrderCountThisYear(startOfYear, endOfYear)).thenReturn(result);

        List<clientdto> dtos = clientService.getTop3ClientsThisYear();

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("SENDER123", dtos.get(0).getSenderId());
        assertEquals("BUYER123", dtos.get(0).getBuyerIdentifier());
        assertEquals(15, dtos.get(0).getOrderCount());
        verify(clientRepository).findTopClientsByOrderCountThisYear(startOfYear, endOfYear);
    }

    @Test
    void getTop3ClientsByOrderCountToday_ShouldReturnTopClients() {
        LocalDate today = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 3);
        Object[] row = new Object[]{1L, "SENDER123", "BUYER123", 5};
        List<Object[]> result = List.of(new Object[][]{row});
        when(clientRepository.findTop3ClientsByOrderCountToday(today, pageable)).thenReturn(result);

        List<clientdto> dtos = clientService.getTop3ClientsByOrderCountToday();

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("SENDER123", dtos.get(0).getSenderId());
        assertEquals("BUYER123", dtos.get(0).getBuyerIdentifier());
        assertEquals(5, dtos.get(0).getOrderCount());
        verify(clientRepository).findTop3ClientsByOrderCountToday(today, pageable);
    }

    @Test
    void getTop3ClientsThisWeek_ShouldReturnTopClients() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
        Object[] row = new Object[]{1L, "SENDER123", "BUYER123", 8};
        List<Object[]> result = List.of(new Object[][]{row});
        when(clientRepository.findTopClientsByOrderCountThisWeek(startOfWeek, endOfWeek)).thenReturn(result);

        List<clientdto> dtos = clientService.getTop3ClientsThisWeek();

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("SENDER123", dtos.get(0).getSenderId());
        assertEquals("BUYER123", dtos.get(0).getBuyerIdentifier());
        assertEquals(8, dtos.get(0).getOrderCount());
        verify(clientRepository).findTopClientsByOrderCountThisWeek(startOfWeek, endOfWeek);
    }

    @Test
    void getTopClients_ShouldReturnTopClients() {
        Pageable topThree = PageRequest.of(0, 3);
        Object[] row = new Object[]{1L, "SENDER123", "BUYER123", 20};
        List<Object[]> result = List.of(new Object[][]{row});
        when(clientRepository.findTopClientsByOrderCount(topThree)).thenReturn(result);

        List<clientdto> dtos = clientService.getTopClients();

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("SENDER123", dtos.get(0).getSenderId());
        assertEquals("BUYER123", dtos.get(0).getBuyerIdentifier());
        assertEquals(20, dtos.get(0).getOrderCount());
        verify(clientRepository).findTopClientsByOrderCount(topThree);
    }

    @Test
    void getTopClientsByRecentOrders_ShouldReturnRecentClients() {
        Object[] row = new Object[]{1L, "SENDER123", "BUYER123", LocalDate.now()};
        List<Object[]> results = List.of(new Object[][]{row});
        when(clientRepository.findTopClientsByRecentOrders(any())).thenReturn(results);

        List<recentclientdto> dtos = clientService.getTopClientsByRecentOrders();

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("SENDER123", dtos.get(0).getSenderId());
        assertEquals("BUYER123", dtos.get(0).getBuyerIdentifier());
        verify(clientRepository).findTopClientsByRecentOrders(any());
    }

    @Test
    void deleteClient_ShouldDeleteClientAndRelatedEntities() {
        testClient.setSellers(new HashSet<>(List.of(testSeller)));
        testClient.setOrders(new ArrayList<>(List.of(testOrder)));
        testSeller.getClients().add(testClient);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(sellerRepository.save(any(seller.class))).thenReturn(testSeller);
        doNothing().when(orderRepository).delete(any(order.class));
        doNothing().when(clientRepository).delete(any(client.class));
        doNothing().when(sellerRepository).delete(any(seller.class));

        clientService.deleteClient(1L);

        verify(clientRepository).findById(1L);
        verify(sellerRepository).save(testSeller);
        verify(orderRepository).delete(testOrder);
        verify(clientRepository).delete(testClient);
        verify(sellerRepository).delete(testSeller);
        assertTrue(testSeller.getClients().isEmpty());
        assertTrue(testClient.getOrders().isEmpty());
        assertTrue(testClient.getSellers().isEmpty());
    }

    @Test
    void deleteClient_ShouldThrowException_WhenClientNotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clientService.deleteClient(1L));
        verify(clientRepository).findById(1L);
        verifyNoInteractions(sellerRepository, orderRepository);
    }
}