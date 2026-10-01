package com.shelfiq.product.service;

import com.shelfiq.common.context.TenantContext;
import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.BadRequestException;
import com.shelfiq.inventory.entity.Inventory;
import com.shelfiq.inventory.repository.InventoryMovementRepository;
import com.shelfiq.inventory.repository.InventoryRepository;
import com.shelfiq.product.dto.ProductCreateDto;
import com.shelfiq.product.dto.ProductResponseDto;
import com.shelfiq.product.entity.Product;
import com.shelfiq.product.repository.CategoryRepository;
import com.shelfiq.product.repository.ProductRepository;
import com.shelfiq.supplier.repository.SupplierRepository;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementRepository inventoryMovementRepository;
    @Mock
    private UserRepository userRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                productRepository,
                categoryRepository,
                supplierRepository,
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
    void createProduct_Success() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setSku("BEV-COKE-01");
        dto.setBarcode("8901111111111");
        dto.setName("Coke Can");
        dto.setSellingPrice(new BigDecimal("40.00"));
        dto.setCostPrice(new BigDecimal("30.00"));
        dto.setInitialStock(50);

        when(productRepository.existsByStoreIdAndSku(100L, "BEV-COKE-01")).thenReturn(false);
        when(productRepository.existsByStoreIdAndBarcode(100L, "8901111111111")).thenReturn(false);

        Product savedProduct = new Product(100L, "BEV-COKE-01", "8901111111111", "Coke Can", null, null, null,
                new BigDecimal("40.00"), new BigDecimal("30.00"), 5, 10, null, 3, "PCS");
        savedProduct.setId(55L);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Inventory inv = new Inventory(100L, savedProduct, 50);
        when(inventoryRepository.findByProductId(55L)).thenReturn(Optional.of(inv));

        ProductResponseDto resp = productService.createProduct(dto);

        assertNotNull(resp);
        assertEquals("BEV-COKE-01", resp.getSku());
        assertEquals(50, resp.getCurrentStock());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(inventoryMovementRepository, times(1)).save(any());
    }

    @Test
    void createProduct_DuplicateSku_ThrowsBadRequest() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setSku("BEV-COKE-01");
        when(productRepository.existsByStoreIdAndSku(100L, "BEV-COKE-01")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> productService.createProduct(dto));
    }
}
