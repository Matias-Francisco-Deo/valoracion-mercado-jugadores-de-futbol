package com.overcode.integration;

import com.overcode.controller.dto.CreateUserRequest;
import com.overcode.controller.dto.TradeResponse;
import com.overcode.controller.dto.UserDto;
import com.overcode.persistency.dto.PlayerRecord;
import com.overcode.persistency.dto.PositionRecord;
import com.overcode.persistency.dto.TransactionRecord;
import com.overcode.persistency.dto.UserJPADTO;
import com.overcode.persistency.repository.PlayerRepository;
import com.overcode.persistency.repository.PositionRepository;
import com.overcode.persistency.repository.TransactionRepository;
import com.overcode.persistency.repository.UserRepository;
import com.overcode.service.impl.TradeServiceImpl;
import com.overcode.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketApiIT {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private TradeServiceImpl tradeService;

    @Test
    void userRegistration_shouldCreateProfileWithZeroCredits() {
        CreateUserRequest request = new CreateUserRequest("alice", "alice@example.com", "secret");
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(any(UserJPADTO.class))).thenAnswer(invocation -> {
            UserJPADTO record = invocation.getArgument(0);
            record.setId(42L);
            return record;
        });

        UserDto createdUser = userService.createUser(request);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.id()).isEqualTo(42L);
        assertThat(createdUser.creditBalance()).isZero();
    }

    @Test
    void buyFlow_shouldAdjustBalanceAndLedger() {
        UserJPADTO buyer = new UserJPADTO("buyer", "buyer@example.com", "secret", 1_000);
        buyer.setId(7L);
        UserJPADTO seller = new UserJPADTO("superuser", "super@market.local", "password", 0);
        seller.setId(1L);
        PlayerRecord player = new PlayerRecord("Lionel Messi", 100, 100);
        player.setId(1L);
        PositionRecord sellerPosition = new PositionRecord(1L, 1L, 100);
        sellerPosition.setId(10L);
        PositionRecord buyerPosition = new PositionRecord(7L, 1L, 0);

        when(userRepository.findById(7L)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(positionRepository.findByUserIdAndPlayerId(1L, 1L)).thenReturn(Optional.of(sellerPosition));
        when(positionRepository.findByUserIdAndPlayerId(7L, 1L)).thenReturn(Optional.empty());
        when(positionRepository.save(any(PositionRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(UserJPADTO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(TransactionRecord.class))).thenAnswer(invocation -> {
            TransactionRecord tx = invocation.getArgument(0);
            tx.setId(99L);
            tx.setTimestamp(Instant.now());
            return tx;
        });

        TradeResponse response = tradeService.buy(7L, 1L, 1L, 5);

        assertThat(response).isNotNull();
        assertThat(response.totalAmount()).isEqualTo(500);
        assertThat(response.transactionId()).isEqualTo(99L);
        assertThat(buyer.getCreditBalance()).isEqualTo(500);
        assertThat(seller.getCreditBalance()).isEqualTo(500);
    }
}
