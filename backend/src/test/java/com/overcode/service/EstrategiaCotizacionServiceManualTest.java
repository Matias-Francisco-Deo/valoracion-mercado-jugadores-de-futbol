package com.overcode.service;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.cotizacion.registro.BusquedaRegistroCotizacion;
import com.overcode.model.cotizacion.registro.RegistroCotizacion;
import com.overcode.model.player.Player;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.mapper.TipoEstrategiaCotizacion;
import com.overcode.service.impl.ConfiguracionCotizacionesServiceImpl;
import com.overcode.service.impl.EstrategiaCotizacionServiceImpl;
import com.overcode.service.impl.PlayerServiceImpl;
import com.overcode.service.impl.RegistroCotizacionServiceImpl;
import com.overcode.testUtils.cotizacion.siempre2.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.siempre2.EstrategiaCotizacionSiempre2JPADTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest()
class EstrategiaCotizacionServiceManualTest {

    public static final EstrategiaCotizacionSiempre2 ESTRATEGIA_COTIZACION_1 = new EstrategiaCotizacionSiempre2(0.0, 1.0);
    public static final EstrategiaCotizacionJPADTO ESTRATEGIA_DTO = EstrategiaCotizacionSiempre2JPADTO.desdeModeloParaTest(ESTRATEGIA_COTIZACION_1);

    @Autowired
    private EstrategiaCotizacionServiceImpl estrategiaCotizacionService;

    @Autowired
    private ConfiguracionCotizacionesServiceImpl configuracionCotizacionesService;

    @Autowired
    private PlayerServiceImpl playerServiceImpl;

    @Autowired
    private RegistroCotizacionServiceImpl registroCotizacionServiceImpl;

    private void contextoConDTODeEstrategia1(Runnable accion) {
        try (MockedStatic<TipoEstrategiaCotizacion> tipo = mockStatic(TipoEstrategiaCotizacion.class)) {
            tipo.when(() -> TipoEstrategiaCotizacion.getValueOf(isA(EstrategiaCotizacion.class)))
                    .thenReturn(ESTRATEGIA_DTO);
            accion.run();
        }
    }


    @Test
    @Disabled("Usar manualmente para testear")
    void cotizarJugadoresManual() {

        final EstrategiaCotizacion[] estrategiaCotizacion = new EstrategiaCotizacion[]{ESTRATEGIA_COTIZACION_1};

        contextoConDTODeEstrategia1(() -> estrategiaCotizacion[0] = estrategiaCotizacionService.guardar(ESTRATEGIA_COTIZACION_1));

        contextoConDTODeEstrategia1(() -> {
            EstrategiaCotizacion estrategiaCotizacionMisma = estrategiaCotizacion[0];

            configuracionCotizacionesService.crearConfiguracionSiNoExiste(new ConfiguracionCotizaciones(estrategiaCotizacionMisma));

            estrategiaCotizacionService.cotizarJugadores();

            Player player = playerServiceImpl.recuperarTodos().getFirst();

            List<RegistroCotizacion> registros = registroCotizacionServiceImpl.recuperarRegistrosDeJugador(
                    new BusquedaRegistroCotizacion(player.getId(),
                            LocalDateTime.now().minusDays(1),
                            LocalDateTime.now())
            );

            assertFalse(registros.isEmpty());
            RegistroCotizacion primerRegistro = registros.getFirst();

            assertNotEquals(player.getCurrentPrice(), primerRegistro.getCotizacion());
        });

    }

    @Test
    @Disabled("Usar manualmente para testear")
    void cotizarJugadoresManualConConfigActual() {

        assertDoesNotThrow(() -> estrategiaCotizacionService.cotizarJugadores());

    }


}
