package com.shelfiq.purchase.entity;

import com.shelfiq.common.domain.BaseEntity;
import com.shelfiq.product.entity.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_items",
        indexes = {
                @Index(name = "idx_poi_po_id", columnList = "purchase_order_id"),
                @Index(name = "idx_poi_product_id", columnList = "product_id")
        }
)
public class PurchaseOrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public PurchaseOrderItem() {
    }

    public PurchaseOrderItem(Product product, Integer quantity, BigDecimal unitCost) {
        this.product = product;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.subtotal = unitCost.multiply(BigDecimal.valueOf(quantity));
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
