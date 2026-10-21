package com.overcode.service.impl;

import com.overcode.model.Player;
import com.overcode.model.SellOrder;
import com.overcode.model.token.Token;
import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.SellOrderRepository;
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
    private final SellOrderRepository sellOrderRepository;

    @Value("${superuser.email:overcode@gmail.com}")
    private String superadminEmail;

    public TokenEmissionServiceImpl(UserRepository userRepository,SellOrderRepository sellOrderRepository) {
        this.userRepository = userRepository;
        this.sellOrderRepository = sellOrderRepository;
    }

    @Override
    public void emitTokensForNewPlayers(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return;
        }

        User superadmin = userRepository.findByEmail(superadminEmail)
                .orElseThrow(() -> new EntidadNoEncontradaException("Super admin not found with email: " + superadminEmail));

        for (Player player : players) {
            List<Token> tokens = player.assignInitialTokensTo(superadmin);

            List<SellOrder> orders = superadmin.sellTokens(player,tokens.size());

            sellOrderRepository.saveAll(orders);
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
