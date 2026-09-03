package com.overcode.service.impl;

import com.overcode.controller.dto.CreateUserRequest;
import com.overcode.controller.dto.PortfolioDto;
import com.overcode.controller.dto.PositionDto;
import com.overcode.controller.dto.TransactionDto;
import com.overcode.controller.dto.UserDto;
import com.overcode.model.User;
import com.overcode.persistency.dto.PositionRecord;
import com.overcode.persistency.dto.TransactionRecord;
import com.overcode.persistency.dto.UserRecord;
import com.overcode.persistency.repository.PositionRepository;
import com.overcode.persistency.repository.TransactionRepository;
import com.overcode.persistency.repository.UserRepository;
import com.overcode.service.exception.NotFoundException;
import com.overcode.service.exception.ValidationException;
import com.overcode.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final TransactionRepository transactionRepository;

    public UserServiceImpl(UserRepository userRepository,
                          PositionRepository positionRepository,
                          TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ValidationException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ValidationException("Email already exists");
        }

        User user = User.create(request.username(), request.email(), request.password());
        UserRecord saved = userRepository.save(new UserRecord(user.getUsername(), user.getEmail(), user.getPassword(), user.getCreditBalance()));
        return new UserDto(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getCreditBalance());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        UserRecord record = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found: " + id));
        return new UserDto(record.getId(), record.getUsername(), record.getEmail(), record.getCreditBalance());
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioDto getPortfolio(Long userId) {
        UserRecord user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        List<PositionDto> positions = positionRepository.findByUserId(userId).stream()
            .map(position -> new PositionDto(position.getPlayerId(), position.getQuantity()))
            .toList();
        return new PortfolioDto(user.getId(), user.getCreditBalance(), positions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactions(Long userId) {
        UserRecord user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        return transactionRepository.findByBuyerIdOrSellerIdOrderByTimestampDesc(user.getId(), user.getId()).stream()
            .map(record -> new TransactionDto(
                record.getId(),
                record.getTimestamp(),
                record.getType(),
                record.getBuyerId(),
                record.getSellerId(),
                record.getPlayerId(),
                record.getQuantity(),
                record.getUnitPrice(),
                record.getTotalAmount()))
            .toList();
    }
}
