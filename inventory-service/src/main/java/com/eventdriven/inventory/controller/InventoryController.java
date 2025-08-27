package com.eventdriven.inventory.controller;

import com.eventdriven.inventory.model.Product;
import com.eventdriven.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Service", description = "APIs for managing inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Product> getProduct(@PathVariable String productId) {
        log.info("Getting product: {}", productId);
        Product product = inventoryService.getProduct(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("Getting all products");
        return ResponseEntity.ok(inventoryService.getAllProducts());
    }

    @PostMapping("/{productId}/reserve")
    @Operation(summary = "Reserve stock for a product")
    public ResponseEntity<Boolean> reserveStock(@PathVariable String productId, @RequestParam int quantity) {
        log.info("Reserving {} units of product: {}", quantity, productId);
        boolean reserved = inventoryService.reserveStock(productId, quantity);
        return ResponseEntity.ok(reserved);
    }

    @PostMapping("/{productId}/release")
    @Operation(summary = "Release reserved stock for a product")
    public ResponseEntity<Boolean> releaseReservedStock(@PathVariable String productId, @RequestParam int quantity) {
        log.info("Releasing {} units of product: {}", quantity, productId);
        boolean released = inventoryService.releaseReservedStock(productId, quantity);
        return ResponseEntity.ok(released);
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("Creating product: {}", product.getProductId());
        Product createdProduct = inventoryService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update a product")
    public ResponseEntity<Product> updateProduct(@PathVariable String productId, @RequestBody Product product) {
        log.info("Updating product: {}", productId);
        product.setProductId(productId);
        Product updatedProduct = inventoryService.updateProduct(product);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Inventory Service is running!");
    }
} 