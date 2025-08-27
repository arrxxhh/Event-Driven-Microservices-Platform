package com.eventdriven.order.service;

import com.eventdriven.order.model.Order;
import com.eventdriven.order.repository.OrderRepository;
import com.eventdriven.shared.dto.OrderRequest;
import com.eventdriven.shared.dto.OrderResponse;
import com.eventdriven.shared.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());

        // Create order
        Order order = new Order(
                request.getCustomerId(),
                request.getItems(),
                request.getTotalAmount(),
                request.getShippingAddress(),
                request.getPaymentMethod());

        // Save to Redis
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getOrderId());

        // Publish event to Kafka
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getOrderId(),
                savedOrder.getCustomerId(),
                savedOrder.getItems(),
                savedOrder.getTotalAmount(),
                savedOrder.getShippingAddress(),
                savedOrder.getPaymentMethod());

        kafkaTemplate.send("order-events", savedOrder.getOrderId(), event);
        log.info("OrderCreatedEvent published for order: {}", savedOrder.getOrderId());

        return mapToResponse(savedOrder);
    }

    public Optional<OrderResponse> getOrder(String orderId) {
        log.info("Fetching order: {}", orderId);
        return orderRepository.findByOrderId(orderId)
                .map(this::mapToResponse);
    }

    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        return ((List<Order>) orderRepository.findAll())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByCustomer(String customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void updateOrderStatus(String orderId, String status) {
        log.info("Updating order {} status to: {}", orderId, status);
        orderRepository.findByOrderId(orderId).ifPresent(order -> {
            order.setStatus(status);
            order.setUpdatedAt(java.time.LocalDateTime.now());
            orderRepository.save(order);
        });
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerId(),
                order.getItems(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getPaymentMethod(),
                order.getCreatedAt(),
                order.getUpdatedAt());
    }
}