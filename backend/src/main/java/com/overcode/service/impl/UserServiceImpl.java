package com.overcode.service.impl;

import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EmailRepetidoException;
import com.overcode.service.exception.NombreRepetidoException;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

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
                .orElseThrow(() -> new EntidadNoEncontradaException("User not found: " + id));
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
