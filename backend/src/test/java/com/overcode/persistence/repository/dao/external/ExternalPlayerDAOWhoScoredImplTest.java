package com.overcode.persistence.repository.dao.external;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ExternalPlayerDAOWhoScoredImplTest {

    @Autowired
    private ExternalPlayerDAOWhoScoredImpl externalPlayerDAOWhoScoredImpl;

    private final PlayerDraftDTO JUGADOR_DRAFT_1 = new PlayerDraftDTO("Kylian Mbappé", "Real Madrid CF");


    @Test
    void encuentraJugadorConDatos(){
        Player player = externalPlayerDAOWhoScoredImpl.getDatosDeJugador(JUGADOR_DRAFT_1);

        assertNotNull(player.getName());
        assertNotNull(player.getClubName());
        assertNotNull(player.getGamesPlayed());
        assertNotNull(player.getGoals());
        assertNotNull(player.getAssists());
        assertNotNull(player.getRating());
        assertNotNull(player.getInterceptions());
        assertNotNull(player.getShotsOnTarget());
        assertNotNull(player.getSuccessfulDribbles());
        assertNotNull(player.getTackles());
        assertNotNull(player.getWasDribbled());
        assertNotNull(player.getKeyPasses());
    }

    @Disabled
    @Test
    void noEncuentraJugadorInexistenteYDevuelveVacio(){



    }

    @Disabled
    @Test
    void encuentraVariosJugadores(){

    }

}
