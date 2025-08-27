package com.eventdriven.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("product")
public class Product {

    @Id
    private String productId;

    @Indexed
    private String name;

    private String description;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Double price;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product(String productId, String name, String description,
            Integer availableQuantity, Double price, String category) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
        this.price = price;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasAvailableStock(int quantity) {
        return availableQuantity >= quantity;
    }

    public void reserveStock(int quantity) {
        if (hasAvailableStock(quantity)) {
            this.availableQuantity -= quantity;
            this.reservedQuantity += quantity;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Insufficient stock for product: " + productId);
        }
    }

    public void releaseReservedStock(int quantity) {
        if (this.reservedQuantity >= quantity) {
            this.reservedQuantity -= quantity;
            this.availableQuantity += quantity;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot release more stock than reserved for product: " + productId);
        }
    }
}