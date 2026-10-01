package com.shelfiq.product.repository;

import com.shelfiq.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStoreId(Long storeId, Pageable pageable);

    List<Product> findByStoreId(Long storeId);

    Optional<Product> findByIdAndStoreId(Long id, Long storeId);

    Optional<Product> findByStoreIdAndBarcode(Long storeId, String barcode);

    Optional<Product> findByStoreIdAndSku(Long storeId, String sku);

    boolean existsByStoreIdAndSku(Long storeId, String sku);

    boolean existsByStoreIdAndBarcode(Long storeId, String barcode);

    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.barcode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchProducts(@Param("storeId") Long storeId, @Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND p.category.id = :categoryId")
    Page<Product> findByStoreIdAndCategoryId(@Param("storeId") Long storeId, @Param("categoryId") Long categoryId, Pageable pageable);
}
