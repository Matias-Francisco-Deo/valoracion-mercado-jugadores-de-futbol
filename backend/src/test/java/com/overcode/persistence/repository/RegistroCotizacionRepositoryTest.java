package com.overcode.persistence.repository;

import com.overcode.model.Player;
import com.overcode.model.Team;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.cotizacion.RegistroCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.TipoEstrategiaCotizacion;
import com.overcode.persistence.repository.impl.EstrategiaCotizacionRepositoryImpl;
import com.overcode.persistence.repository.impl.RegistroCotizacionRepositoryImpl;
import com.overcode.persistence.repository.impl.TeamRepositoryImpl;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2MapADTO;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.overcode.testUtils.TestPlayerUtil.getJugadorConNombre;
import static com.overcode.testUtils.TestTeamUtil.getTeamConJugadores;
import static junit.framework.TestCase.*;

@SpringBootTest()
class RegistroCotizacionRepositoryTest {

    public static final EstrategiaCotizacionSiempre2 ESTRATEGIA_COTIZACION_1 = new EstrategiaCotizacionSiempre2(10.0, 2.0);

    private EstrategiaCotizacionSiempre2MapADTO estrategiaADTO = new EstrategiaCotizacionSiempre2MapADTO();

    @Autowired
    private EstrategiaCotizacionRepositoryImpl estrategiaCotizacionRepository;

    private TeamRepositoryImpl teamRepository;

    @Autowired
    private TestService testService;
    @Autowired
    private RegistroCotizacionRepositoryImpl registroCotizacionRepositoryImpl;

    @AfterEach
    void setUp() {
        testService.eliminarCotizaciones();
        TipoEstrategiaCotizacion.agregarMapEstrategia(estrategiaADTO);

    }

    @BeforeEach
    void tearDown() {
        TipoEstrategiaCotizacion.eliminarMapEstrategia(estrategiaADTO);
        testService.eliminarCotizaciones();
    }



    @Test
    @Transactional
    void guardarPersisteLosRegistros() {

        Player jugador1 = getJugadorConNombre("pepito");
        Team team1 = getTeamConJugadores(List.of(jugador1));

        EstrategiaCotizacion estrategiaCotizacionUsada = estrategiaCotizacionRepository.guardar(ESTRATEGIA_COTIZACION_1);
        teamRepository.guardar(team1);

        List<RegistroCotizacion> registros = List.of(getRegistroCotizacion(jugador1, estrategiaCotizacionUsada));

        registroCotizacionRepositoryImpl.crearTodos(registros);

        List<RegistroCotizacion> registrosRecuperados = registroCotizacionRepositoryImpl.recuperarRegistrosDeJugador(jugador1.getId());
        // TODO método para agarrar las de hoy, y las de otros días usando rangos?

        assertFalse(registrosRecuperados.isEmpty());
        assertNotNull(registrosRecuperados.getFirst().getId());
        assertEquals(10.0, registros.getFirst().getCotizacion());

    }

    private static @NonNull RegistroCotizacion getRegistroCotizacion(Player jugador1, EstrategiaCotizacion estrategiaCotizacionUsada) {
        return new RegistroCotizacion(
                jugador1,
                10.0,
                estrategiaCotizacionUsada
        );
    }


}
