package com.overcode.service.impl;

import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EmailRepetidoException;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.NombreRepetidoException;
import com.overcode.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    public String superuserName;
    public String superuserEmail;
    public String superuserPassword;

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository,
                           @Value("${superuser.name}") String superuserName,
                           @Value("${superuser.password}") String superuserPassword,
                           @Value("${superuser.email}") String superuserEmail
    ) {
        this.superuserName = superuserName;
        this.superuserEmail = superuserEmail;
        this.superuserPassword = superuserPassword;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User guardar(User user) {
        validarUsuarioNuevo(user);

        return userRepository.guardar(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User recuperar(Long id) {
        return userRepository.recuperar(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> recuperarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User crearSuperusuario() {

        Optional<User> optionalSuperuser = userRepository.findByUsername(superuserName);

        if (optionalSuperuser.isPresent()) {
            return optionalSuperuser.get();
        }

        User superuser = new User(superuserName, superuserEmail, superuserPassword);
        return userRepository.guardar(superuser);
    }

    private void validarUsuarioNuevo(User userACrear) {
        if (userRepository.existsByUsername(userACrear.getUsername())) {
            throw new NombreRepetidoException("El nombre de usuario " + userACrear.getUsername() + " ya existe");
        }
        if (userRepository.existsByEmail(userACrear.getEmail())) {
            throw new EmailRepetidoException("El email " + userACrear.getEmail() + " ya existe");
        }
    }


}
