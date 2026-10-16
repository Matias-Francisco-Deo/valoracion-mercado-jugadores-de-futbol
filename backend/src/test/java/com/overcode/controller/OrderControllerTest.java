package com.overcode.controller;

import com.overcode.model.BuyOrder;
import com.overcode.model.orden.OrderStatus;
import com.overcode.model.SellOrder;
import com.overcode.model.SellPost;
import com.overcode.model.User;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.interfaces.P2POrderService;
import com.overcode.service.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

        verify(p2pOrderService, times(1)).executeMarketBuy(any(BuyOrder.class));
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
                .when(p2pOrderService).executeMarketBuy(any(BuyOrder.class));

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

        SellOrder createdOrder = new SellOrder(1L, USER_ID, PLAYER_ID, 101L, 12, OrderStatus.OPEN, Instant.now());
        when(p2pOrderService.createSellOrders(any(SellPost.class))).thenReturn(List.of(createdOrder));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService)).build();

        mockMvc.perform(post("/orders/sell")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].playerId").value(10))
                .andExpect(jsonPath("$[0].tokenId").value(101L))
                .andExpect(jsonPath("$[0].price").value(12))
                .andExpect(jsonPath("$[0].status").value("OPEN"));
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

    // ------------------------------ /orders/{id}/cancel ------------------------------

    @Test
    void cancelOrderDevuelveOkCuandoSeCancela() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperarPorEmail("ivan@gmail.com")).thenReturn(Optional.of(user));

        Long orderId = 123L;
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

        mockMvc.perform(post("/orders/123/cancel"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(p2pOrderService);
    }
}
