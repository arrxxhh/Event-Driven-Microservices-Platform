package com.eventdriven.shared.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentProcessedEvent extends BaseEvent {

    private String orderId;
    private String customerId;
    private Double amount;
    private String paymentMethod;
    private Boolean success;
    private String transactionId;
    private String message;

    public PaymentProcessedEvent() {
        super("PaymentProcessedEvent");
    }

    public PaymentProcessedEvent(String orderId, String customerId, Double amount,
            String paymentMethod, Boolean success, String transactionId, String message) {
        super("PaymentProcessedEvent");
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
    }
}