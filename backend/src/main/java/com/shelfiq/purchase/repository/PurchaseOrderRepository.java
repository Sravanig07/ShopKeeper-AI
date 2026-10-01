package com.shelfiq.purchase.repository;

import com.shelfiq.purchase.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Page<PurchaseOrder> findByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable);

    Optional<PurchaseOrder> findByIdAndStoreId(Long id, Long storeId);

    Optional<PurchaseOrder> findByStoreIdAndPoNumber(Long storeId, String poNumber);
}
