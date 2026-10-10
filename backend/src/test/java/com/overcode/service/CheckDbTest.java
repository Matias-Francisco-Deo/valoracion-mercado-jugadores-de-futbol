package com.overcode.service;

import com.overcode.model.Portfolio;
import com.overcode.persistence.repository.interfaces.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class CheckDbTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    @Transactional(readOnly = true)
    public void printPortfolios() {
        System.out.println("====== DB PORTFOLIOS ======");
        for (Portfolio p : portfolioRepository.findAll()) {
            System.out.println("User: " + p.getUserId() + ", Credits: " + p.getCredits() + ", Holdings: " + p.getTokenHoldings().size());
            p.getTokenHoldings().forEach(th -> System.out.println("  Player: " + th.getPlayerId() + " Qty: " + th.getQuantity()));
        }
        System.out.println("===========================");
    }
}
