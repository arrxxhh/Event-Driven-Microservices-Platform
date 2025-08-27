package com.eventdriven.notification.service;

import com.eventdriven.shared.events.*;
import com.eventdriven.notification.model.Notification;
import com.eventdriven.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received order created event: {}", event.getOrderId());
        
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setOrderId(event.getOrderId());
        notification.setCustomerId(event.getCustomerId());
        notification.setType("ORDER_CREATED");
        notification.setMessage("Your order has been created successfully. Order ID: " + event.getOrderId());
        notification.setStatus("PENDING");
        notification.setChannel("EMAIL");
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
        log.info("Created notification for order: {}", event.getOrderId());
    }

    @KafkaListener(topics = "payment-events", groupId = "notification-service")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Received payment processed event: {}", event.getOrderId());
        
        String message = event.getStatus().equals("SUCCESS") 
            ? "Payment processed successfully for order: " + event.getOrderId()
            : "Payment failed for order: " + event.getOrderId();
            
        String type = event.getStatus().equals("SUCCESS") ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED";
        
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setOrderId(event.getOrderId());
        notification.setType(type);
        notification.setMessage(message);
        notification.setStatus("PENDING");
        notification.setChannel("EMAIL");
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
        log.info("Created payment notification for order: {}", event.getOrderId());
    }

    @KafkaListener(topics = "inventory-events", groupId = "notification-service")
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("Received inventory reserved event: {}", event.getOrderId());
        
        if (!event.isReserved()) {
            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setOrderId(event.getOrderId());
            notification.setType("INVENTORY_UNAVAILABLE");
            notification.setMessage("Sorry, the requested quantity is not available for product: " + event.getProductId());
            notification.setStatus("PENDING");
            notification.setChannel("EMAIL");
            notification.setCreatedAt(LocalDateTime.now());
            
            notificationRepository.save(notification);
            log.info("Created inventory notification for order: {}", event.getOrderId());
        }
    }

    public List<Notification> getAllNotifications() {
        return (List<Notification>) notificationRepository.findAll();
    }

    public Notification getNotificationById(String notificationId) {
        return notificationRepository.findById(notificationId).orElse(null);
    }

    public List<Notification> getNotificationsByCustomer(String customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }

    public List<Notification> getNotificationsByOrder(String orderId) {
        return notificationRepository.findByOrderId(orderId);
    }

    public Notification sendNotification(Notification notification) {
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus("PENDING");
        
        // Simulate sending notification
        log.info("Sending notification: {}", notification.getMessage());
        
        // In a real implementation, you would integrate with email/SMS services
        notification.setStatus("SENT");
        notification.setSentAt(LocalDateTime.now());
        
        return notificationRepository.save(notification);
    }
} 