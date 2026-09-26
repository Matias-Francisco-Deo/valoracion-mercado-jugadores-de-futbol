package com.overcode.testUtils;
import com.overcode.persistence.repository.dao.jpa.PlayerDAOJPA;
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

    public void eliminarUsuarios() {
        userDAO.deleteAll();

    }

    public void eliminarJugadores() {
        playerDAO.deleteAll();
    }

}