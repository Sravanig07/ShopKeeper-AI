package com.shelfiq.supplier.controller;

import com.shelfiq.common.response.ApiResponse;
import com.shelfiq.supplier.dto.SupplierDto;
import com.shelfiq.supplier.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@Tag(name = "Suppliers", description = "Supplier catalog, lead times, and reliability ratings")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    @Operation(summary = "List all suppliers for the active store")
    public ResponseEntity<ApiResponse<List<SupplierDto>>> getAllSuppliers() {
        return ResponseEntity.ok(ApiResponse.ok(supplierService.getAllSuppliers()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single supplier details by ID")
    public ResponseEntity<ApiResponse<SupplierDto>> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(supplierService.getSupplierById(id)));
    }

    @PostMapping
    @Operation(summary = "Register new supplier")
    public ResponseEntity<ApiResponse<SupplierDto>> createSupplier(@Valid @RequestBody SupplierDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Supplier created successfully.", supplierService.createSupplier(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier details")
    public ResponseEntity<ApiResponse<SupplierDto>> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Supplier updated successfully.", supplierService.updateSupplier(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.ok("Supplier deleted successfully.", null));
    }
}
