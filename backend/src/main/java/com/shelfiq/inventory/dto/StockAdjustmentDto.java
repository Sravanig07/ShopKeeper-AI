package com.shelfiq.inventory.dto;

import com.shelfiq.inventory.entity.InventoryMovement;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentDto {

    @NotNull(message = "Quantity delta is required")
    private Integer quantityDelta;

    @NotNull(message = "Movement type is required")
    private InventoryMovement.MovementType movementType = InventoryMovement.MovementType.ADJUSTMENT;

    private String reason;
    private String referenceId;

    public StockAdjustmentDto() {
    }

    public StockAdjustmentDto(Integer quantityDelta, InventoryMovement.MovementType movementType, String reason, String referenceId) {
        this.quantityDelta = quantityDelta;
        this.movementType = movementType;
        this.reason = reason;
        this.referenceId = referenceId;
    }

    public Integer getQuantityDelta() {
        return quantityDelta;
    }

    public void setQuantityDelta(Integer quantityDelta) {
        this.quantityDelta = quantityDelta;
    }

    public InventoryMovement.MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(InventoryMovement.MovementType movementType) {
        this.movementType = movementType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
