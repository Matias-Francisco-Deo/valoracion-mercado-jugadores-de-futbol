package com.overcode.persistence.repository.dao.jpa;

import com.overcode.model.orden.OrderStatus;
import com.overcode.persistence.dto.jpa.SellOrderJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellOrderDAOJPA extends JpaRepository<SellOrderJPADTO, Long> {

    List<SellOrderJPADTO> findByPlayerIdAndStatusOrderByPriceAscCreatedAtAsc(
            Long playerId, OrderStatus status);
}
