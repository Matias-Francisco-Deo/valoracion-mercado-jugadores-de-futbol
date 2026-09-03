package com.overcode.persistency.repository;

import com.overcode.persistency.dto.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {

    List<TransactionRecord> findByBuyerIdOrSellerIdOrderByTimestampDesc(Long buyerId, Long sellerId);
}
