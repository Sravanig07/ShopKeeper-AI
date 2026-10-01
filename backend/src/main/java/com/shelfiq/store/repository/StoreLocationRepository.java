package com.shelfiq.store.repository;

import com.shelfiq.store.entity.StoreLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreLocationRepository extends JpaRepository<StoreLocation, Long> {
    Optional<StoreLocation> findByStoreId(Long storeId);
    List<StoreLocation> findByGeohash5(String geohash5);
    List<StoreLocation> findByGeohash6(String geohash6);
}
