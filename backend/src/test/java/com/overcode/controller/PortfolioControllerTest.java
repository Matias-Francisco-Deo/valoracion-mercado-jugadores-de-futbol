package com.overcode.controller;

import com.overcode.model.Portfolio;
import com.overcode.model.TokenHolding;
import com.overcode.model.User;
import com.overcode.service.interfaces.PortfolioService;
import com.overcode.service.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PortfolioControllerTest {

    private final Principal principal = () -> "ivan@gmail.com";

    private PortfolioController controllerWith(PortfolioService portfolioService, UserService userService) {
        return new PortfolioController(portfolioService, userService);
    }

    @Test
    void depositDevuelveOkYLosCreditosActualizados() throws Exception {
        PortfolioService portfolioService = mock(PortfolioService.class);
        UserService userService = mock(UserService.class);
        when(userService.recuperarPorEmail("ivan@gmail.com"))
                .thenReturn(Optional.of(new User("ivan", "ivan@gmail.com", "pass")));
        Portfolio portfolio = new Portfolio(UUID.randomUUID(), 735L, BigDecimal.valueOf(10000), 0L);
        when(portfolioService.deposit(any(), any())).thenReturn(portfolio);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(portfolioService, userService)).build();

        mockMvc.perform(post("/portfolio/deposit")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":10000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Credits deposited successfully"))
                .andExpect(jsonPath("$.credits").value(10000));

        verify(portfolioService, times(1)).deposit(any(), any());
    }

    @Test
    void depositSinPrincipalDevuelve401() throws Exception {
        PortfolioService portfolioService = mock(PortfolioService.class);
        UserService userService = mock(UserService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(portfolioService, userService)).build();

        mockMvc.perform(post("/portfolio/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":10000}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(portfolioService);
    }

    @Test
    void depositConMontoInvalidoDevuelve400() throws Exception {
        PortfolioService portfolioService = mock(PortfolioService.class);
        UserService userService = mock(UserService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(portfolioService, userService)).build();

        mockMvc.perform(post("/portfolio/deposit")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":-5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPortfolioDevuelveCreditosYHoldings() throws Exception {
        PortfolioService portfolioService = mock(PortfolioService.class);
        UserService userService = mock(UserService.class);
        when(userService.recuperarPorEmail("ivan@gmail.com"))
                .thenReturn(Optional.of(new User("ivan", "ivan@gmail.com", "pass")));
        UUID portfolioId = UUID.randomUUID();
        Portfolio portfolio = new Portfolio(portfolioId, 735L, BigDecimal.valueOf(500), 0L);
        portfolio.addTokenHolding(new TokenHolding(UUID.randomUUID(), portfolioId, 1247L, 5));
        when(portfolioService.getByUserId(any())).thenReturn(portfolio);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(portfolioService, userService)).build();

        mockMvc.perform(get("/portfolio").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credits").value(500))
                .andExpect(jsonPath("$.holdings[0].playerId").value(1247))
                .andExpect(jsonPath("$.holdings[0].quantity").value(5));
    }

    @Test
    void getPortfolioSinPrincipalDevuelve401() throws Exception {
        PortfolioService portfolioService = mock(PortfolioService.class);
        UserService userService = mock(UserService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controllerWith(portfolioService, userService)).build();

        mockMvc.perform(get("/portfolio"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(portfolioService);
    }
}
