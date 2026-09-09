package com.overcode.service.impl;

import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EmailRepetidoException;
import com.overcode.service.exception.NombreRepetidoException;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    public static final String SUPERUSER_NAME = "superuser"; // TODO abstraer a .env?
    public static final String SUPERUSER_EMAIL = "overcode@gmail.com";
    public static final String SUPERUSER_PASSWORD = "overcodesuperuser";

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository
    ) {
        this.userRepository = userRepository;

    }

    @Override
    @Transactional
    public User guardar(User userACrear) {
        validarUsuarioNuevo(userACrear);

        return userRepository.guardar(userACrear);
    }

    @Override
    @Transactional(readOnly = true)
    public User recuperar(Long id) {
        return userRepository.recuperar(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado"));
    }

    @Override
    @Transactional
    public User crearSuperusuario() {

        Optional<User> optionalSuperuser = userRepository.findByUsername(SUPERUSER_NAME);

        if (optionalSuperuser.isPresent()) {
            return optionalSuperuser.get();
        }

        User superuser = new User(SUPERUSER_NAME, SUPERUSER_EMAIL, SUPERUSER_PASSWORD);
        return this.guardar(superuser);
    }

    private void validarUsuarioNuevo(User userACrear) {
        if (userRepository.existsByUsername(userACrear.getUsername())) {
            throw new NombreRepetidoException("El nombre de usuario ya existe" + userACrear.getUsername());
        }
        if (userRepository.existsByEmail(userACrear.getEmail())) {
            throw new EmailRepetidoException("El email ya existe" + userACrear.getEmail());
        }
    }


}
