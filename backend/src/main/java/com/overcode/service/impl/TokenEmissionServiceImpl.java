package com.overcode.service.impl;

import com.overcode.model.User;
import com.overcode.model.player.Player;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.TokenEmissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TokenEmissionServiceImpl implements TokenEmissionService {

    private final UserRepository userRepository;

    @Value("${superuser.email:overcode@gmail.com}")
    private String superadminEmail;

    public TokenEmissionServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void emitTokensForNewPlayers(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return;
        }

        User superadmin = userRepository.findByEmail(superadminEmail)
                .orElseThrow(() -> new EntidadNoEncontradaException("Super admin not found with email: " + superadminEmail));

        for (Player player : players) {
            player.assignInitialTokensTo(superadmin);
        }
        userRepository.guardar(superadmin);
    }

    @Override
    public void emitTokensForNewPlayer(Player player) {
        if (player != null) {
            emitTokensForNewPlayers(List.of(player));
        }
    }
}
