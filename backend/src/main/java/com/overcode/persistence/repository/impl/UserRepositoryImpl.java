package com.overcode.persistence.repository.impl;

import com.overcode.model.User;
import com.overcode.persistence.dto.UserJPADTO;
import com.overcode.persistence.repository.dao.UserDAO;
import com.overcode.persistence.repository.interfaces.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserDAO userDAO;

    public UserRepositoryImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDAO.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userDAO.existsByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDAO.findByUsername(username).map(UserJPADTO::aModelo);
    }

    @Override
    public User save(User user) {
        UserJPADTO dto = UserJPADTO.desdeModelo(user);
        UserJPADTO userDto = userDAO.save(dto);
        return userDto.aModelo();
    }

    @Override
    public Optional<User> recuperar(Long id) {
        return userDAO.findById(id).map(UserJPADTO::aModelo);
    }
}
