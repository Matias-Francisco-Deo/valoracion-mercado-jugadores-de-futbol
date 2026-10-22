package com.overcode.controller.dto.p2p;

import com.overcode.model.orden.OrderStatus;
import com.overcode.model.SellOrder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class SellOrderResponseDTO {
    private final Long id;
    private final Long userId;
    private final Long playerId;
    private final Long tokenId;
    private final Integer price;
    private final OrderStatus status;
    private final Instant createdAt;

    public SellOrderResponseDTO(SellOrder order) {
        this.id = order.getId();
        this.userId = order.getOwner().getId();
        this.playerId = order.getPlayer().getId();
        this.tokenId = order.getToken().getId();
        this.price = order.getPrice();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
    }

    public static SellOrderResponseDTO from(SellOrder order) {
        return new SellOrderResponseDTO(order);
    }

    public static List<SellOrderResponseDTO> from(List<SellOrder> orders) {
        if (orders == null) {
            return java.util.Collections.emptyList();
        }
        return orders.stream().map(SellOrderResponseDTO::from).toList();
    }
}
