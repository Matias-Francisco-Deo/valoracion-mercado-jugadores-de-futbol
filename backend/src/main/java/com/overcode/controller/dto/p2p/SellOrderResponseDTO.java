package com.overcode.controller.dto.p2p;

import com.overcode.model.venta.OrdenDeCompra;
import com.overcode.model.venta.orden.OrderStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class SellOrderResponseDTO {
    private final Long id;
    private final Long userId;
    private final Long playerId;
    private final Long tokenId;
    private final Double price;
    private final OrderStatus status;
    private final Instant createdAt;

    public SellOrderResponseDTO(OrdenDeCompra order) {
        this.id = order.getId();
        this.userId = order.getOwner().getId();
        this.playerId = order.getPlayer().getId();
        this.tokenId = order.getToken().getId();
        this.price = order.getPrice();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
    }

    public static SellOrderResponseDTO from(OrdenDeCompra order) {
        return new SellOrderResponseDTO(order);
    }

    public static List<SellOrderResponseDTO> from(List<OrdenDeCompra> orders) {
        if (orders == null) {
            return java.util.Collections.emptyList();
        }
        return orders.stream().map(SellOrderResponseDTO::from).toList();
    }
}
