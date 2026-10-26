package com.overcode.service.interfaces;

import com.overcode.model.Position;
import com.overcode.model.User;
import com.overcode.model.token.SummaryTokenPlayer;
import com.overcode.model.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User guardar(User user);

    User recuperar(Long id);

    User crearSuperusuario();

    Optional<User> recuperarPorEmail(String email);

    Page<SummaryTokenPlayer> buscarSummaryTokensPorUsuario(Long userId, String playerName, Pageable pageable);

    Page<Transaction> recuperarTransaccionesDeUnUser(Long userId, Pageable pageable);

    List<Position> recuperarPortfolioDeUser(Long userId);

    User depositarCreditos(Long userId, Double amount);
}
