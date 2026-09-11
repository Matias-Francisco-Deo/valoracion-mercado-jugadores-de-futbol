package com.overcode.service.interfaces;

import com.overcode.model.User;

import java.util.Optional;

public interface UserService {

    User guardar(User user);

    User recuperar(Long id);

    User crearSuperusuario();

    Optional<User> recuperarPorEmail(String email);

    boolean existePorEmail(String email);
}
