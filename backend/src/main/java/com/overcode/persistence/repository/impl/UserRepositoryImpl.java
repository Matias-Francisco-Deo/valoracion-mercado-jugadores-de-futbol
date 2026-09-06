package com.overcode.persistence.repository.impl;

import com.overcode.model.User;
import com.overcode.persistence.dto.UserJPADTO;
import com.overcode.persistence.repository.dao.UserDAOJPA;
import com.overcode.persistence.repository.interfaces.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserDAOJPA userDAOJPA;

    public UserRepositoryImpl(UserDAOJPA userDAOJPA) {
        this.userDAOJPA = userDAOJPA;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDAOJPA.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userDAOJPA.existsByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDAOJPA.findByUsername(username).map(UserJPADTO::aModelo);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDAOJPA.findByEmail(email).map(UserJPADTO::aModelo);
    }

    @Override
    public User save(User user) {
        UserJPADTO dto = UserJPADTO.desdeModelo(user);
        UserJPADTO userDto = userDAOJPA.save(dto);
        return userDto.aModelo();
    }

    @Override
    public Optional<User> recuperar(Long id) {
        return userDAOJPA.findById(id).map(UserJPADTO::aModelo);
    }
}
