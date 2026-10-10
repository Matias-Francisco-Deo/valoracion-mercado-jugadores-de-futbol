package com.overcode.controller.dto.order;

public record OrderBuyResponseDTO(
        Long playerId,
        Integer quantity
) {
    public static OrderBuyResponseDTO desdeModelo(OrderBuy orderBuy){
        return new OrderBuyResponseDTO(
                orderBuy.playerId(),
                orderBuy.quantity()
        );
    }
}

