package com.shelfiq.product.entity;

import com.shelfiq.common.domain.BaseEntity;
import com.shelfiq.supplier.entity.Supplier;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_store_sku", columnNames = {"store_id", "sku"}),
                @UniqueConstraint(name = "uk_store_barcode", columnNames = {"store_id", "barcode"})
        },
        indexes = {
                @Index(name = "idx_products_store_id", columnList = "store_id"),
                @Index(name = "idx_products_barcode", columnList = "barcode"),
                @Index(name = "idx_products_sku", columnList = "sku"),
                @Index(name = "idx_products_category", columnList = "category_id")
        }
)
public class Product extends BaseEntity {

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "sku", nullable = false, length = 64)
    private String sku;

    @Column(name = "barcode", length = 64)
    private String barcode;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "brand", length = 100)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "selling_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "cost_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "min_stock", nullable = false)
    private Integer minStock = 5;

    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "lead_time_days")
    private Integer leadTimeDays = 3;

    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure = "PCS";

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public Product() {
    }

    public Product(Long storeId, String sku, String barcode, String name, String brand, Category category,
                   String description, BigDecimal sellingPrice, BigDecimal costPrice, Integer minStock,
                   Integer safetyStock, Supplier supplier, Integer leadTimeDays, String unitOfMeasure) {
        this.storeId = storeId;
        this.sku = sku;
        this.barcode = barcode;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.description = description;
        this.sellingPrice = sellingPrice;
        this.costPrice = costPrice;
        this.minStock = minStock != null ? minStock : 5;
        this.safetyStock = safetyStock != null ? safetyStock : 10;
        this.supplier = supplier;
        this.leadTimeDays = leadTimeDays != null ? leadTimeDays : (supplier != null ? supplier.getLeadTimeDays() : 3);
        this.unitOfMeasure = unitOfMeasure != null ? unitOfMeasure : "PCS";
        this.isActive = true;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getMinStock() {
        return minStock;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }

    public Integer getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(Integer safetyStock) {
        this.safetyStock = safetyStock;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
