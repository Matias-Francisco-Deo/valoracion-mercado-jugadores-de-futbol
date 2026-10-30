package com.overcode.persistence.repository.impl;

import com.overcode.model.venta.transaction.TransaccionFinanciera;
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
    public TransaccionFinanciera guardar(TransaccionFinanciera transaccionFinanciera){
        TransactionJPADTO dto = TransactionJPADTO.desdeModelo(transaccionFinanciera);
        TransactionJPADTO transactionDTO = transactionDAOJPA.save(dto);
        return transactionDTO.aModelo();
    }

    @Override
    public Optional<TransaccionFinanciera> recuperar(Long id) {
        return transactionDAOJPA.findById(id).map(TransactionJPADTO::aModelo);
    }

    @Override
    public Page<TransaccionFinanciera> recuperarPorUserId(Long userId, Pageable pageable) {
        return transactionDAOJPA.findAllByOwner_IdOrderByDateDesc(userId,pageable).map(TransactionJPADTO::aModelo);
    }

    @Override
    public List<TransaccionFinanciera> recuperarTodasPorUser(Long userId) {
        return transactionDAOJPA.findAllByOwner_IdOrderByDateAscIdAsc(userId).stream().map(TransactionJPADTO::aModelo).toList();
    }

    @Override
    public List<TransaccionFinanciera> guardarTodos(List<TransaccionFinanciera> transaccionFinancieras) {
        List<TransactionJPADTO> transactionDTOS = transactionDAOJPA.saveAll(transaccionFinancieras.stream().map(TransactionJPADTO::desdeModelo).toList());
        return transactionDTOS.stream().map(TransactionJPADTO::aModelo).toList();
    }

}
