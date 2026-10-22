package com.overcode.controller;

import com.overcode.controller.exception.GlobalExceptionHandler;
import com.overcode.model.BuyOrder;
import com.overcode.model.Player;
import com.overcode.model.orden.OrderStatus;
import com.overcode.model.SellOrder;
import com.overcode.model.SellPost;
import com.overcode.model.User;
import com.overcode.model.token.Token;
import com.overcode.model.orden.OrderSellSummary;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.interfaces.P2POrderService;
import com.overcode.service.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private MockMvc mockMvcWith(P2POrderService p2pOrderService, UserService userService) {
        return MockMvcBuilders.standaloneSetup(controllerWith(p2pOrderService, userService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
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

        User seller = new User("placeholder", "placeholder@gmail.com", "placeholder");
        seller.setId(USER_ID);
        Player player = new Player();
        player.setId(PLAYER_ID);
        Token token = new Token(101L, seller, player);
        SellOrder createdOrder = new SellOrder(1L, seller, player, token, 12, OrderStatus.OPEN, Instant.now());
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

    // ------------------------------ /orders/{playerId}/cancel ------------------------------

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

    // ------------------------------ /orders/for-sell ------------------------------

    @Test
    void searchOpenOrdersSellSinPrincipalDevuelveTodasLasOrdenesAbiertas() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        OrderSellSummary summaryDTO = new OrderSellSummary(PLAYER_ID, "Lionel Messi", 5, 15.0);
        Page<OrderSellSummary> page = new PageImpl<>(List.of(summaryDTO), PageRequest.of(0, 20), 1);

        when(p2pOrderService.buscarOrdersSellSummaryAbiertas(isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/for-sell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerId").value(PLAYER_ID))
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(5))
                .andExpect(jsonPath("$.content[0].unitPrice").value(15.0))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(p2pOrderService, times(1)).buscarOrdersSellSummaryAbiertas(isNull(), isNull(), any(Pageable.class));
        verifyNoInteractions(userService);
    }

    @Test
    void searchOpenOrdersSellConUsuarioAutenticadoExcluyeSusPropiasOrdenes() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperarPorEmail("ivan@gmail.com")).thenReturn(Optional.of(user));

        OrderSellSummary summaryDTO = new OrderSellSummary(PLAYER_ID, "Lionel Messi", 3, 20.0);
        Page<OrderSellSummary> page = new PageImpl<>(List.of(summaryDTO), PageRequest.of(0, 20), 1);

        when(p2pOrderService.buscarOrdersSellSummaryAbiertas(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(page);

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/for-sell").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerId").value(PLAYER_ID))
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(3))
                .andExpect(jsonPath("$.content[0].unitPrice").value(20.0));

        verify(p2pOrderService, times(1)).buscarOrdersSellSummaryAbiertas(eq(USER_ID), isNull(), any(Pageable.class));
    }

    @Test
    void searchOpenOrdersSellConFiltroPorNombreDeJugadorDevuelveOrdenesFiltradas() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        OrderSellSummary summaryDTO = new OrderSellSummary(PLAYER_ID, "Lionel Messi", 2, 25.0);
        Page<OrderSellSummary> page = new PageImpl<>(List.of(summaryDTO), PageRequest.of(0, 20), 1);

        when(p2pOrderService.buscarOrdersSellSummaryAbiertas(isNull(), eq("Messi"), any(Pageable.class)))
                .thenReturn(page);

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/for-sell").param("nombre", "Messi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(2));

        verify(p2pOrderService, times(1)).buscarOrdersSellSummaryAbiertas(isNull(), eq("Messi"), any(Pageable.class));
    }

    @Test
    void searchOpenOrdersSellConPaginacionPersonalizada() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        Page<OrderSellSummary> pageVacia = new PageImpl<>(List.of(), PageRequest.of(2, 5), 0);
        when(p2pOrderService.buscarOrdersSellSummaryAbiertas(isNull(), isNull(), any(Pageable.class)))
                .thenReturn(pageVacia);

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/for-sell")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(p2pOrderService).buscarOrdersSellSummaryAbiertas(isNull(), isNull(), captor.capture());
        assertEquals(2, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
    }

    @Test
    void searchOpenOrdersSellDevuelveListaVaciaCuandoNoHayOrdenes() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        Page<OrderSellSummary> pageVacia = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(p2pOrderService.buscarOrdersSellSummaryAbiertas(isNull(), isNull(), any(Pageable.class)))
                .thenReturn(pageVacia);

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/for-sell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void searchOpenOrdersSellConPrincipalCuyoUsuarioNoExisteDevuelve409() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        when(userService.recuperarPorEmail("ivan@gmail.com")).thenReturn(Optional.empty());

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/for-sell").principal(principal))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User not found"));

        verifyNoInteractions(p2pOrderService);
    }

    // ------------------------------ /orders/{id}/ordersSell ------------------------------

    @Test
    void searchUserOpenOrdersSellDevuelveOrdenesAbiertasDelUsuario() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        OrderSellSummary summaryDTO = new OrderSellSummary(PLAYER_ID, "Lionel Messi", 4, 30.0);
        Page<OrderSellSummary> page = new PageImpl<>(List.of(summaryDTO), PageRequest.of(0, 10), 1);

        when(p2pOrderService.buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(page);

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/" + USER_ID + "/ordersSell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerId").value(PLAYER_ID))
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(4))
                .andExpect(jsonPath("$.content[0].unitPrice").value(30.0))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(p2pOrderService, times(1)).buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), isNull(), any(Pageable.class));
    }

    @Test
    void searchUserOpenOrdersSellConFiltroPorNombreDeJugadorDevuelveOrdenesFiltradas() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        OrderSellSummary summaryDTO = new OrderSellSummary(PLAYER_ID, "Lionel Messi", 1, 35.0);
        Page<OrderSellSummary> page = new PageImpl<>(List.of(summaryDTO), PageRequest.of(0, 10), 1);

        when(p2pOrderService.buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), eq("Messi"), any(Pageable.class)))
                .thenReturn(page);

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/" + USER_ID + "/ordersSell").param("nombre", "Messi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(1));

        verify(p2pOrderService, times(1)).buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), eq("Messi"), any(Pageable.class));
    }

    @Test
    void searchUserOpenOrdersSellConPaginacionPersonalizada() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        Page<OrderSellSummary> pageVacia = new PageImpl<>(List.of(), PageRequest.of(1, 5), 0);
        when(p2pOrderService.buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(pageVacia);

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/" + USER_ID + "/ordersSell")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(p2pOrderService).buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), isNull(), captor.capture());
        assertEquals(1, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
    }

    @Test
    void searchUserOpenOrdersSellDevuelveListaVaciaCuandoUsuarioNoTieneOrdenes() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        Page<OrderSellSummary> pageVacia = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(p2pOrderService.buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(pageVacia);

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/" + USER_ID + "/ordersSell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void searchUserOpenOrdersSellDevuelve404CuandoUsuarioNoExiste() throws Exception {
        P2POrderService p2pOrderService = mock(P2POrderService.class);
        UserService userService = mock(UserService.class);

        when(p2pOrderService.buscarOrdersSellSummaryAbiertasPorUsuario(eq(999L), isNull(), any(Pageable.class)))
                .thenThrow(new EntidadNoEncontradaException("El usuario no existe"));

        MockMvc mockMvc = mockMvcWith(p2pOrderService, userService);

        mockMvc.perform(get("/orders/999/ordersSell"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }
}
