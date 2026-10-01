package com.shelfiq.inventory.entity;

import com.shelfiq.common.domain.BaseEntity;
import com.shelfiq.product.entity.Product;
import com.shelfiq.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory_movements",
        indexes = {
                @Index(name = "idx_inv_movements_store_id", columnList = "store_id"),
                @Index(name = "idx_inv_movements_product_id", columnList = "product_id"),
                @Index(name = "idx_inv_movements_created_at", columnList = "created_at")
        }
)
public class InventoryMovement extends BaseEntity {

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private MovementType movementType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", length = 30)
    private ReferenceType referenceType;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "quantity_delta", nullable = false)
    private Integer quantityDelta;

    @Column(name = "previous_stock", nullable = false)
    private Integer previousStock;

    @Column(name = "new_stock", nullable = false)
    private Integer newStock;

    @Column(name = "reason")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public enum MovementType {
        SALE,
        RESTOCK,
        RETURN,
        DAMAGE,
        ADJUSTMENT
    }

    public enum ReferenceType {
        SALE_TRANSACTION,
        PURCHASE_ORDER,
        MANUAL_AUDIT,
        CSV_IMPORT
    }

    public InventoryMovement() {
    }

    public InventoryMovement(Long storeId, Product product, MovementType movementType,
                             ReferenceType referenceType, String referenceId, Integer quantityDelta,
                             Integer previousStock, Integer newStock, String reason, User createdBy) {
        this.storeId = storeId;
        this.product = product;
        this.movementType = movementType;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.quantityDelta = quantityDelta;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.reason = reason;
        this.createdBy = createdBy;
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

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getQuantityDelta() {
        return quantityDelta;
    }

    public void setQuantityDelta(Integer quantityDelta) {
        this.quantityDelta = quantityDelta;
    }

    public Integer getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(Integer previousStock) {
        this.previousStock = previousStock;
    }

    public Integer getNewStock() {
        return newStock;
    }

    public void setNewStock(Integer newStock) {
        this.newStock = newStock;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
