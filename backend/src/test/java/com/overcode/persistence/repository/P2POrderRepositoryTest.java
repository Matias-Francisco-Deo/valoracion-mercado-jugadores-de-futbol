package com.overcode.persistence.repository;

import com.overcode.model.OrderStatus;
import com.overcode.model.OrderType;
import com.overcode.model.P2POrder;
import com.overcode.persistence.repository.dao.jpa.P2POrderDAOJPA;
import com.overcode.persistence.repository.interfaces.P2POrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class P2POrderRepositoryTest {

    @Autowired
    private P2POrderRepository p2pOrderRepository;

    @Autowired
    private P2POrderDAOJPA p2pOrderDAOJPA;

    private static final Long PLAYER_ID = 999_001L;

    @AfterEach
    void tearDown() {
        p2pOrderDAOJPA.deleteAll();
    }

    private P2POrder sellOrder(Long userId, int quantity, String price) {
        return new P2POrder(UUID.randomUUID(), userId, PLAYER_ID, OrderType.SELL, quantity,
                new BigDecimal(price), OrderStatus.OPEN, Instant.now(), null);
    }

    @Test
    @Transactional
    void saveAsignaVersionParaUnaOrdenNueva() {
        P2POrder saved = p2pOrderRepository.save(sellOrder(1L, 5, "10.00"));

        assertNotNull(saved.getVersion());
        assertTrue(p2pOrderRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @Transactional
    void findOpenSellOrdersDevuelveSoloAbiertasOrdenadasPorPrecioAscendenteYLuegoFifo() throws InterruptedException {
        P2POrder expensive = p2pOrderRepository.save(sellOrder(1L, 5, "20.00"));
        Thread.sleep(5);
        P2POrder cheap = p2pOrderRepository.save(sellOrder(2L, 5, "5.00"));
        Thread.sleep(5);
        P2POrder sameAsCheapButLater = p2pOrderRepository.save(sellOrder(3L, 5, "5.00"));

        P2POrder cancelled = p2pOrderRepository.save(sellOrder(4L, 5, "1.00"));
        cancelled.cancel();
        p2pOrderRepository.save(cancelled);

        List<P2POrder> open = p2pOrderRepository.findOpenSellOrders(PLAYER_ID);

        assertEquals(3, open.size());
        assertEquals(cheap.getId(), open.get(0).getId());
        assertEquals(sameAsCheapButLater.getId(), open.get(1).getId());
        assertEquals(expensive.getId(), open.get(2).getId());
        assertTrue(open.stream().noneMatch(o -> o.getId().equals(cancelled.getId())));
    }

    @Test
    @Transactional
    void saveAllPersisteVariasOrdenesEnUnSoloBatch() {
        P2POrder orderA = sellOrder(1L, 5, "10.00");
        P2POrder orderB = sellOrder(2L, 3, "11.00");

        List<P2POrder> saved = p2pOrderRepository.saveAll(List.of(orderA, orderB));

        assertEquals(2, saved.size());
        assertTrue(saved.stream().allMatch(o -> o.getVersion() != null));
        assertEquals(2, p2pOrderRepository.findOpenSellOrders(PLAYER_ID).size());
    }

    @Test
    @Transactional
    void findByIdDevuelveVacioSiNoExiste() {
        assertTrue(p2pOrderRepository.findById(UUID.randomUUID()).isEmpty());
    }
}
