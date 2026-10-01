package com.shelfiq.inventory.controller;

import com.shelfiq.common.response.ApiResponse;
import com.shelfiq.common.response.PagedResponse;
import com.shelfiq.inventory.dto.InventoryMovementDto;
import com.shelfiq.inventory.dto.InventorySummaryDto;
import com.shelfiq.inventory.dto.StockAdjustmentDto;
import com.shelfiq.inventory.service.InventoryService;
import com.shelfiq.product.dto.ProductResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Inventory adjustments, stock health summary, and movement audit trail")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{productId}/adjust")
    @Operation(summary = "Adjust product inventory with immutable audit movement record")
    public ResponseEntity<ApiResponse<ProductResponseDto>> adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustmentDto dto) {
        ProductResponseDto updated = inventoryService.adjustStock(productId, dto);
        return ResponseEntity.ok(ApiResponse.ok("Inventory adjusted successfully.", updated));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get overall inventory health KPIs for active store")
    public ResponseEntity<ApiResponse<InventorySummaryDto>> getInventorySummary() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getInventorySummary()));
    }

    @GetMapping("/movements")
    @Operation(summary = "Get paginated audit log of all inventory movements across store")
    public ResponseEntity<ApiResponse<PagedResponse<InventoryMovementDto>>> getMovements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getMovements(pageable)));
    }

    @GetMapping("/{productId}/movements")
    @Operation(summary = "Get historical movement audit log for specific product")
    public ResponseEntity<ApiResponse<List<InventoryMovementDto>>> getProductMovements(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getProductMovements(productId)));
    }
}
