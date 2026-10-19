package com.overcode.persistence.repository.interfaces;

import com.overcode.model.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction guardar(Transaction transaction);

    Optional<Transaction> recuperar(Long id);

    Page<Transaction> recuperarPorUserId(Long userId, Pageable pageable);

    List<Transaction> guardarTodos(List<Transaction> transactions);
}
