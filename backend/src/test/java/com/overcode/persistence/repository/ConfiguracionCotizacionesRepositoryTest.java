package com.overcode.persistence.repository;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.TipoEstrategiaCotizacion;
import com.overcode.persistence.repository.interfaces.ConfiguracionCotizacionesRepository;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2JPADTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mockStatic;


@SpringBootTest()
class ConfiguracionCotizacionesRepositoryTest {

    @Autowired
    private ConfiguracionCotizacionesRepository configuracionCotizacionesRepository;
    @Autowired
    private TestService testService;


    @BeforeEach
    void setUp() {
        testService.eliminarEstrategiasCotizacion();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarEstrategiasCotizacion();
    }

    @Test
    void siNoHayConfiguracionGuardadaDevuelveVacio() {
        assertTrue(configuracionCotizacionesRepository.recuperarPorId(1L).isEmpty());
    }

    @Test
    void siNoHayConfiguracionGuardadaNoExiste() {
        assertFalse(configuracionCotizacionesRepository.existePorId(1L));
    }

    @Test
    void seCreaUnaConfiguracion() {
        EstrategiaCotizacion estrategia = new EstrategiaCotizacionSiempre2(1.0, 1.0);
        EstrategiaCotizacionJPADTO estrategiaDTO = new EstrategiaCotizacionSiempre2JPADTO();
        estrategiaDTO.setFactorEscala(estrategia.getFactorEscala());
        estrategiaDTO.setValorBase(estrategia.getValorBase());

        ConfiguracionCotizaciones configuracionCotizaciones;
        ConfiguracionCotizaciones configuracionACrear;

        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(estrategia))
                    .thenReturn(estrategiaDTO);


            configuracionACrear = new ConfiguracionCotizaciones(1L, estrategia);
            configuracionCotizaciones = configuracionCotizacionesRepository.guardar(configuracionACrear);

        }

        assertNotNull(configuracionCotizaciones);
        assertNotNull(configuracionCotizaciones.getId());
        assertEquals(estrategia.getValorBase(), configuracionACrear.getEstrategiaCotizacion().getValorBase());

    }

    @Test
    void deHaberUnaConfiguracionExiste() {
        EstrategiaCotizacion estrategia = new EstrategiaCotizacionSiempre2(1.0, 1.0);
        EstrategiaCotizacionJPADTO estrategiaDTO = new EstrategiaCotizacionSiempre2JPADTO();
        estrategiaDTO.setFactorEscala(estrategia.getFactorEscala());
        estrategiaDTO.setValorBase(estrategia.getValorBase());

        ConfiguracionCotizaciones configuracionCotizaciones;
        ConfiguracionCotizaciones configuracionACrear;

        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(estrategia))
                    .thenReturn(estrategiaDTO);


            configuracionACrear = new ConfiguracionCotizaciones(1L, estrategia);
            configuracionCotizaciones = configuracionCotizacionesRepository.guardar(configuracionACrear);

        }

        assertTrue(configuracionCotizacionesRepository.existePorId(configuracionCotizaciones.getId()));

    }


}
