package com.overcode.testUtils;

import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRating;
import com.overcode.persistence.dto.jpa.bonus.mapper.TipoDailyBonusMission;
import com.overcode.persistence.repository.dao.jpa.*;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.UserService;
import com.overcode.testUtils.bonus.mission.MapTestMissionADTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.overcode.testUtils.TestDailyBonusUtil.getDailyBonusBase;

@Service
@Transactional
public class TestService {

    public static final MapTestMissionADTO TEST_MISSION_ADTO = new MapTestMissionADTO();
    @Autowired
    private UserDAOJPA userDAO;

    @Autowired
    private PlayerDAOJPA playerDAO;

    @Autowired
    private TeamDAOJPA teamDAO;

    @Autowired
    private PlayerGameDataDAOJPA playerGameDataDAO;

    @Autowired
    private EstrategiaCotizacionDAOJPA estrategiaCotizacionDAO;

    @Autowired
    private ConfiguracionMercadoDAOJPA configuracionCotizacionesDAO;

    @Autowired
    private RegistroCotizacionDAOJPA registroCotizacionDAO;
    @Autowired
    private DailyBonusDAOJPA dailyBonusDAOJPA;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ConfiguracionMercadoService configuracionMercadoService;
    @Autowired
    private UserService userService;

    public void eliminarUsuarios() {
        entityManager.createNativeQuery("DELETE FROM sell_orders").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM transactions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM tokens").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE user_daily_bonuses, users RESTART IDENTITY CASCADE").executeUpdate();
        userDAO.deleteAll();

    }

    public void eliminarEquipos() {
        entityManager.createNativeQuery("DELETE FROM teams").executeUpdate();
        teamDAO.deleteAll();

    }

    public void eliminarDatosJugadores() {
        entityManager.createNativeQuery("DELETE FROM sell_orders").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM transactions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM players_game_data").executeUpdate();
        playerGameDataDAO.deleteAll();

    }

    public void eliminarJugadoresYEquipos() {
        eliminarDatosJugadores();
        eliminarJugadores();
        eliminarEquipos();
    }

    private void eliminarJugadores() {
        entityManager.createNativeQuery("TRUNCATE TABLE visit_player_mission, visit_3_players_mission, users RESTART IDENTITY CASCADE").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM sell_orders").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM transactions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM tokens").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM players").executeUpdate();
        playerDAO.deleteAll();
    }

    public void eliminarCotizaciones() {
        entityManager.createNativeQuery("DELETE FROM registros_cotizaciones").executeUpdate();
        registroCotizacionDAO.deleteAll();
        eliminarEstrategiasCotizacion();
    }

    public void eliminarEstrategiasCotizacion() {
        eliminarConfig();

        entityManager.createNativeQuery("DELETE FROM estrategias_cotizacion").executeUpdate();
        estrategiaCotizacionDAO.deleteAll();
    }

    public void eliminarConfig() {
        eliminarDailyBonuses();
        entityManager.createNativeQuery("TRUNCATE TABLE configuraciones_mercado RESTART IDENTITY CASCADE").executeUpdate();
        quitarTestMissionMap();
    }

    public static void quitarTestMissionMap() {
        TipoDailyBonusMission.eliminarMapMision(TEST_MISSION_ADTO);
    }

    public void eliminarDailyBonuses() {
        entityManager.createNativeQuery("TRUNCATE TABLE diary_rewards_list, diary_bonus_rewards_data, diary_bonuses, user_daily_bonuses RESTART IDENTITY CASCADE;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE buy_mission, buy_visit_mission, visit_player_mission, visit_3_players_mission RESTART IDENTITY CASCADE;").executeUpdate();
    }

    public void eliminarRegistros() {
        entityManager.createNativeQuery("DELETE FROM sell_orders").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM transactions").executeUpdate();

    }

    public void crearConfiguracionSimple() {
        agregarTestMissionMap();
        configuracionMercadoService.crearConfiguracionSiNoExiste(new ConfiguracionMercado(new EstrategiaCotizacionRating(), getDailyBonusBase()));
        userService.crearSuperusuario();
    }

    public static void agregarTestMissionMap() {
        TipoDailyBonusMission.agregarMapMision(TEST_MISSION_ADTO);
    }
}