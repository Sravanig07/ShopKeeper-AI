package com.shelfiq.purchase.entity;

import com.shelfiq.common.domain.BaseEntity;
import com.shelfiq.supplier.entity.Supplier;
import com.shelfiq.user.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders",
        indexes = {
                @Index(name = "idx_po_store_id", columnList = "store_id"),
                @Index(name = "idx_po_number", columnList = "po_number"),
                @Index(name = "idx_po_created_at", columnList = "created_at")
        }
)
public class PurchaseOrder extends BaseEntity {

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "po_number", nullable = false, unique = true, length = 64)
    private String poNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PoStatus status = PoStatus.DRAFT;

    @Column(name = "total_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "notes")
    private String notes;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private User receivedBy;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    public enum PoStatus {
        DRAFT,
        ORDERED,
        RECEIVED,
        CANCELLED
    }

    public PurchaseOrder() {
    }

    public PurchaseOrder(Long storeId, String poNumber, Supplier supplier, PoStatus status,
                         String notes, LocalDate expectedDeliveryDate) {
        this.storeId = storeId;
        this.poNumber = poNumber;
        this.supplier = supplier;
        this.status = status;
        this.notes = notes;
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public void addItem(PurchaseOrderItem item) {
        items.add(item);
        item.setPurchaseOrder(this);
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public PoStatus getStatus() {
        return status;
    }

    public void setStatus(PoStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public User getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(User receivedBy) {
        this.receivedBy = receivedBy;
    }

    public List<PurchaseOrderItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItem> items) {
        this.items = items;
    }
}
