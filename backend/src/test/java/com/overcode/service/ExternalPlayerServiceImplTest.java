package com.overcode.service;

import com.overcode.model.Player;
import com.overcode.service.impl.ExternalPlayerServiceImpl;
import com.overcode.service.interfaces.PlayerService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExternalPlayerServiceImplTest {

    @Autowired
    private ExternalPlayerServiceImpl externalPlayerServiceImpl;

    @Autowired
    private PlayerService playerService;

    @Disabled("Use automatically to generate players up to the limit set in the method")
    @Test
    void encuentraJugadoresConDatos(){
        Optional<List<Player>> optionalPlayers = externalPlayerServiceImpl.actualizarJugadores(1);

        assertTrue(optionalPlayers.isPresent());
        List<Player> players = optionalPlayers.get();
        assertFalse(players.isEmpty());

        players.forEach((player -> {
            assertNotNull(player.getId());
            assertNotNull(player.getTokens());
            assertNotNull(player.getName());
            assertNotNull(player.getTeam());
            assertNotNull(player.getPlayerGameData());
            assertNotNull(player.getPlayerGameData().getRating());
            assertNotNull(player.getPlayerGameData().getAssists());
            assertNotNull(player.getPlayerGameData().getPosition());
        }
        ));
    }

    @Disabled("Use automatically to generate players up to the limit set in the method")
    @Test
    void encuentraJugadoresConDatosAsync(){
        CompletableFuture<Void> future = externalPlayerServiceImpl.actualizarJugadoresAsync();

        future.join();

        List<Player> players = playerService.recuperarTodos();

        assertFalse(players.isEmpty());

        players.forEach((player -> {
            assertNotNull(player.getId());
            assertNotNull(player.getTokens());
            assertNotNull(player.getName());
            assertNotNull(player.getTeam());
            assertNotNull(player.getPlayerGameData());
        }
        ));
    }

}
