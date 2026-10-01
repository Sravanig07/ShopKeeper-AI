package com.shelfiq.product.service;

import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.BadRequestException;
import com.shelfiq.common.exception.ResourceNotFoundException;
import com.shelfiq.common.response.PagedResponse;
import com.shelfiq.inventory.entity.Inventory;
import com.shelfiq.inventory.entity.InventoryMovement;
import com.shelfiq.inventory.repository.InventoryMovementRepository;
import com.shelfiq.inventory.repository.InventoryRepository;
import com.shelfiq.product.dto.ProductCreateDto;
import com.shelfiq.product.dto.ProductResponseDto;
import com.shelfiq.product.dto.ProductUpdateDto;
import com.shelfiq.product.entity.Category;
import com.shelfiq.product.entity.Product;
import com.shelfiq.product.repository.CategoryRepository;
import com.shelfiq.product.repository.ProductRepository;
import com.shelfiq.supplier.entity.Supplier;
import com.shelfiq.supplier.repository.SupplierRepository;
import com.shelfiq.user.entity.User;
import com.shelfiq.user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final UserRepository userRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            SupplierRepository supplierRepository,
            InventoryRepository inventoryRepository,
            InventoryMovementRepository inventoryMovementRepository,
            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponseDto> getProducts(String query, Long categoryId, Boolean lowStockOnly, Pageable pageable) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Page<Product> page;

        if (StringUtils.hasText(query)) {
            page = productRepository.searchProducts(storeId, query, pageable);
        } else if (categoryId != null) {
            page = productRepository.findByStoreIdAndCategoryId(storeId, categoryId, pageable);
        } else {
            page = productRepository.findByStoreId(storeId, pageable);
        }

        List<ProductResponseDto> dtos = page.getContent().stream()
                .map(this::toDto)
                .filter(dto -> lowStockOnly == null || !lowStockOnly || "LOW_STOCK".equals(dto.getStockStatus()) || "OUT_OF_STOCK".equals(dto.getStockStatus()))
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
    public ProductResponseDto getProductById(Long id) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Product product = productRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return toDto(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "barcodeLookups", key = "#barcode + '_' + T(com.shelfiq.common.context.TenantContextHolder).getRequiredStoreId()", unless = "#result == null")
    public ProductResponseDto getProductByBarcode(String barcode) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Product product = productRepository.findByStoreIdAndBarcode(storeId, barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "barcode", barcode));
        return toDto(product);
    }

    @Transactional
    public ProductResponseDto createProduct(ProductCreateDto dto) {
        Long storeId = TenantContextHolder.getRequiredStoreId();

        if (productRepository.existsByStoreIdAndSku(storeId, dto.getSku())) {
            throw new BadRequestException("A product with SKU '" + dto.getSku() + "' already exists in this store.");
        }

        if (StringUtils.hasText(dto.getBarcode()) && productRepository.existsByStoreIdAndBarcode(storeId, dto.getBarcode())) {
            throw new BadRequestException("A product with barcode '" + dto.getBarcode() + "' already exists in this store.");
        }

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));
        }

        Supplier supplier = null;
        if (dto.getSupplierId() != null) {
            supplier = supplierRepository.findByIdAndStoreId(dto.getSupplierId(), storeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", dto.getSupplierId()));
        }

        Product product = new Product(
                storeId,
                dto.getSku().trim().toUpperCase(),
                StringUtils.hasText(dto.getBarcode()) ? dto.getBarcode().trim() : null,
                dto.getName().trim(),
                dto.getBrand(),
                category,
                dto.getDescription(),
                dto.getSellingPrice(),
                dto.getCostPrice(),
                dto.getMinStock(),
                dto.getSafetyStock(),
                supplier,
                dto.getLeadTimeDays(),
                dto.getUnitOfMeasure()
        );
        Product savedProduct = productRepository.save(product);

        // Initialize inventory
        int initialStock = dto.getInitialStock() != null ? dto.getInitialStock() : 0;
        Inventory inventory = new Inventory(storeId, savedProduct, initialStock);
        inventoryRepository.save(inventory);

        // Record initial inventory movement if stock > 0
        if (initialStock > 0) {
            Long userId = TenantContextHolder.getRequiredUserId();
            User user = userRepository.findById(userId).orElse(null);

            InventoryMovement movement = new InventoryMovement(
                    storeId,
                    savedProduct,
                    InventoryMovement.MovementType.ADJUSTMENT,
                    InventoryMovement.ReferenceType.MANUAL_AUDIT,
                    "INIT-" + savedProduct.getId(),
                    initialStock,
                    0,
                    initialStock,
                    "Initial stock recorded on product creation",
                    user
            );
            inventoryMovementRepository.save(movement);
        }

        return toDto(savedProduct);
    }

    @Transactional
    @CacheEvict(value = "barcodeLookups", allEntries = true)
    public ProductResponseDto updateProduct(Long id, ProductUpdateDto dto) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Product product = productRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (StringUtils.hasText(dto.getBarcode()) && !dto.getBarcode().equals(product.getBarcode())) {
            Optional<Product> existingBarcode = productRepository.findByStoreIdAndBarcode(storeId, dto.getBarcode());
            if (existingBarcode.isPresent() && !existingBarcode.get().getId().equals(id)) {
                throw new BadRequestException("Barcode '" + dto.getBarcode() + "' is already in use by another product.");
            }
            product.setBarcode(dto.getBarcode().trim());
        }

        product.setName(dto.getName());
        product.setBrand(dto.getBrand());
        product.setDescription(dto.getDescription());
        product.setSellingPrice(dto.getSellingPrice());
        product.setCostPrice(dto.getCostPrice());
        if (dto.getMinStock() != null) product.setMinStock(dto.getMinStock());
        if (dto.getSafetyStock() != null) product.setSafetyStock(dto.getSafetyStock());
        if (dto.getLeadTimeDays() != null) product.setLeadTimeDays(dto.getLeadTimeDays());
        if (dto.getUnitOfMeasure() != null) product.setUnitOfMeasure(dto.getUnitOfMeasure());
        product.setActive(dto.isActive());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));
            product.setCategory(category);
        }

        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findByIdAndStoreId(dto.getSupplierId(), storeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", dto.getSupplierId()));
            product.setSupplier(supplier);
        }

        Product updated = productRepository.save(product);
        return toDto(updated);
    }

    @Transactional
    @CacheEvict(value = "barcodeLookups", allEntries = true)
    public void deleteProduct(Long id) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Product product = productRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Soft delete
        product.setActive(false);
        productRepository.save(product);
    }

    public ProductResponseDto toDto(Product p) {
        Optional<Inventory> invOpt = inventoryRepository.findByProductId(p.getId());
        int currentStock = invOpt.map(Inventory::getCurrentStock).orElse(0);
        int incomingStock = invOpt.map(Inventory::getIncomingStock).orElse(0);
        int reservedStock = invOpt.map(Inventory::getReservedStock).orElse(0);

        String status = "OPTIMAL";
        if (currentStock <= 0) {
            status = "OUT_OF_STOCK";
        } else if (currentStock <= p.getSafetyStock()) {
            status = "LOW_STOCK";
        } else if (p.getSafetyStock() > 0 && currentStock > (p.getSafetyStock() * 4)) {
            status = "OVERSTOCKED";
        }

        return new ProductResponseDto(
                p.getId(),
                p.getSku(),
                p.getBarcode(),
                p.getName(),
                p.getBrand(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : "Uncategorized",
                p.getDescription(),
                p.getSellingPrice(),
                p.getCostPrice(),
                p.getMinStock(),
                p.getSafetyStock(),
                currentStock,
                incomingStock,
                reservedStock,
                status,
                p.getSupplier() != null ? p.getSupplier().getId() : null,
                p.getSupplier() != null ? p.getSupplier().getName() : "None",
                p.getLeadTimeDays(),
                p.getUnitOfMeasure(),
                p.isActive()
        );
    }
}
