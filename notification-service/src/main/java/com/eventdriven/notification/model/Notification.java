package com.eventdriven.notification.model;

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
@RedisHash("notification")
public class Notification {

    @Id
    private String notificationId;

    @Indexed
    private String orderId;

    @Indexed
    private String customerId;

    private String type; // ORDER_CREATED, PAYMENT_SUCCESS, PAYMENT_FAILED, etc.
    private String message;
    private String status; // SENT, PENDING, FAILED
    private String channel; // EMAIL, SMS, PUSH
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
} 