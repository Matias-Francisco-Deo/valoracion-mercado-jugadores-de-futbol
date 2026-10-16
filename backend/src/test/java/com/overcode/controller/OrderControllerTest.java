package com.overcode.controller;

import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;
import com.overcode.model.User;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.interfaces.P2POrderService;
import com.overcode.service.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest {

    private final Principal principal = () -> "ivan@gmail.com";
    private static final Long USER_ID = 735L;
    private static final Long PLAYER_ID = 10L;

    private OrderController controllerWith(P2POrderService p2pOrderService, UserService userService) {
        return new OrderController(p2pOrderService, userService);
    }

    // ------------------------------ /orders/buy ------------------------------

    @Test
    void buyTokensDevuelveOkCuandoElSweepSeEjecuta() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperarPorEmail("ivan@gmail.com")).thenReturn(Optional.of(user));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService)).build();

        mockMvc.perform(post("/orders/buy")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tokens successfully purchased"))
                .andExpect(jsonPath("$.playerId").value(10))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(p2pOrderService, times(1)).executeMarketBuy(USER_ID, PLAYER_ID, 5);
    }

    @Test
    void buyTokensSinPrincipalDevuelve401() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService)).build();

        mockMvc.perform(post("/orders/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":5}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(p2pOrderService);
    }

    @Test
    void buyTokensConCantidadInvalidaDevuelve400() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService)).build();

        mockMvc.perform(post("/orders/buy")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":-1}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(p2pOrderService);
    }

    @Test
    void buyTokensSinLiquidezDevuelve409() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperarPorEmail("ivan@gmail.com")).thenReturn(Optional.of(user));
        doThrow(new InsufficientLiquidityException("Not enough liquidity to buy 5 tokens for player 10"))
                .when(p2pOrderService).executeMarketBuy(USER_ID, PLAYER_ID, 5);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService)).build();

        mockMvc.perform(post("/orders/buy")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":5}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Not enough liquidity to buy 5 tokens for player 10"));
    }

    // ------------------------------ /orders/sell ------------------------------

    @Test
    void sellTokensDevuelveCreatedConLaOrdenPublicada() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperarPorEmail("ivan@gmail.com")).thenReturn(Optional.of(user));

        UUID orderId = UUID.randomUUID();
        P2POrder createdOrder = P2POrder.builder()
                .id(orderId)
                .userId(USER_ID)
                .playerId(PLAYER_ID)
                .type(OrderType.SELL)
                .quantity(5)
                .pricePerToken(new BigDecimal("12.00"))
                .status(OrderStatus.OPEN)
                .createdAt(Instant.now())
                .version(null)
                .build();
        when(p2pOrderService.createSellOrder(USER_ID, PLAYER_ID, 5)).thenReturn(createdOrder);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService)).build();

        mockMvc.perform(post("/orders/sell")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.playerId").value(10))
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.pricePerToken").value(12.00))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void sellTokensSinPrincipalDevuelve401() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService)).build();

        mockMvc.perform(post("/orders/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":5}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(p2pOrderService);
    }

    @Test
    void sellTokensConCantidadInvalidaDevuelve400() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService)).build();

        mockMvc.perform(post("/orders/sell")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":0}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(p2pOrderService);
    }

    // ------------------------------ /orders/{playerId}/cancel ------------------------------

    @Test
    void cancelOrderDevuelveOkCuandoSeCancela() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperarPorEmail("ivan@gmail.com")).thenReturn(Optional.of(user));

        UUID orderId = UUID.randomUUID();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService)).build();

        mockMvc.perform(post("/orders/" + orderId + "/cancel").principal(principal))
                .andExpect(status().isOk());

        verify(p2pOrderService, times(1)).cancelOrder(orderId, USER_ID);
    }

    @Test
    void cancelOrderSinPrincipalDevuelve401() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService)).build();

        mockMvc.perform(post("/orders/" + UUID.randomUUID() + "/cancel"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(p2pOrderService);
    }
}
