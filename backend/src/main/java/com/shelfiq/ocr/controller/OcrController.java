package com.shelfiq.ocr.controller;

import com.shelfiq.common.response.ApiResponse;
import com.shelfiq.ocr.dto.OcrConfirmRestockRequestDto;
import com.shelfiq.ocr.dto.OcrParsedInvoiceDto;
import com.shelfiq.ocr.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ocr")
@Tag(name = "OCR Invoice Scanner", description = "Scan supplier invoices and bills to automatically extract line items and restock inventory")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping(value = "/scan-invoice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload paper invoice or bill photo and extract structured line items")
    public ResponseEntity<ApiResponse<OcrParsedInvoiceDto>> scanInvoice(@RequestParam("file") MultipartFile file) {
        OcrParsedInvoiceDto parsed = ocrService.scanInvoice(file);
        return ResponseEntity.ok(ApiResponse.ok("Invoice scanned and parsed successfully.", parsed));
    }

    @PostMapping("/confirm-restock")
    @Operation(summary = "Confirm parsed invoice items and automatically restock inventory with audit movements")
    public ResponseEntity<ApiResponse<Map<String, Object>>> confirmRestock(@Valid @RequestBody OcrConfirmRestockRequestDto request) {
        Map<String, Object> result = ocrService.confirmRestock(request);
        return ResponseEntity.ok(ApiResponse.ok("Inventory restocked from invoice successfully.", result));
    }
}
