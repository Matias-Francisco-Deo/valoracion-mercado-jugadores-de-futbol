package com.overcode.controller;

import com.overcode.service.interfaces.ExternalPlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExternalPlayerControllerTest {

    @Test
    void activarActualizacionJugadoresDevuelveAcceptedYDisparaServicioUnaVez() throws Exception {
        ExternalPlayerService externalPlayerService = mock(ExternalPlayerService.class);
        when(externalPlayerService.actualizarJugadoresAsync()).thenReturn(CompletableFuture.completedFuture(null));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ExternalPlayerController(externalPlayerService)).build();

        mockMvc.perform(post("/api/admin/players/actualizar-jugadores"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Scraper manual iniciado en background. Este proceso puede tardar varias horas."));

        verify(externalPlayerService, times(1)).actualizarJugadoresAsync();
    }

    @Test
    void activarActualizacionJugadoresPropagaLaExcepcionDelServicio() {
        ExternalPlayerService externalPlayerService = mock(ExternalPlayerService.class);
        when(externalPlayerService.actualizarJugadoresAsync()).thenThrow(new RuntimeException("fallo del scraper"));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ExternalPlayerController(externalPlayerService)).build();

        assertThrows(Exception.class, () -> mockMvc.perform(post("/api/admin/players/actualizar-jugadores")));
    }
}
