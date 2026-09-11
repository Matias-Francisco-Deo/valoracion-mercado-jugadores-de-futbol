package com.overcode.service.interfaces;

import com.overcode.model.User;

public interface UserService {

    User guardar(User user);

    User recuperar(Long id);

    User crearSuperusuario();

    User recuperarPorEmail(String email);

    boolean existePorEmail(String email);
}
