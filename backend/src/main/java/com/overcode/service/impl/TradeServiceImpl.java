package com.overcode.service.impl;

import com.overcode.controller.dto.TradeResponse;
import com.overcode.persistency.dto.PlayerRecord;
import com.overcode.persistency.dto.PositionRecord;
import com.overcode.persistency.dto.TransactionRecord;
import com.overcode.persistency.dto.UserRecord;
import com.overcode.persistency.repository.PlayerRepository;
import com.overcode.persistency.repository.PositionRepository;
import com.overcode.persistency.repository.TransactionRepository;
import com.overcode.persistency.repository.UserRepository;
import com.overcode.service.exception.ConflictException;
import com.overcode.service.exception.NotFoundException;
import com.overcode.service.exception.ValidationException;
import com.overcode.service.interfaces.TradeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TradeServiceImpl implements TradeService {

    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final TransactionRepository transactionRepository;

    public TradeServiceImpl(PlayerRepository playerRepository,
                           UserRepository userRepository,
                           PositionRepository positionRepository,
                           TransactionRepository transactionRepository) {
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public TradeResponse buy(Long buyerId, Long sellerId, Long playerId, Integer quantity) {
        validateTrade(buyerId, sellerId, playerId, quantity);

        UserRecord buyer = userRepository.findById(buyerId)
            .orElseThrow(() -> new NotFoundException("Buyer not found: " + buyerId));
        UserRecord seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new NotFoundException("Seller not found: " + sellerId));
        PlayerRecord player = playerRepository.findById(playerId)
            .orElseThrow(() -> new NotFoundException("Player not found: " + playerId)); // TODO cambiar excepción por una más específica

        int totalAmount = quantity * player.getCurrentPrice();
        if (buyer.getCreditBalance() < totalAmount) {
            throw new ConflictException("Buyer does not have enough credits"); // TODO es lógica de modelo
        }

        PositionRecord sellerPosition = positionRepository.findByUserIdAndPlayerId(sellerId, playerId)
            .orElseThrow(() -> new ConflictException("Seller has no position for player " + playerId)); // TODO es lógica de modelo
        if (sellerPosition.getQuantity() < quantity) {
            throw new ConflictException("Seller lacks required token quantity"); // TODO es lógica de modelo
        }

        PositionRecord buyerPosition = positionRepository.findByUserIdAndPlayerId(buyerId, playerId)
            .orElse(new PositionRecord(buyerId, playerId, 0));

        buyer.setCreditBalance(buyer.getCreditBalance() - totalAmount); // TODO es lógica de modelo
        seller.setCreditBalance(seller.getCreditBalance() + totalAmount); // TODO es lógica de modelo

        buyerPosition.setQuantity(buyerPosition.getQuantity() + quantity); // TODO es lógica de modelo
        sellerPosition.setQuantity(sellerPosition.getQuantity() - quantity); // TODO es lógica de modelo

        positionRepository.save(buyerPosition);
        positionRepository.save(sellerPosition);
        userRepository.save(buyer);
        userRepository.save(seller);

        TransactionRecord tx = transactionRepository.save(new TransactionRecord( // TODO es lógica de modelo
            Instant.now(),
            "BUY",
            buyer.getId(),
            seller.getId(),
            player.getId(),
            quantity,
            player.getCurrentPrice(),
            totalAmount));

        return new TradeResponse(tx.getId(), buyer.getId(), seller.getId(), player.getId(),
            quantity, player.getCurrentPrice(), totalAmount, tx.getTimestamp());
    }

    @Override
    @Transactional
    public TradeResponse sell(Long sellerId, Long buyerId, Long playerId, Integer quantity) { // TODO ojo con lógica de modelo
        validateTrade(buyerId, sellerId, playerId, quantity);

        UserRecord seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new NotFoundException("Seller not found: " + sellerId));
        UserRecord buyer = userRepository.findById(buyerId)
            .orElseThrow(() -> new NotFoundException("Buyer not found: " + buyerId));
        PlayerRecord player = playerRepository.findById(playerId)
            .orElseThrow(() -> new NotFoundException("Player not found: " + playerId));

        int totalAmount = quantity * player.getCurrentPrice();
        if (sellerId.equals(buyerId)) {
            throw new ValidationException("Seller and buyer must be different users");
        }

        PositionRecord sellerPosition = positionRepository.findByUserIdAndPlayerId(sellerId, playerId)
            .orElseThrow(() -> new ConflictException("Seller has no position for player " + playerId));
        if (sellerPosition.getQuantity() < quantity) {
            throw new ConflictException("Seller lacks required token quantity");
        }
        if (buyer.getCreditBalance() < totalAmount) {
            throw new ConflictException("Buyer does not have enough credits");
        }

        PositionRecord buyerPosition = positionRepository.findByUserIdAndPlayerId(buyerId, playerId)
            .orElse(new PositionRecord(buyerId, playerId, 0));

        buyer.setCreditBalance(buyer.getCreditBalance() - totalAmount);
        seller.setCreditBalance(seller.getCreditBalance() + totalAmount);

        buyerPosition.setQuantity(buyerPosition.getQuantity() + quantity);
        sellerPosition.setQuantity(sellerPosition.getQuantity() - quantity);

        positionRepository.save(buyerPosition);
        positionRepository.save(sellerPosition);
        userRepository.save(buyer);
        userRepository.save(seller);

        TransactionRecord tx = transactionRepository.save(new TransactionRecord(
            Instant.now(),
            "SELL",
            buyer.getId(),
            seller.getId(),
            player.getId(),
            quantity,
            player.getCurrentPrice(),
            totalAmount));

        return new TradeResponse(tx.getId(), buyer.getId(), seller.getId(), player.getId(),
            quantity, player.getCurrentPrice(), totalAmount, tx.getTimestamp());
    }

    private void validateTrade(Long buyerId, Long sellerId, Long playerId, Integer quantity) {
        if (buyerId == null || sellerId == null || playerId == null) {
            throw new ValidationException("buyerId, sellerId and playerId are required");
        }
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("quantity must be positive");
        }
        if (buyerId.equals(sellerId)) {
            throw new ValidationException("Buyer and seller must be different users");
        }
    }
}
