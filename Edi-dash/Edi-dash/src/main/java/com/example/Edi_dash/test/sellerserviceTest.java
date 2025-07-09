package com.example.Edi_dash.test;


import com.example.Edi_dash.sevice.sellerservice;
import entities.model.client;
import entities.model.order;
import entities.model.seller;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.clientrepository;
import repository.orderrepository;
import repository.sellerrepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class sellerserviceTest {

    @Mock
    private sellerrepository sellerRepository;

    @Mock
    private orderrepository orderRepository;

    @Mock
    private clientrepository clientRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private sellerservice sellerService;

    private seller seller;
    private client client;
    private order order;

    @BeforeEach
    void setUp() {
        seller = new seller();
        seller.setId(1L);
        seller.setReceiverId("REC123");
        seller.setClients(new HashSet<>());
        seller.setOrders(new ArrayList<>());

        client = new client();
        client.setId(1L);
        client.setSellers(new HashSet<>(List.of(seller)));
        client.setOrders(new ArrayList<>());
        seller.getClients().add(client);

        order = new order();
        order.setId(1L);
        order.setSeller(seller);
        order.setClient(client);
        seller.getOrders().add(order);
        client.getOrders().add(order);
    }

    @Test
    void getAllSellers_ReturnsAllSellers() {
        // Arrange
        List<seller> sellers = List.of(seller);
        when(sellerRepository.findAll()).thenReturn(sellers);

        // Act
        List<seller> result = sellerService.getAllSellers();

        // Assert
        assertEquals(1, result.size());
        assertEquals(seller, result.get(0));
        verify(sellerRepository).findAll();
    }

    @Test
    void searchSellerByReceiverId_ValidReceiverId_ReturnsMatchingSellers() {
        // Arrange
        String receiverId = "REC123";
        List<seller> sellers = List.of(seller);
        when(sellerRepository.findByReceiverIdContainingIgnoreCase(receiverId)).thenReturn(sellers);

        // Act
        List<seller> result = sellerService.searchSellerByReceiverId(receiverId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(seller, result.get(0));
        verify(sellerRepository).findByReceiverIdContainingIgnoreCase(receiverId);
    }

    @Test
    void searchSellerByReceiverId_EmptyReceiverId_ReturnsEmptyList() {
        // Arrange
        String receiverId = "";
        when(sellerRepository.findByReceiverIdContainingIgnoreCase(receiverId)).thenReturn(Collections.emptyList());

        // Act
        List<seller> result = sellerService.searchSellerByReceiverId(receiverId);

        // Assert
        assertTrue(result.isEmpty());
        verify(sellerRepository).findByReceiverIdContainingIgnoreCase(receiverId);
    }

    @Test
    void getSellerById_ExistingId_ReturnsSeller() {
        // Arrange
        Long id = 1L;
        when(sellerRepository.findById(id)).thenReturn(Optional.of(seller));

        // Act
        Optional<seller> result = sellerService.getSellerById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(seller, result.get());
        verify(sellerRepository).findById(id);
    }

    @Test
    void getSellerById_NonExistingId_ReturnsEmpty() {
        // Arrange
        Long id = 999L;
        when(sellerRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<seller> result = sellerService.getSellerById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(sellerRepository).findById(id);
    }

    @Test
    void getSellerByReceiverId_ValidReceiverId_ReturnsSeller() {
        // Arrange
        String receiverId = "REC123";
        when(sellerRepository.findByReceiverId(receiverId)).thenReturn(List.of(seller));

        // Act
        seller result = sellerService.getSellerByReceiverId(receiverId);

        // Assert
        assertEquals(seller, result);
        verify(sellerRepository).findByReceiverId(receiverId);
    }

    @Test
    void getSellerByReceiverId_EmptyReceiverId_ThrowsIndexOutOfBoundsException() {
        // Arrange
        String receiverId = "";
        when(sellerRepository.findByReceiverId(receiverId)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(IndexOutOfBoundsException.class, () -> sellerService.getSellerByReceiverId(receiverId));
        verify(sellerRepository).findByReceiverId(receiverId);
    }

    @Test
    void getSellerByReceiverId_NonExistingReceiverId_ThrowsIndexOutOfBoundsException() {
        // Arrange
        String receiverId = "NONEXISTENT";
        when(sellerRepository.findByReceiverId(receiverId)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(IndexOutOfBoundsException.class, () -> sellerService.getSellerByReceiverId(receiverId));
        verify(sellerRepository).findByReceiverId(receiverId);
    }

    @Test
    void deleteSeller_ExistingSellerWithClientsAndOrders_DeletesSuccessfully() {
        // Arrange
        Long sellerId = 1L;
        when(sellerRepository.findById(sellerId)).thenReturn(Optional.of(seller));

        // Act
        sellerService.deleteSeller(sellerId);

        // Assert
        verify(sellerRepository).findById(sellerId);
        verify(sellerRepository).delete(seller);
        verify(orderRepository).delete(any(order.class));
        verify(clientRepository).delete(client);
        assertTrue(seller.getClients().isEmpty());
        assertTrue(seller.getOrders().isEmpty());
        assertTrue(client.getSellers().isEmpty());
        assertNull(order.getSeller());
    }

    @Test
    void deleteSeller_NonExistingSeller_ThrowsRuntimeException() {
        // Arrange
        Long sellerId = 999L;
        when(sellerRepository.findById(sellerId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> sellerService.deleteSeller(sellerId));
        assertEquals("Seller not found", exception.getMessage());
        verify(sellerRepository).findById(sellerId);
        verifyNoMoreInteractions(sellerRepository, orderRepository, clientRepository);
    }



    @Test
    void deleteSeller_SellerWithNoClientsAndOrders_DeletesSuccessfully() {
        // Arrange
        Long sellerId = 1L;
        seller.setClients(new HashSet<>());
        seller.setOrders(new ArrayList<>());
        when(sellerRepository.findById(sellerId)).thenReturn(Optional.of(seller));

        // Act
        sellerService.deleteSeller(sellerId);

        // Assert
        verify(sellerRepository).findById(sellerId);
        verify(sellerRepository).delete(seller);
        verifyNoInteractions(orderRepository, clientRepository);
        assertTrue(seller.getClients().isEmpty());
        assertTrue(seller.getOrders().isEmpty());
    }
}