package com.shelfiq.inventory.service;

import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.InsufficientStockException;
import com.shelfiq.common.exception.ResourceNotFoundException;
import com.shelfiq.common.response.PagedResponse;
import com.shelfiq.inventory.dto.InventoryMovementDto;
import com.shelfiq.inventory.dto.InventorySummaryDto;
import com.shelfiq.inventory.dto.StockAdjustmentDto;
import com.shelfiq.inventory.entity.Inventory;
import com.shelfiq.inventory.entity.InventoryMovement;
import com.shelfiq.inventory.repository.InventoryMovementRepository;
import com.shelfiq.inventory.repository.InventoryRepository;
import com.shelfiq.product.dto.ProductResponseDto;
import com.shelfiq.product.entity.Product;
import com.shelfiq.product.repository.ProductRepository;
import com.shelfiq.product.service.ProductService;
import com.shelfiq.user.entity.User;
import com.shelfiq.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public InventoryService(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository inventoryMovementRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            ProductService productService) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    @Transactional
    public ProductResponseDto adjustStock(Long productId, StockAdjustmentDto dto) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Long userId = TenantContextHolder.getRequiredUserId();
        User user = userRepository.findById(userId).orElse(null);

        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseGet(() -> new Inventory(storeId, product, 0));

        int previousStock = inventory.getCurrentStock();
        int newStock = previousStock + dto.getQuantityDelta();

        if (newStock < 0) {
            throw new InsufficientStockException(
                    String.format("Cannot decrement stock by %d. Current stock for '%s' is only %d.",
                            Math.abs(dto.getQuantityDelta()), product.getName(), previousStock)
            );
        }

        inventory.setCurrentStock(newStock);
        if (dto.getQuantityDelta() > 0) {
            inventory.setLastRestockedAt(LocalDateTime.now());
        }
        inventoryRepository.save(inventory);

        // Record movement audit
        InventoryMovement movement = new InventoryMovement(
                storeId,
                product,
                dto.getMovementType(),
                InventoryMovement.ReferenceType.MANUAL_AUDIT,
                dto.getReferenceId() != null ? dto.getReferenceId() : "ADJ-" + System.currentTimeMillis(),
                dto.getQuantityDelta(),
                previousStock,
                newStock,
                dto.getReason() != null ? dto.getReason() : "Manual stock adjustment",
                user
        );
        inventoryMovementRepository.save(movement);

        return productService.toDto(product);
    }

    @Transactional(readOnly = true)
    public InventorySummaryDto getInventorySummary() {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        List<Inventory> inventories = inventoryRepository.findByStoreId(storeId);

        int totalProducts = inventories.size();
        int totalUnitsInStock = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        int lowStockCount = 0;
        int outOfStockCount = 0;

        for (Inventory inv : inventories) {
            int stock = inv.getCurrentStock();
            totalUnitsInStock += stock;
            Product p = inv.getProduct();
            if (p != null && p.getCostPrice() != null) {
                totalValue = totalValue.add(p.getCostPrice().multiply(BigDecimal.valueOf(stock)));
                if (stock <= 0) {
                    outOfStockCount++;
                } else if (stock <= p.getSafetyStock()) {
                    lowStockCount++;
                }
            }
        }

        return new InventorySummaryDto(
                totalProducts,
                totalUnitsInStock,
                totalValue,
                lowStockCount,
                outOfStockCount
        );
    }

    @Transactional(readOnly = true)
    public PagedResponse<InventoryMovementDto> getMovements(Pageable pageable) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Page<InventoryMovement> page = inventoryMovementRepository.findByStoreIdOrderByCreatedAtDesc(storeId, pageable);

        List<InventoryMovementDto> dtos = page.getContent().stream()
                .map(this::toMovementDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional(readOnly = true)
    public List<InventoryMovementDto> getProductMovements(Long productId) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        return inventoryMovementRepository.findByStoreIdAndProductIdOrderByCreatedAtDesc(storeId, productId).stream()
                .map(this::toMovementDto)
                .collect(Collectors.toList());
    }

    private InventoryMovementDto toMovementDto(InventoryMovement m) {
        Product p = m.getProduct();
        User u = m.getCreatedBy();
        return new InventoryMovementDto(
                m.getId(),
                p != null ? p.getId() : null,
                p != null ? p.getSku() : "N/A",
                p != null ? p.getName() : "Unknown Product",
                m.getMovementType().name(),
                m.getReferenceType() != null ? m.getReferenceType().name() : "N/A",
                m.getReferenceId(),
                m.getQuantityDelta(),
                m.getPreviousStock(),
                m.getNewStock(),
                m.getReason(),
                u != null ? u.getFullName() : "System",
                m.getCreatedAt()
        );
    }
}
