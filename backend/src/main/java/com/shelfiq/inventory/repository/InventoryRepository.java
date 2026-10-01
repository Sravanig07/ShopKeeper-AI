package com.shelfiq.inventory.repository;

import com.shelfiq.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);
    Optional<Inventory> findByProductIdAndStoreId(Long productId, Long storeId);
    List<Inventory> findByStoreId(Long storeId);

    @Query("SELECT i FROM Inventory i JOIN i.product p WHERE i.storeId = :storeId AND i.currentStock <= p.safetyStock")
    List<Inventory> findLowStockInventories(@Param("storeId") Long storeId);
}
