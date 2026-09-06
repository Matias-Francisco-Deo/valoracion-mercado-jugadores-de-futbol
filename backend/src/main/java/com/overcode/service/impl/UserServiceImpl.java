package com.overcode.service.impl;

import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EmailRepetidoException;
import com.overcode.service.exception.NombreRepetidoException;
import com.overcode.service.exception.NotFoundException;
import com.overcode.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
//    private final PositionRepository positionRepository;
//    private final TransactionRepository transactionRepository;

    public UserServiceImpl(UserRepository userRepository
//                           PositionRepository positionRepository,
//                           TransactionRepository transactionRepository
    ) {
        this.userRepository = userRepository;
//        this.positionRepository = positionRepository;
//        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public User create(User userACrear) {
        if (userRepository.existsByUsername(userACrear.getUsername())) {
            throw new NombreRepetidoException("El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(userACrear.getEmail())) {
            throw new EmailRepetidoException("El email ya existe");
        }

        return userRepository.save(userACrear);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.recuperar(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

//    @Override
//    @Transactional(readOnly = true)
//    public PortfolioDto getPortfolio(Long userId) {
//        UserJPADTO user = userDAO.findById(userId)
//            .orElseThrow(() -> new NotFoundException("User not found: " + userId));
//        List<PositionDto> positions = positionRepository.findByUserId(userId).stream()
//            .map(position -> new PositionDto(position.getPlayerId(), position.getQuantity()))
//            .toList();
//        return new PortfolioDto(user.getId(), user.getCreditBalance(), positions);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<TransactionDto> getTransactions(Long userId) {
//        UserJPADTO user = userDAO.findById(userId)
//            .orElseThrow(() -> new NotFoundException("User not found: " + userId));
//        return transactionRepository.findByBuyerIdOrSellerIdOrderByTimestampDesc(user.getId(), user.getId()).stream()
//            .map(record -> new TransactionDto(
//                record.getId(),
//                record.getTimestamp(),
//                record.getType(),
//                record.getBuyerId(),
//                record.getSellerId(),
//                record.getPlayerId(),
//                record.getQuantity(),
//                record.getUnitPrice(),
//                record.getTotalAmount()))
//            .toList();
//    }
}
