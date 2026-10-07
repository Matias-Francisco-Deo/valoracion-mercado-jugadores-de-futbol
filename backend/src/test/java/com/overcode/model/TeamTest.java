package com.overcode.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.overcode.testUtils.TestTeamUtil.getTeam;
import static com.overcode.testUtils.TestTeamUtil.getTeamConJugadores;
import static com.overcode.testUtils.TestTeamUtil.getTeamConNombreYLiga;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TeamTest {

    @Test
    void alCrearElEquipoComienzaSinJugadores() {
        Team team = getTeam();

        assertEquals(0, team.getPlayers().size());
    }

    @Test
    void alCrearElEquipoConListaVaciaNoTieneJugadores() {
        Team team = getTeamConJugadores(List.of());

        assertEquals(0, team.getPlayers().size());
    }

    @Test
    void alAgregarUnJugadorQuedaEnElEquipoYSeAsociaConElEquipo() {
        Team team = getTeamConNombreYLiga("River Plate", "Liga Profesional");
        Player player = new Player("Leonardo Pisculichi", 10L, null);

        team.addPlayer(player);

        assertEquals(1, team.getPlayers().size());
        assertEquals(team, player.getTeam());
    }

    @Test
    void alAgregarUnaListaDeJugadoresTodosQuedanAsociadosAlEquipo() {
        Team team = getTeamConNombreYLiga("Boca Juniors", "Liga Profesional");
        Player player1 = new Player("Carlos Tevez", 20L, null);
        Player player2 = new Player("Juan Roman Riquelme", 21L, null);

        team.addPlayers(List.of(player1, player2));

        assertEquals(2, team.getPlayers().size());
        assertEquals(team, player1.getTeam());
        assertEquals(team, player2.getTeam());
    }

    @Test
    void alRemoverUnJugadorSeQuitaDelEquipoYSeDesasocia() {
        Team team = getTeamConNombreYLiga("Boca Juniors", "Liga Profesional");
        Player player = new Player("Carlos Tevez", 20L, null);
        team.addPlayer(player);

        team.removePlayer(player);

        assertEquals(0, team.getPlayers().size());
        assertNull(player.getTeam());
    }
}
