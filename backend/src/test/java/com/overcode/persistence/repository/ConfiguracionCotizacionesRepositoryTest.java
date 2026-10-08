package com.overcode.persistence.repository;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.TipoEstrategiaCotizacion;
import com.overcode.persistence.repository.interfaces.ConfiguracionCotizacionesRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2JPADTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void siempreDebeHaberUnaConfiguracionGuardadaDeLoContrarioTiraError() {
        assertThrows(EntidadNoEncontradaException.class, () -> configuracionCotizacionesRepository.recuperar());
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
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(EstrategiaCotizacionSiempre2.class))
                    .thenReturn(estrategiaDTO);


            configuracionACrear = new ConfiguracionCotizaciones(estrategia);
            configuracionCotizaciones = configuracionCotizacionesRepository.guardar(configuracionACrear);

        }

        assertNotNull(configuracionCotizaciones);
        assertNotNull(configuracionCotizaciones.getId());
        assertEquals(estrategia.getValorBase(), configuracionACrear.getEstrategiaCotizacion().getValorBase());

    }

    @Test
    void alGuardarUnaConfiguracionNuevaPisaALaYaExistente() {

        EstrategiaCotizacion estrategia = new EstrategiaCotizacionSiempre2(1.0, 1.0);
        EstrategiaCotizacionJPADTO estrategiaDTO = new EstrategiaCotizacionSiempre2JPADTO();
        estrategiaDTO.setFactorEscala(estrategia.getFactorEscala());
        estrategiaDTO.setValorBase(estrategia.getValorBase());
        ConfiguracionCotizaciones configuracionACrear;


        EstrategiaCotizacion estrategiaConValorBase = new EstrategiaCotizacionSiempre2(2.0, 1.0);
        EstrategiaCotizacionJPADTO estrategiaDTOConValorBase = new EstrategiaCotizacionSiempre2JPADTO();
        estrategiaDTOConValorBase.setFactorEscala(estrategiaConValorBase.getFactorEscala());
        estrategiaDTOConValorBase.setValorBase(estrategiaConValorBase.getValorBase());

        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(EstrategiaCotizacionSiempre2.class))
                    .thenReturn(estrategiaDTO).thenReturn(estrategiaDTOConValorBase);



            configuracionACrear = new ConfiguracionCotizaciones(estrategia);
            configuracionCotizacionesRepository.guardar(configuracionACrear);

            ConfiguracionCotizaciones configuracionCotizacionesOriginal = configuracionCotizacionesRepository.recuperar();

            ConfiguracionCotizaciones configuracionCotizacionesNueva = new ConfiguracionCotizaciones();
            configuracionCotizacionesNueva.setId(2L);
            configuracionCotizacionesNueva.setEstrategiaCotizacion(estrategiaConValorBase);

            ConfiguracionCotizaciones configuracionCotizaciones = configuracionCotizacionesRepository.guardar(configuracionCotizacionesNueva);

            assertEquals(configuracionCotizacionesOriginal.getId(), configuracionCotizaciones.getId());
            assertEquals(estrategiaConValorBase.getValorBase(), configuracionCotizaciones.getEstrategiaCotizacion().getValorBase());

        }


    }


}
