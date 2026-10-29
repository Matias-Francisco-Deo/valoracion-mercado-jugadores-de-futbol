package com.overcode.model.venta;

import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import com.overcode.model.venta.orden.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class OrdenDeCompra extends Order {

    private Token token;
    private Double price;
    private OrderStatus status;

    public OrdenDeCompra() {
        super();
        this.status = OrderStatus.OPEN;
    }

    public OrdenDeCompra(Long id, User seller, Player player, Token token, Double price, OrderStatus status, Instant createdAt) {
        super(id, seller, player, createdAt);
        this.token = token;
        this.price = price;
        this.status = status != null ? status : OrderStatus.OPEN;
    }

    public OrdenDeCompra(User seller, Player player, Token token, Double price) {
        this(null, seller, player, token, price, OrderStatus.OPEN, Instant.now());
    }

    public boolean isOpen() {
        return this.status == OrderStatus.OPEN;
    }

    public boolean belongsTo(Long candidateUserId) {
        return this.owner != null && this.owner.getId().equals(candidateUserId);
    }

    public void executePurchase(User buyer) {
        if (!isOpen()) {
            throw new IllegalStateException(
                    "Only OPEN orders can be purchased."
            );
        }

        buyer.debitCredits(price);
        owner.addCredits(price);
        buyer.addToken(token);

        markCompleted();
    }

    public void markCompleted() {
        if (!isOpen()) {
            throw new IllegalStateException("Only OPEN orders can be completed.");
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel(Long userId) {
        if (!belongsTo(userId)) {
            throw new IllegalStateException("You can only cancel your own orders.");
        }
        if (!isOpen()) {
            throw new IllegalStateException("Only OPEN orders can be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
