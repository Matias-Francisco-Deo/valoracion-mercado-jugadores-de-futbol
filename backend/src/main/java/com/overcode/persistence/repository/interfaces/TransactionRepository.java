package com.overcode.persistence.repository.interfaces;

import com.overcode.model.market.transaction.TransaccionFinanciera;
import com.overcode.model.position.PlayerPositionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    TransaccionFinanciera guardar(TransaccionFinanciera transaccionFinanciera);

    Optional<TransaccionFinanciera> recuperar(Long id);

    Page<TransaccionFinanciera> recuperarPorUserId(Long userId, Pageable pageable);

    List<TransaccionFinanciera> guardarTodos(List<TransaccionFinanciera> transaccionFinancieras);

    List<TransaccionFinanciera> recuperarTodasPorUser(Long userId);

    List<PlayerPositionData> findPlayerPositionsData(Long userId);
}
