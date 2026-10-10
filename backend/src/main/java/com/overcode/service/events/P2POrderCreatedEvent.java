package com.overcode.service.events;

import org.springframework.context.ApplicationEvent;
import java.util.UUID;

public class P2POrderCreatedEvent extends ApplicationEvent {
    private final UUID orderId;

    public P2POrderCreatedEvent(Object source, UUID orderId) {
        super(source);
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
