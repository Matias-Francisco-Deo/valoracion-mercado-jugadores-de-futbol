package com.overcode.persistence.repository.interfaces;

import com.overcode.model.User;

import java.util.Optional;

public interface UserRepository {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User guardar(User user);

    Optional<User> recuperar(Long id);
}
