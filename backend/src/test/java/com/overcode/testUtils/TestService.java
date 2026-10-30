package com.overcode.testUtils;

import com.overcode.persistence.repository.dao.jpa.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TestService {

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
    private LoginBonusDAOJPA loginBonusDAO;

    @PersistenceContext
    private EntityManager entityManager;

    public void eliminarUsuarios() {
        entityManager.createNativeQuery("DELETE FROM sell_orders").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM transactions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM tokens").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
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
        entityManager.createNativeQuery("DELETE FROM configuraciones_mercado").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM login_bonuses").executeUpdate();
        configuracionCotizacionesDAO.deleteAll();
    }

    public void eliminarLoginBonuses() {
        eliminarConfig();

        entityManager.createNativeQuery("DELETE FROM login_bonuses").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM rewards_list").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM login_bonus_rewards_data").executeUpdate();

    }

    public void eliminarRegistros() {
        entityManager.createNativeQuery("DELETE FROM sell_orders").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM transactions").executeUpdate();

    }

}