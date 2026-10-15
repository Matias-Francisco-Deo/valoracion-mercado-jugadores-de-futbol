package com.overcode.service;

import com.overcode.model.Player;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacion;
import com.overcode.model.cotizacion.RegistroCotizacion;
import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import com.overcode.persistence.dto.jpa.cotizacion.TipoEstrategiaCotizacion;
import com.overcode.service.impl.ConfiguracionCotizacionesServiceImpl;
import com.overcode.service.impl.EstrategiaCotizacionServiceImpl;
import com.overcode.service.impl.PlayerServiceImpl;
import com.overcode.service.impl.RegistroCotizacionServiceImpl;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2JPADTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest()
class EstrategiaCotizacionServiceManualTest {

    public static final EstrategiaCotizacionSiempre2 ESTRATEGIA_COTIZACION_1 = new EstrategiaCotizacionSiempre2(0.0, 1.0);
    public static final EstrategiaCotizacionJPADTO ESTRATEGIA_DTO = EstrategiaCotizacionSiempre2JPADTO.desdeModeloParaTest(ESTRATEGIA_COTIZACION_1);

    public static final EstrategiaCotizacionSiempre2 ESTRATEGIA_COTIZACION_2 = new EstrategiaCotizacionSiempre2(2.0, 2.0);
    public static final EstrategiaCotizacionJPADTO ESTRATEGIA_DTO_2 = EstrategiaCotizacionSiempre2JPADTO.desdeModeloParaTest(ESTRATEGIA_COTIZACION_2);

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

//        Player player = getJugadorConNombre("Pepito"); // vale exactamente 1
//        Player playerPersistido = playerServiceImpl.crear(player);

        final EstrategiaCotizacion[] estrategiaCotizacion = new EstrategiaCotizacion[]{ESTRATEGIA_COTIZACION_1};

        contextoConDTODeEstrategia1(() -> estrategiaCotizacion[0] = estrategiaCotizacionService.guardar(ESTRATEGIA_COTIZACION_1));

        contextoConDTODeEstrategia1(() -> {
            EstrategiaCotizacion estrategiaCotizacionMisma = estrategiaCotizacion[0];

            configuracionCotizacionesService.crearConfiguracionSiNoExiste(new ConfiguracionCotizaciones(estrategiaCotizacionMisma));

            estrategiaCotizacionService.cotizarJugadores();

            Player player = playerServiceImpl.recuperarTodos().getFirst();

            List<RegistroCotizacion> registros = registroCotizacionServiceImpl.recuperarRegistrosDeJugador(player.getId());

            assertFalse(registros.isEmpty());
            RegistroCotizacion primerRegistro = registros.getFirst();

            assertNotEquals(player.getCurrentPrice(), primerRegistro.getCotizacion());
        });

    }


}
