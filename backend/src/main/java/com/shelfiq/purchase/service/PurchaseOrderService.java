package com.shelfiq.purchase.service;

import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.BadRequestException;
import com.shelfiq.common.exception.ResourceNotFoundException;
import com.shelfiq.common.response.PagedResponse;
import com.shelfiq.inventory.entity.Inventory;
import com.shelfiq.inventory.entity.InventoryMovement;
import com.shelfiq.inventory.repository.InventoryMovementRepository;
import com.shelfiq.inventory.repository.InventoryRepository;
import com.shelfiq.product.entity.Product;
import com.shelfiq.product.repository.ProductRepository;
import com.shelfiq.purchase.dto.PurchaseOrderCreateDto;
import com.shelfiq.purchase.dto.PurchaseOrderItemDto;
import com.shelfiq.purchase.dto.PurchaseOrderResponseDto;
import com.shelfiq.purchase.entity.PurchaseOrder;
import com.shelfiq.purchase.entity.PurchaseOrderItem;
import com.shelfiq.purchase.repository.PurchaseOrderRepository;
import com.shelfiq.supplier.entity.Supplier;
import com.shelfiq.supplier.repository.SupplierRepository;
import com.shelfiq.user.entity.User;
import com.shelfiq.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final UserRepository userRepository;

    public PurchaseOrderService(
            PurchaseOrderRepository purchaseOrderRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            InventoryMovementRepository inventoryMovementRepository,
            UserRepository userRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PurchaseOrderResponseDto createPurchaseOrder(PurchaseOrderCreateDto dto) {
        Long storeId = TenantContextHolder.getRequiredStoreId();

        Supplier supplier = supplierRepository.findByIdAndStoreId(dto.getSupplierId(), storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", dto.getSupplierId()));

        String datePrefix = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String randomSuffix = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        String poNumber = "PO-" + datePrefix + "-" + randomSuffix;

        PurchaseOrder po = new PurchaseOrder(
                storeId,
                poNumber,
                supplier,
                PurchaseOrder.PoStatus.ORDERED,
                dto.getNotes(),
                dto.getExpectedDeliveryDate() != null ? dto.getExpectedDeliveryDate() : LocalDate.now().plusDays(supplier.getLeadTimeDays() != null ? supplier.getLeadTimeDays() : 2)
        );

        BigDecimal totalCost = BigDecimal.ZERO;

        for (PurchaseOrderItemDto itemDto : dto.getItems()) {
            Product product = productRepository.findByIdAndStoreId(itemDto.getProductId(), storeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDto.getProductId()));

            BigDecimal unitCost = itemDto.getUnitCost() != null ? itemDto.getUnitCost() : product.getCostPrice();
            PurchaseOrderItem item = new PurchaseOrderItem(product, itemDto.getQuantity(), unitCost);
            po.addItem(item);
            totalCost = totalCost.add(item.getSubtotal());
        }

        po.setTotalCost(totalCost);
        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return toDto(saved);
    }

    @Transactional
    public PurchaseOrderResponseDto receivePurchaseOrder(Long id) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Long userId = TenantContextHolder.getRequiredUserId();
        User currentUser = userRepository.findById(userId).orElse(null);

        PurchaseOrder po = purchaseOrderRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        if (po.getStatus() == PurchaseOrder.PoStatus.RECEIVED) {
            throw new BadRequestException("Purchase Order " + po.getPoNumber() + " has already been received.");
        }

        po.setStatus(PurchaseOrder.PoStatus.RECEIVED);
        po.setReceivedAt(LocalDateTime.now());
        po.setReceivedBy(currentUser);

        // Restock inventory for each PO item
        for (PurchaseOrderItem item : po.getItems()) {
            Product p = item.getProduct();
            Inventory inv = inventoryRepository.findByProductIdAndStoreId(p.getId(), storeId)
                    .orElseGet(() -> new Inventory(storeId, p, 0));

            int prevStock = inv.getCurrentStock();
            int newStock = prevStock + item.getQuantity();

            inv.setCurrentStock(newStock);
            inv.setLastRestockedAt(LocalDateTime.now());
            inventoryRepository.save(inv);

            InventoryMovement movement = new InventoryMovement(
                    storeId,
                    p,
                    InventoryMovement.MovementType.RESTOCK,
                    InventoryMovement.ReferenceType.PURCHASE_ORDER,
                    po.getPoNumber(),
                    item.getQuantity(),
                    prevStock,
                    newStock,
                    "PO Delivery Restock " + po.getPoNumber(),
                    currentUser
            );
            inventoryMovementRepository.save(movement);
        }

        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public PagedResponse<PurchaseOrderResponseDto> getPurchaseOrders(Pageable pageable) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Page<PurchaseOrder> page = purchaseOrderRepository.findByStoreIdOrderByCreatedAtDesc(storeId, pageable);

        List<PurchaseOrderResponseDto> dtos = page.getContent().stream()
                .map(this::toDto)
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
    public PurchaseOrderResponseDto getPurchaseOrderById(Long id) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        PurchaseOrder po = purchaseOrderRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
        return toDto(po);
    }

    private PurchaseOrderResponseDto toDto(PurchaseOrder po) {
        List<PurchaseOrderItemDto> itemDtos = po.getItems().stream()
                .map(i -> new PurchaseOrderItemDto(
                        i.getId(),
                        i.getProduct() != null ? i.getProduct().getId() : null,
                        i.getProduct() != null ? i.getProduct().getName() : "Unknown",
                        i.getProduct() != null ? i.getProduct().getSku() : "N/A",
                        i.getQuantity(),
                        i.getUnitCost(),
                        i.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new PurchaseOrderResponseDto(
                po.getId(),
                po.getPoNumber(),
                po.getStoreId(),
                po.getSupplier() != null ? po.getSupplier().getId() : null,
                po.getSupplier() != null ? po.getSupplier().getName() : "Supplier",
                po.getStatus(),
                po.getTotalCost(),
                po.getNotes(),
                po.getExpectedDeliveryDate(),
                po.getReceivedAt(),
                po.getReceivedBy() != null ? po.getReceivedBy().getFullName() : null,
                po.getCreatedAt(),
                itemDtos
        );
    }
}
