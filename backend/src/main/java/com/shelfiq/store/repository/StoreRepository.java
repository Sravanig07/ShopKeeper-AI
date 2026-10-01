package com.shelfiq.store.repository;

import com.shelfiq.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreCode(String storeCode);
    boolean existsByStoreCode(String storeCode);
    List<Store> findByOwnerId(Long ownerId);
}
