package com.overcode.controller;

import com.overcode.controller.exception.GlobalExceptionHandler;
import com.overcode.model.market.PeticionDeposito;
import com.overcode.model.market.Position;
import com.overcode.model.market.transaction.TransaccionFinanciera;
import com.overcode.model.market.transaction.TransactionType;
import com.overcode.model.player.Player;
import com.overcode.model.token.SummaryTokenPlayer;
import com.overcode.model.user.Portfolio;
import com.overcode.model.user.User;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private UserService userService;
    private MockMvc mockMvc;

    private static final Long USER_ID = 1L;
    private final Principal principal = USER_ID::toString;
    private static final Long PLAYER_ID = 10L;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Player crearJugadorConPrecio(Long id, String nombre, Double precio) {
        Player player = new Player();
        player.setId(id);
        player.setName(nombre);
        player.setCurrentPrice(precio);
        return player;
    }

    // ------------------------------ /users/{userId}/tokens ------------------------------

    @Test
    void buscarSummaryTokensPorUsuarioDevuelvePaginaConTokensDelUsuario() throws Exception {
        SummaryTokenPlayer tokenDTO = new SummaryTokenPlayer(PLAYER_ID, "Lionel Messi", 5, 100.0);
        Page<SummaryTokenPlayer> page = new PageImpl<>(List.of(tokenDTO), PageRequest.of(0, 10), 1);

        when(userService.buscarSummaryTokensPorUsuario(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/users/" + USER_ID + "/tokens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerId").value(PLAYER_ID))
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(5))
                .andExpect(jsonPath("$.content[0].unitPrice").value(100.0))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(userService, times(1)).buscarSummaryTokensPorUsuario(eq(USER_ID), isNull(), any(Pageable.class));
    }

    @Test
    void buscarSummaryTokensPorUsuarioConFiltroDeNombreDevuelveTokensFiltrados() throws Exception {
        SummaryTokenPlayer tokenDTO = new SummaryTokenPlayer(PLAYER_ID, "Lionel Messi", 3, 150.0);
        Page<SummaryTokenPlayer> page = new PageImpl<>(List.of(tokenDTO), PageRequest.of(0, 10), 1);

        when(userService.buscarSummaryTokensPorUsuario(eq(USER_ID), eq("Messi"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/users/" + USER_ID + "/tokens").param("playerName", "Messi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(3))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(userService, times(1)).buscarSummaryTokensPorUsuario(eq(USER_ID), eq("Messi"), any(Pageable.class));
    }

    @Test
    void buscarSummaryTokensPorUsuarioConPaginacionPersonalizada() throws Exception {
        Page<SummaryTokenPlayer> pageVacia = new PageImpl<>(List.of(), PageRequest.of(2, 5), 0);
        when(userService.buscarSummaryTokensPorUsuario(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(pageVacia);

        mockMvc.perform(get("/users/" + USER_ID + "/tokens")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userService).buscarSummaryTokensPorUsuario(eq(USER_ID), isNull(), captor.capture());
        assertEquals(2, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
    }

    @Test
    void buscarSummaryTokensPorUsuarioDevuelveListaVaciaCuandoUsuarioNoTieneTokens() throws Exception {
        Page<SummaryTokenPlayer> pageVacia = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(userService.buscarSummaryTokensPorUsuario(eq(USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(pageVacia);

        mockMvc.perform(get("/users/" + USER_ID + "/tokens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void buscarSummaryTokensPorUsuarioDevuelve404CuandoUsuarioNoExiste() throws Exception {
        when(userService.buscarSummaryTokensPorUsuario(eq(999L), isNull(), any(Pageable.class)))
                .thenThrow(new EntidadNoEncontradaException("El usuario no existe"));

        mockMvc.perform(get("/users/999/tokens"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }

    // ------------------------------ /users/{userId}/portfolio ------------------------------

    @Test
    void getPortfolioDevuelveListaDePosicionesDelUsuario() throws Exception {
        Player player = crearJugadorConPrecio(PLAYER_ID, "Lionel Messi", 120d);
        TransaccionFinanciera tx = new TransaccionFinanciera(100L, null, player, LocalDateTime.now(), TransactionType.BUY, 5, 100.0);
        Position position = new Position(player, List.of(tx));

        when(userService.recuperarPortfolioDeUser(any(User.class))).thenReturn(new Portfolio(List.of(position)));

        mockMvc.perform(get("/users/" + USER_ID + "/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerId").value(PLAYER_ID))
                .andExpect(jsonPath("$[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$[0].quantity").value(5))
                .andExpect(jsonPath("$[0].averagePurchasePrice").value(100.0))
                .andExpect(jsonPath("$[0].currentPrice").value(120.0))
                .andExpect(jsonPath("$[0].currentValue").value(600.0))
                .andExpect(jsonPath("$[0].profitLoss").value(100.0))
                .andExpect(jsonPath("$[0].profitLossPercentage").value(20.0));

        verify(userService, times(1)).recuperarPortfolioDeUser(any(User.class));
    }

    @Test
    void getPortfolioDevuelveListaVaciaCuandoUsuarioNoTienePosiciones() throws Exception {
        when(userService.recuperarPortfolioDeUser(any(User.class))).thenReturn(new Portfolio(List.of()));

        mockMvc.perform(get("/users/" + USER_ID + "/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService, times(1)).recuperarPortfolioDeUser(any(User.class));
    }

    @Test
    void getPortfolioDevuelve404CuandoUsuarioNoExiste() throws Exception {
        when(userService.recuperarPortfolioDeUser(any(User.class)))
                .thenThrow(new EntidadNoEncontradaException("El usuario no existe"));

        mockMvc.perform(get("/users/-1/portfolio"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }

    // ------------------------------ /users/{userId}/transactions ------------------------------

    @Test
    void getTransaccionesDevuelvePaginaDeTransaccionesDelUsuario() throws Exception {
        Player player = crearJugadorConPrecio(PLAYER_ID, "Lionel Messi", 50d);
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 15, 14, 30, 0);
        TransaccionFinanciera tx = new TransaccionFinanciera(1L, null, player, fecha, TransactionType.BUY, 4, 50.0);

        Page<TransaccionFinanciera> page = new PageImpl<>(List.of(tx), PageRequest.of(0, 10), 1);
        when(userService.recuperarTransaccionesDeUnUser(eq(USER_ID), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/users/" + USER_ID + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].playerName").value("Lionel Messi"))
                .andExpect(jsonPath("$.content[0].quantity").value(4))
                .andExpect(jsonPath("$.content[0].unitPrice").value(50.0))
                .andExpect(jsonPath("$.content[0].totalPrice").value(200.0))
                .andExpect(jsonPath("$.content[0].type").value("BUY"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(userService, times(1)).recuperarTransaccionesDeUnUser(eq(USER_ID), any(Pageable.class));
    }

    @Test
    void getTransaccionesConPaginacionPersonalizada() throws Exception {
        Page<TransaccionFinanciera> pageVacia = new PageImpl<>(List.of(), PageRequest.of(1, 15), 0);
        when(userService.recuperarTransaccionesDeUnUser(eq(USER_ID), any(Pageable.class)))
                .thenReturn(pageVacia);

        mockMvc.perform(get("/users/" + USER_ID + "/transactions")
                        .param("page", "1")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userService).recuperarTransaccionesDeUnUser(eq(USER_ID), captor.capture());
        assertEquals(1, captor.getValue().getPageNumber());
        assertEquals(15, captor.getValue().getPageSize());
    }

    @Test
    void getTransaccionesDevuelveListaVaciaCuandoUsuarioNoTieneTransacciones() throws Exception {
        Page<TransaccionFinanciera> pageVacia = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(userService.recuperarTransaccionesDeUnUser(eq(USER_ID), any(Pageable.class)))
                .thenReturn(pageVacia);

        mockMvc.perform(get("/users/" + USER_ID + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getTransaccionesDevuelve404CuandoUsuarioNoExiste() throws Exception {
        when(userService.recuperarTransaccionesDeUnUser(eq(999L), any(Pageable.class)))
                .thenThrow(new EntidadNoEncontradaException("El usuario no existe"));

        mockMvc.perform(get("/users/999/transactions"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }

    // ------------------------------ /users/{id} ------------------------------

    @Test
    void getUserDevuelveOkConDatosDelUsuario() throws Exception {
        User user = new User("ivan", "ivan@gmail.com", "pass");
        user.setId(USER_ID);
        user.addCredits(50d);
        when(userService.recuperar(USER_ID)).thenReturn(user);

        mockMvc.perform(get("/users/" + USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.username").value("ivan"))
                .andExpect(jsonPath("$.email").value("ivan@gmail.com"))
                .andExpect(jsonPath("$.creditBalance").value(50));

        verify(userService, times(1)).recuperar(USER_ID);
    }

    @Test
    void getUserDevuelve404CuandoUsuarioNoExiste() throws Exception {
        when(userService.recuperar(-1L)).thenThrow(new EntidadNoEncontradaException("Usuario no encontrado"));

        mockMvc.perform(get("/users/-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
    }

    // ------------------------------------------------ créditos ----------------------------------------------


    @Test
    void depositDevuelveOkYLosCreditosActualizados() throws Exception {
        UserService userServiceMock = mock(UserService.class);
        User user = new User(USER_ID, "ivan", "ivan@gmail.com", "pass", 0d, null);
        when(userServiceMock.recuperar(USER_ID)).thenReturn(user);
        User updatedUser = new User(USER_ID, "ivan", "ivan@gmail.com", "pass", 10000d, null);
        when(userServiceMock.depositarCreditos(any(PeticionDeposito.class))).thenReturn(updatedUser);

        MockMvc userControllerMock = MockMvcBuilders.standaloneSetup(new UserController(userServiceMock)).build();

        userControllerMock.perform(post("/users/deposit")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":10000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credits").value(10000));

        verify(userServiceMock, times(1)).depositarCreditos(any(PeticionDeposito.class));
    }

    @Test
    void depositConMontoInvalidoDevuelve400() throws Exception {
        UserService userServiceMock = mock(UserService.class);

        MockMvc userControllerMock = MockMvcBuilders.standaloneSetup(new UserController(userServiceMock)).build();

        userControllerMock.perform(post("/users/deposit")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":-5}"))
                .andExpect(status().isBadRequest());
    }

}
