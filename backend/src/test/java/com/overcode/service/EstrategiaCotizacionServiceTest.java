package com.overcode.service;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.exception.EstrategiaInvalidaException;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.TipoEstrategiaCotizacion;
import com.overcode.persistence.repository.impl.EstrategiaCotizacionRepositoryImpl;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.impl.ConfiguracionCotizacionesServiceImpl;
import com.overcode.service.impl.EstrategiaCotizacionServiceImpl;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2JPADTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest()
class EstrategiaCotizacionServiceTest {

    public static final EstrategiaCotizacionSiempre2 ESTRATEGIA_COTIZACION_1 = new EstrategiaCotizacionSiempre2(10.0, 2.0);
    public static final EstrategiaCotizacionJPADTO ESTRATEGIA_DTO = EstrategiaCotizacionSiempre2JPADTO.desdeModeloParaTest(ESTRATEGIA_COTIZACION_1);

    public static final EstrategiaCotizacionSiempre2 ESTRATEGIA_COTIZACION_2 = new EstrategiaCotizacionSiempre2(15.0, 3.0);
    public static final EstrategiaCotizacionJPADTO ESTRATEGIA_DTO_2 = EstrategiaCotizacionSiempre2JPADTO.desdeModeloParaTest(ESTRATEGIA_COTIZACION_2);

    @Autowired
    private EstrategiaCotizacionServiceImpl estrategiaCotizacionService;

    @Autowired
    private ConfiguracionCotizacionesServiceImpl configuracionCotizacionesService;

    @Autowired
    private TestService testService;
    @Autowired
    private EstrategiaCotizacionRepositoryImpl estrategiaCotizacionRepositoryImpl;

    @AfterEach
    void setUp() {
        testService.eliminarEstrategiasCotizacion();
    }

    @BeforeEach
    void tearDown() {
        testService.eliminarEstrategiasCotizacion();
    }

    private EstrategiaCotizacion guardarEstrategia1() {
        EstrategiaCotizacion guardada;
        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(EstrategiaCotizacionSiempre2.class))
                    .thenReturn(ESTRATEGIA_DTO);

            guardada = estrategiaCotizacionService.guardar(ESTRATEGIA_COTIZACION_1);

        }
        return guardada;
    }

    private void contextoConDTODeEstrategia1(Runnable accion) {
        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(EstrategiaCotizacionSiempre2.class))
                    .thenReturn(ESTRATEGIA_DTO);
            accion.run();
        }
    }

    private void contextoConDTODeEstrategia2(Runnable accion) {
        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(EstrategiaCotizacionSiempre2.class))
                    .thenReturn(ESTRATEGIA_DTO_2);
            accion.run();
        }
    }

    private EstrategiaCotizacion guardarEstrategia2() {
        EstrategiaCotizacion guardada;
        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(EstrategiaCotizacionSiempre2.class))
                    .thenReturn(ESTRATEGIA_DTO_2);

            guardada = estrategiaCotizacionService.guardar(ESTRATEGIA_COTIZACION_2);

        }
        return guardada;
    }

    @Test
    void guardarPersisteLaEstrategia() {

        EstrategiaCotizacion guardada;

        guardada = guardarEstrategia1();

        assertNotNull(guardada.getId());
    }


    @Test
    void guardarFallaCuandoLaEstrategiaNoEstaMapeada() {
        EstrategiaCotizacionSiempre2 estrategia = ESTRATEGIA_COTIZACION_1;

        assertThrows(InvalidDataAccessApiUsageException.class, () -> estrategiaCotizacionService.guardar(estrategia));
    }

    @Test
    void recuperarTodosDevuelveEstrategiaGuardada() {

        guardarEstrategia1();

        EstrategiaCotizacion recuperada = estrategiaCotizacionService.recuperarTodos().getFirst();

        assertEquals(recuperada.getFactorEscala(), ESTRATEGIA_COTIZACION_1.getFactorEscala());
        assertEquals(recuperada.getValorBase(), ESTRATEGIA_COTIZACION_1.getValorBase());
    }

    @Test
    void recuperarTodosDevuelveEstrategiasGuardadas() {

        guardarEstrategia1();
        guardarEstrategia2();

        List<EstrategiaCotizacion> recuperadas = estrategiaCotizacionService.recuperarTodos();

        EstrategiaCotizacion estrategia1 = recuperadas.getFirst();
        EstrategiaCotizacion estrategia2 = recuperadas.get(1);

        assertEquals(estrategia1.getFactorEscala(), ESTRATEGIA_COTIZACION_1.getFactorEscala());
        assertEquals(estrategia1.getValorBase(), ESTRATEGIA_COTIZACION_1.getValorBase());

        assertEquals(estrategia2.getFactorEscala(), ESTRATEGIA_COTIZACION_2.getFactorEscala());
        assertEquals(estrategia2.getValorBase(), ESTRATEGIA_COTIZACION_2.getValorBase());
    }

    @Test
    void recuperarTodosDevuelveListaVaciaCuandoNoHayEstrategias() {
        assertTrue(estrategiaCotizacionService.recuperarTodos().isEmpty());
    }

    @Test
    void sePuedeElegirLaEstrategiaYLaConfiguracionQuedaEstablecidaConElla() {
        final EstrategiaCotizacion[] estrategiaCotizacion = new EstrategiaCotizacionSiempre2[]{ESTRATEGIA_COTIZACION_1};
        final EstrategiaCotizacion[] estrategiaCotizacion2 = new EstrategiaCotizacion[]{ESTRATEGIA_COTIZACION_2};

        contextoConDTODeEstrategia1(() -> estrategiaCotizacion[0] = estrategiaCotizacionService.guardar(ESTRATEGIA_COTIZACION_1));
        contextoConDTODeEstrategia2(() -> estrategiaCotizacion2[0] = estrategiaCotizacionService.guardar(ESTRATEGIA_COTIZACION_2));

        contextoConDTODeEstrategia1(() -> {
            EstrategiaCotizacion estrategiaActual = estrategiaCotizacion[0];
            EstrategiaCotizacion estrategiaAUsar = estrategiaCotizacion2[0];

            configuracionCotizacionesService.guardar(new ConfiguracionCotizaciones(estrategiaActual));
            estrategiaCotizacionService.seleccionarEstrategia(estrategiaAUsar.getId());
            ConfiguracionCotizaciones config = configuracionCotizacionesService.recuperar();
            assertEquals(estrategiaAUsar.getId(), config.getEstrategiaCotizacion().getId());
        });
    }

    @Test
    void sePuedeElegirLaEstrategiaYaUsadaYSeSigueUsando() {
        final EstrategiaCotizacion[] estrategiaCotizacion = new EstrategiaCotizacion[]{ESTRATEGIA_COTIZACION_1};

        contextoConDTODeEstrategia1(() -> estrategiaCotizacion[0] = estrategiaCotizacionService.guardar(ESTRATEGIA_COTIZACION_1));

        contextoConDTODeEstrategia1(() -> {
            EstrategiaCotizacion estrategiaCotizacionMisma = estrategiaCotizacion[0];

            configuracionCotizacionesService.guardar(new ConfiguracionCotizaciones(estrategiaCotizacionMisma));
            estrategiaCotizacionService.seleccionarEstrategia(estrategiaCotizacionMisma.getId());
            ConfiguracionCotizaciones config = configuracionCotizacionesService.recuperar();
            assertEquals(estrategiaCotizacionMisma.getId(), config.getEstrategiaCotizacion().getId());
        });
    }

    @Test
    void elegirEstrategiaQueNoExisteDaError() {

        final EstrategiaCotizacion[] estrategiaCotizacion = new EstrategiaCotizacion[]{ESTRATEGIA_COTIZACION_1};

        contextoConDTODeEstrategia1(() -> estrategiaCotizacion[0] = estrategiaCotizacionService.guardar(ESTRATEGIA_COTIZACION_1));

        contextoConDTODeEstrategia1(() -> {
            EstrategiaCotizacion estrategiaCotizacionMisma = estrategiaCotizacion[0];

            configuracionCotizacionesService.guardar(new ConfiguracionCotizaciones(estrategiaCotizacionMisma));
            estrategiaCotizacionService.seleccionarEstrategia(estrategiaCotizacionMisma.getId());

            assertThrows(EntidadNoEncontradaException.class, () -> estrategiaCotizacionService.seleccionarEstrategia(-1L));
        });



    }

    @Test
    @Transactional
    void sePuedeModificarElFactorEscalaDeUnaEstrategia() {

        EstrategiaCotizacion estrategiaCotizacion = guardarEstrategia1();

        estrategiaCotizacionService.actualizar(estrategiaCotizacion.getId(), 3.0);

        EstrategiaCotizacion estrategiaActualizada = estrategiaCotizacionRepositoryImpl.recuperar(estrategiaCotizacion.getId()).get();

        assertEquals(3.0, estrategiaActualizada.getFactorEscala());

    }

        @Test
    @Transactional
    void noSePuedeModificarElValorDelFactorANumerosIgualesOMenoresA0() {

        EstrategiaCotizacion estrategiaCotizacion = guardarEstrategia1();

        Long id = estrategiaCotizacion.getId();
        assertThrows(EstrategiaInvalidaException.class, () -> {
            estrategiaCotizacionService.actualizar(id, 0.0);
        });

        assertThrows(EstrategiaInvalidaException.class, () -> {
            estrategiaCotizacionService.actualizar(id, -1.0);
        });

    }


}
