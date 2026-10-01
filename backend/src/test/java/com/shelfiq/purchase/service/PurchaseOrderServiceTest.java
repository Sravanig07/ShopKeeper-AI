package com.shelfiq.purchase.service;

import com.shelfiq.common.context.TenantContext;
import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.inventory.entity.Inventory;
import com.shelfiq.inventory.entity.InventoryMovement;
import com.shelfiq.inventory.repository.InventoryMovementRepository;
import com.shelfiq.inventory.repository.InventoryRepository;
import com.shelfiq.product.entity.Product;
import com.shelfiq.product.repository.ProductRepository;
import com.shelfiq.purchase.dto.PurchaseOrderResponseDto;
import com.shelfiq.purchase.entity.PurchaseOrder;
import com.shelfiq.purchase.entity.PurchaseOrderItem;
import com.shelfiq.purchase.repository.PurchaseOrderRepository;
import com.shelfiq.supplier.entity.Supplier;
import com.shelfiq.supplier.repository.SupplierRepository;
import com.shelfiq.user.entity.User;
import com.shelfiq.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementRepository inventoryMovementRepository;
    @Mock
    private UserRepository userRepository;

    private PurchaseOrderService purchaseOrderService;

    @BeforeEach
    void setUp() {
        purchaseOrderService = new PurchaseOrderService(
                purchaseOrderRepository,
                supplierRepository,
                productRepository,
                inventoryRepository,
                inventoryMovementRepository,
                userRepository
        );
        TenantContextHolder.setContext(new TenantContext(1L, "owner@shelfiq.io", 100L, Set.of("ROLE_STORE_OWNER")));
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void receivePurchaseOrder_RestocksInventoryAndMarksReceived() {
        Supplier supplier = new Supplier(100L, "Beverage World", "Amitabh", "a@bw.com", "+91 999", 2, new BigDecimal("0.98"));
        supplier.setId(10L);

        Product product = new Product(100L, "BEV-SPRITE-500", "8901234567892", "Sprite 500ml", null, null, null,
                new BigDecimal("40.00"), new BigDecimal("31.50"), 10, 15, supplier, 2, "PCS");
        product.setId(20L);

        PurchaseOrder po = new PurchaseOrder(100L, "PO-20261001-A1B2", supplier, PurchaseOrder.PoStatus.ORDERED, "Urgent", LocalDate.now());
        po.setId(50L);
        po.addItem(new PurchaseOrderItem(product, 24, new BigDecimal("31.50")));

        Inventory inv = new Inventory(100L, product, 5);
        User user = new User("owner@shelfiq.io", "pass", "Owner", "+91 999");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(purchaseOrderRepository.findByIdAndStoreId(50L, 100L)).thenReturn(Optional.of(po));
        when(inventoryRepository.findByProductIdAndStoreId(20L, 100L)).thenReturn(Optional.of(inv));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(i -> i.getArgument(0));

        PurchaseOrderResponseDto received = purchaseOrderService.receivePurchaseOrder(50L);

        assertNotNull(received);
        assertEquals(PurchaseOrder.PoStatus.RECEIVED, received.getStatus());
        assertEquals(29, inv.getCurrentStock()); // 5 + 24
        verify(inventoryRepository, times(1)).save(inv);
        verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
    }
}
