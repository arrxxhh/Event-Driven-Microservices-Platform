package com.eventdriven.inventory.service;

import com.eventdriven.inventory.model.Product;
import com.eventdriven.inventory.repository.ProductRepository;
import com.eventdriven.shared.dto.OrderItem;
import com.eventdriven.shared.events.InventoryReservedEvent;
import com.eventdriven.shared.events.InventoryRollbackEvent;
import com.eventdriven.shared.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-events", groupId = "inventory-service")
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for order: {}", event.getOrderId());

        try {
            boolean allItemsReserved = true;
            String failureReason = null;

            // Try to reserve inventory for all items
            for (OrderItem item : event.getItems()) {
                Optional<Product> productOpt = productRepository.findByProductId(item.getProductId());

                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    if (product.hasAvailableStock(item.getQuantity())) {
                        product.reserveStock(item.getQuantity());
                        productRepository.save(product);
                        log.info("Reserved {} units of product {} for order {}",
                                item.getQuantity(), item.getProductId(), event.getOrderId());
                    } else {
                        allItemsReserved = false;
                        failureReason = "Insufficient stock for product: " + item.getProductId();
                        log.warn("Failed to reserve inventory for product: {} - insufficient stock",
                                item.getProductId());
                        break;
                    }
                } else {
                    allItemsReserved = false;
                    failureReason = "Product not found: " + item.getProductId();
                    log.warn("Failed to reserve inventory - product not found: {}", item.getProductId());
                    break;
                }
            }

            // Publish inventory reservation result
            for (OrderItem item : event.getItems()) {
                InventoryReservedEvent inventoryEvent = new InventoryReservedEvent(
                        event.getOrderId(),
                        item.getProductId(),
                        item.getQuantity(),
                        allItemsReserved,
                        allItemsReserved ? "Inventory reserved successfully" : failureReason);

                kafkaTemplate.send("inventory-events", event.getOrderId(), inventoryEvent);
                log.info("Published InventoryReservedEvent for product: {} - success: {}",
                        item.getProductId(), allItemsReserved);
            }

        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for order: {}", event.getOrderId(), e);

            // Publish failure event for all items
            for (OrderItem item : event.getItems()) {
                InventoryReservedEvent failureEvent = new InventoryReservedEvent(
                        event.getOrderId(),
                        item.getProductId(),
                        item.getQuantity(),
                        false,
                        "Error processing inventory reservation: " + e.getMessage());

                kafkaTemplate.send("inventory-events", event.getOrderId(), failureEvent);
            }
        }
    }

    @KafkaListener(topics = "payment-events", groupId = "inventory-service")
    public void handlePaymentProcessedEvent(com.eventdriven.shared.events.PaymentProcessedEvent event) {
        log.info("Received PaymentProcessedEvent for order: {}", event.getOrderId());

        // If payment failed, release reserved inventory
        if (!event.getSuccess()) {
            log.info("Payment failed for order: {}, releasing reserved inventory", event.getOrderId());
            // In a real implementation, you would need to retrieve the order details
            // to know which items to release. For now, we'll just log the action.
        }
    }

    public Optional<Product> getProduct(String productId) {
        log.info("Fetching product: {}", productId);
        return productRepository.findByProductId(productId);
    }

    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return (List<Product>) productRepository.findAll();
    }

    public Product createProduct(Product product) {
        log.info("Creating product: {}", product.getProductId());
        return productRepository.save(product);
    }

    public void updateProductStock(String productId, int quantity) {
        log.info("Updating stock for product: {} by quantity: {}", productId, quantity);
        productRepository.findByProductId(productId).ifPresent(product -> {
            product.setAvailableQuantity(product.getAvailableQuantity() + quantity);
            product.setUpdatedAt(java.time.LocalDateTime.now());
            productRepository.save(product);
        });
    }

    public void releaseReservedStock(String productId, int quantity) {
        log.info("Releasing reserved stock for product: {} quantity: {}", productId, quantity);
        productRepository.findByProductId(productId).ifPresent(product -> {
            product.releaseReservedStock(quantity);
            productRepository.save(product);
        });
    }
}