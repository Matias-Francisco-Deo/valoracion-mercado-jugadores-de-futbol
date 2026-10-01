package com.overcode.testUtils;

import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
import com.overcode.persistence.repository.dao.jpa.PlayerGameDataDAOJPA;
import com.overcode.persistence.repository.dao.jpa.TeamDAOJPA;
import com.overcode.persistence.repository.dao.jpa.UserDAOJPA;
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

    public void eliminarUsuarios() {
        userDAO.deleteAll();

    }

    public void eliminarEquipos() {
        teamDAO.deleteAll();

    }

    public void eliminarDatosJugadores() {
        playerGameDataDAO.deleteAll();

    }

    public void eliminarJugadores() {

        playerDAO.deleteAll();
    }

}