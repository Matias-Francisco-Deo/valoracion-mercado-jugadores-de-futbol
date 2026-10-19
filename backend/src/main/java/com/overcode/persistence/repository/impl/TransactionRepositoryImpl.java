package com.overcode.persistence.repository.impl;

import com.overcode.model.transaction.Transaction;
import com.overcode.persistence.dto.jpa.TransactionJPADTO;
import com.overcode.persistence.repository.dao.jpa.TransactionDAOJPA;
import com.overcode.persistence.repository.interfaces.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {
    private final TransactionDAOJPA transactionDAOJPA;

    public TransactionRepositoryImpl(TransactionDAOJPA transactionDAOJPA){
        this.transactionDAOJPA = transactionDAOJPA;
    }

    @Override
    public Transaction guardar(Transaction transaction){
        TransactionJPADTO dto = TransactionJPADTO.desdeModelo(transaction);
        TransactionJPADTO transactionDTO = transactionDAOJPA.save(dto);
        return transactionDTO.aModelo();
    }

    @Override
    public Optional<Transaction> recuperar(Long id) {
        return transactionDAOJPA.findById(id).map(TransactionJPADTO::aModelo);
    }

    @Override
    public Page<Transaction> recuperarPorUserId(Long userId, Pageable pageable) {
        return transactionDAOJPA.findAllByOwner_IdOrderByDateDesc(userId,pageable).map(TransactionJPADTO::aModelo);
    }

    @Override
    public List<Transaction> guardarTodos(List<Transaction> transactions) {
        List<TransactionJPADTO> transactionDTOS = transactionDAOJPA.saveAll(transactions.stream().map(TransactionJPADTO::desdeModelo).toList());
        return transactionDTOS.stream().map(TransactionJPADTO::aModelo).toList();
    }

}
