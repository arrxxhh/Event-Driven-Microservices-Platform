package com.eventdriven.shared.events;

import com.eventdriven.shared.dto.OrderItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends BaseEvent {

    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private Double totalAmount;
    private String shippingAddress;
    private String paymentMethod;

    public OrderCreatedEvent() {
        super("OrderCreatedEvent");
    }

    public OrderCreatedEvent(String orderId, String customerId, List<OrderItem> items,
            Double totalAmount, String shippingAddress, String paymentMethod) {
        super("OrderCreatedEvent");
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }
}