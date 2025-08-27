package com.eventdriven.shared.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationEvent extends BaseEvent {

    private String orderId;
    private String customerId;
    private String notificationType;
    private String message;
    private String recipient;

    public NotificationEvent() {
        super("NotificationEvent");
    }

    public NotificationEvent(String orderId, String customerId, String notificationType,
            String message, String recipient) {
        super("NotificationEvent");
        this.orderId = orderId;
        this.customerId = customerId;
        this.notificationType = notificationType;
        this.message = message;
        this.recipient = recipient;
    }
}