package com.overcode.controller;

import com.overcode.controller.dto.market.DepositPetition;
import com.overcode.controller.dto.portfolio.PortfolioResponseDTO;
import com.overcode.controller.dto.portfolio.PositionResponseDTO;
import com.overcode.controller.dto.token.SummaryTokenPageResponseDTO;
import com.overcode.controller.dto.transaction.TransactionPageResponseDTO;
import com.overcode.controller.dto.user.DepositRequest;
import com.overcode.controller.dto.user.DepositResponseDTO;
import com.overcode.controller.dto.user.UserResponseDTO;
import com.overcode.model.token.SummaryTokenPlayer;
import com.overcode.model.user.Portfolio;
import com.overcode.model.user.User;
import com.overcode.model.venta.transaction.TransaccionFinanciera;
import com.overcode.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for user information")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        User user = userService.recuperar(id);
        UserResponseDTO dto = UserResponseDTO.desdeModelo(user);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping("/{userId}/tokens")
    public ResponseEntity<SummaryTokenPageResponseDTO> buscarSummaryTokensPorUsuario(
            @PathVariable Long userId,
            @RequestParam(required = false) String playerName,
            @PageableDefault(size = 10) Pageable pageable
    ) {

        Page<SummaryTokenPlayer> resultado = userService.buscarSummaryTokensPorUsuario(userId, playerName, pageable);

        SummaryTokenPageResponseDTO respuesta = SummaryTokenPageResponseDTO.desdeModelo(resultado);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{userId}/transactions")
    public  ResponseEntity<TransactionPageResponseDTO> getTransacciones(
            @PathVariable Long userId,
            Pageable pageable
    ){
        Page<TransaccionFinanciera> transactions = userService.recuperarTransaccionesDeUnUser(userId, pageable);

        return ResponseEntity.ok(TransactionPageResponseDTO.desdeModelo(transactions));
    }

    @GetMapping("/{userId}/portfolio")
    public ResponseEntity<List<PositionResponseDTO>> getPortfolio(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        Portfolio portfolio = userService.recuperarPortfolioDeUser(user);
        PortfolioResponseDTO portfolioResponseDTO = PortfolioResponseDTO.desdeModelo(portfolio);

        return ResponseEntity.ok(portfolioResponseDTO.positions());
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponseDTO> deposit(@Valid @RequestBody DepositRequest request, Principal principal) {

        long userId = Long.parseLong(principal.getName());

        DepositPetition depositPetition = request.aModelo(userId);

        User updatedUser = userService.depositarCreditos(depositPetition);
        return ResponseEntity.ok(DepositResponseDTO.desdeModelo(updatedUser));

    }

}
