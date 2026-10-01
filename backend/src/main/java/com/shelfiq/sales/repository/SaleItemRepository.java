package com.shelfiq.sales.repository;

import com.shelfiq.sales.entity.SaleItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySaleTransactionId(Long saleTransactionId);

    @Query("SELECT i.product.id, i.product.name, i.product.sku, SUM(i.quantity), SUM(i.subtotal) " +
           "FROM SaleItem i JOIN i.saleTransaction t " +
           "WHERE t.storeId = :storeId AND t.createdAt >= :since " +
           "GROUP BY i.product.id, i.product.name, i.product.sku " +
           "ORDER BY SUM(i.quantity) DESC")
    List<Object[]> findTopSoldProductsSince(@Param("storeId") Long storeId, @Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT i.product.id, SUM(i.quantity) " +
           "FROM SaleItem i JOIN i.saleTransaction t " +
           "WHERE t.storeId = :storeId AND t.createdAt >= :since " +
           "GROUP BY i.product.id")
    List<Object[]> getQuantitiesSoldSince(@Param("storeId") Long storeId, @Param("since") LocalDateTime since);

    @Query("SELECT MAX(t.createdAt) FROM SaleItem i JOIN i.saleTransaction t WHERE t.storeId = :storeId AND i.product.id = :productId")
    LocalDateTime findLastSoldDate(@Param("storeId") Long storeId, @Param("productId") Long productId);
}
