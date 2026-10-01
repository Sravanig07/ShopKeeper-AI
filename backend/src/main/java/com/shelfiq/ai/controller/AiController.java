package com.shelfiq.ai.controller;

import com.shelfiq.ai.dto.*;
import com.shelfiq.ai.service.AiRetailService;
import com.shelfiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Retail Intelligence", description = "Demand forecasting, restock recommendations, dead-stock detection, and retail copilot")
public class AiController {

    private final AiRetailService aiRetailService;

    public AiController(AiRetailService aiRetailService) {
        this.aiRetailService = aiRetailService;
    }

    @GetMapping("/restock-recommendations")
    @Operation(summary = "Get prioritized replenishment recommendations based on velocity and lead times")
    public ResponseEntity<ApiResponse<List<RestockRecommendationDto>>> getRestockRecommendations() {
        return ResponseEntity.ok(ApiResponse.ok(aiRetailService.getRestockRecommendations()));
    }

    @GetMapping("/dead-stock")
    @Operation(summary = "Get slow-moving and dead-stock SKUs with locked capital analysis")
    public ResponseEntity<ApiResponse<List<DeadStockDto>>> getDeadStock() {
        return ResponseEntity.ok(ApiResponse.ok(aiRetailService.getDeadStockAnalysis()));
    }

    @GetMapping("/insights")
    @Operation(summary = "Get high-level store health score and strategic insights")
    public ResponseEntity<ApiResponse<AiStoreInsightsDto>> getStoreInsights() {
        return ResponseEntity.ok(ApiResponse.ok(aiRetailService.getStoreInsights()));
    }

    @PostMapping("/chat")
    @Operation(summary = "Ask the AI Store Copilot questions about inventory, sales, suppliers, or replenishment")
    public ResponseEntity<ApiResponse<AiChatResponseDto>> chatWithCopilot(@Valid @RequestBody AiChatRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok(aiRetailService.chatWithCopilot(request)));
    }
}
