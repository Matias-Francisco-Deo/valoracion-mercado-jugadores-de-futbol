package com.overcode.service;

import com.overcode.model.Player;
import com.overcode.service.impl.ExternalPlayerServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExternalPlayerServiceImplTest {

    @Autowired
    private ExternalPlayerServiceImpl externalPlayerServiceImpl;

    @Disabled("Use automatically to generate players up to the max capacity set in the repository")
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
        }
        ));
    }

}
