package com.shelfiq.inventory.entity;

import com.shelfiq.common.domain.BaseEntity;
import com.shelfiq.product.entity.Product;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventories",
        indexes = {
                @Index(name = "idx_inventories_store_id", columnList = "store_id")
        }
)
public class Inventory extends BaseEntity {

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;

    @Column(name = "incoming_stock", nullable = false)
    private Integer incomingStock = 0;

    @Column(name = "reserved_stock", nullable = false)
    private Integer reservedStock = 0;

    @Column(name = "last_restocked_at")
    private LocalDateTime lastRestockedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public Inventory() {
    }

    public Inventory(Long storeId, Product product, Integer currentStock) {
        this.storeId = storeId;
        this.product = product;
        this.currentStock = currentStock != null ? currentStock : 0;
        this.incomingStock = 0;
        this.reservedStock = 0;
        this.version = 0L;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getIncomingStock() {
        return incomingStock;
    }

    public void setIncomingStock(Integer incomingStock) {
        this.incomingStock = incomingStock;
    }

    public Integer getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(Integer reservedStock) {
        this.reservedStock = reservedStock;
    }

    public LocalDateTime getLastRestockedAt() {
        return lastRestockedAt;
    }

    public void setLastRestockedAt(LocalDateTime lastRestockedAt) {
        this.lastRestockedAt = lastRestockedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
