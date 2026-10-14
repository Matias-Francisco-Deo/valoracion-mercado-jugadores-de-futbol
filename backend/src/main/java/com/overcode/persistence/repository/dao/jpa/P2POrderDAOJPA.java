package com.overcode.persistence.repository.dao.jpa;

import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.persistence.dto.jpa.P2POrderJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface P2POrderDAOJPA extends JpaRepository<P2POrderJPADTO, UUID> {

    // Resting SELL book for a player: cheapest first then FIFO — what a market BUY sweeps (FillPlan).
    List<P2POrderJPADTO> findByPlayerIdAndStatusAndTypeOrderByPricePerTokenAscCreatedAtAsc(
            Long playerId, OrderStatus status, OrderType type);
}
