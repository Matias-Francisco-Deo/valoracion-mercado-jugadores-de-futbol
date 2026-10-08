package com.overcode.persistence.repository;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.persistence.dto.jpa.cotizacion.ConfiguracionCotizacionesJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.TipoEstrategiaCotizacion;
import com.overcode.persistence.repository.dao.jpa.ConfiguracionCotizacionesDAOJPA;
import com.overcode.persistence.repository.impl.ConfiguracionContizacionesRepositoryImpl;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2JPADTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfiguracionCotizacionesRepositoryTest {

    @Mock
    private ConfiguracionCotizacionesDAOJPA dao;

    private ConfiguracionContizacionesRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ConfiguracionContizacionesRepositoryImpl(dao);
    }

    @Test
    void recuperarCuandoNoExisteConfiguracionDevuelveConfiguracionVaciaConIdSingleton() {
        when(dao.findById(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID)).thenReturn(Optional.empty());

        ConfiguracionCotizaciones configuracion = repository.recuperar();

        assertEquals(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID, configuracion.getId());
        assertNull(configuracion.getEstrategiaCotizacion());
        verify(dao).findById(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID);
        verifyNoMoreInteractions(dao);
    }

    @Test
    void recuperarCuandoExisteConfiguracionMapeaLaEntidadConIdSingleton() {
        var estrategiaDto = EstrategiaCotizacionSiempre2JPADTO.desdeModeloParaTest(
                new EstrategiaCotizacionSiempre2(120.5, 2.5));
        estrategiaDto.setId(9L);

        ConfiguracionCotizacionesJPADTO configuracionDto = new ConfiguracionCotizacionesJPADTO();
        configuracionDto.setId(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID);
        configuracionDto.setEstrategiaCotizacion(estrategiaDto);
        when(dao.findById(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID))
                .thenReturn(Optional.of(configuracionDto));

        ConfiguracionCotizaciones configuracion = repository.recuperar();

        assertEquals(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID, configuracion.getId());
        assertNotNull(configuracion.getEstrategiaCotizacion());
        assertEquals(9L, configuracion.getEstrategiaCotizacion().getId());
        assertEquals(120.5, configuracion.getEstrategiaCotizacion().getValorBase());
        assertEquals(2.5, configuracion.getEstrategiaCotizacion().getFactorEscala());
        verify(dao).findById(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID);
        verifyNoMoreInteractions(dao);
    }

    @Test
    void guardarMapeaLaConfiguracionYDevuelveElModeloPersistido() {
        EstrategiaCotizacionSiempre2 estrategia = new EstrategiaCotizacionSiempre2(80.0, 3.0);
        estrategia.setId(14L);
        ConfiguracionCotizaciones configuracion = new ConfiguracionCotizaciones(estrategia);
        configuracion.setId(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID);
        EstrategiaCotizacionSiempre2JPADTO estrategiaDto =
                EstrategiaCotizacionSiempre2JPADTO.desdeModeloParaTest(estrategia);

        try (MockedStatic<TipoEstrategiaCotizacion> tipos = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipos.when(() -> TipoEstrategiaCotizacion.getValueOf(EstrategiaCotizacionSiempre2.class))
                    .thenReturn(estrategiaDto);
            when(dao.save(any(ConfiguracionCotizacionesJPADTO.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ConfiguracionCotizaciones guardada = repository.guardar(configuracion);

            ArgumentCaptor<ConfiguracionCotizacionesJPADTO> captor =
                    ArgumentCaptor.forClass(ConfiguracionCotizacionesJPADTO.class);
            verify(dao).save(captor.capture());
            ConfiguracionCotizacionesJPADTO persistida = captor.getValue();
            assertEquals(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID, persistida.getId());
            assertEquals(14L, persistida.getEstrategiaCotizacion().getId());
            assertEquals(80.0, persistida.getEstrategiaCotizacion().getValorBase());
            assertEquals(3.0, persistida.getEstrategiaCotizacion().getFactorEscala());

            assertEquals(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID, guardada.getId());
            assertEquals(14L, guardada.getEstrategiaCotizacion().getId());
            assertEquals(80.0, guardada.getEstrategiaCotizacion().getValorBase());
            assertEquals(3.0, guardada.getEstrategiaCotizacion().getFactorEscala());
        }
    }

    @Test
    void recuperarPropagaErroresDelDao() {
        RuntimeException error = new RuntimeException("database unavailable");
        when(dao.findById(ConfiguracionContizacionesRepositoryImpl.CONFIG_ID)).thenThrow(error);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> repository.recuperar());

        assertSame(error, thrown);
    }

    @Test
    void guardarPropagaErroresDelDao() {
        EstrategiaCotizacionSiempre2 estrategia = new EstrategiaCotizacionSiempre2(80.0, 3.0);
        ConfiguracionCotizaciones configuracion = new ConfiguracionCotizaciones(estrategia);
        RuntimeException error = new RuntimeException("database unavailable");

        try (MockedStatic<TipoEstrategiaCotizacion> tipos = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipos.when(() -> TipoEstrategiaCotizacion.getValueOf(EstrategiaCotizacionSiempre2.class))
                    .thenReturn(EstrategiaCotizacionSiempre2JPADTO.desdeModeloParaTest(estrategia));
            when(dao.save(any(ConfiguracionCotizacionesJPADTO.class))).thenThrow(error);

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> repository.guardar(configuracion));

            assertSame(error, thrown);
        }
    }
}
