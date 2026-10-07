package com.overcode.persistence.repository;

import com.overcode.model.PlayerGameData;
import com.overcode.persistence.dto.jpa.PlayerGameDataJPADTO;
import com.overcode.persistence.repository.dao.jpa.PlayerGameDataDAOJPA;
import com.overcode.persistence.repository.interfaces.PlayerGameDataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "scraper.api.key=test-api-key"
})
@ActiveProfiles("test")
class PlayerGameDataRepositoryTest {

    @Autowired
    private PlayerGameDataRepository playerGameDataRepository;

    @Autowired
    private PlayerGameDataDAOJPA playerGameDataDAOJPA;

    @Test
    @Transactional
    void guardarPersisteTodosLosCamposEstadisticosYRecuperaElRegistro() {
        PlayerGameData playerGameData = new PlayerGameData(
                731,
                619,
                827,
                623,
                914,
                838,
                9.91,
                "Midfielder"
        );

        PlayerGameData resultado = playerGameDataRepository.guardar(playerGameData);
        playerGameDataDAOJPA.flush();

        assertSame(playerGameData, resultado);

        PlayerGameDataJPADTO persistido = playerGameDataDAOJPA.findAll().stream()
                .filter(dto -> dto.getGoals().equals(731)
                        && dto.getAssists().equals(619)
                        && dto.getRating().equals(9.91))
                .findFirst()
                .orElseThrow();

        assertNotNull(persistido.getId());
        assertEquals(731, persistido.getGoals());
        assertEquals(619, persistido.getAssists());
        assertEquals(827, persistido.getShotsOnTarget());
        assertEquals(623, persistido.getKeyPasses());
        assertEquals(914, persistido.getTackles());
        assertEquals(838, persistido.getSuccessfulDribbles());
        assertEquals(9.91, persistido.getRating());
        assertEquals("Midfielder", persistido.getPosition());

        Optional<PlayerGameData> recuperado = playerGameDataRepository.recuperar(persistido.getId());
        assertTrue(recuperado.isPresent());
        assertEquals(731, recuperado.get().getGoals());
        assertEquals(619, recuperado.get().getAssists());
        assertEquals(827, recuperado.get().getShotsOnTarget());
        assertEquals(623, recuperado.get().getKeyPasses());
        assertEquals(914, recuperado.get().getTackles());
        assertEquals(838, recuperado.get().getSuccessfulDribbles());
        assertEquals(9.91, recuperado.get().getRating());
        assertEquals("Midfielder", recuperado.get().getPosition());
    }

    @Test
    @Transactional
    void recuperarDevuelveVacioCuandoElRegistroNoExiste() {
        assertTrue(playerGameDataRepository.recuperar(-1L).isEmpty());
    }
}
