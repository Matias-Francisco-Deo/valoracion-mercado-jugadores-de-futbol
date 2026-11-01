package com.overcode.service.cotizaciones;

import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.mapper.TipoEstrategiaCotizacion;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.impl.ConfiguracionMercadoServiceImpl;
import com.overcode.testUtils.TestService;
import com.overcode.testUtils.cotizacion.siempre2.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.siempre2.EstrategiaCotizacionSiempre2JPADTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mockStatic;


@SpringBootTest()
class ConfiguracionMercadoServiceImplTest {

    @Autowired
    private ConfiguracionMercadoServiceImpl configuracionCotizacionesService;

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
        assertThrows(EntidadNoEncontradaException.class, () -> configuracionCotizacionesService.recuperar());
    }

    @Test
    void seCreaUnaConfiguracionSiNoExiste() {
        EstrategiaCotizacion estrategia = new EstrategiaCotizacionSiempre2(1.0, 1.0);
        EstrategiaCotizacionJPADTO estrategiaDTO = new EstrategiaCotizacionSiempre2JPADTO();
        estrategiaDTO.setFactorEscala(estrategia.getFactorEscala());
        estrategiaDTO.setValorBase(estrategia.getValorBase());

        ConfiguracionMercado configuracionMercado;
        ConfiguracionMercado configuracionACrear;

        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(isA(EstrategiaCotizacion.class)))
                    .thenReturn(estrategiaDTO);


            configuracionACrear = new ConfiguracionMercado(estrategia);
            configuracionMercado = configuracionCotizacionesService.crearConfiguracionSiNoExiste(configuracionACrear);

        }

        assertNotNull(configuracionMercado);
        assertNotNull(configuracionMercado.getId());
        assertEquals(estrategia.getValorBase(), configuracionACrear.getEstrategiaCotizacion().getValorBase());

    }

    @Test
    void alIntentarCrearUnaConfiguracionNuevaSeMantieneLaAnterior() {

        EstrategiaCotizacion estrategia = new EstrategiaCotizacionSiempre2(1.0, 1.0);
        EstrategiaCotizacionJPADTO estrategiaDTO = new EstrategiaCotizacionSiempre2JPADTO();
        estrategiaDTO.setFactorEscala(estrategia.getFactorEscala());
        estrategiaDTO.setValorBase(estrategia.getValorBase());
        ConfiguracionMercado configuracionACrear;


        EstrategiaCotizacion estrategiaConValorBase = new EstrategiaCotizacionSiempre2(2.0, 1.0);
        EstrategiaCotizacionJPADTO estrategiaDTOConValorBase = new EstrategiaCotizacionSiempre2JPADTO();
        estrategiaDTOConValorBase.setFactorEscala(estrategiaConValorBase.getFactorEscala());
        estrategiaDTOConValorBase.setValorBase(estrategiaConValorBase.getValorBase());

        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(isA(EstrategiaCotizacion.class)))
                    .thenReturn(estrategiaDTO).thenReturn(estrategiaDTOConValorBase);



            configuracionACrear = new ConfiguracionMercado(estrategia);
            configuracionCotizacionesService.crearConfiguracionSiNoExiste(configuracionACrear);

            ConfiguracionMercado configuracionMercadoOriginal = configuracionCotizacionesService.recuperar();

            ConfiguracionMercado configuracionMercadoNueva = new ConfiguracionMercado();
            configuracionMercadoNueva.setId(2L);
            configuracionMercadoNueva.setEstrategiaCotizacion(estrategiaConValorBase);

            ConfiguracionMercado configuracionMercadoCreada = configuracionCotizacionesService
                    .crearConfiguracionSiNoExiste(configuracionMercadoNueva);

            assertEquals(configuracionMercadoOriginal.getId(), configuracionMercadoCreada.getId());
            assertEquals(estrategia.getValorBase(), configuracionMercadoCreada.getEstrategiaCotizacion().getValorBase());

        }


    }


}
