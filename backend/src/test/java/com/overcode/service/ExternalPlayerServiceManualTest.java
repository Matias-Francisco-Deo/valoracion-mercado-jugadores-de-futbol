package com.overcode.service;

import com.overcode.model.player.Player;
import com.overcode.service.impl.ExternalPlayerServiceImpl;
import com.overcode.service.impl.PlayerServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ExternalPlayerServiceManualTest {

    @Autowired
    private ExternalPlayerServiceImpl externalPlayerServiceImpl;
    @Autowired
    private PlayerServiceImpl playerServiceImpl;


    @Test
    @Disabled("Use automatically to generate players up to the limit set in the method")
    void encuentraJugadoresConDatos(){
        Optional<List<Player>> optionalPlayers = externalPlayerServiceImpl.actualizarJugadores(5);

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

    @Test
    @Disabled("Use automatically to generate players up to the limit set in the method")
    void encuentraJugadoresConDatosAsync(){
        CompletableFuture<Void> future = externalPlayerServiceImpl.actualizarJugadoresAsync(null);

        future.join();

        List<Player> players = playerServiceImpl.recuperarTodos();

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
