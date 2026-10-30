package com.overcode.persistence.repository;

import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.mapper.TipoEstrategiaCotizacion;
import com.overcode.persistence.repository.interfaces.ConfiguracionMercadoRepository;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.cotizacion.siempre2.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.siempre2.EstrategiaCotizacionSiempre2JPADTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mockStatic;


@SpringBootTest()
class ConfiguracionMercadoRepositoryTest {

    @Autowired
    private ConfiguracionMercadoRepository configuracionMercadoRepository;
    @Autowired
    private TestService testService;


    @BeforeEach
    void setUp() {
        testService.eliminarCotizaciones();
    }

    @AfterEach
    void tearDown() {
        testService.eliminarCotizaciones();
    }

    @Test
    void siNoHayConfiguracionGuardadaDevuelveVacio() {
        assertTrue(configuracionMercadoRepository.recuperarPorId(1L).isEmpty());
    }

    @Test
    void siNoHayConfiguracionGuardadaNoExiste() {
        assertFalse(configuracionMercadoRepository.existePorId(1L));
    }

    @Test
    void seCreaUnaConfiguracion() {
        EstrategiaCotizacion estrategia = new EstrategiaCotizacionSiempre2(1.0, 1.0);
        EstrategiaCotizacionJPADTO estrategiaDTO = new EstrategiaCotizacionSiempre2JPADTO();
        estrategiaDTO.setFactorEscala(estrategia.getFactorEscala());
        estrategiaDTO.setValorBase(estrategia.getValorBase());

        ConfiguracionMercado configuracionMercado;
        ConfiguracionMercado configuracionACrear;

        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(estrategia))
                    .thenReturn(estrategiaDTO);


            configuracionACrear = new ConfiguracionMercado(1L, estrategia);
            configuracionMercado = configuracionMercadoRepository.guardar(configuracionACrear);

        }

        assertNotNull(configuracionMercado);
        assertNotNull(configuracionMercado.getId());
        assertEquals(estrategia.getValorBase(), configuracionACrear.getEstrategiaCotizacion().getValorBase());

    }

    @Test
    void deHaberUnaConfiguracionExiste() {
        EstrategiaCotizacion estrategia = new EstrategiaCotizacionSiempre2(1.0, 1.0);
        EstrategiaCotizacionJPADTO estrategiaDTO = new EstrategiaCotizacionSiempre2JPADTO();
        estrategiaDTO.setFactorEscala(estrategia.getFactorEscala());
        estrategiaDTO.setValorBase(estrategia.getValorBase());

        ConfiguracionMercado configuracionMercado;
        ConfiguracionMercado configuracionACrear;

        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(estrategia))
                    .thenReturn(estrategiaDTO);


            configuracionACrear = new ConfiguracionMercado(1L, estrategia);
            configuracionMercado = configuracionMercadoRepository.guardar(configuracionACrear);

        }

        assertTrue(configuracionMercadoRepository.existePorId(configuracionMercado.getId()));

    }


}
