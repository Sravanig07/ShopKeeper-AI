package com.shelfiq.sales.controller;

import com.shelfiq.common.response.ApiResponse;
import com.shelfiq.common.response.PagedResponse;
import com.shelfiq.sales.dto.SaleCheckoutRequestDto;
import com.shelfiq.sales.dto.SaleResponseDto;
import com.shelfiq.sales.dto.TodaySalesSummaryDto;
import com.shelfiq.sales.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sales")
@Tag(name = "Sales & POS", description = "POS checkout, transaction receipts, and daily sales metrics")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Process POS checkout, decrement stock atomically, and generate invoice")
    public ResponseEntity<ApiResponse<SaleResponseDto>> checkout(@Valid @RequestBody SaleCheckoutRequestDto dto) {
        SaleResponseDto sale = saleService.checkout(dto);
        return ResponseEntity.ok(ApiResponse.ok("Sale completed successfully.", sale));
    }

    @GetMapping
    @Operation(summary = "Get paginated transaction history for the store")
    public ResponseEntity<ApiResponse<PagedResponse<SaleResponseDto>>> getSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(saleService.getSales(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific sale invoice details")
    public ResponseEntity<ApiResponse<SaleResponseDto>> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(saleService.getSaleById(id)));
    }

    @GetMapping("/analytics/today")
    @Operation(summary = "Get today's real-time sales metrics and top selling items")
    public ResponseEntity<ApiResponse<TodaySalesSummaryDto>> getTodayAnalytics() {
        return ResponseEntity.ok(ApiResponse.ok(saleService.getTodayAnalytics()));
    }

    @GetMapping("/analytics/leaderboard")
    @Operation(summary = "Get regional sales leaderboard across cities, states, and regions for items and categories")
    public ResponseEntity<ApiResponse<com.shelfiq.sales.dto.LeaderboardResponseDto>> getRegionalLeaderboard(
            @RequestParam(defaultValue = "CITY") String groupBy,
            @RequestParam(defaultValue = "ITEM") String type,
            @RequestParam(defaultValue = "TODAY") String timeRange) {
        return ResponseEntity.ok(ApiResponse.ok(saleService.getRegionalLeaderboard(groupBy, type, timeRange)));
    }
}
