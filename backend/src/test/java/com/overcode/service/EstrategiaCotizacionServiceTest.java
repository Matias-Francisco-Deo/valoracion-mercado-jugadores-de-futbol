package com.overcode.service;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.TipoEstrategiaCotizacion;
import com.overcode.persistence.repository.interfaces.ConfiguracionCotizacionesRepository;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
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
    private EstrategiaCotizacionService estrategiaCotizacionService;

    @Autowired
    private ConfiguracionCotizacionesRepository configuracionCotizacionesRepository;

    @Autowired
    private TestService testService;

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

//    @Test
//    void recuperarDevuelveLaEstrategia() {
//
//        Long id = guardarEstrategia1().getId();
//
//        EstrategiaCotizacion recuperada = estrategiaCotizacionService.recuperar(id).get();
//
//        assertEquals(id, recuperada.getId());
//        assertEquals(recuperada.getFactorEscala(), ESTRATEGIA_COTIZACION_1.getFactorEscala());
//        assertEquals(recuperada.getValorBase(), ESTRATEGIA_COTIZACION_1.getValorBase());
//    }

//    @Test
//    void recuperarDevuelveVacioCuandoLaEstrategiaNoExiste() {
//        assertTrue(estrategiaCotizacionService.recuperar(-1L).isEmpty());
//    }

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

        EstrategiaCotizacion guardada;
        guardada = guardarEstrategia1();

        estrategiaCotizacionService.seleccionarEstrategia(guardada.getId());

        ConfiguracionCotizaciones config = configuracionCotizacionesRepository.recuperar();
        assertEquals(guardada.getId(), config.getEstrategiaCotizacion().getId());

    }


}
