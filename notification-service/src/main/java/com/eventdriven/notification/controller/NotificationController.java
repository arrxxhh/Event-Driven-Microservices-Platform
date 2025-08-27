package com.eventdriven.notification.controller;

import com.eventdriven.notification.model.Notification;
import com.eventdriven.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Service", description = "APIs for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get all notifications")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        log.info("Getting all notifications");
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<Notification> getNotificationById(@PathVariable String notificationId) {
        log.info("Getting notification by ID: {}", notificationId);
        Notification notification = notificationService.getNotificationById(notificationId);
        if (notification != null) {
            return ResponseEntity.ok(notification);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get notifications by customer ID")
    public ResponseEntity<List<Notification>> getNotificationsByCustomer(@PathVariable String customerId) {
        log.info("Getting notifications for customer: {}", customerId);
        return ResponseEntity.ok(notificationService.getNotificationsByCustomer(customerId));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get notifications by order ID")
    public ResponseEntity<List<Notification>> getNotificationsByOrder(@PathVariable String orderId) {
        log.info("Getting notifications for order: {}", orderId);
        return ResponseEntity.ok(notificationService.getNotificationsByOrder(orderId));
    }

    @PostMapping
    @Operation(summary = "Send a new notification")
    public ResponseEntity<Notification> sendNotification(@RequestBody Notification notification) {
        log.info("Sending notification: {}", notification.getMessage());
        Notification sentNotification = notificationService.sendNotification(notification);
        return ResponseEntity.ok(sentNotification);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running!");
    }
} 