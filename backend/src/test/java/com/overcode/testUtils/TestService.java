package com.overcode.testUtils;
import com.overcode.persistence.repository.dao.UserDAOJPA;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TestService {

//    @PersistenceContext
//    private EntityManager entityManager;

    @Autowired
    private UserDAOJPA userDAO;

    public void eliminarUsuarios() {
        userDAO.deleteAll();

    }


}