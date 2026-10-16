package com.overcode.service.impl;

import com.overcode.model.User;
import com.overcode.model.dto.SummaryTokenPlayerDTO;
import com.overcode.persistence.repository.interfaces.TokenRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.security.PasswordHasher;
import com.overcode.service.exception.EmailRepetidoException;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.NombreRepetidoException;
import com.overcode.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    public String superuserName;
    public String superuserEmail;
    public String superuserPassword;

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenRepository tokenRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordHasher passwordHasher,
                           TokenRepository tokenRepository,
                           @Value("${superuser.name}") String superuserName,
                           @Value("${superuser.password}") String superuserPassword,
                           @Value("${superuser.email}") String superuserEmail
    ) {
        this.superuserName = superuserName;
        this.superuserEmail = superuserEmail;
        this.superuserPassword = superuserPassword;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenRepository = tokenRepository;
    }

    @Override
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
    public User crearSuperusuario() {

        Optional<User> optionalSuperuser = userRepository.findByUsername(superuserName);

        if (optionalSuperuser.isPresent()) {
            return optionalSuperuser.get();
        }

        User superuser = new User(superuserName, superuserEmail, passwordHasher.hash(superuserPassword));
        return userRepository.guardar(superuser);
    }

    @Override
    public Page<SummaryTokenPlayerDTO> buscarSummaryTokensPorUsuario(Long userId, String playerName, Pageable pageable){
        if (!userRepository.existsById(userId)) {
            throw new EntidadNoEncontradaException("El usuario no existe");
        }

        return tokenRepository.buscarSummaryTokensPorUsuario(userId,playerName,pageable);
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
