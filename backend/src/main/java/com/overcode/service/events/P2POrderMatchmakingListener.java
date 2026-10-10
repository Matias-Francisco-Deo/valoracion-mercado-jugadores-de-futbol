package com.overcode.service.events;

import com.overcode.service.interfaces.P2POrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class P2POrderMatchmakingListener {

    private static final Logger logger = LoggerFactory.getLogger(P2POrderMatchmakingListener.class);
    private final P2POrderService p2pOrderService;

    public P2POrderMatchmakingListener(P2POrderService p2pOrderService) {
        this.p2pOrderService = p2pOrderService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedEvent(P2POrderCreatedEvent event) {
        try {
            logger.info("Iniciando matchmaking asincrónico para la orden: {}", event.getOrderId());
            p2pOrderService.processMatchmaking(event.getOrderId());
        } catch (Exception e) {
            logger.error("Error procesando matchmaking para orden: {}", event.getOrderId(), e);
        }
    }
}
