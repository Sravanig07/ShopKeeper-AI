package com.shelfiq.supplier.repository;

import com.shelfiq.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByStoreId(Long storeId);
    Optional<Supplier> findByIdAndStoreId(Long id, Long storeId);
}
