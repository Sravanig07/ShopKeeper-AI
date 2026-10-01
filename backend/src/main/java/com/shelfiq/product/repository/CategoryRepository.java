package com.shelfiq.product.repository;

import com.shelfiq.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.storeId IS NULL OR c.storeId = :storeId ORDER BY c.name ASC")
    List<Category> findAllAvailableForStore(@Param("storeId") Long storeId);

    Optional<Category> findByIdAndStoreId(Long id, Long storeId);

    Optional<Category> findByNameIgnoreCase(String name);
}
