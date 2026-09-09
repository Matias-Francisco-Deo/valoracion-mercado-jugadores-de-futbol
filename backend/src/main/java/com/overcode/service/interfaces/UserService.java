package com.overcode.service.interfaces;

import com.overcode.model.User;

public interface UserService {

    User guardar(User request);

    User recuperar(Long id);

    User crearSuperusuario();
}
