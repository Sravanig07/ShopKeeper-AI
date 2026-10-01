package com.shelfiq.inventory.service;

import com.shelfiq.common.context.TenantContext;
import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.InsufficientStockException;
import com.shelfiq.inventory.dto.StockAdjustmentDto;
import com.shelfiq.inventory.entity.Inventory;
import com.shelfiq.inventory.entity.InventoryMovement;
import com.shelfiq.inventory.repository.InventoryMovementRepository;
import com.shelfiq.inventory.repository.InventoryRepository;
import com.shelfiq.product.dto.ProductResponseDto;
import com.shelfiq.product.entity.Product;
import com.shelfiq.product.repository.ProductRepository;
import com.shelfiq.product.service.ProductService;
import com.shelfiq.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementRepository inventoryMovementRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductService productService;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(
                inventoryRepository,
                inventoryMovementRepository,
                productRepository,
                userRepository,
                productService
        );
        TenantContextHolder.setContext(new TenantContext(1L, "owner@shelfiq.io", 100L, Set.of("ROLE_STORE_OWNER")));
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void adjustStock_PositiveDelta_Success() {
        Product product = new Product(100L, "SKU-1", "BAR-1", "Product 1", null, null, null,
                new BigDecimal("50.00"), new BigDecimal("35.00"), 5, 10, null, 3, "PCS");
        product.setId(10L);

        Inventory inv = new Inventory(100L, product, 20);

        when(productRepository.findByIdAndStoreId(10L, 100L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductIdAndStoreId(10L, 100L)).thenReturn(Optional.of(inv));
        when(productService.toDto(product)).thenReturn(new ProductResponseDto());

        StockAdjustmentDto dto = new StockAdjustmentDto(15, InventoryMovement.MovementType.RESTOCK, "Vendor delivery", "PO-100");

        ProductResponseDto result = inventoryService.adjustStock(10L, dto);

        assertNotNull(result);
        assertEquals(35, inv.getCurrentStock());
        verify(inventoryRepository, times(1)).save(inv);
        verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
    }

    @Test
    void adjustStock_ExcessiveDecrement_ThrowsInsufficientStock() {
        Product product = new Product(100L, "SKU-1", "BAR-1", "Product 1", null, null, null,
                new BigDecimal("50.00"), new BigDecimal("35.00"), 5, 10, null, 3, "PCS");
        product.setId(10L);

        Inventory inv = new Inventory(100L, product, 5);

        when(productRepository.findByIdAndStoreId(10L, 100L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductIdAndStoreId(10L, 100L)).thenReturn(Optional.of(inv));

        StockAdjustmentDto dto = new StockAdjustmentDto(-10, InventoryMovement.MovementType.DAMAGE, "Damaged in transit", null);

        assertThrows(InsufficientStockException.class, () -> inventoryService.adjustStock(10L, dto));
        verify(inventoryRepository, never()).save(any());
    }
}
