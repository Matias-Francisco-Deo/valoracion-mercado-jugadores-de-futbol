package com.overcode.persistence.repository.impl;

import com.overcode.model.token.Token;
import com.overcode.model.token.SummaryTokenPlayer;
import com.overcode.persistence.dto.jpa.TokenJPADTO;
import com.overcode.persistence.repository.dao.jpa.TokenDAOJPA;
import com.overcode.persistence.repository.interfaces.TokenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class TokenRepositoryImpl implements TokenRepository {

    private final TokenDAOJPA tokenDAO;

    public TokenRepositoryImpl(TokenDAOJPA tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Token> findById(Long id) {
        return tokenDAO.findById(id).map(TokenJPADTO::aModelo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Token> findByOwnerIdAndPlayerId(Long ownerId, Long playerId) {
        return tokenDAO.findByOwnerIdAndPlayerId(ownerId, playerId).stream()
                .map(TokenJPADTO::aModelo)
                .toList();
    }

    @Override
    @Transactional
    public void setTokenOwner(Long tokenId, Long newOwnerUserId) {
        tokenDAO.updateOwnerId(tokenId, newOwnerUserId);
    }

    @Override
    @Transactional
    public void clearTokenOwner(Long tokenId) {
        tokenDAO.clearOwner(tokenId);
    }

    @Override
    public Page<SummaryTokenPlayer> buscarSummaryTokensPorUsuario(Long userId, String playerName, Pageable pageable){
        return tokenDAO.buscarSummaryTokensPorUsuario(userId,playerName,pageable);
    }
}
