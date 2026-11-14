package com.overcode.persistence.repository;

import com.overcode.model.player.Player;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        testService.eliminarJugadoresYEquipos();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarJugadoresYEquipos();
    }

    @Test
    void recuperar3JugadoresAlAzarDevuelveTresJugadoresDistintos() {
        List<Player> savedPlayers = IntStream.range(0, 10)
                .mapToObj(index -> {
                    Player player = new Player();
                    player.setName("random-player-" + index);
                    return player;
                })
                .toList();

        playerRepository.guardarTodos(savedPlayers);

        List<Player> selection = playerRepository.recuperar3JugadoresAlAzar();
        Set<Long> selectedIds = selection.stream()
                .map(Player::getId)
                .collect(Collectors.toCollection(HashSet::new));

        assertEquals(3, selection.size());
        assertEquals(3, selectedIds.size(), "the selection must not contain duplicate players");
    }
}
