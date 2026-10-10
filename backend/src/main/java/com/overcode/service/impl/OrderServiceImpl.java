package com.overcode.service.impl;

import com.overcode.controller.dto.order.OrderBuy;
import com.overcode.model.Player;
import com.overcode.model.User;
import com.overcode.persistence.repository.dao.jpa.TokenForSaleProjection;
import com.overcode.persistence.repository.interfaces.OrderRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.ValidationException;
import com.overcode.service.interfaces.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private PlayerRepository playerRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, PlayerRepository playerRepository){
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public Page<TokenForSaleProjection> searchTokensForSale(String playerName, Pageable pageable){
        return orderRepository.searchTokensForSale(playerName,pageable);
    }

    @Override
    @Transactional
    public OrderBuy buyTokens(Long playerId, Integer quantity){
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("La cantidad debe ser mayor que cero");
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntidadNoEncontradaException("El usuario no existe."));

        Player player = playerRepository.recuperar(playerId)
                .orElseThrow(() ->new EntidadNoEncontradaException("El jugador no existe"));

        Integer totalPrice = player.getCurrentPrice()*quantity;
        if(buyer.getCreditBalance() < totalPrice){throw new ValidationException("Creditos insuficientes para realizar la compra");}

        List<Long> tokenIds = orderRepository.findRandomTokensForUpdate(playerId,quantity);
        if (tokenIds.size() < quantity) {throw new ValidationException("No hay suficientes tokens disponibles");}//cambiar exepciones?

        Integer updatedRows = orderRepository.transferTokens(tokenIds, buyer.getId());
        if (updatedRows != quantity) {throw new IllegalStateException( "No se pudieron transferir todos los tokens");}

        buyer.setCreditBalance(buyer.getCreditBalance() - totalPrice);
        userRepository.guardar(buyer);

        return new OrderBuy(playerId,quantity);
    }
}
