package com.overcode.service.impl;

import com.overcode.model.Position;
import com.overcode.model.User;
import com.overcode.model.player.Player;
import com.overcode.model.token.SummaryTokenPlayer;
import com.overcode.model.transaction.Transaction;
import com.overcode.persistence.repository.interfaces.TokenRepository;
import com.overcode.persistence.repository.interfaces.TransactionRepository;
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

import java.util.List;
import java.util.Map;
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
    private final TransactionRepository transactionRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordHasher passwordHasher,
                           TokenRepository tokenRepository,
                           TransactionRepository transactionRepository,
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
        this.transactionRepository = transactionRepository;
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
    public Page<SummaryTokenPlayer> buscarSummaryTokensPorUsuario(Long userId, String playerName, Pageable pageable){
        if (!userRepository.existsById(userId)) {
            throw new EntidadNoEncontradaException("El usuario no existe");
        }

        return tokenRepository.buscarSummaryTokensPorUsuario(userId,playerName,pageable);
    }

    @Override
    public Page<Transaction> recuperarTransaccionesDeUnUser(Long userId, Pageable pageable){
        if (!userRepository.existsById(userId)) {throw new EntidadNoEncontradaException("El usuario no existe");}

        return transactionRepository.recuperarPorUserId(userId,pageable);
    }

    @Override
    public List<Position> recuperarPortfolioDeUser(Long userId){
        if(!userRepository.existsById(userId)){
            throw new EntidadNoEncontradaException("El usuario no existe");
        }
        List<Transaction> transactions = transactionRepository.recuperarTodasPorUserId(userId);

        Map<Long, List<Transaction>> transactionsPorJugador = new java.util.LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            Long playerId = transaction.getPlayer().getId();
            transactionsPorJugador.computeIfAbsent(playerId, id -> new java.util.ArrayList<>()).add(transaction);
        }

        List<Position> positions = new java.util.ArrayList<>();

        for (Map.Entry<Long, List<Transaction>> entry : transactionsPorJugador.entrySet()) {
            List<Transaction> transactionsDelJugador = entry.getValue();

            Player player = transactionsDelJugador.get(0).getPlayer();

            Position position = new Position(player, transactionsDelJugador);
            if (position.getQuantity() > 0) {
                positions.add(position);
            }
        }

        return positions;
    }

    @Override
    public User depositarCreditos(Long userId, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor a cero");
        }
        User user = recuperar(userId);
        user.creditCredits(amount);
        return userRepository.guardar(user);
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
