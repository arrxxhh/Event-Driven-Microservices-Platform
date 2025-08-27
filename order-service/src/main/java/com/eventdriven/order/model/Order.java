package com.eventdriven.order.model;

import com.eventdriven.shared.dto.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("order")
public class Order {

    @Id
    private String orderId;

    @Indexed
    private String customerId;

    private List<OrderItem> items;
    private Double totalAmount;
    private String status;
    private String shippingAddress;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order(String customerId, List<OrderItem> items, Double totalAmount,
            String shippingAddress, String paymentMethod) {
        this.orderId = java.util.UUID.randomUUID().toString();
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = "PENDING";
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}