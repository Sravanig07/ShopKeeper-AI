package com.shelfiq.purchase.controller;

import com.shelfiq.common.response.ApiResponse;
import com.shelfiq.common.response.PagedResponse;
import com.shelfiq.purchase.dto.PurchaseOrderCreateDto;
import com.shelfiq.purchase.dto.PurchaseOrderResponseDto;
import com.shelfiq.purchase.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@Tag(name = "Purchase Orders", description = "Supplier purchase orders and one-click stock receiving")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    @Operation(summary = "Create a new purchase order for a supplier")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> createPurchaseOrder(@Valid @RequestBody PurchaseOrderCreateDto dto) {
        PurchaseOrderResponseDto created = purchaseOrderService.createPurchaseOrder(dto);
        return ResponseEntity.ok(ApiResponse.ok("Purchase order created successfully.", created));
    }

    @GetMapping
    @Operation(summary = "Get paginated purchase orders for the store")
    public ResponseEntity<ApiResponse<PagedResponse<PurchaseOrderResponseDto>>> getPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.getPurchaseOrders(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific purchase order details")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.getPurchaseOrderById(id)));
    }

    @PostMapping("/{id}/receive")
    @Operation(summary = "Receive shipment for purchase order and automatically restock inventory")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> receivePurchaseOrder(@PathVariable Long id) {
        PurchaseOrderResponseDto received = purchaseOrderService.receivePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.ok("Purchase order received and inventory updated.", received));
    }
}
