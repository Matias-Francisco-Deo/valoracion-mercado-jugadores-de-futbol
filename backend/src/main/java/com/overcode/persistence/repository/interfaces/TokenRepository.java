package com.overcode.persistence.repository.interfaces;

import com.overcode.model.token.Token;
import com.overcode.model.token.SummaryTokenPlayer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {

    Optional<Token> findById(Long id);

    List<Token> findByOwnerIdAndPlayerId(Long ownerId, Long playerId);

    void setTokenOwner(Long tokenId, Long newOwnerUserId);

    void clearTokenOwner(Long tokenId);

    Page<SummaryTokenPlayer> buscarSummaryTokensPorUsuario(Long userId, String playerName, Pageable pageable);
}
