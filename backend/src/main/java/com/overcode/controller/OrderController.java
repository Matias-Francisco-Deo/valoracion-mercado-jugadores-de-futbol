package com.overcode.controller;

import com.overcode.controller.dto.token.TokenPageResponseDTO;
import com.overcode.controller.dto.token.TokenSearchResponseDTO;
import com.overcode.service.interfaces.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order", description = "Endpoints for token operations")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/for-sale")
    public TokenPageResponseDTO searchTokensForSale(
            @RequestParam(required = false) String playerName,
            Pageable pageable
    ){
        Page<TokenSearchResponseDTO> page = orderService
                .searchTokensForSale(playerName, pageable)
                .map(TokenSearchResponseDTO::desdeModelo);

        return TokenPageResponseDTO.desdeModelo(page);
    }
}
