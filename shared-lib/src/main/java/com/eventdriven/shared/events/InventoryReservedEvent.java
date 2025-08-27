package com.eventdriven.shared.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryReservedEvent extends BaseEvent {

    private String orderId;
    private String productId;
    private Integer quantity;
    private Boolean success;
    private String message;

    public InventoryReservedEvent() {
        super("InventoryReservedEvent");
    }

    public InventoryReservedEvent(String orderId, String productId, Integer quantity,
            Boolean success, String message) {
        super("InventoryReservedEvent");
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.success = success;
        this.message = message;
    }
}