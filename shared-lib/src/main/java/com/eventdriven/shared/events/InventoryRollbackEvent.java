package com.eventdriven.shared.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryRollbackEvent extends BaseEvent {

    private String orderId;
    private String productId;
    private Integer quantity;
    private String reason;

    public InventoryRollbackEvent() {
        super("InventoryRollbackEvent");
    }

    public InventoryRollbackEvent(String orderId, String productId, Integer quantity, String reason) {
        super("InventoryRollbackEvent");
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.reason = reason;
    }
}