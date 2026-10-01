package com.shelfiq.product.entity;

import com.shelfiq.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_categories_store_id", columnList = "store_id")
})
public class Category extends BaseEntity {

    @Column(name = "store_id")
    private Long storeId; // null indicates global master category

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    public Category() {
    }

    public Category(Long storeId, String name, String description, Category parentCategory) {
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.parentCategory = parentCategory;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }
}
