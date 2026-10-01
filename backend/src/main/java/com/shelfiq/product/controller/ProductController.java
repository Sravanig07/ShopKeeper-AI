package com.shelfiq.product.controller;

import com.shelfiq.common.response.ApiResponse;
import com.shelfiq.common.response.PagedResponse;
import com.shelfiq.product.dto.ProductCreateDto;
import com.shelfiq.product.dto.ProductResponseDto;
import com.shelfiq.product.dto.ProductUpdateDto;
import com.shelfiq.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product catalog management, search, and barcode lookups")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List paginated products with search and category filtering")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponseDto>>> getProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "false") Boolean lowStockOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<ProductResponseDto> response = productService.getProducts(query, categoryId, lowStockOnly, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single product with inventory details by ID")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProductById(id)));
    }

    @GetMapping("/lookup/barcode")
    @Operation(summary = "Fast barcode lookup for POS scanning (Redis cached)")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductByBarcode(@RequestParam String code) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProductByBarcode(code)));
    }

    @PostMapping
    @Operation(summary = "Create new product in current store catalog")
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(@Valid @RequestBody ProductCreateDto dto) {
        ProductResponseDto created = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Product created successfully.", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product details")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDto dto) {
        ProductResponseDto updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully.", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete / deactivate product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deactivated.", null));
    }
}
