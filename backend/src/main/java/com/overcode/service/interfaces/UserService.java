package com.overcode.service.interfaces;

import com.overcode.controller.dto.CreateUserRequest;
import com.overcode.controller.dto.PortfolioDto;
import com.overcode.controller.dto.TransactionDto;
import com.overcode.controller.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(CreateUserRequest request);

    UserDto getUser(Long id);

    PortfolioDto getPortfolio(Long userId);

    List<TransactionDto> getTransactions(Long userId);
}
