package com.eventdriven.notification.repository;

import com.eventdriven.notification.model.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, String> {
    
    List<Notification> findByCustomerId(String customerId);
    List<Notification> findByOrderId(String orderId);
    List<Notification> findByStatus(String status);
    List<Notification> findByType(String type);
} 