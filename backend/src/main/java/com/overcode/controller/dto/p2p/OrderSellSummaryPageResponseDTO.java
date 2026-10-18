package com.overcode.controller.dto.p2p;

import com.overcode.model.dto.OrderSellSummaryDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record OrderSellSummaryPageResponseDTO(
        List<OrderSellSummaryResponseDTO> content,
        int number,
        int size,
        long totalElements,
        int totalPages
) {
    public static OrderSellSummaryPageResponseDTO desdeModelo(Page<OrderSellSummaryDTO> modelo) {
        return new OrderSellSummaryPageResponseDTO(
                modelo.getContent().stream().map(OrderSellSummaryResponseDTO::desdeModelo).toList(),
                modelo.getNumber(),
                modelo.getSize(),
                modelo.getTotalElements(),
                modelo.getTotalPages()
        );
    }
}