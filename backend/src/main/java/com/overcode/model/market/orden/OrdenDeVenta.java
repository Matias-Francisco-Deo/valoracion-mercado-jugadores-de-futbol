package com.overcode.model.market.orden;

import com.overcode.model.exception.OperacionInvalidaOrdenException;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class OrdenDeVenta extends Orden {

    private Token token;
    private Double price;
    private OrderStatus status;

    public OrdenDeVenta() {
        super();
        this.status = OrderStatus.OPEN;
    }

    public OrdenDeVenta(Long id, User seller, Player player, Token token, Double price, OrderStatus status, Instant createdAt) {
        super(id, seller, player, createdAt);
        this.token = token;
        this.price = price;
        this.status = status != null ? status : OrderStatus.OPEN;
    }

    public OrdenDeVenta(User seller, Player player, Token token, Double price) {
        this(null, seller, player, token, price, OrderStatus.OPEN, Instant.now());
    }

    public boolean isOpen() {
        return this.status == OrderStatus.OPEN;
    }

    public boolean belongsTo(User anotherUser) {
        return this.owner != null && this.owner.getId().equals(anotherUser.getId());
    }

    public void comprarPorUsuario(User buyer) {
        if (!isOpen()) {
            throw new OperacionInvalidaOrdenException(
                    "Sólo se pueden comprar órdenes abiertas"
            );
        }

        buyer.debitCredits(price);
        owner.addCredits(price);
        buyer.addToken(token);

        markCompleted();
    }

    public void markCompleted() {
        if (!isOpen()) {
            throw new OperacionInvalidaOrdenException("Only OPEN orders can be completed.");
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel(User owner) {
        if (!belongsTo(owner)) {
            throw new OperacionInvalidaOrdenException("You can only cancel your own orders.");
        }
        if (!isOpen()) {
            throw new OperacionInvalidaOrdenException("Only OPEN orders can be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
