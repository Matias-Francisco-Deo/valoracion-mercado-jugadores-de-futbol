package com.overcode.controller;

import com.overcode.controller.exception.GlobalExceptionHandler;
import com.overcode.model.player.Player;
import com.overcode.model.token.Token;
import com.overcode.model.user.User;
import com.overcode.model.venta.BuyOrder;
import com.overcode.model.venta.OrdenDeCompra;
import com.overcode.model.venta.SellPost;
import com.overcode.model.venta.orden.OrderSellSummary;
import com.overcode.model.venta.orden.OrderStatus;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.InsufficientLiquidityException;
import com.overcode.service.interfaces.TokenMarketService;
import com.overcode.service.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest {

    private static final Long USER_ID = 735L;
    private final Principal principal = USER_ID::toString;
    private static final Long PLAYER_ID = 10L;

    @Autowired
    private UserService userService;

    private OrderController controllerWith(TokenMarketService tokenMarketService, UserService userService) {
        return new OrderController(tokenMarketService, userService);
    }

    private MockMvc mockMvcWith(TokenMarketService tokenMarketService, UserService userService) {
        return MockMvcBuilders.standaloneSetup(controllerWith(tokenMarketService, userService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ------------------------------ /orders/buy ------------------------------

    @Test
    void buyTokensDevuelveOkCuandoElSweepSeEjecuta() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperar(USER_ID)).thenReturn(user);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(tokenMarketService, userService)).build();

        mockMvc.perform(post("/orders/buy")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":5}"))
                .andExpect(status().isOk());

        verify(tokenMarketService, times(1)).buy(any(BuyOrder.class));
    }


    @Test
    void buyTokensConCantidadInvalidaDevuelve400() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(tokenMarketService, userService)).build();

        mockMvc.perform(post("/orders/buy")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":-1}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tokenMarketService);
    }

    @Test
    void buyTokensSinLiquidezDevuelve409() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperar(USER_ID)).thenReturn(user);
        doThrow(new InsufficientLiquidityException("Not enough liquidity to buy 5 tokens for player 10"))
                .when(tokenMarketService).buy(any(BuyOrder.class));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(tokenMarketService, userService)).build();

        mockMvc.perform(post("/orders/buy")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":5}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Not enough liquidity to buy 5 tokens for player 10"));
    }

    // ------------------------------ /orders/sell ------------------------------

    @Test
    void sellTokensDevuelveCreated() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperar(USER_ID)).thenReturn(user);

        User seller = new User("placeholder", "placeholder@gmail.com", "placeholder");
        seller.setId(USER_ID);
        Player player = new Player();
        player.setId(PLAYER_ID);
        Token token = new Token(101L, seller, player);
        OrdenDeCompra createdOrder = new OrdenDeCompra(1L, seller, player, token, 12d, OrderStatus.OPEN, Instant.now());
        when(tokenMarketService.sell(any(SellPost.class))).thenReturn(List.of(createdOrder));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(tokenMarketService, userService)).build();

        mockMvc.perform(post("/orders/sell")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":1}"))
                .andExpect(status().isCreated());
    }


    @Test
    void sellTokensConCantidadInvalidaDevuelve400() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(tokenMarketService, userService)).build();

        mockMvc.perform(post("/orders/sell")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":10,\"quantity\":0}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tokenMarketService);
    }

    // ------------------------------ /orders/{playerId}/cancel ------------------------------

    @Test
    void cancelOrderDevuelveOkCuandoSeCancela() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperar(USER_ID)).thenReturn(user);

        Long orderId = 123L;
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(tokenMarketService, userService)).build();

        mockMvc.perform(post("/orders/" + orderId + "/cancel").principal(principal))
                .andExpect(status().isOk());

        verify(tokenMarketService, times(1)).cancel(orderId, USER_ID);
    }


    // ------------------------------ /orders/forSell ------------------------------

    @Test
    void searchOpenOrdersSellConUsuarioAutenticadoExcluyeSusPropiasOrdenes() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        when(userService.recuperar(USER_ID)).thenReturn(user);

        OrderSellSummary summaryDTO = new OrderSellSummary(PLAYER_ID, "Lionel Messi", 3, 20.0);
        Page<OrderSellSummary> page = new PageImpl<>(List.of(summaryDTO), PageRequest.of(0, 20), 1);

        when(tokenMarketService.buscarOrdersSellSummaryAbiertas(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(page);

        MockMvc mockMvc = mockMvcWith(tokenMarketService, userService);

        mockMvc.perform(get("/orders/forSell").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerId").value(PLAYER_ID))
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(3))
                .andExpect(jsonPath("$.content[0].unitPrice").value(20.0));

        verify(tokenMarketService, times(1)).buscarOrdersSellSummaryAbiertas(eq(USER_ID), isNull(), any(Pageable.class));
    }

    @Test
    void searchOpenOrdersSellConFiltroPorNombreDeJugadorDevuelveOrdenesFiltradas() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);

        OrderSellSummary summaryDTO = new OrderSellSummary(PLAYER_ID, "Lionel Messi", 2, 25.0);
        Page<OrderSellSummary> page = new PageImpl<>(List.of(summaryDTO), PageRequest.of(0, 20), 1);

        when(tokenMarketService.buscarOrdersSellSummaryAbiertas(isNull(), eq("Messi"), any(Pageable.class)))
                .thenReturn(page);

        MockMvc mockMvc = mockMvcWith(tokenMarketService, userService);

        mockMvc.perform(get("/orders/forSell")
                        .principal(principal)
                        .param("nombre", "Messi")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(2));

        verify(tokenMarketService, times(1)).buscarOrdersSellSummaryAbiertas(isNull(), eq("Messi"), any(Pageable.class));
    }

    @Test
    void searchOpenOrdersSellConPaginacionPersonalizada() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);

        Page<OrderSellSummary> pageVacia = new PageImpl<>(List.of(), PageRequest.of(2, 5), 0);
        when(tokenMarketService.buscarOrdersSellSummaryAbiertas(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(pageVacia);

        MockMvc mockMvc = mockMvcWith(tokenMarketService, userService);

        mockMvc.perform(get("/orders/forSell")
                        .principal(principal)
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(tokenMarketService).buscarOrdersSellSummaryAbiertas(eq(USER_ID), isNull(), captor.capture());
        assertEquals(2, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
    }

    @Test
    void searchOpenOrdersSellDevuelveListaVaciaCuandoNoHayOrdenes() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);

        Page<OrderSellSummary> pageVacia = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(tokenMarketService.buscarOrdersSellSummaryAbiertas(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(pageVacia);

        MockMvc mockMvc = mockMvcWith(tokenMarketService, userService);

        mockMvc.perform(get("/orders/forSell")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(tokenMarketService).buscarOrdersSellSummaryAbiertas(eq(USER_ID), isNull(), any(Pageable.class));
    }

    @Test
    void searchOpenOrdersSellConPrincipalCuyoUsuarioNoExisteDevuelve404() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        when(tokenMarketService.buscarOrdersSellSummaryAbiertas(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenThrow(new EntidadNoEncontradaException("User not found"));

        MockMvc mockMvc = mockMvcWith(tokenMarketService, userService);

        mockMvc.perform(get("/orders/forSell")
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(tokenMarketService).buscarOrdersSellSummaryAbiertas(eq(USER_ID), isNull(), any(Pageable.class));
    }

    // ------------------------------ /orders/{id}/ordersSell ------------------------------

    @Test
    void searchUserOpenOrdersSellDevuelveOrdenesAbiertasDelUsuario() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);

        OrderSellSummary summaryDTO = new OrderSellSummary(PLAYER_ID, "Lionel Messi", 4, 30.0);
        Page<OrderSellSummary> page = new PageImpl<>(List.of(summaryDTO), PageRequest.of(0, 10), 1);

        when(tokenMarketService.buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(page);

        MockMvc mockMvc = mockMvcWith(tokenMarketService, userService);

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

        verify(tokenMarketService, times(1)).buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), isNull(), any(Pageable.class));
    }

    @Test
    void searchUserOpenOrdersSellConFiltroPorNombreDeJugadorDevuelveOrdenesFiltradas() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);

        OrderSellSummary summaryDTO = new OrderSellSummary(PLAYER_ID, "Lionel Messi", 1, 35.0);
        Page<OrderSellSummary> page = new PageImpl<>(List.of(summaryDTO), PageRequest.of(0, 10), 1);

        when(tokenMarketService.buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), eq("Messi"), any(Pageable.class)))
                .thenReturn(page);

        MockMvc mockMvc = mockMvcWith(tokenMarketService, userService);

        mockMvc.perform(get("/orders/" + USER_ID + "/ordersSell").param("nombre", "Messi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(1));

        verify(tokenMarketService, times(1)).buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), eq("Messi"), any(Pageable.class));
    }

    @Test
    void searchUserOpenOrdersSellConPaginacionPersonalizada() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);

        Page<OrderSellSummary> pageVacia = new PageImpl<>(List.of(), PageRequest.of(1, 5), 0);
        when(tokenMarketService.buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(pageVacia);

        MockMvc mockMvc = mockMvcWith(tokenMarketService, userService);

        mockMvc.perform(get("/orders/" + USER_ID + "/ordersSell")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(tokenMarketService).buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), isNull(), captor.capture());
        assertEquals(1, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
    }

    @Test
    void searchUserOpenOrdersSellDevuelveListaVaciaCuandoUsuarioNoTieneOrdenes() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);

        Page<OrderSellSummary> pageVacia = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(tokenMarketService.buscarOrdersSellSummaryAbiertasPorUsuario(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(pageVacia);

        MockMvc mockMvc = mockMvcWith(tokenMarketService, userService);

        mockMvc.perform(get("/orders/" + USER_ID + "/ordersSell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void searchUserOpenOrdersSellDevuelve404CuandoUsuarioNoExiste() throws Exception {
        TokenMarketService tokenMarketService = mock(TokenMarketService.class);
        UserService userService = mock(UserService.class);

        when(tokenMarketService.buscarOrdersSellSummaryAbiertasPorUsuario(eq(999L), isNull(), any(Pageable.class)))
                .thenThrow(new EntidadNoEncontradaException("El usuario no existe"));

        MockMvc mockMvc = mockMvcWith(tokenMarketService, userService);

        mockMvc.perform(get("/orders/999/ordersSell"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }
}
