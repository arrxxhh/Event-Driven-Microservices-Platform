package com.eventdriven.inventory.repository;

import com.eventdriven.inventory.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, String> {
    
    Optional<Product> findByProductId(String productId);
    List<Product> findByCategory(String category);
    List<Product> findByAvailableQuantityGreaterThan(int quantity);
} 