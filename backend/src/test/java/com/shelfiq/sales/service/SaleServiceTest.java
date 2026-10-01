package com.shelfiq.sales.service;

import com.shelfiq.common.context.TenantContext;
import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.InsufficientStockException;
import com.shelfiq.inventory.entity.Inventory;
import com.shelfiq.inventory.entity.InventoryMovement;
import com.shelfiq.inventory.repository.InventoryMovementRepository;
import com.shelfiq.inventory.repository.InventoryRepository;
import com.shelfiq.product.entity.Product;
import com.shelfiq.product.repository.ProductRepository;
import com.shelfiq.sales.dto.SaleCheckoutRequestDto;
import com.shelfiq.sales.dto.SaleItemRequestDto;
import com.shelfiq.sales.dto.SaleResponseDto;
import com.shelfiq.sales.entity.SaleTransaction;
import com.shelfiq.sales.repository.SaleItemRepository;
import com.shelfiq.sales.repository.SaleTransactionRepository;
import com.shelfiq.user.entity.User;
import com.shelfiq.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleTransactionRepository saleTransactionRepository;
    @Mock
    private SaleItemRepository saleItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementRepository inventoryMovementRepository;
    @Mock
    private UserRepository userRepository;

    private SaleService saleService;

    @BeforeEach
    void setUp() {
        saleService = new SaleService(
                saleTransactionRepository,
                saleItemRepository,
                productRepository,
                inventoryRepository,
                inventoryMovementRepository,
                userRepository
        );
        TenantContextHolder.setContext(new TenantContext(1L, "cashier@shelfiq.io", 100L, Set.of("ROLE_EMPLOYEE")));
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void checkout_ValidCart_DeductsInventoryAndCompletesSale() {
        Product product = new Product(100L, "BEV-COKE-500", "8901234567890", "Coca-Cola 500ml", null, null, null,
                new BigDecimal("40.00"), new BigDecimal("32.00"), 10, 20, null, 2, "PCS");
        product.setId(5L);

        Inventory inventory = new Inventory(100L, product, 30);
        User user = new User("cashier@shelfiq.io", "pass", "Cashier A", "+91 9999999999");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findByIdAndStoreId(5L, 100L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductIdAndStoreId(5L, 100L)).thenReturn(Optional.of(inventory));
        when(saleTransactionRepository.save(any(SaleTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SaleCheckoutRequestDto dto = new SaleCheckoutRequestDto();
        dto.setPaymentMethod(SaleTransaction.PaymentMethod.UPI);
        dto.setCustomerName("Rahul Kumar");
        dto.setCustomerPhone("+91 9888877777");
        dto.setItems(List.of(new SaleItemRequestDto(5L, 3, new BigDecimal("40.00"), BigDecimal.ZERO)));

        SaleResponseDto response = saleService.checkout(dto);

        assertNotNull(response);
        assertEquals(27, inventory.getCurrentStock()); // 30 - 3
        verify(inventoryRepository, times(1)).save(inventory);
        verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        verify(saleTransactionRepository, times(1)).save(any(SaleTransaction.class));
    }

    @Test
    void checkout_InsufficientStock_ThrowsException() {
        Product product = new Product(100L, "BEV-COKE-500", "8901234567890", "Coca-Cola 500ml", null, null, null,
                new BigDecimal("40.00"), new BigDecimal("32.00"), 10, 20, null, 2, "PCS");
        product.setId(5L);

        Inventory inventory = new Inventory(100L, product, 2);

        when(productRepository.findByIdAndStoreId(5L, 100L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductIdAndStoreId(5L, 100L)).thenReturn(Optional.of(inventory));

        SaleCheckoutRequestDto dto = new SaleCheckoutRequestDto();
        dto.setPaymentMethod(SaleTransaction.PaymentMethod.CASH);
        dto.setItems(List.of(new SaleItemRequestDto(5L, 5, new BigDecimal("40.00"), BigDecimal.ZERO)));

        assertThrows(InsufficientStockException.class, () -> saleService.checkout(dto));
        verify(inventoryRepository, never()).save(any());
        verify(saleTransactionRepository, never()).save(any());
    }
}
