package com.overcode.persistence.repository.impl;

import com.overcode.model.User;
import com.overcode.persistence.dto.UserJPADTO;
import com.overcode.persistence.repository.TransactionRepository;
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
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public Optional<UserJPADTO> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        UserJPADTO dto = UserJPADTO.desdeModelo(user);
        UserJPADTO userDto = userDAO.save(dto);
        return userDto.aModelo();
    }
}
