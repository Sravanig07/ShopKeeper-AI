package com.shelfiq.sales.entity;

import com.shelfiq.common.domain.BaseEntity;
import com.shelfiq.product.entity.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_items",
        indexes = {
                @Index(name = "idx_sale_items_txn_id", columnList = "sale_transaction_id"),
                @Index(name = "idx_sale_items_product_id", columnList = "product_id")
        }
)
public class SaleItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_transaction_id", nullable = false)
    private SaleTransaction saleTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    public SaleItem() {
    }

    public SaleItem(Product product, Integer quantity, BigDecimal unitPrice, BigDecimal discountAmount) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity)).subtract(this.discountAmount);
    }

    public SaleTransaction getSaleTransaction() {
        return saleTransaction;
    }

    public void setSaleTransaction(SaleTransaction saleTransaction) {
        this.saleTransaction = saleTransaction;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
