package com.shelfiq.inventory.repository;

import com.shelfiq.inventory.entity.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    Page<InventoryMovement> findByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable);
    List<InventoryMovement> findByStoreIdAndProductIdOrderByCreatedAtDesc(Long storeId, Long productId);
}
