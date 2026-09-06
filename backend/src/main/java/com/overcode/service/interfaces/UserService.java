package com.overcode.service.interfaces;

import com.overcode.controller.dto.PortfolioDto;
import com.overcode.controller.dto.TransactionDto;
import com.overcode.model.User;

import java.util.List;

public interface UserService {

    User create(User request);

    User getUser(Long id);

//    PortfolioDto getPortfolio(Long userId);
//
//    List<TransactionDto> getTransactions(Long userId);
}
