package com.shelfiq.sales.repository;

import com.shelfiq.sales.entity.SaleTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, Long> {

    Page<SaleTransaction> findByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable);

    Optional<SaleTransaction> findByIdAndStoreId(Long id, Long storeId);

    Optional<SaleTransaction> findByStoreIdAndInvoiceNumber(Long storeId, String invoiceNumber);

    List<SaleTransaction> findByStoreIdAndCreatedAtBetween(Long storeId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(s) FROM SaleTransaction s WHERE s.storeId = :storeId AND s.createdAt >= :start AND s.createdAt <= :end")
    long countSalesBetween(@Param("storeId") Long storeId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.netAmount), 0) FROM SaleTransaction s WHERE s.storeId = :storeId AND s.createdAt >= :start AND s.createdAt <= :end")
    BigDecimal sumRevenueBetween(@Param("storeId") Long storeId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
