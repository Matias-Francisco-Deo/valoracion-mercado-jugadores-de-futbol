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
        Optional<List<Player>> optionalPlayers = externalPlayerServiceImpl.actualizarJugadores(null);

        assertTrue(optionalPlayers.isPresent());
        assertFalse(optionalPlayers.get().isEmpty());

        optionalPlayers.get().forEach((player -> {
            assertNotNull(player.getId());
            assertNotNull(player.getTokens());
            assertNotNull(player.getCurrentPrice());

            assertNotNull(player.getName());
            assertNotNull(player.getClubName());
            assertNotNull(player.getGoals());
            assertNotNull(player.getAssists());
            assertNotNull(player.getRating());
            assertNotNull(player.getInterceptions());
            assertNotNull(player.getShotsOnTarget());
            assertNotNull(player.getSuccessfulDribbles());
            assertNotNull(player.getTackles());
            assertNotNull(player.getKeyPasses());
        }
                ));
    }

}
