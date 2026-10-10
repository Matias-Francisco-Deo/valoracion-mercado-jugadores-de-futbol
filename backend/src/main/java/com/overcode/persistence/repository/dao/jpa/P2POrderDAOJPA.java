package com.overcode.persistence.repository.dao.jpa;

import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.persistence.dto.jpa.P2POrderJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface P2POrderDAOJPA extends JpaRepository<P2POrderJPADTO, UUID> {

    // Matchmaking query for BUY orders (we want the lowest price SELL order)
    Optional<P2POrderJPADTO> findFirstByPlayerIdAndStatusAndTypeAndQuantityAndPricePerTokenLessThanEqualOrderByPricePerTokenAscCreatedAtAsc(
            Long playerId, OrderStatus status, OrderType type, int quantity, BigDecimal maxPrice);

    // Matchmaking query for SELL orders (we want the highest price BUY order)
    Optional<P2POrderJPADTO> findFirstByPlayerIdAndStatusAndTypeAndQuantityAndPricePerTokenGreaterThanEqualOrderByPricePerTokenDescCreatedAtAsc(
            Long playerId, OrderStatus status, OrderType type, int quantity, BigDecimal minPrice);
}
